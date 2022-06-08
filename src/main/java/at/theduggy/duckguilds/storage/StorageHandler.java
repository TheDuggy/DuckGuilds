package at.theduggy.duckguilds.storage;

import at.theduggy.duckguilds.Main;
import at.theduggy.duckguilds.config.GuildConfigHandler;
import at.theduggy.duckguilds.objects.GuildObject;
import at.theduggy.duckguilds.objects.GuildPlayerObject;
import at.theduggy.duckguilds.storage.systemTypes.GuildFileSystem;
import at.theduggy.duckguilds.storage.systemTypes.MySql.MySqlSystem;
import at.theduggy.duckguilds.utils.GuildTextUtils;

import java.io.FileNotFoundException;
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

    public void createPersonalPlayerStorageSection(GuildPlayerObject player, boolean inNewThread) {

        if (inNewThread){
            new Thread(() -> {
                if (storageType==StorageType.File){
                    try {
                        GuildFileSystem.createPersonalPlayerFile(player);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }else if (storageType == StorageType.MySQL){
                    try {
                        MySqlSystem.createPersonalPlayerRecord(player);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }

            }).start();
        }else {
            if (storageType==StorageType.File){
                try {
                    GuildFileSystem.createPersonalPlayerFile(player);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }else if (storageType == StorageType.MySQL){
                try {
                    MySqlSystem.createPersonalPlayerRecord(player);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
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


    public void deleteGuildSection(GuildObject guildObject, boolean inNewThread) {
        if (inNewThread) {
            new Thread(() -> {
                try {
                    if (storageType.equals(StorageType.File)) {
                        GuildFileSystem.deleteGuildFile(guildObject);
                    } else if (storageType.equals(StorageType.MySQL)) {
                        MySqlSystem.deleteGuildRecord(guildObject);
                    }
                } catch (Exception e) {
                    Main.log("Failed to delete guild-storage-section for guild " + guildObject.getName() + " with storage-type " + storageType + "! Caused by: " + e.getClass().getSimpleName() + " (" + e.getMessage() + ")", Main.LogLevel.WARNING);
                }
            }).start();
        }else {
            try {
                if (storageType.equals(StorageType.File)) {
                    GuildFileSystem.deleteGuildFile(guildObject);
                } else if (storageType.equals(StorageType.MySQL)) {
                    MySqlSystem.deleteGuildRecord(guildObject);
                }
            } catch (Exception e) {
                Main.log("Failed to delete guild-storage-section for guild " + guildObject.getName() + " with storage-type " + storageType + "! Caused by: " + e.getClass().getSimpleName() + " (" + e.getMessage() + ")", Main.LogLevel.WARNING);
            }
        }
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

    public void deleteRootStorageSection(){
        try {
            if (storageType==StorageType.File){
                GuildFileSystem.deleteRootFolders();
            }else if (storageType==StorageType.MySQL){
                MySqlSystem.deleteGuildTables();
            }
        }catch (Exception e){
            Main.log("Failed to delete guild-rootstroage-sections for storage-type " + storageType + "! Caused by: " + e.getClass().getSimpleName() + " (" + e.getMessage() + ")", Main.LogLevel.WARNING);
        }
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

    public void loadStorageWithoutCaching(StorageType storageType) throws SQLException, FileNotFoundException {
        if (storageType==StorageType.File){
            GuildFileSystem.initWithoutCaching();
        }else if (storageType==StorageType.MySQL){
            MySqlSystem.initWithoutCache();
        }
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

    public void deletePlayerStorageSection(GuildPlayerObject playerObject){
        try {
            if (storageType==StorageType.File){
                GuildFileSystem.deletePersonalPlayerFile(playerObject);
            }else if (storageType.equals(StorageType.MySQL)){
                MySqlSystem.deletePersonalPlayerRecord(playerObject);
            }
        }catch (Exception e){
            Main.log("Failed to delete " + playerObject.getUniqueId() + "(" + playerObject.getName() + ")'s storage section with storage-type " + storageType + "! Caused by: " + e.getClass().getSimpleName() + " (" + e.getMessage() + ")", Main.LogLevel.WARNING);
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

    public void migrateStorage(StorageType newStorageType) throws SQLException, IOException {
        new Thread(() -> {
            StorageType oldStorageType = this.storageType;
            System.out.println(Main.getPlayerCache().size());
            try {
            this.storageType = newStorageType;
            try {
                loadStorageWithoutCaching(newStorageType);
            } catch (SQLException | FileNotFoundException e) {
                throw new RuntimeException(e);
            }
            Main.setStorageBusy(true);
            Main.log("#".repeat(69), Main.LogLevel.WARNING);
            Main.log("The plugin is not usable until the migration is complete from now on!", Main.LogLevel.WARNING);
            Main.log("#".repeat(69), Main.LogLevel.WARNING);
            Main.log("", Main.LogLevel.WARNING);
            Main.log("------------recreate guild-sections------------", Main.LogLevel.DEFAULT);
            for (GuildObject guild : Main.getGuildCache().values()){
                createGuildStorageSection(guild);
                Main.log("Recreated storage-section for guild " + guild.getName() + " in storage-type " + newStorageType + "!", Main.LogLevel.DEFAULT);

            }
            Main.log("------------recreate player-sections------------", Main.LogLevel.DEFAULT);
            for (GuildPlayerObject guildPlayer : Main.getPlayerCache().values()){
                createPersonalPlayerStorageSection(guildPlayer,false);
                Main.log("Recreated " + guildPlayer.getUniqueId() + "(" + guildPlayer.getName() + ")'s storage-section in storage-type " + newStorageType + "!", Main.LogLevel.DEFAULT);
            }

            if (GuildConfigHandler.deleteOldStorageSectionsWhileMigration()) {
                this.storageType = oldStorageType;
                Main.log("-----------delete guilds-sections-----------", Main.LogLevel.DEFAULT);
                for (GuildObject guild : Main.getGuildCache().values()) {
                    deleteGuildSection(guild, false);
                    Main.log("Deleted storage-section for guild " + guild.getName() + " from storage-type " + oldStorageType + "!", Main.LogLevel.DEFAULT);
                }
                Main.log("------------delete player-sections------------", Main.LogLevel.DEFAULT);
                for (GuildPlayerObject guildPlayer : Main.getPlayerCache().values()){
                    deletePlayerStorageSection(guildPlayer);
                    Main.log("Deleted " + guildPlayer.getUniqueId() + "(" + guildPlayer.getName() + ")'s storage-section from storage-type " + oldStorageType + "!", Main.LogLevel.DEFAULT);
                }
                Main.log("------------delete root-sections------------", Main.LogLevel.DEFAULT);
                deleteRootStorageSection();
                this.storageType=newStorageType;
            }
            Main.mainFileConfiguration.set("storageType", newStorageType.toString());
            Main.plugin.saveConfig();
            Main.plugin.reloadConfig();
            Main.log("", Main.LogLevel.WARNING);
            Main.setStorageBusy(false);
            Main.log("#".repeat(77), Main.LogLevel.WARNING);
            Main.log("Migration complete! The plugin is now usable with the new storage-type " + newStorageType + "!", Main.LogLevel.WARNING);
            Main.log("#".repeat(77), Main.LogLevel.WARNING);
            System.out.println(Main.getPlayerCache().size());
        }catch (Exception e){
            this.storageType=oldStorageType;
                try {
                    loadStorage();
                } catch (SQLException | IOException ex) {
                    throw new RuntimeException(ex);
                }
                Main.mainFileConfiguration.set("storageType", oldStorageType.name());
            Main.plugin.saveConfig();
            Main.plugin.reloadConfig();
            Main.log("Failed to migrate from " + oldStorageType + " to " + newStorageType + "! Using old storage-type " + oldStorageType + "! Caused by: " + e.getClass().getSimpleName() + " (" +e.getMessage() + ")", Main.LogLevel.WARNING);
            //TODO Fix why plugin is recreating guilds in old file-system!
        }

        }).start();
    }

    public enum StorageType{
        File,MySQL
    }
}