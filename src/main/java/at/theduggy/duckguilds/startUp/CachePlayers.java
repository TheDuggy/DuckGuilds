package at.theduggy.duckguilds.startUp;

import at.theduggy.duckguilds.Main;
import at.theduggy.duckguilds.config.GuildsConfig;
import at.theduggy.duckguilds.files.GuildFiles;
import at.theduggy.duckguilds.leaveGuild.PlayerLeaveGuild;
import at.theduggy.duckguilds.other.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.UUID;

public class CachePlayers implements Listener {
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
    @EventHandler
    public void onJoin(PlayerJoinEvent e) throws IOException, ParseException {
        Player p = e.getPlayer();
        if (!GuildFiles.checkForPersonalPlayerGuildFolder(p)){
            GuildFiles.createPersonalPlayerGuildFolder(p);
        }
        if (!GuildFiles.checkForPersonalPlayerGuildTeamsFile(p)){
            GuildFiles.createPersonalPlayerGuildTeamsFile(p);
        }
        if (!GuildFiles.checkForPlayerNameFile(p)){
            GuildFiles.createPlayerNameFile(p);
        }
        File playerDataRootFile = new File(GuildFiles.guildPlayerFolder + "/" + p.getUniqueId());
        if (Utils.containsPlayerDataFolderNeededFiles(playerDataRootFile)){
            HashMap<String,Object> tempPalyerData = new HashMap<>();
            FileReader getPlayerName = new FileReader(GuildFiles.guildPlayerFolder + "/" + p.getUniqueId() + "/name.json",StandardCharsets.UTF_8);
            JSONObject getPlayerNameToJsonObject = (JSONObject) new JSONParser().parse(getPlayerName);
            getPlayerName.close();
            FileReader getPlayerGuild = new FileReader(GuildFiles.guildPlayerFolder + "/" + p.getUniqueId() + "/guild.json", StandardCharsets.UTF_8);
            JSONObject getPlayerGuildToObject = (JSONObject) new JSONParser().parse(getPlayerGuild);
            getPlayerGuild.close();
            String playerName = (String) getPlayerNameToJsonObject.get("name");
            String playerGuild = (String) getPlayerGuildToObject.get("guild");
            tempPalyerData.put("name",  playerName);
            tempPalyerData.put("guild", Utils.convertStringToUTF8(playerGuild));
            if (Utils.isPlayerOnline(p.getUniqueId())){
                tempPalyerData.put("online",true);
            }else {
                tempPalyerData.put("online",false);
            }
            Main.cachedPlayers.put(p.getUniqueId(), tempPalyerData);
            Bukkit.getLogger().warning(Main.cachedPlayers.toString());
        }
    }


    public static void cachePlayerThatAreAlreadyOnlIne() throws IOException, ParseException {
        for (Player p: Bukkit.getServer().getOnlinePlayers()){
            if (!GuildFiles.checkForPersonalPlayerGuildFolder(p)){
                GuildFiles.createPersonalPlayerGuildFolder(p);
            }
            if (!GuildFiles.checkForPersonalPlayerGuildTeamsFile(p)){
                GuildFiles.createPersonalPlayerGuildTeamsFile(p);
            }
            if (!GuildFiles.checkForPlayerNameFile(p)){
                GuildFiles.createPlayerNameFile(p);
            }
            File playerDataRootFile = new File(GuildFiles.guildPlayerFolder + "/" + p.getUniqueId());
            if (Utils.containsPlayerDataFolderNeededFiles(playerDataRootFile)){
                HashMap<String,Object> tempPalyerData = new HashMap<>();
                FileReader getPlayerName = new FileReader(GuildFiles.guildPlayerFolder + "/" + p.getUniqueId() + "/name.json",StandardCharsets.UTF_8);
                JSONObject getPlayerNameToJsonObject = (JSONObject) new JSONParser().parse(getPlayerName);
                getPlayerName.close();
                FileReader getPlayerGuild = new FileReader(GuildFiles.guildPlayerFolder + "/" + p.getUniqueId() + "/guild.json", StandardCharsets.UTF_8);
                JSONObject getPlayerGuildToObject = (JSONObject) new JSONParser().parse(getPlayerGuild);
                getPlayerGuild.close();
                String playerName = (String) getPlayerNameToJsonObject.get("name");
                String playerGuild = (String) getPlayerGuildToObject.get("guild");
                tempPalyerData.put("name",  playerName);
                tempPalyerData.put("guild", Utils.convertStringToUTF8(playerGuild));
                if (Utils.isPlayerOnline(p.getUniqueId())){
                    tempPalyerData.put("online",true);
                }else {
                    tempPalyerData.put("online",false);
                }
                Main.cachedPlayers.put(p.getUniqueId(), tempPalyerData);
                Bukkit.getLogger().warning(Main.cachedPlayers.toString());
            }
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
