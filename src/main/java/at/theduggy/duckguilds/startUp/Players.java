package at.theduggy.duckguilds.startUp;

import at.theduggy.duckguilds.Main;
import at.theduggy.duckguilds.files.GuildFiles;
import at.theduggy.duckguilds.other.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scoreboard.Team;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.UUID;

public class Players implements Listener {


    public static void addPlayersOnReload() throws IOException, ParseException {
        if (Bukkit.getOnlinePlayers().size()!=0){
            for (Player p:Bukkit.getOnlinePlayers()){
                startBasics(p);
            }
        }
    }

    public static void startBasics(Player p) throws IOException, ParseException {
        Path guildPlayerFolder = Paths.get(Main.guildRootFolder + "/playerData");
        Path personalPlayerGuildFolder = Paths.get(guildPlayerFolder + "/" + p.getUniqueId());
        Path playerNameData = Paths.get(personalPlayerGuildFolder + "/data.json");
        JSONParser getPlayerDataParser = new JSONParser();
        FileReader fileReader = new FileReader(playerNameData.toFile());
        JSONObject getPlayerData= (JSONObject) getPlayerDataParser.parse(fileReader);
        fileReader.close();
        String oldName = (String) getPlayerData.get("name");
        if (!oldName.equals(p.getName())){
            JSONObject newPlayerName= new JSONObject();
            getPlayerData.remove("name");
            getPlayerData.put("name", p.getName());
            FileWriter changePlayerNameInFile = new FileWriter(String.valueOf(playerNameData), StandardCharsets.UTF_8);
            changePlayerNameInFile.write(newPlayerName.toJSONString());
            changePlayerNameInFile.close();
        }
        addToOldGuild(p);
    }

    @EventHandler
    public void cachePlayersOnJoin(PlayerJoinEvent e) throws IOException, ParseException {
        Player p = e.getPlayer();
        if (!GuildFiles.checkForPersonalPlayerGuildFolder(p)){
            GuildFiles.createPersonalPlayerGuildFolder(p);
        }
        if (!GuildFiles.checkForPlayerDataFile(p)){
            GuildFiles.createPlayerDataFile(p);
        }
        HashMap<String,Object> tempPlayerData = new HashMap<>();
        FileReader getPlayerName = new FileReader(GuildFiles.guildPlayerFolder + "/" + p.getUniqueId() + "/data.json",StandardCharsets.UTF_8);
        JSONObject getPlayerDataToJsonObject = (JSONObject) new JSONParser().parse(getPlayerName);
        getPlayerName.close();
        String playerName = (String) getPlayerDataToJsonObject.get("name");
        String playerGuild = (String) getPlayerDataToJsonObject.get("guild");
        tempPlayerData.put("name",  playerName);
        tempPlayerData.put("guild", playerGuild);
        if (Utils.isPlayerOnline(p.getUniqueId())){
            tempPlayerData.put("online",true);
        }else {
            tempPlayerData.put("online",false);
        }
        Main.cachedPlayers.put(p.getUniqueId(), tempPlayerData);
        Bukkit.getLogger().warning(Main.cachedPlayers.toString());
    }

    @EventHandler
    public void managePlayersOnJoin(PlayerJoinEvent e) throws IOException, ParseException {
        Player p = e.getPlayer();
        Path guildPlayerFolder = Paths.get(Main.guildRootFolder + "/playerData");
        Path personalPlayerGuildFolder = Paths.get(guildPlayerFolder + "/" + p.getUniqueId());
        Path playerNameFile = Paths.get(personalPlayerGuildFolder + "/data.json");

        JSONParser getPlayerNameParser = new JSONParser();
        FileReader fileReader = new FileReader(playerNameFile.toFile());
        JSONObject getPlayerName= (JSONObject) getPlayerNameParser.parse(fileReader);
        fileReader.close();
        String oldName = (String) getPlayerName.get("name");
        if (!oldName.equals(p.getName())){
            JSONObject newPlayerName= new JSONObject();
            newPlayerName.put("name", p.getName());
            FileWriter changePlayerNameInFile = new FileWriter(String.valueOf(playerNameFile), StandardCharsets.UTF_8);
            changePlayerNameInFile.write(newPlayerName.toJSONString());
            changePlayerNameInFile.close();
        }
        addToOldGuild(p);

    }

