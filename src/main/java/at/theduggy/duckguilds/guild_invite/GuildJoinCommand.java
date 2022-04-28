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
package at.theduggy.duckguilds.guild_invite;


import at.theduggy.duckguilds.Main;
import at.theduggy.duckguilds.other.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class GuildJoinCommand {

    public static void inviteReceive(Player player, String guildName) throws IOException, ParseException {

        if (!Utils.isPlayerInGuild(player)){
            if (Main.getGuildCache().containsKey(guildName)){
                if (Main.guildInvites.get(guildName).contains(player.getName())){
                    addGuildToPlayerGuildFile(guildName,player);
                    addPlayerToGuildFile(player,guildName);
                    reindexGuild(player,guildName);
                    Team team;
                    try {
                      team = Main.getScoreboard().registerNewTeam(guildName);
                    }catch (IllegalArgumentException e){
                        team = Main.getScoreboard().getTeam(guildName);
                    }
                    team.setColor(Main.getGuildCache().get(guildName).getGuildColor().getChatColor());
                    team.setSuffix(ChatColor.GRAY + "[" +  Main.getGuildCache().get(guildName).getTagColor().getChatColor() + Main.getGuildCache().get(guildName).getTag() + ChatColor.GRAY + "]");
                    team.addEntry(player.getName());
                    player.setDisplayName(Main.getGuildCache().get(guildName).getGuildColor() + player.getName() + ChatColor.GRAY + "[" + Main.getGuildCache().get(guildName).getTagColor().getChatColor() + Main.getGuildCache().get(guildName).getTag() + ChatColor.GRAY + "]" + ChatColor.WHITE);
                    player.sendMessage(Main.prefix + ChatColor.GREEN + " You successfully joined " + ChatColor.GOLD + guildName + ChatColor.GREEN + "!");
                    for (Player playerFromServer: Bukkit.getOnlinePlayers()){
                        playerFromServer.setScoreboard(Main.getScoreboard());
                        if (Utils.getPlayerGuild(playerFromServer).equals(guildName)){
                            player.sendMessage(Main.prefix + ChatColor.YELLOW  + player.getName() + ChatColor.GREEN + " has joined your guild!");
                        }
                    }
                    Main.guildInvites.get(guildName).remove(player.getName());
                }else {
                    player.sendMessage(Main.prefix + ChatColor.RED + "You aren't invited to this guild!");
                }
            }else {
                player.sendMessage(Main.guildDoesntExists);
            }
        }else {
            player.sendMessage(Main.playerAlreadyInGuild);
        }
    }

    public static void addPlayerToGuildFile(Player player, String guildName) throws IOException, ParseException {
        Path guildGuildsFolder = Paths.get(Main.guildRootFolder + "/guilds");
        Path guildFile = Paths.get(guildGuildsFolder + "/" + guildName + ".json");
        FileReader fileReader = new FileReader(guildFile.toFile(),StandardCharsets.UTF_8);
        JSONParser jsonParser = new JSONParser();
        JSONObject oldGuildFile = (JSONObject) jsonParser.parse(fileReader);
        fileReader.close();
        ArrayList<String> allPlayersOfThatGuild = (ArrayList<String>) oldGuildFile.get("players");
        allPlayersOfThatGuild.add(player.getUniqueId().toString());
        oldGuildFile.remove("players");
        oldGuildFile.put("players",allPlayersOfThatGuild);
        FileWriter fileWriter = new FileWriter(guildFile.toFile(), StandardCharsets.UTF_8);
        fileWriter.write(oldGuildFile.toJSONString());
        fileWriter.close();
    }
    public static void addGuildToPlayerGuildFile(String name, Player player) throws IOException, ParseException {
        Main.getPlayerCache().get(player.getUniqueId()).setGuild(name);
    }

    public static void reindexGuild(Player player,String name) throws ParseException {
        Main.getGuildCache().get(name).getPlayers().add(player.getUniqueId());
    }
}
