package at.theduggy.duckguilds.storage;

import at.theduggy.duckguilds.Main;
import at.theduggy.duckguilds.logging.GuildLogger;
import at.theduggy.duckguilds.objects.GuildExportObject;
import at.theduggy.duckguilds.objects.GuildObject;
import at.theduggy.duckguilds.objects.GuildPlayerObject;
import at.theduggy.duckguilds.storage.systemTypes.GuildFileSystem;
import at.theduggy.duckguilds.storage.systemTypes.MySqlSystem;
import at.theduggy.duckguilds.storage.systemTypes.StorageType;
import at.theduggy.duckguilds.utils.GuildTextUtils;

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
            GuildLogger.getLogger().error("Failed to check if player-section exists for player " + player + " with storage-type " + storageType.getStorageSystemID() + "! Caused by: " + e.getClass().getSimpleName() + " (" + e.getMessage() + ")");
        }
        return false;
    }

    public void createPersonalPlayerSection(GuildPlayerObject player, boolean inNewThread) {
        if (inNewThread){
            threadPool.submit(() -> {
                try {
                    storageType.createPersonalPlayerSection(player);
                } catch (Exception e) {
                    GuildLogger.getLogger().error("Failed to create Player section " + player.getUniqueId().toString() + " (" + player.getName() + ")! Caused by: " + e.getClass().getSimpleName() + " (" + e.getMessage() + ")");
                }
            });
        }else {
            try {
                storageType.createPersonalPlayerSection(player);
            } catch (Exception e) {
                GuildLogger.getLogger().error("Failed to create Player section " + player.getUniqueId().toString() + " (" + player.getName() + ")! Caused by: " + e.getClass().getSimpleName() + " (" + e.getMessage() + ")");
            }
        }
    }


    public void createGuildSection(GuildObject guildData) {
        try {
            storageType.createGuildSection(guildData);
        }catch (Exception e){
            GuildLogger.getLogger().error("Failed to create guild-storage-section for guild " + guildData.getName() + " with storage-type " + storageType.getStorageSystemID() + "! Caused by: " + e.getClass().getSimpleName() + " (" + e.getMessage() + ")");
        }
    }


    public void deleteGuildSection(GuildObject guildObject, boolean inNewThread) {
        if (inNewThread) {
            threadPool.submit(() -> {
                try {
                    storageType.deleteGuildSection(guildObject);
                } catch (Exception e) {
                    GuildLogger.getLogger().error("Failed to delete guild-storage-section for guild " + guildObject.getName() + " with storage-type " + storageType.getStorageSystemID() + "! Caused by: " + e.getClass().getSimpleName() + " (" + e.getMessage() + ")");
                }
            });
        }else {
            try {
                storageType.deleteGuildSection(guildObject);
            } catch (Exception e) {
                GuildLogger.getLogger().error("Failed to delete guild-storage-section for guild " + guildObject.getName() + " with storage-type " + storageType.getStorageSystemID() + "! Caused by: " + e.getClass().getSimpleName() + " (" + e.getMessage() + ")");
            }
        }
    }

    public void removePlayerFromGuildSection(GuildPlayerObject player, GuildObject guild) {
        threadPool.submit(() -> {
            try {
                storageType.removePlayerFromGuildSection(player, guild);
            }catch (Exception e){
                GuildLogger.getLogger().error("Failed to remove player " + player.getUniqueId() + " (" + player.getName() + ") from guild " + guild.getName() + " with storage-type " + storageType.getStorageSystemID() + "! Caused by: " + e.getClass().getSimpleName() + " (" + e.getMessage() + ")");
            }
        });
    }

    public void deleteRootStorageSection(){
        try {
            storageType.deleteRootSection();
        }catch (Exception e){
            GuildLogger.getLogger().error("Failed to delete guild-root-storage-sections for storage-type "  + storageType.getStorageSystemID() +"! Caused by: " + e.getClass().getSimpleName() + " (" + e.getMessage() + ")");
        }
    }

    public String getPlayerNameFromPlayerSection(GuildPlayerObject player) {
        try {
            return storageType.getPlayerNameFromPlayerSection(player);
        }catch (Exception e){
            GuildLogger.getLogger().error("Failed to get name from player-name from player-storage-section for player " + player.getUniqueId() + " with storage-type " + storageType.getStorageSystemID() + "! Caused by: " + e.getClass().getSimpleName() + " (" + e.getMessage() + ")");
        }
        return null;
    }

    public void updatePlayerSection(GuildPlayerObject guildPlayerObject) {
        threadPool.submit(() -> {
            try {
                storageType.updatePlayerSection(guildPlayerObject);
            }catch (Exception e){
                GuildLogger.getLogger().error("Failed to update player-storage-section for player " + guildPlayerObject.getUniqueId() + " with storage-type " + storageType.getStorageSystemID() + "! Caused by: " + e.getClass().getSimpleName() + " ("  + e.getMessage() + ")");
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
            }else if (Main.getGuildConfigHandler().useFileSystemOnInvalidConnection()){
                this.storageType = new GuildFileSystem();
                storageType.load();
            }else {
                Main.shutDown("Failed to connect to " + Main.getGuildConfigHandler().getDataBase().getJdbcUrl() + "!");
            }
        }else {
            storageType.load();
        }
    }

    public void deletePlayerSection(GuildPlayerObject playerObject){
        try {
            storageType.deletePlayerSection(playerObject);
        }catch (Exception e){
            GuildLogger.getLogger().error("Failed to delete " + playerObject.getUniqueId() + "(" + playerObject.getName() + ")'s storage section with storage-type "  + storageType.getStorageSystemID() +"! Caused by: " + e.getClass().getSimpleName() + " (" + e.getMessage() + ")");
        }
    }

    public void addPlayerToGuildSection(GuildObject guild, GuildPlayerObject player) {
        threadPool.submit(() -> {
            try {
                storageType.addPlayerToGuildSection(guild, player);
            }catch (Exception e){
                GuildLogger.getLogger().error("Failed to add player " + player.getUniqueId() + " (" + player.getName() + ") to guild-storage-section for guild " + guild.getName() + " with storage-tye "  + storageType.getStorageSystemID() + "! Caused by: " + e.getClass().getSimpleName() + " (" + e.getMessage() + ")" );
            }
        });

    }

    public void migrateStorage(StorageType newStorageType) throws SQLException, IOException {
        new Thread(() -> {
            long start = 0;
            long end = 0;
            StorageType oldStorageType = this.storageType;
            try {
            this.storageType = newStorageType;
            try {
                loadStorageWithoutCaching();
            } catch (SQLException | FileNotFoundException e) {
                throw new RuntimeException(e);
            }
            Main.setStorageBusy(true);
            GuildLogger.getLogger().debug("=".repeat(69));
            GuildLogger.getLogger().debug("The plugin is not usable until the migration is complete from now on!");
            GuildLogger.getLogger().debug("");
            start = System.currentTimeMillis();
            GuildLogger.getLogger().debug("Recreate guild-sections...");
            for (GuildObject guild : Main.getGuildCache().values()){
                createGuildSection(guild);
            }
            end = System.currentTimeMillis();

            GuildLogger.getLogger().debug("Done (" + GuildTextUtils.formatTimeTake(start - end) + ")");
            GuildLogger.getLogger().debug("Recreate player-sections...");
            start = System.currentTimeMillis();
            for (GuildPlayerObject guildPlayer : Main.getPlayerCache().values()){
                createPersonalPlayerSection(guildPlayer,false);
            }
            end = System.currentTimeMillis();
            GuildLogger.getLogger().debug("Done (" + GuildTextUtils.formatTimeTake(start - end) + ")");

            if (Main.getGuildConfigHandler().deleteOldStorageSectionsWhileMigration()) {
                this.storageType = oldStorageType;
                start = System.currentTimeMillis();
                GuildLogger.getLogger().debug("Delete guilds-sections...");
                for (GuildObject guild : Main.getGuildCache().values()) {
                    deleteGuildSection(guild, false);
                }
                end = System.currentTimeMillis();
                GuildLogger.getLogger().debug("Done (" + GuildTextUtils.formatTimeTake(start - end) + ")");

                GuildLogger.getLogger().debug("Delete player-sections...");
                start = System.currentTimeMillis();
                for (GuildPlayerObject guildPlayer : Main.getPlayerCache().values()){
                    deletePlayerSection(guildPlayer);
                }
                end = System.currentTimeMillis();
                GuildLogger.getLogger().debug("Done (" + GuildTextUtils.formatTimeTake(start - end) + ")");


                GuildLogger.getLogger().debug("Delete root-sections...");
                start = System.currentTimeMillis();
                deleteRootStorageSection();
                end = System.currentTimeMillis();
                GuildLogger.getLogger().debug("Done (" + GuildTextUtils.formatTimeTake(start - end) + ")");

                this.storageType=newStorageType;
            }
            Main.mainFileConfiguration.set("storageType", newStorageType.getStorageSystemID());
            Main.plugin.saveConfig();
            Main.plugin.reloadConfig();
            GuildLogger.getLogger().debug("");
            Main.setStorageBusy(false);
            GuildLogger.getLogger().debug("Migration complete! The plugin is now usable with the new storage-type " + newStorageType + "!");
            GuildLogger.getLogger().debug("=".repeat(77));
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
            GuildLogger.getLogger().error("Failed to migrate from "  + oldStorageType.getStorageSystemID() + " to " + newStorageType + "! Using old storage-type "  + oldStorageType.getStorageSystemID() + "! Caused by: " + e.getClass().getSimpleName() + " (" +e.getMessage() + ")");
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
            GuildLogger.getLogger().debug("Successfully exported " + Main.getPlayerCache().size() + " Player" + (Main.getPlayerCache().size() > 1?"s":"") +" and " + Main.getGuildCache().size() + " Guild" + (Main.getGuildCache().size() > 1?"s":"") + " in file " + compressedFile.getAbsolutePath() + "!");
        }catch (Exception e){
            e.printStackTrace();
            GuildLogger.getLogger().error("Failed to export storage! Caused by: " + e.getClass().getSimpleName() + "(" + e.getMessage() + ")");
        }
    }


    public static void importStorage(File path){
        try {
            if (new ZipFile(path).size()>1){
                GuildLogger.getLogger().error("The zip-file " + path + " has more than one entry in it. Failed to import guilds!");
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
                    GuildLogger.getLogger().info(guildObject.getName() + " already exist, skipped!");
                }
            }

            for (GuildPlayerObject guildPlayerObjectData: guildExportObject.getGuildPlayers()){
                if (!Main.getPlayerCache().containsKey(guildPlayerObjectData.getUniqueId())) {
                    Main.getMainStorage().createPersonalPlayerSection(guildPlayerObjectData, true);
                }else {
                    GuildLogger.getLogger().info(guildPlayerObjectData.getUniqueId() + " (" + guildPlayerObjectData.getName() + ") already exist, skipped!");
                }
            }

            Main.getMainStorage().loadStorage();
        }catch (Exception e){
            e.printStackTrace();
            GuildLogger.getLogger().error("Failed to import guild-export-file " + path + "! Caused by: " +e.getClass().getSimpleName() + "(" +e.getMessage() + ")");
        }
    }
}