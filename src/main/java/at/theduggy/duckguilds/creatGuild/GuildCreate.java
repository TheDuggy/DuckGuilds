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

package at.theduggy.duckguilds.creatGuild;

import at.theduggy.duckguilds.Main;
import at.theduggy.duckguilds.config.GuildConfig;
import at.theduggy.duckguilds.logging.AutoLogger;
import at.theduggy.duckguilds.other.Utils;
import org.apache.log4j.Level;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

  public class GuildCreate {

    public static void createGuild(Player p, ChatColor color, String name, String tag, ChatColor tagColor) throws IOException, ParseException {
            if (name.length() <= 50) {
                if (!Utils.isPlayerInGuild(p)) {
                    if (!Utils.guildExists(name)) {
                        if (Utils.isStringReadyToUse(name)) {
                            if (Utils.isReadyForCreate(tag)) {
                                if (tag.length() <= 4) {
                                    if (GuildConfig.getMaxGuildSize()>0){
                                        Bukkit.getLogger().warning(String.valueOf(GuildConfig.getMaxGuildSize()));
                                        if (Main.cachedGuilds.size()> GuildConfig.getMaxGuildSize()){
                                            addPlayerToTeamAndCreateFiles(p,color,name,tag,tagColor);
                                            AutoLogger.logMessage( p.getUniqueId() + "(" + p.getName() + ") created the guild " + name + "!%n Tag: " + tag + "%n Color: " + color + "%n tagColor: " + tag, Level.INFO);
                                        }else {
                                            p.sendMessage(Main.prefix + ChatColor.RED + "The servers max guild-level was reached, which is " + ChatColor.YELLOW + GuildConfig.getMaxGuildSize() + ChatColor.RED + " and the amount of guilds on this server is " + ChatColor.YELLOW + Main.cachedGuilds.size() + ChatColor.RED + " !" + " You can't create guilds till a minimum of 1 is deleted!");
                                        }
                                    }else {
                                        addPlayerToTeamAndCreateFiles(p,color,name,tag,tagColor);
                                    }
                                } else {
                                    p.sendMessage(Main.prefix + ChatColor.RED + "The tag is " + ChatColor.YELLOW + tag.length() + ChatColor.RED + " characters long, but can only be 4 characters long!");
                                }
                            } else {
                                p.sendMessage(Main.prefix + "The tag contains forbidden symbols!");
                            }
                        } else {
                            p.sendMessage(Main.prefix + ChatColor.RED + "Tha guild-name " + ChatColor.YELLOW + name + ChatColor.RED + " is invalid, because it contains other symbols than alphabetic characters or digits!");
                        }
                    } else {
                        Bukkit.getLogger().info(Main.cachedGuilds.toString());
                        p.sendMessage(Main.prefix + ChatColor.RED + "Guild already exists!");
                    }
                }else {
                    p.sendMessage(Main.playerAlreadyInGuild);
                }
        }else {
                p.sendMessage(Main.prefix + ChatColor.RED + "The name of a guild can't be longer that 50 characters!");
            }
    }

    public static void addPlayerToTeamAndCreateFiles(Player p, ChatColor color, String name, String tag, ChatColor tagColor) throws IOException, ParseException {
        Team guild;
        try {
             guild = Main.scoreboard.registerNewTeam(name);
        }catch (IllegalArgumentException e){
            guild = Main.scoreboard.getTeam(name);
        }
        ArrayList<UUID> players = new ArrayList<>();
        players.add(p.getUniqueId());
        ArrayList<String> playersForFile = new ArrayList<>();
        playersForFile.add(p.getUniqueId().toString());
        JSONObject components = new JSONObject();
        guild.setSuffix(ChatColor.GRAY + "[" + tagColor + tag + ChatColor.GRAY + "]");
        guild.setColor(color);
        guild.setDisplayName(name);

        components.put("name", name);
        components.put("color", Utils.getChatColorCode(color));
        components.put("tag", tag);
        components.put("tagColor", Utils.getChatColorCode(tagColor));
        components.put("players", playersForFile);
        components.put("head", p.getUniqueId().toString());
        HashMap<String, Object> guildData = new HashMap<>();
        guildData.put("name", name);
        guildData.put("color", Utils.getChatColorCode(color));
        guildData.put("tag", tag);
        guildData.put("tagColor", Utils.getChatColorCode(tagColor));
        guildData.put("players", players);
        guildData.put("head", p.getUniqueId());
        createGuildAndSafeFile(p, name, components, guildData);
        addGuildToPlayerGuildFile(name, p);
        guild.addEntry(p.getName());
        p.setDisplayName(color + p.getName() + ChatColor.GRAY + "[" + tagColor + tag + ChatColor.GRAY + "]" + ChatColor.WHITE);
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.setScoreboard(Main.scoreboard);
        }
        p.sendMessage(Main.prefix + ChatColor.GREEN + "Your guild with the name " + ChatColor.UNDERLINE + "" + ChatColor.GOLD + name + ChatColor.GREEN + " has been created!");
    }


    public static void addPlayerToTeamAndCreateFilesWhenTeamAlreadyInUse(Player p, ChatColor color, String name, String tag, ChatColor tagColor){

    }

    public static void createGuildAndSafeFile(Player p,String name, JSONObject components, HashMap<String,Object> dataToCache) throws IOException, ParseException {
        Path guildGuildsFolder = Paths.get(Main.guildRootFolder + "/guilds");
        Path guildFile = Paths.get(guildGuildsFolder + "/" + name + ".json");
        if (Files.exists(guildFile)){
            p.sendMessage( Main.prefix + ChatColor.RED + "Guild already exists!");
        }else {
            Files.createFile(guildFile);
            FileWriter fileWriter = new FileWriter(String.valueOf(guildFile),StandardCharsets.UTF_8);
            fileWriter.write(components.toJSONString());
            fileWriter.close();
            indexGuild(dataToCache,name);
        }

    }

    public static void indexGuild(HashMap<String,Object> dataToCache,String name) throws IOException, ParseException {

        Main.cachedGuilds.put(name, dataToCache);
        Path guildIndexFile = Paths.get(Main.guildRootFolder + "/index.json");
        JSONParser jsonParser = new JSONParser();
        FileReader fileReader = new FileReader(guildIndexFile.toFile(), StandardCharsets.UTF_8);
        JSONObject allGuildsJson = (JSONObject) jsonParser.parse(fileReader);
        fileReader.close();
        ArrayList<String> allGuilds = (ArrayList<String>) allGuildsJson.get("guilds");
        allGuilds.add(name);
        allGuildsJson.remove("guilds");
        allGuildsJson.put("guilds", allGuilds);
        FileWriter writeNewGuildsToIndexFile = new FileWriter(String.valueOf(guildIndexFile),StandardCharsets.UTF_8);
        writeNewGuildsToIndexFile.write(allGuildsJson.toJSONString());
        writeNewGuildsToIndexFile.close();
    }

    public static void addGuildToPlayerGuildFile(String name, Player p) throws IOException, ParseException {
        Path guildPlayerFolder = Paths.get(Main.guildRootFolder + "/playerData");
        Path personalPlayerGuildFolder = Paths.get(guildPlayerFolder + "/" + p.getUniqueId());
        Path personalPlayerGuildTeamsFile = Paths.get(personalPlayerGuildFolder + "/data.json");
        JSONParser jsonParser = new JSONParser();
        FileReader fileReader = new FileReader(personalPlayerGuildTeamsFile.toFile(),StandardCharsets.UTF_8);
        JSONObject addGuildToPlayerGuildFile = (JSONObject) jsonParser.parse(fileReader);
        fileReader.close();
        addGuildToPlayerGuildFile.remove("guild");
        addGuildToPlayerGuildFile.put("guild",name);
        FileWriter fileWriter = new FileWriter(String.valueOf(personalPlayerGuildTeamsFile),StandardCharsets.UTF_8);
        fileWriter.write(addGuildToPlayerGuildFile.toJSONString());
        fileWriter.close();
        UUID uuidFromPlayer = p.getUniqueId();
        HashMap<String,Object> tempCachedPlayerData = new HashMap<>();
        tempCachedPlayerData.put("name", Main.cachedPlayers.get(uuidFromPlayer).get("name"));
        tempCachedPlayerData.put("guild",name);
        tempCachedPlayerData.put("online", Main.cachedPlayers.get(uuidFromPlayer).get("online"));
        Main.cachedPlayers.remove(uuidFromPlayer);
        Main.cachedPlayers.put(uuidFromPlayer, tempCachedPlayerData);
    }
}
