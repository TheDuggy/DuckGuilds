package at.theduggy.duckguilds.storage.systemTypes;

import at.theduggy.duckguilds.Main;
import at.theduggy.duckguilds.objects.*;
import at.theduggy.duckguilds.utils.GuildTextUtils;
import at.theduggy.duckguilds.utils.ScoreboardHandler;
import com.google.gson.JsonObject;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class GuildFileSystem {
    //TODO Create files if not exists independent
    public static File PLAYER_DATA_FOLDER = new File(Main.guildRootFolder.getPath() + "/playerData");
    public static File GUILD_DATA_FOLDER = new File(Main.guildRootFolder.getPath() + "/guilds");

    public static boolean personalGuildPlayerFileExists(UUID player){
        return Files.exists(Path.of(PLAYER_DATA_FOLDER + "/" + player + ".json"));
    }

    public static void init() throws IOException {
        initFolders();
        cacheGuildFiles();
        cachePlayers();
        applyGuildsOnPlayers();
    }

    public static void initWithoutCaching(){
        initFolders();
    }

    public static void deletePersonalPlayerFile(GuildPlayerObject player) throws IOException {
        Files.delete(Path.of(PLAYER_DATA_FOLDER + "/" + player.getUniqueId() + ".json"));
    }

    public static void deleteRootFolders() throws IOException {
        Files.delete(PLAYER_DATA_FOLDER.toPath());
        Main.log("Deleted player-data-folder " + PLAYER_DATA_FOLDER.toPath() + "!", Main.LogLevel.DEFAULT);
        Files.delete(GUILD_DATA_FOLDER.toPath());
        Main.log("Deleted guild-data-folder " + GUILD_DATA_FOLDER.toPath() + "!", Main.LogLevel.DEFAULT);
        Files.delete(Main.guildRootFolder.toPath());
        Main.log("Deleted root-guild-folder " + Main.guildRootFolder.toPath() + "!", Main.LogLevel.DEFAULT);
    }

    public static void createPersonalPlayerFile(GuildPlayerObject player) throws IOException {
        Files.createFile(Path.of(PLAYER_DATA_FOLDER + "/"+ player.getUniqueId() + ".json"));
        FileWriter writeJsonData = new FileWriter(PLAYER_DATA_FOLDER + "/" + player.getUniqueId() + ".json");
        writeJsonData.write(Main.getGsonInstance().toJson(player));
        writeJsonData.close();
    }
    
    public static void createGuildFile(GuildObject guildObject) throws IOException {
        Path guildGuildsFolder = Paths.get(Main.guildRootFolder + "/guilds");
        Path guildFile = Paths.get(guildGuildsFolder + "/" + guildObject.getName() + ".json");
        Files.createFile(guildFile);
        BufferedWriter fileWriter = new BufferedWriter(new FileWriter(String.valueOf(guildFile), StandardCharsets.UTF_8));
        fileWriter.write(Main.getGsonInstance().toJson(guildObject));
        fileWriter.close();
    }



    public static void cacheGuildFiles() {
        Path guildGuildsFolder = Paths.get(Main.guildRootFolder + "/guilds");
        if (guildGuildsFolder.toFile().listFiles().length>0) {
            Main.log("--------------caching guilds--------------", Main.LogLevel.DEFAULT);
            for (File file : guildGuildsFolder.toFile().listFiles()) {
                if (GuildTextUtils.getFileExtension(file).equals(".json")) {
                    try {
                        GuildObject guildObject = Main.getGsonInstance().fromJson(readPrettyJsonFile(file), GuildObject.class);
                        ScoreboardHandler.addGuild(guildObject);
                        Main.getGuildCache().put(guildObject.getName(), guildObject); // guild indexed to HasMap
                        Main.log("Caching " + GuildTextUtils.getFileBaseName(file) + " with storage-type File!", Main.LogLevel.DEFAULT);
                    } catch (Exception e) {
                        Main.log("Failed to cache guild-file for guild " + GuildTextUtils.getFileBaseName(file) + "! Caused by: " + e.getClass().getSimpleName() + "(" + e.getMessage() + ")", Main.LogLevel.WARNING);
                    }
                }
            }
        }
    }

    private static void applyGuildsOnPlayers(){
        for (GuildObject guildObject:Main.getGuildCache().values()){
            for (UUID uuid:guildObject.getPlayers()){
                Main.getPlayerCache().get(uuid).setGuild(guildObject.getName());
            }
        }
    }

    public static void deleteGuildFile(GuildObject guildObject) throws IOException {
        Path guild = Path.of(GUILD_DATA_FOLDER + "/" + guildObject.getName() + ".json");
        Files.delete(guild);

    }

    public static void removePlayerFromGuildFile(GuildPlayerObject player, GuildObject guildName) throws IOException {
        File guildFile = new File(GUILD_DATA_FOLDER + "/" + guildName.getName() + ".json");
        GuildObject guildObject = Main.getGsonInstance().fromJson(readPrettyJsonFile(guildFile), GuildObject.class);
        guildObject.getPlayers().remove(player.getUniqueId());
        FileWriter fileWriter = new FileWriter(guildFile, StandardCharsets.UTF_8);
        fileWriter.write(Main.getGsonInstance().toJson(guildObject));
        fileWriter.close();
    }

    private static String readPrettyJsonFile(File fileToRead) throws IOException {
        StringBuilder rawJsonData = new StringBuilder();
        Scanner getRawJsonData = new Scanner(new FileReader(fileToRead,StandardCharsets.UTF_8));
        while (getRawJsonData.hasNext()){
            rawJsonData.append(getRawJsonData.nextLine());
        }
        getRawJsonData.close();
        return rawJsonData.toString();
    }

    public static void cachePlayers() throws IOException {
        for (File file:PLAYER_DATA_FOLDER.listFiles()){
            if (GuildTextUtils.isStringUUID(GuildTextUtils.getFileBaseName(file))) {
                try {
                    cachePlayer(UUID.fromString(GuildTextUtils.getFileBaseName(file)));
                }catch (Exception e){
                    Main.log("Failed to cache player " + GuildTextUtils.getFileBaseName(file) + "! Caused by: " + e.getClass().getSimpleName() + " (" + e.getMessage() + ")", Main.LogLevel.WARNING);
                }

            }
        }
    }

    public static void cachePlayer(UUID player) {
        try {
            Main.log("Caching " + player+ " with storage-type File!", Main.LogLevel.DEFAULT);
            File playerFile = new File(PLAYER_DATA_FOLDER + "/" + player + ".json");
            GuildPlayerObject guildPlayerObject = Main.getGsonInstance().fromJson(readPrettyJsonFile(playerFile), GuildPlayerObject.class);//new GuildPlayerObject(player,GuildTextUtils.isPlayerOnline(player), (String) jsonData.get("name"),guild);
            guildPlayerObject.setGuild("");
            guildPlayerObject.setOnline(false);
            guildPlayerObject.setPlayer(UUID.fromString(GuildTextUtils.getFileBaseName(playerFile)));
            Main.getPlayerCache().put(player, guildPlayerObject);
        }catch (Exception e){
            Main.log("Failed to cache player " + player + "! Caused by: " + e.getClass().getSimpleName() + " (" + e.getMessage() + ")", Main.LogLevel.WARNING);
        }

    }

    public static void initFolders()  {
        if (Main.guildRootFolder.exists()){
            if (!GUILD_DATA_FOLDER.exists()){
                try {
                    Files.createDirectory(GUILD_DATA_FOLDER.toPath());
                }catch (Exception e){
                    Main.log("Failed to create guild-data-folder " + GUILD_DATA_FOLDER.toPath() + "! Caused by: " + e.getClass().getSimpleName() + " (" + e.getMessage() + ")", Main.LogLevel.WARNING);
                }
            }
            if (!PLAYER_DATA_FOLDER.exists()){
                try {
                    Files.createDirectory(PLAYER_DATA_FOLDER.toPath());
                }catch (Exception e){
                    Main.log("Failed to create player-data-folder " + PLAYER_DATA_FOLDER.toPath() + "! Caused by: " + e.getClass().getSimpleName() + " (" + e.getMessage() + ")", Main.LogLevel.WARNING);
                }
            }
        }else {
            try {
                Files.createDirectory(Main.guildRootFolder.toPath());
            }catch (Exception e){
                Main.log("Failed to create guild-root-folder " + Main.guildRootFolder.toPath() + "! Caused by: " + e.getClass().getSimpleName() + " (" + e.getMessage() + ")", Main.LogLevel.WARNING);
            }

            try {
                Files.createDirectory(PLAYER_DATA_FOLDER.toPath());
            }catch (Exception e){
                Main.log("Failed to create player-data-folder " + PLAYER_DATA_FOLDER.toPath() + "! Caused by: " + e.getClass().getSimpleName() + "(" + e.getMessage() + ")", Main.LogLevel.WARNING);
            }
            try {
                Files.createDirectory(GUILD_DATA_FOLDER.toPath());
            }catch (Exception e){
                Main.log("Failed to create guild-data-folder " + GUILD_DATA_FOLDER.toPath() + "! Caused by: " + e.getClass().getSimpleName() + " (" + e.getMessage() + ")", Main.LogLevel.WARNING);
            }
        }
    }

    public static String getPlayerNameFromPlayerFile(GuildPlayerObject player) throws IOException {
        File personalPlayerFile = new File(PLAYER_DATA_FOLDER + "/" + player.getUniqueId() + ".json");
        GuildPlayerObject guildPlayerObject = Main.getGsonInstance().fromJson(readPrettyJsonFile(personalPlayerFile),GuildPlayerObject.class);
        return guildPlayerObject.getName();

    }

    public static void updatePlayerFile(GuildPlayerObject guildPlayerObject) throws IOException {
        File personalPlayerFile = new File(PLAYER_DATA_FOLDER + "/" + guildPlayerObject.getUniqueId() + ".json");
        guildPlayerObject.setName(guildPlayerObject.getName());
        FileWriter writeNewData = new FileWriter(personalPlayerFile, StandardCharsets.UTF_8);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("name", guildPlayerObject.getGuild());
        writeNewData.write(Main.getGsonInstance().toJson(jsonObject));
        writeNewData.close();
    }

    public static void addPlayerToGuildFile(GuildObject guildObject, GuildPlayerObject guildPlayerObject) throws IOException {
        FileWriter fileWriter = new FileWriter(GUILD_DATA_FOLDER + "/" + guildObject.getName() + ".json", StandardCharsets.UTF_8);
        if (!guildObject.getPlayers().contains(guildPlayerObject.getUniqueId())){
            guildObject.getPlayers().add(guildPlayerObject.getUniqueId());
        }
        fileWriter.write(Main.getGsonInstance().toJson(guildObject));
        fileWriter.close();
    }

    public static void exportStorage() throws IOException {
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
            ZipFile zipFile = new ZipFile(path);
            if (zipFile.size()>1){
                Main.log("The zip-file " + path + " has more than one entry in it. Failed to import guilds!", Main.LogLevel.WARNING);
            }

            ZipInputStream zis = new ZipInputStream(new FileInputStream(path));
            String result = "";
            ZipEntry zipEntry = null;
            while ((zipEntry=zis.getNextEntry()) != null){
                byte[] buffer = new byte[1024];
                int read = 0;
                while ((read=zis.read(buffer)) != -1){
                    result += new String(buffer, StandardCharsets.UTF_8).trim();
                }
            }
            zis.close();
            result = result.trim();
            result = result.replaceAll("\n","");

            GuildExportObject guildExportObject = Main.getGsonInstance().fromJson(result, GuildExportObject.class);
            for ( GuildObject guildObject : guildExportObject.getGuildObjects()){
                if (!Main.getGuildCache().containsKey(guildObject.getName())){
                    Main.getMainStorage().createGuildStorageSection(guildObject);
                }else {
                    Main.log(guildObject.getName() + " already exist, skipped!", Main.LogLevel.WARNING);
                }

            }

            for (GuildPlayerObject guildPlayerObjectData: guildExportObject.getGuildPlayers()){
                if (!Main.getPlayerCache().containsKey(guildPlayerObjectData.getUniqueId())) {
                    Main.getMainStorage().createPersonalPlayerStorageSection(guildPlayerObjectData, true);
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
