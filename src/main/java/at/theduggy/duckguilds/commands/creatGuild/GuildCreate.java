  /*DuckGuilds: a plugin for creating/managing guilds
    Copyright (C) 2021 Georg Kollegger (or TheDuggy/CoderTheDuggy)

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.*/

package at.theduggy.duckguilds.commands.creatGuild;

import at.theduggy.duckguilds.Main;
import at.theduggy.duckguilds.config.GuildConfigHandler;
import at.theduggy.duckguilds.objects.GuildColor;
import at.theduggy.duckguilds.objects.GuildMetadata;
import at.theduggy.duckguilds.objects.GuildObject;
import at.theduggy.duckguilds.utils.GuildTextUtils;
import at.theduggy.duckguilds.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

  public class GuildCreate {

    public static void createGuild(Player player, ChatColor color, String name, String tag, ChatColor tagColor) throws IOException, ParseException, SQLException {
            if (name.length() <= 30) {
                if (!Utils.isPlayerInGuild(player)) {
                    if (!Utils.guildExists(name)) {
                        if (GuildTextUtils.isStringReadyToUse(name)) {
                            if (GuildTextUtils.isReadyForCreate(tag)) {
                                if (tag.length() <= 4) {
                                    if (!(GuildConfigHandler.getMaxGuildSize() instanceof Boolean)){
                                        Bukkit.getLogger().warning(String.valueOf(GuildConfigHandler.getMaxGuildSize()));
                                        if (Main.getGuildCache().size() > (int) GuildConfigHandler.getMaxGuildSize()){
                                            addPlayerToTeamAndCreateFiles(player,color,name,tag,tagColor);
                                        }else {
                                            player.sendMessage(GuildTextUtils.maxServerGuildsReached);
                                        }
                                    }else {
                                        addPlayerToTeamAndCreateFiles(player,color,name,tag,tagColor);
                                    }
                                } else {
                                    player.sendMessage(GuildTextUtils.prefix + ChatColor.RED + "The tag is " + ChatColor.YELLOW + tag.length() + ChatColor.RED + " characters long, but can only be 4 characters long!");
                                }
                            } else {
                                player.sendMessage(GuildTextUtils.prefix + ChatColor.RED + "Tha guild-name " + ChatColor.YELLOW + tag + ChatColor.RED + " is invalid, because it contains other symbols than alphabetic characters or digits!");
                            }
                        } else {
                            player.sendMessage( GuildTextUtils.prefix + ChatColor.RED + "Tha guild-name " + ChatColor.YELLOW + name + ChatColor.RED + " is invalid, because it contains other symbols than alphabetic characters or digits!");
                        }
                    } else {
                        player.sendMessage(GuildTextUtils.prefix + ChatColor.RED + "The guild " + name + "already exists!");
                    }
                }else {
                    player.sendMessage(GuildTextUtils.playerAlreadyInGuild);
                }
        }else {
                player.sendMessage(GuildTextUtils.guildNameToLong);
            }
    }

    public static void addPlayerToTeamAndCreateFiles(Player player, ChatColor color, String name, String tag, ChatColor tagColor) throws SQLException, IOException {
        Team guild;
        try {
             guild = Main.getScoreboard().registerNewTeam(name);
        }catch (IllegalArgumentException e){
            guild = Main.getScoreboard().getTeam(name);
        }
        ArrayList<UUID> players = new ArrayList<>();
        players.add(player.getUniqueId());
        guild.setColor(color);
        guild.setSuffix(ChatColor.GRAY + "[" + tagColor + tag + ChatColor.GRAY + "]");
        guild.setColor(color);
        guild.setDisplayName(name);
        GuildObject guildObject = new GuildObject();
        guildObject.setGuildColor(new GuildColor(color));
        guildObject.setTag(tag);
        guildObject.setTagColor(new GuildColor(tagColor));
        guildObject.setPlayers(players);
        guildObject.setHead(player.getUniqueId());
        guildObject.setName(name);
        guildObject.setGuildMetadata(new GuildMetadata(LocalDateTime.now(), Main.getPlayerCache().get(player.getUniqueId()).getName()));
        //TODO Make a detailed option to save stuff like creation-time!
        Main.getMainStorage().createGuildStorageSection(guildObject);
        Main.getGuildCache().put(name, guildObject);
        reCachePlayer(name, player);
        guild.addEntry(player.getName());
        player.setDisplayName(color + player.getName() + ChatColor.GRAY + "[" + tagColor + tag + ChatColor.GRAY + "]" + ChatColor.WHITE);
        for (Player playerFromServer : Bukkit.getOnlinePlayers()) {
            playerFromServer.setScoreboard(Main.getScoreboard());
        }
        player.sendMessage(GuildTextUtils.prefix + ChatColor.GREEN + "Your guild with the name " + ChatColor.UNDERLINE + "" + ChatColor.GOLD + name + ChatColor.GREEN + " has been created!");
    }

    public static void reCachePlayer(String name, Player player){
        UUID uuidFromPlayer = player.getUniqueId();
        Main.getPlayerCache().get(uuidFromPlayer).setGuild(name);
    }
}
