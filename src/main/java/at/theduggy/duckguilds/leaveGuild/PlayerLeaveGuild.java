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
package at.theduggy.duckguilds.leaveGuild;

import at.theduggy.duckguilds.Main;
import at.theduggy.duckguilds.config.GuildsConfig;
import at.theduggy.duckguilds.files.GuildFiles;
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
import java.util.HashMap;
import java.util.UUID;

public class PlayerLeaveGuild {

    public static void leaveGuild(Player p, String name) throws IOException, ParseException {
        if (Main.cachedGuilds.containsKey(name)) {
            HashMap<String, Object> playersList = Main.cachedGuilds.get(name);
            ArrayList<UUID> players = (ArrayList<UUID>) playersList.get("players");
            if (players.contains(p.getUniqueId())) {
                    removePlayerFromScoreboard(p, name);
                    removeGuildFromPlayerFile(p);
                    changeGuildFile(p, name);
                    reindexAndChangeFile(name);
                    p.sendMessage(Main.prefix + ChatColor.RED + "You left the guild " + ChatColor.YELLOW + name + ChatColor.RED + "!");
            } else {
                p.sendMessage(Main.youArentInThatGuild);

            }
        }else {
            p.sendMessage(Main.guildDoesntExists);
        }
    }

    public static void removeGuildFromPlayerFile(Player p) throws IOException, ParseException {
        Path guildPlayerFolder = Paths.get(Main.guildRootFolder + "/playerData");
        Path personalPlayerGuildFolder = Paths.get(guildPlayerFolder + "/" + p.getUniqueId());
        Path personalPlayerGuildTeamsFile = Paths.get(personalPlayerGuildFolder + "/data.json");
        JSONParser jsonParser = new JSONParser();
        FileReader fileReader = new FileReader(personalPlayerGuildTeamsFile.toFile(),StandardCharsets.UTF_8);
        JSONObject oldGuild = (JSONObject) jsonParser.parse(fileReader);
        fileReader.close();
        oldGuild.remove("guild");
        oldGuild.put("guild","");
        FileWriter fileWriter = new FileWriter(personalPlayerGuildTeamsFile.toFile(),StandardCharsets.UTF_8);
        fileWriter.write(oldGuild.toJSONString());
        fileWriter.close();
        UUID uuidFromPlayer = p.getUniqueId();
        HashMap<String,Object> tempCachedPlayerData = new HashMap<>();
        tempCachedPlayerData.put("name", Main.cachedPlayers.get(uuidFromPlayer).get("name"));
        tempCachedPlayerData.put("guild","");
        tempCachedPlayerData.put("online", Main.cachedPlayers.get(uuidFromPlayer).get("online"));
        Main.cachedPlayers.remove(uuidFromPlayer);
        Main.cachedPlayers.put(uuidFromPlayer, tempCachedPlayerData);
    }

    public static void changeGuildFile(Player p, String name) throws IOException, ParseException {
        Path guildGuildsFolder = Paths.get(Main.guildRootFolder + "/guilds");
        Path guildFile = Paths.get(guildGuildsFolder + "/" + name + ".json");
        JSONParser jsonParser = new JSONParser();
        FileReader fileReader = new FileReader(guildFile.toFile(),StandardCharsets.UTF_8);
        JSONObject guildFileJsonString = (JSONObject) jsonParser.parse(fileReader);
        fileReader.close();
        ArrayList<String> members = (ArrayList<String>) guildFileJsonString.get("players");
        for (int i = 0;i<=members.size();i++){
            if (members.get(i).equals(p.getUniqueId().toString())){
                members.remove(i);
                break;
            }
        }
        guildFileJsonString.remove("players");
        guildFileJsonString.put("players",members);
        FileWriter fileWriter = new FileWriter(guildFile.toFile(),StandardCharsets.UTF_8);
        fileWriter.write(guildFileJsonString.toJSONString());
        fileWriter.close();
        p.setDisplayName(ChatColor.WHITE + "<" + p.getName() + ">");
    }

    public static void reindexAndChangeFile(String name) throws IOException, ParseException {
        Path guildGuildsFolder = Paths.get(Main.guildRootFolder + "/guilds");
        Path guildFile = Paths.get(guildGuildsFolder + "/" + name + ".json");
        JSONParser jsonParser = new JSONParser();
        FileReader fileReader = new FileReader(guildFile.toFile(),StandardCharsets.UTF_8);
        JSONObject guildFileJsonString = (JSONObject) jsonParser.parse(fileReader);
        fileReader.close();
        HashMap<String,Object> guildDataFromFile = new HashMap<>();
        ArrayList<UUID> players = new ArrayList<>();
        for (String s: (ArrayList<String>)guildFileJsonString.get("players")){
            players.add(UUID.fromString(s));
        }
        guildDataFromFile.put("players",players);
        guildDataFromFile.put("head", UUID.fromString((String) guildFileJsonString.get("head")));
        guildDataFromFile.put("color",  guildFileJsonString.get("color"));
        guildDataFromFile.put("tagColor",  guildFileJsonString.get("tagColor"));
        guildDataFromFile.put("name", guildFileJsonString.get("name"));
        guildDataFromFile.put("tag", guildFileJsonString.get("tag"));
        Main.cachedGuilds.remove(name);
        Main.cachedGuilds.put(name, guildFileJsonString);

    }
    public static void removePlayerFromScoreboard(Player p, String name){
        Team team = Main.scoreboard.getTeam(name);
        team.removeEntry(p.getName());
        for (Player player:Bukkit.getOnlinePlayers()){
            player.setScoreboard(Main.scoreboard);
        }
    }
}
