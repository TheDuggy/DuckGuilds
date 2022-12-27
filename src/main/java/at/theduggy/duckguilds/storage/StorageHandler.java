package at.theduggy.duckguilds.storage;

import at.theduggy.duckguilds.Main;
import at.theduggy.duckguilds.config.GuildConfigHandler;
import at.theduggy.duckguilds.objects.GuildExportObject;
import at.theduggy.duckguilds.objects.GuildObject;
import at.theduggy.duckguilds.objects.GuildPlayerObject;
import at.theduggy.duckguilds.storage.systemTypes.GuildFileSystem;
import at.theduggy.duckguilds.storage.systemTypes.MySqlSystem;
import at.theduggy.duckguilds.storage.systemTypes.StorageType;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class StorageHandler {

    private StorageType storageType;
    private final ThreadPoolExecutor threadPool = (ThreadPoolExecutor) Executors.newCachedThreadPool();


    public StorageHandler(String storageSystemID){
        switch (storageSystemID){
            case "File":
                this.storageType = new GuildFileSystem();
                break;

            case "MySQL":
                this.storageType = new MySqlSystem();
                break;
        }
    }

    public String getStorageSystemID() {
        return storageType.getStorageSystemID();
    }

    public StorageType getStorageType() {
        return storageType;
    }

    public boolean personalGuildPlayerSectionExists(UUID player) {
        try {
            return storageType.personalPlayerSectionExists(player);
        }catch (Exception e){
            Main.log("Failed to check if player-section exists for player " + player + " with storage-type " + storageType.getStorageSystemID() + "! Caused by: " + e.getClass().getSimpleName() + " (" + e.getMessage() + ")", Main.LogLevel.WARNING);
        }
        return false;
    }

    public void createPersonalPlayerSection(GuildPlayerObject player, boolean inNewThread) {
        if (inNewThread){
            threadPool.submit(() -> {
                try {
                    storageType.createPersonalPlayerSection(player);
                } catch (Exception e) {
                    Main.log("Failed to create Player section " + player.getUniqueId().toString() + " (" + player.getName() + ")! Caused by: " + e.getClass().getSimpleName() + " (" + e.getMessage() + ")", Main.LogLevel.WARNING);
                }
            });
        }else {
            try {
                storageType.createPersonalPlayerSection(player);
            } catch (Exception e) {
                Main.log("Failed to create Player section " + player.getUniqueId().toString() + " (" + player.getName() + ")! Caused by: " + e.getClass().getSimpleName() + " (" + e.getMessage() + ")", Main.LogLevel.WARNING);
            }
        }
    }


    public void createGuildSection(GuildObject guildData) {
        try {
            storageType.createGuildSection(guildData);
        }catch (Exception e){
            Main.log("Failed to create guild-storage-section for guild " + guildData.getName() + " with storage-type " + storageType.getStorageSystemID() + "! Caused by: " + e.getClass().getSimpleName() + " (" + e.getMessage() + ")", Main.LogLevel.WARNING);
        }
    }


    public void deleteGuildSection(GuildObject guildObject, boolean inNewThread) {
        if (inNewThread) {
            threadPool.submit(() -> {
                try {
                    storageType.deleteGuildSection(guildObject);
                } catch (Exception e) {
                    Main.log("Failed to delete guild-storage-section for guild " + guildObject.getName() + " with storage-type " + storageType.getStorageSystemID() + "! Caused by: " + e.getClass().getSimpleName() + " (" + e.getMessage() + ")", Main.LogLevel.WARNING);
                }
            });
        }else {
            try {
                storageType.deleteGuildSection(guildObject);
            } catch (Exception e) {
                Main.log("Failed to delete guild-storage-section for guild " + guildObject.getName() + " with storage-type " + storageType.getStorageSystemID() + "! Caused by: " + e.getClass().getSimpleName() + " (" + e.getMessage() + ")", Main.LogLevel.WARNING);
            }
        }
    }

    public void removePlayerFromGuildSection(GuildPlayerObject player, GuildObject guild) {
        threadPool.submit(() -> {
            try {
                storageType.removePlayerFromGuildSection(player, guild);
            }catch (Exception e){
                Main.log("Failed to remove player " + player.getUniqueId() + " (" + player.getName() + ") from guild " + guild.getName() + " with storage-type " + storageType.getStorageSystemID() + "! Caused by: " + e.getClass().getSimpleName() + " (" + e.getMessage() + ")", Main.LogLevel.WARNING);
            }
        });
    }

    public void deleteRootStorageSection(){
        try {
            storageType.deleteRootSection();
        }catch (Exception e){
            Main.log("Failed to delete guild-root-storage-sections for storage-type "  + storageType.getStorageSystemID() +"! Caused by: " + e.getClass().getSimpleName() + " (" + e.getMessage() + ")", Main.LogLevel.WARNING);
        }
    }

    public String getPlayerNameFromPlayerSection(GuildPlayerObject player) {
        try {
            return storageType.getPlayerNameFromPlayerSection(player);
        }catch (Exception e){
            Main.log("Failed to get name from player-name from player-storage-section for player " + player.getUniqueId() + " with storage-type " + storageType.getStorageSystemID() + "! Caused by: " + e.getClass().getSimpleName() + " (" + e.getMessage() + ")", Main.LogLevel.WARNING);
        }
        return null;
    }

    public void updatePlayerSection(GuildPlayerObject guildPlayerObject) {
        threadPool.submit(() -> {
            try {
                storageType.updatePlayerSection(guildPlayerObject);
            }catch (Exception e){
                Main.log("Failed to update player-storage-section for player " + guildPlayerObject.getUniqueId() + " with storage-type " + storageType.getStorageSystemID() + "! Caused by: " + e.getClass().getSimpleName() + " ("  + e.getMessage() + ")", Main.LogLevel.WARNING);
            }
        });
    }

    public void loadStorageWithoutCaching() throws SQLException, FileNotFoundException {
        storageType.loadWithoutCaching();
    }

    public void loadStorage() throws SQLException, IOException {
        if (storageType instanceof MySqlSystem){
            if (MySqlSystem.connectionAvailable()){
                storageType.load();
            }else if (GuildConfigHandler.useFileSystemOnInvalidConnection()){
                this.storageType = new GuildFileSystem();
                storageType.load();
            }else {
                Main.shutDown("Failed to connect to " + GuildConfigHandler.getDataBase().getJdbcUrl() + "!");
            }
        }else {
            storageType.load();
        }
    }

    public void deletePlayerSection(GuildPlayerObject playerObject){
        try {
            storageType.deletePlayerSection(playerObject);
        }catch (Exception e){
            Main.log("Failed to delete " + playerObject.getUniqueId() + "(" + playerObject.getName() + ")'s storage section with storage-type "  + storageType.getStorageSystemID() +"! Caused by: " + e.getClass().getSimpleName() + " (" + e.getMessage() + ")", Main.LogLevel.WARNING);
        }
    }

    public void addPlayerToGuildSection(GuildObject guild, GuildPlayerObject player) {
        threadPool.submit(() -> {
            try {
                storageType.addPlayerToGuildSection(guild, player);
            }catch (Exception e){
                Main.log("Failed to add player " + player.getUniqueId() + " (" + player.getName() + ") to guild-storage-section for guild " + guild.getName() + " with storage-tye "  + storageType.getStorageSystemID() + "! Caused by: " + e.getClass().getSimpleName() + " (" + e.getMessage() + ")", Main.LogLevel.WARNING );
            }
        });

    }

    public void migrateStorage(StorageType newStorageType) throws SQLException, IOException {
        new Thread(() -> {
            StorageType oldStorageType = this.storageType;
            try {
            this.storageType = newStorageType;
            try {
                loadStorageWithoutCaching();
            } catch (SQLException | FileNotFoundException e) {
                throw new RuntimeException(e);
            }
            Main.setStorageBusy(true);
            Main.log("=".repeat(69), Main.LogLevel.WARNING);
            Main.log("The plugin is not usable until the migration is complete from now on!", Main.LogLevel.WARNING);
            Main.log("", Main.LogLevel.WARNING);
            Main.log("------------recreate guild-sections------------", Main.LogLevel.WARNING);
            for (GuildObject guild : Main.getGuildCache().values()){
                createGuildSection(guild);
                Main.log("Recreated storage-section for guild " + guild.getName() + " in storage-type " + newStorageType + "!", Main.LogLevel.DEFAULT);

            }
            Main.log("------------recreate player-sections------------", Main.LogLevel.WARNING);
            for (GuildPlayerObject guildPlayer : Main.getPlayerCache().values()){
                createPersonalPlayerSection(guildPlayer,false);
                Main.log("Recreated " + guildPlayer.getUniqueId() + "(" + guildPlayer.getName() + ")'s storage-section in storage-type " + newStorageType + "!", Main.LogLevel.DEFAULT);
            }

            if (GuildConfigHandler.deleteOldStorageSectionsWhileMigration()) {
                this.storageType = oldStorageType;
                Main.log("-----------delete guilds-sections-----------", Main.LogLevel.WARNING);
                for (GuildObject guild : Main.getGuildCache().values()) {
                    deleteGuildSection(guild, false);
                    Main.log("Deleted storage-section for guild " + guild.getName() + " from storage-type "  + oldStorageType.getStorageSystemID() + "!", Main.LogLevel.DEFAULT);
                }
                Main.log("------------delete player-sections------------", Main.LogLevel.WARNING);
                for (GuildPlayerObject guildPlayer : Main.getPlayerCache().values()){
                    deletePlayerSection(guildPlayer);
                    Main.log("Deleted " + guildPlayer.getUniqueId() + "(" + guildPlayer.getName() + ")'s storage-section from storage-type "  + oldStorageType.getStorageSystemID() + "!", Main.LogLevel.DEFAULT);
                }
                Main.log("------------delete root-sections------------", Main.LogLevel.WARNING);
                deleteRootStorageSection();
                this.storageType=newStorageType;
            }
            Main.mainFileConfiguration.set("storageType", newStorageType.getStorageSystemID());
            Main.plugin.saveConfig();
            Main.plugin.reloadConfig();
            Main.log("", Main.LogLevel.WARNING);
            Main.setStorageBusy(false);
            Main.log("Migration complete! The plugin is now usable with the new storage-type " + newStorageType + "!", Main.LogLevel.WARNING);
            Main.log("=".repeat(77), Main.LogLevel.WARNING);
        }catch (Exception e){
                e.printStackTrace();
            this.storageType=oldStorageType;
                try {
                    loadStorage();
                } catch (SQLException | IOException ex) {
                    throw new RuntimeException(ex);
                }
                Main.mainFileConfiguration.set("storageType", oldStorageType.getStorageSystemID());
            Main.plugin.saveConfig();
            Main.plugin.reloadConfig();
            Main.setStorageBusy(false);
            Main.log("Failed to migrate from "  + oldStorageType.getStorageSystemID() + " to " + newStorageType + "! Using old storage-type "  + oldStorageType.getStorageSystemID() + "! Caused by: " + e.getClass().getSimpleName() + " (" +e.getMessage() + ")", Main.LogLevel.WARNING);
        }

        }).start();
    }

    public static void exportStorage() {
        try {
            File exportFolder = new File(Main.plugin.getDataFolder() + "/export/");
            if (!Files.exists(exportFolder.toPath())){
                Files.createDirectory(exportFolder.toPath());
            }

            HashMap<String, Integer> files = new HashMap<>();

            for (File f : exportFolder.listFiles()){
                int number = 0;
                String filename = f.getName().split("\\.")[1].split("_")[1];
                System.out.println(filename);
                String fileNameBase = f.getName().split("\\.")[0] + "." + f.getName().split("\\.")[1];
                if (fileNameBase.split("_").length > 4){
                    number = Integer.parseInt(fileNameBase.split("_")[4]);
                    filename = fileNameBase.split("_")[3];
                }
                if (files.containsKey(filename)){
                    if (number > files.get(filename)){
                        files.replace(filename, number);
                    }
                }else {
                    files.put(filename, number);
                }
            }

            String dateString = DateTimeFormatter.ofPattern("yyyy-MM-dd").format(LocalDate.now());
            String dataFileName = "guild_data_v" + Main.getPlugin(Main.class).getDescription().getVersion() + "_" + dateString;
            if (files.containsKey(dateString)){
                dataFileName +=  "_" + (files.get(dateString) + 1);
                System.out.println(dataFileName);
            }

            File compressedFile = new File(exportFolder + "/" + dataFileName + ".zip");
            ZipOutputStream compressedData = new ZipOutputStream(new FileOutputStream(compressedFile));
            GuildExportObject guildExportObject = new GuildExportObject();
            for (GuildObject guild:Main.getGuildCache().values()){
                guildExportObject.addGuild(guild);
            }
            for (GuildPlayerObject guildPlayer:Main.getPlayerCache().values()){
                guildExportObject.addGuildPlayer(guildPlayer);
            }
            String dataToExport = Main.getGsonInstance().toJson(guildExportObject);
            compressedData.putNextEntry(new ZipEntry(dataFileName + ".data"));
            compressedData.write(dataToExport.getBytes(StandardCharsets.UTF_8));
            compressedData.closeEntry();
            compressedData.close();
            Main.log("Successfully exported " + Main.getPlayerCache().size() + " Player" + (Main.getPlayerCache().size() > 1?"s":"") +" and " + Main.getGuildCache().size() + " Guild" + (Main.getGuildCache().size() > 1?"s":"") + " in file " + compressedFile.getAbsolutePath() + "!", Main.LogLevel.DEFAULT);
        }catch (Exception e){
            e.printStackTrace();
            Main.log("Failed to export storage! Caused by: " + e.getClass().getSimpleName() + "(" + e.getMessage() + ")", Main.LogLevel.WARNING);
        }
    }


    public static void importStorage(File path){
        try {
            if (new ZipFile(path).size()>1){
                Main.log("The zip-file " + path + " has more than one entry in it. Failed to import guilds!", Main.LogLevel.WARNING);
                return;
            }

            ZipInputStream zis = new ZipInputStream(new FileInputStream(path));
            ByteArrayOutputStream bOut = new ByteArrayOutputStream();
            zis.getNextEntry();
            byte[] buffer = new byte[1024];
            for (int len; (len = zis.read(buffer)) != -1;){
                System.out.println(len);
                bOut.write(buffer, 0, len);
            }
            zis.closeEntry();
            bOut.close();
            zis.close();
            String data = bOut.toString(StandardCharsets.UTF_8);
            data = data.replaceAll("\n", "");
            zis.close();
            GuildExportObject guildExportObject = Main.getGsonInstance().fromJson(data, GuildExportObject.class);
            for ( GuildObject guildObject : guildExportObject.getGuildObjects()){
                if (!Main.getGuildCache().containsKey(guildObject.getName())){
                    Main.getMainStorage().createGuildSection(guildObject);
                }else {
                    Main.log(guildObject.getName() + " already exist, skipped!", Main.LogLevel.WARNING);
                }
            }

            for (GuildPlayerObject guildPlayerObjectData: guildExportObject.getGuildPlayers()){
                if (!Main.getPlayerCache().containsKey(guildPlayerObjectData.getUniqueId())) {
                    Main.getMainStorage().createPersonalPlayerSection(guildPlayerObjectData, true);
                }else {
                    Main.log(guildPlayerObjectData.getUniqueId() + " (" + guildPlayerObjectData.getName() + ") already exist, skipped!", Main.LogLevel.WARNING);
                }
            }

            Main.getMainStorage().loadStorage();
        }catch (Exception e){
            e.printStackTrace();
            Main.log("Failed to import guild-export-file " + path + "! Caused by: " +e.getClass().getSimpleName() + "(" +e.getMessage() + ")", Main.LogLevel.WARNING);
        }
    }
}