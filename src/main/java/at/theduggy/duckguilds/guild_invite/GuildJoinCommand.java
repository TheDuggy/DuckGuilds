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
import java.util.HashMap;
import java.util.UUID;

public class GuildJoinCommand {

    public static void inviteReceive(Player player, String guildName) throws IOException, ParseException {
        Path guildGuildsFolder = Paths.get(Main.guildRootFolder + "/guilds");
        Path guildFile = Paths.get(guildGuildsFolder + "/" + guildName + ".json");
        if (!Utils.isPlayerInGuild(player)){
            if (Main.guildInvites.containsKey(guildName)){
                if (Main.guildInvites.get(guildName).contains(player.getName())){
                    addGuildToPlayerGuildFile(guildName,player);

                    addPlayerToGuildFile(player,guildName);
                    reindexGuild(player,guildName);
                   Main.getScoreboard().getTeam(guildName).addEntry(player.getName());
                   FileReader fileReader = new FileReader(guildFile.toFile(), StandardCharsets.UTF_8);
                   JSONParser jsonParser = new JSONParser();
                   JSONObject guildInfo = (JSONObject) jsonParser.parse(fileReader);
                   fileReader.close();
                   player.setDisplayName(Utils.getGuildChatColor(guildName) + player.getName() + ChatColor.GRAY + Utils.getTagColor(guildName) + guildInfo.get("tag") + ChatColor.GRAY + "]" + ChatColor.WHITE);
                    for (Player playerFromServer: Bukkit.getOnlinePlayers()){
                        playerFromServer.setScoreboard(Main.getScoreboard());

                        if (Utils.getPlayerGuild(playerFromServer).equals(guildName)){
                            if (Utils.getIfPlayerIsHeadOfGuild(guildName, playerFromServer)){
                                playerFromServer.sendMessage(Main.prefix + " " + ChatColor.YELLOW + player.getName() + ChatColor.RED + " has joined your guild!");
                            }else {
                                playerFromServer.sendMessage(Main.prefix + ChatColor.GREEN + player.getName() + " joined your guild!");
                            }
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
            player.sendMessage(Main.prefix + ChatColor.RED + "You are already in a guild! You can leave it and if you are the head, you can delete it! See more options with /guild help!");
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
        Path guildPlayerFolder = Paths.get(Main.guildRootFolder + "/playerData");
        Path personalPlayerGuildFolder = Paths.get(guildPlayerFolder + "/" + player.getUniqueId());
        Path personalPlayerGuildTeamsFile = Paths.get(personalPlayerGuildFolder + "/guild.json");
        JSONParser jsonParser = new JSONParser();
        FileReader fileReader = new FileReader(personalPlayerGuildTeamsFile.toFile(),StandardCharsets.UTF_8);
        JSONObject addGuildToPlayerGuildFile = (JSONObject) jsonParser.parse(fileReader);
        fileReader.close();
        addGuildToPlayerGuildFile.remove("guild");
        addGuildToPlayerGuildFile.put("guild",name);
        FileWriter fileWriter = new FileWriter(String.valueOf(personalPlayerGuildTeamsFile),StandardCharsets.UTF_8);
        fileWriter.write(addGuildToPlayerGuildFile.toJSONString());
        fileWriter.close();
    }

    public static void reindexGuild(Player player,String name) throws ParseException {
        HashMap<String, Object> tempCachedData =Main.getGuildCache().get(name);
        ArrayList<UUID> players = new ArrayList<>();
        players.add(player.getUniqueId());
        tempCachedData.remove("players");
        tempCachedData.put("players", players);
        Main.getGuildCache().remove(name);
        Main.getGuildCache().put(name,tempCachedData);
    }
}