    public static void addToOldGuild(Player p) throws IOException, ParseException {
        Path guildPlayerFolder = Paths.get(Main.guildRootFolder + "/playerData");
        Path personalPlayerGuildFolder = Paths.get(guildPlayerFolder + "/" + p.getUniqueId());
        Path personalPlayerData = Paths.get(personalPlayerGuildFolder + "/data.json");
        JSONParser jsonParser = new JSONParser();
        FileReader fileReader = new FileReader(personalPlayerData.toFile(),StandardCharsets.UTF_8);
        JSONObject getPlayerGuild = (JSONObject) jsonParser.parse(fileReader);
        fileReader.close();
        String guild = (String) getPlayerGuild.get("guild");
        if (!guild.equals("")){
            Bukkit.getLogger().warning(guild);
            HashMap<String,Object> indexedGuild = Main.cachedGuilds.get(guild);
            Team guildTeam;
            try {
                guildTeam = Main.scoreboard.registerNewTeam(guild);
            }catch (IllegalArgumentException e){
                guildTeam = Main.scoreboard.getTeam(guild);
            }
            ChatColor tagColor =  Utils.translateFromStringToChatColor((String) indexedGuild.get("tagColor"));
            String tag = (String) indexedGuild.get("tag");
            ChatColor color = Utils.translateFromStringToChatColor((String) indexedGuild.get("color"));
            guildTeam.setSuffix(ChatColor.GRAY + "[" + tagColor+ tag + ChatColor.GRAY + "]");
            guildTeam.setDisplayName(guild);
            guildTeam.setColor(color);
            guildTeam.addEntry(p.getName());
            p.setDisplayName(color + p.getName() + ChatColor.GRAY + "[" + tagColor + tag + ChatColor.GRAY + "]" + ChatColor.WHITE);
            for (Player player:Bukkit.getOnlinePlayers()){
                player.setScoreboard(Main.scoreboard);
            }
        }else {
            p.setScoreboard(Main.scoreboard);
        }

    }

    public static void cachePlayers() throws IOException, ParseException {
        cachePlayerThatAreAlreadyOnlIne();
        if (GuildFiles.guildPlayerFolder.toFile().listFiles().length>0) {
            for (File f : GuildFiles.guildPlayerFolder.toFile().listFiles()) {
                if (f.isDirectory()){
                    if (Utils.isStringUUID(f.getName())){
                        if (Utils.containsPlayerDataFolderNeededFiles(f)){
                            HashMap<String,Object> tempPalyerData = new HashMap<>();
                            FileReader getPlayerName = new FileReader(GuildFiles.guildPlayerFolder + "/" + f.getName() + "/name.json",StandardCharsets.UTF_8);
                            JSONObject getPlayerNameToJsonObject = (JSONObject) new JSONParser().parse(getPlayerName);
                            getPlayerName.close();
                            FileReader getPlayerGuild = new FileReader(GuildFiles.guildPlayerFolder + "/" + f.getName() + "/guild.json",StandardCharsets.UTF_8);
                            JSONObject getPlayerGuildToObject = (JSONObject) new JSONParser().parse(getPlayerGuild);
                            getPlayerGuild.close();
                            String playerName = (String) getPlayerNameToJsonObject.get("name");
                            String playerGuild = (String) getPlayerGuildToObject.get("guild");
                            tempPalyerData.put("name",  playerName);
                            tempPalyerData.put("guild", playerGuild);
                            if (Utils.isPlayerOnline(UUID.fromString(f.getName()))){
                                tempPalyerData.put("online",true);
                            }else {
                                tempPalyerData.put("online",false);
                            }
                            Main.cachedPlayers.put(UUID.fromString(f.getName()), tempPalyerData);
                            Bukkit.getLogger().warning(Main.cachedPlayers.toString());

                        }
                    }
                }
            }
        }
    }



    public static void cachePlayerThatAreAlreadyOnlIne() throws IOException, ParseException {
        for (Player p: Bukkit.getServer().getOnlinePlayers()){
            if (!GuildFiles.checkForPersonalPlayerGuildFolder(p)){
                GuildFiles.createPersonalPlayerGuildFolder(p);
            }
            if (!GuildFiles.checkForPlayerDataFile(p)){
                GuildFiles.createPlayerDataFile(p);
            }
            HashMap<String,Object> tempPlayerData = new HashMap<>();
            FileReader getPlayerName = new FileReader(GuildFiles.guildPlayerFolder + "/" + p.getUniqueId() + "/data.json",StandardCharsets.UTF_8);
            JSONObject getPlayerDataToJsonObject = (JSONObject) new JSONParser().parse(getPlayerName);
            getPlayerName.close();
            String playerName = (String) getPlayerDataToJsonObject.get("name");
            String playerGuild = (String) getPlayerDataToJsonObject.get("guild");
            tempPlayerData.put("name",  playerName);
            tempPlayerData.put("guild", playerGuild);
            if (Utils.isPlayerOnline(p.getUniqueId())){
                tempPlayerData.put("online",true);
            }else {
                tempPlayerData.put("online",false);
            }
            Main.cachedPlayers.put(p.getUniqueId(), tempPlayerData);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e){
        Player p = e.getPlayer();
        UUID uuidFromPlayer = p.getUniqueId();
        HashMap<String,Object> tempCachedPlayerData = new HashMap<>();
        tempCachedPlayerData.put("name", Main.cachedPlayers.get(uuidFromPlayer).get("name"));
        tempCachedPlayerData.put("guild",Main.cachedPlayers.get(uuidFromPlayer).get("guild"));
        tempCachedPlayerData.put("online", false);
        Main.cachedPlayers.remove(uuidFromPlayer);
        Main.cachedPlayers.put(uuidFromPlayer, tempCachedPlayerData);
        Bukkit.getLogger().warning(Main.cachedPlayers.toString());
    }
}
