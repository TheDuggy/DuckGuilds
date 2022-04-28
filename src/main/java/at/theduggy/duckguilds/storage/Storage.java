package at.theduggy.duckguilds.storage;

import at.theduggy.duckguilds.config.GuildConfig;
import at.theduggy.duckguilds.objects.GuildObject;
import at.theduggy.duckguilds.objects.GuildPlayerObject;
import at.theduggy.duckguilds.storage.systemTypes.GuildFileSystem;
import org.bukkit.entity.Player;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.UUID;

public class Storage {

    public static boolean personalGuildPlayerStorageSectionExists(UUID player){
        if (GuildConfig.getStorageType().equals(StorageType.FILE)){
            return GuildFileSystem.personalGuildPlayerFileExists(player);
        }else{
            return false;
        }
    }

    public static void createPersonalPlayerStorageSection(Player player) throws IOException {
        if (GuildConfig.getStorageType().equals(StorageType.FILE)){
            GuildFileSystem.createPersonalPlayerFile(player);
        }
    }

    public static void createGuildStorageSection(GuildObject guildData, String name) throws IOException {
        if (GuildConfig.getStorageType().equals(StorageType.FILE)){
            GuildFileSystem.createGuildFile(guildData, name);
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

    public static void updatePlayerData(UUID player, GuildPlayerObject guildPlayerObject, String name) throws IOException {
        if (GuildConfig.getStorageType().equals(StorageType.FILE)){
            GuildFileSystem.updatePlayerData(player, guildPlayerObject, name);
        }
    }


    public static void loadStorage() throws IOException {
        if (GuildConfig.getStorageType().equals(StorageType.FILE)){
            GuildFileSystem.initFolders();
            GuildFileSystem.cacheGuildsFiles();
            GuildFileSystem.cachePlayers();
        }
    }

    public enum StorageType{
        FILE,MySQL
    }
}