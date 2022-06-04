package at.theduggy.duckguilds.storage;

import at.theduggy.duckguilds.Main;
import at.theduggy.duckguilds.config.GuildConfigHandler;
import at.theduggy.duckguilds.objects.GuildObject;
import at.theduggy.duckguilds.objects.GuildPlayerObject;
import at.theduggy.duckguilds.storage.systemTypes.GuildFileSystem;
import at.theduggy.duckguilds.storage.systemTypes.MySql.MySqlSystem;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.sql.SQLException;
import java.util.UUID;

public class StorageHandler {

    public StorageType storageType;

    public StorageHandler(StorageType storageType){
        this.storageType=storageType;
    }


    public boolean personalGuildPlayerStorageSectionExists(UUID player) {
        try {
            if (storageType.equals(StorageType.File)){
                return GuildFileSystem.personalGuildPlayerFileExists(player);
            }else if(storageType.equals(StorageType.MySQL)){
                return MySqlSystem.personalPlayerTableExists(player);
            }
        }catch (Exception e){
            Main.log("Failed to check if player-section exists for player " + player + " with storage-type " + storageType + "! Caused by: " + e.getClass().getSimpleName() + " (" + e.getMessage() + ")", Main.LogLevel.WARNING);
        }
        return false;
    }

    public void createPersonalPlayerStorageSection(Player player) {
        new Thread(() -> {
            try {
                if (storageType.equals(StorageType.File)){
                    GuildFileSystem.createPersonalPlayerFile(player);
                }else if (storageType == StorageType.MySQL){
                    MySqlSystem.createPersonalPlayerTable(player);
                }
            }catch (Exception e){
                Main.log("Failed to create player-storage-section for player " + player.getUniqueId() + " (" + player.getName() + ") with system-type " + storageType + "! Caused by: " + e.getClass().getSimpleName() + " (" + e.getMessage() + ")", Main.LogLevel.WARNING);
            }
        }).start();
    }

    public void createGuildStorageSection(GuildObject guildData) {
        try {
            if (storageType.equals(StorageType.File)){
                GuildFileSystem.createGuildFile(guildData);
            }if (storageType.equals(StorageType.MySQL)){
                MySqlSystem.createGuildRecord(guildData);
            }
        }catch (Exception e){
            Main.log("Failed to create guild-storage-section for guild " + guildData.getName() + " with storage-type " + storageType + "! Caused by: " + e.getClass().getSimpleName() + " (" + e.getMessage() + ")", Main.LogLevel.WARNING);
        }
    }


    public void deleteGuildSection(GuildObject guildObject) {
        new Thread(() -> {
            try {
                if (storageType.equals(StorageType.File)){
                    GuildFileSystem.deleteGuildFile(guildObject);
                }else if (storageType.equals(StorageType.MySQL)){
                    MySqlSystem.deleteGuildRecord(guildObject);
                }
            }catch (Exception e){
                Main.log("Failed to delete guild-storage-section for guild " + guildObject.getName() + " with storage-type " + storageType + "! Caused by: " + e.getClass().getSimpleName() + " (" + e.getMessage() + ")", Main.LogLevel.WARNING);
            }
        }).start();
    }

    public void removePlayerFromGuildSection(GuildPlayerObject player, GuildObject guild) {
        new Thread(() -> {
            try {
                if (storageType.equals(StorageType.File)){
                    GuildFileSystem.removePlayerFromGuildFile(player, guild);
                }else if (storageType.equals(StorageType.MySQL)){
                    MySqlSystem.removePlayerFromGuildRecord(player,guild);
                }
            }catch (Exception e){
                Main.log("Failed to remove player " + player.getUniqueId() + " (" + player.getName() + ") from guild " + guild.getName() + " with storage-type " + storageType + "! Caused by: " + e.getClass().getSimpleName() + " (" + e.getMessage() + ")", Main.LogLevel.WARNING);
            }
        }).start();
    }

    public String getPlayerNameFromPlayerSection(GuildPlayerObject player) {
        try {
            if (storageType.equals(StorageType.File)){
                return GuildFileSystem.getPlayerNameFromPlayerFile(player);
            }else if (storageType.equals(StorageType.MySQL)){
                return MySqlSystem.getPlayerNameFromPlayerRecord(player);
            }
        }catch (Exception e){
            Main.log("Failed to get name from player-name from player-storage-section for player " + player.getUniqueId() + " with storage-type " + storageType + "! Caused by: " + e.getClass().getSimpleName() + " (" + e.getMessage() + ")", Main.LogLevel.WARNING);
        }
        return null;
    }

    public void updatePlayerSection(GuildPlayerObject guildPlayerObject) {
        new Thread(() -> {
            try {
                if (storageType.equals(StorageType.File)){
                    GuildFileSystem.updatePlayerFile(guildPlayerObject);
                }else if (storageType.equals(StorageType.MySQL)){
                    MySqlSystem.updatePlayerRecord(guildPlayerObject);
                }
            }catch (Exception e){
                Main.log("Failed to update player-storage-section for player " + guildPlayerObject.getUniqueId() + " with storage-type " + storageType + "! Caused by: " + e.getClass().getSimpleName() + " ("  + e.getMessage() + ")", Main.LogLevel.WARNING);
            }
        }).start();
    }


    public void loadStorage() throws SQLException, IOException {

        if (storageType.equals(StorageType.File)){
            GuildFileSystem.init();
        }else if (storageType.equals(StorageType.MySQL)){
            if (MySqlSystem.connectionAvailable()){
                MySqlSystem.init();
            }else if (GuildConfigHandler.useFileSystemOnInvalidConnection()){
                GuildFileSystem.init();
            }else {
                Main.shutDown("Failed to connect to " + GuildConfigHandler.getDataBase().getJdbcUrl() + "!");
            }
        }
    }

    public void addPlayerToGuildField(GuildObject guild, GuildPlayerObject player) {
        new Thread(() -> {
            try {
                if (storageType.equals(StorageType.File)){
                    GuildFileSystem.addPlayerToGuildFile(guild,player);
                }else if (storageType.equals(StorageType.MySQL)){
                    MySqlSystem.addPlayerToGuildRecord(guild,player);
                }
            }catch (Exception e){
                Main.log("Failed to add player " + player.getUniqueId() + " (" + player.getName() + ") to guild-storage-section for guild " + guild.getName() + " with storage-tye " + storageType +  "! Caused by: " + e.getClass().getSimpleName() + " (" + e.getMessage() + ")", Main.LogLevel.WARNING );
            }
        }).start();

    }

    public enum StorageType{
        File,MySQL

    }
}