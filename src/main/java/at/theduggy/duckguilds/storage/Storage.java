package at.theduggy.duckguilds.storage;

import at.theduggy.duckguilds.Main;
import at.theduggy.duckguilds.config.GuildConfig;
import at.theduggy.duckguilds.files.GuildFiles;
import at.theduggy.duckguilds.other.Utils;
import at.theduggy.duckguilds.storage.systemTypes.GuildFileSystem;
import org.bukkit.ChatColor;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class Storage {


    public static void createGuildField(HashMap<String,Object> guildData,String name) throws IOException {
        if (GuildConfig.getStorageType().equals(StorageType.FILE)){
            GuildFileSystem.createGuildFile(guildData, name);
        }
    }

    public static void cacheGuilds() throws IOException, ParseException {
        if (GuildConfig.getStorageType().equals(StorageType.FILE)){
            GuildFileSystem.cacheGuildsFiles();
        }
    }

    public static void deleteGuildField(String guildName) throws IOException {
        if (GuildConfig.getStorageType().equals(StorageType.FILE)){
            GuildFileSystem.deleteGuildFile(guildName);
        }
    }

    public static void removePlayerFromGuildField(UUID player,String guildName) throws IOException, ParseException {
        if (GuildConfig.getStorageType().equals(StorageType.FILE)){
            GuildFileSystem.removePlayerFromGuildFile(player, guildName);
        }
    }

    public static String getPlayerDataFromStorage(UUID player) throws IOException, ParseException {
        if (GuildConfig.getStorageType().equals(StorageType.FILE)){
            return GuildFileSystem.getPlayerDataFromFile(player);
        }
        return null;
    }

    public static void updatePlayerData(UUID player,HashMap<String,String> newPlayerData) throws IOException {
        if (GuildConfig.getStorageType().equals(StorageType.FILE)){
            GuildFileSystem.updatePlayerData(player, newPlayerData);
        }
    }

    public static void cachePlayers() throws IOException, ParseException {
        if (GuildConfig.getStorageType().equals(StorageType.FILE)){
            GuildFileSystem.cachePlayers();
        }
    }

    public static void cachePlayer(UUID player,String guild) throws IOException, ParseException {
        if (GuildConfig.getStorageType().equals(StorageType.FILE)){
            GuildFileSystem.cachePlayer(player,guild);
        }
    }
}