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

    public StorageType storageType;

    public Storage(StorageType storageType){
        this.storageType=storageType;
    }

    public boolean personalGuildPlayerStorageSectionExists(UUID player){
        if (GuildConfig.getStorageType().equals(StorageType.File)){
            return GuildFileSystem.personalGuildPlayerFileExists(player);
        }else{
            return false;
        }
    }

    public void createPersonalPlayerStorageSection(Player player) throws IOException {
        if (GuildConfig.getStorageType().equals(StorageType.File)){
            GuildFileSystem.createPersonalPlayerFile(player);
        }
    }

    public void createGuildStorageSection(GuildObject guildData, String name) throws IOException {
        if (GuildConfig.getStorageType().equals(StorageType.File)){
            GuildFileSystem.createGuildFile(guildData, name);
        }
    }


    public void deleteGuildField(String guildName) throws IOException {
        if (GuildConfig.getStorageType().equals(StorageType.File)){
            GuildFileSystem.deleteGuildFile(guildName);
        }
    }

    public void removePlayerFromGuildField(UUID player,String guildName) throws IOException, ParseException {
        if (GuildConfig.getStorageType().equals(StorageType.File)){
            GuildFileSystem.removePlayerFromGuildFile(player, guildName);
        }
    }

    public String getPlayerDataFromStorage(UUID player) throws IOException, ParseException {
        if (GuildConfig.getStorageType().equals(StorageType.File)){
            return GuildFileSystem.getPlayerDataFromFile(player);
        }
        return null;
    }

    public void updatePlayerData(UUID player, GuildPlayerObject guildPlayerObject, String name) throws IOException {
        if (GuildConfig.getStorageType().equals(StorageType.File)){
            GuildFileSystem.updatePlayerData(player, guildPlayerObject, name);
        }
    }


    public void loadStorage() throws IOException {
        if (GuildConfig.getStorageType().equals(StorageType.File)){
            GuildFileSystem.initFolders();
            GuildFileSystem.cacheGuildFiles();
            GuildFileSystem.cachePlayers();
        }
    }

    public enum StorageType{
        File,MySQL

    }
}