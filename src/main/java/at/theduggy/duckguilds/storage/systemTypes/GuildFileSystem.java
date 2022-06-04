package at.theduggy.duckguilds.storage.systemTypes;

import at.theduggy.duckguilds.Main;
import at.theduggy.duckguilds.objects.GuildObject;
import at.theduggy.duckguilds.objects.GuildPlayerObject;
import at.theduggy.duckguilds.utils.GuildTextUtils;
import at.theduggy.duckguilds.utils.ScoreboardHandler;
import at.theduggy.duckguilds.utils.Utils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.bukkit.entity.Player;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.UUID;

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

    public static void createPersonalPlayerFile(Player player) throws IOException {
        Files.createFile(Path.of(PLAYER_DATA_FOLDER + "/"+ player.getUniqueId() + ".json"));
        JsonObject rawJsonData = new JsonObject();
        rawJsonData.addProperty("name", player.getName());
        FileWriter writeJsonData = new FileWriter(PLAYER_DATA_FOLDER + "/" + player.getUniqueId() + ".json");
        writeJsonData.write(Main.getGsonInstance().toJson(rawJsonData));
        writeJsonData.close();
    }
    
    public static void createGuildFile(GuildObject guildObject) throws IOException {
        Path guildGuildsFolder = Paths.get(Main.guildRootFolder + "/guilds");
        Path guildFile = Paths.get(guildGuildsFolder + "/" + guildObject.getName() + ".json");
        Files.createFile(guildFile);
        BufferedWriter fileWriter = new BufferedWriter(new FileWriter(String.valueOf(guildFile), StandardCharsets.UTF_8));
        System.out.println(guildObject);
        fileWriter.write(Main.getGsonInstance().toJson(guildObject));
        fileWriter.close();
    }



    public static void cacheGuildFiles() {
        Path guildGuildsFolder = Paths.get(Main.guildRootFolder + "/guilds");
        for (File file : guildGuildsFolder.toFile().listFiles()) {
            if (GuildTextUtils.getFileExtension(file).equals(".json")) {
                try {
                    GuildObject guildObject = Main.getGsonInstance().fromJson(readPrettyJsonFile(file), GuildObject.class);
                    ScoreboardHandler.addGuild(guildObject);
                    Main.getGuildCache().put(guildObject.getName(), guildObject); // guild indexed to HasMap
                    Main.log("Cached " + GuildTextUtils.getFileBaseName(file) + "!", Main.LogLevel.DEFAULT);
                }catch (Exception e){
                    Main.log("Failed to cache guild-file for guild " + GuildTextUtils.getFileBaseName(file) + "! Caused by: " + e.getClass().getSimpleName() + "(" + e.getMessage()+")", Main.LogLevel.WARNING);
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
        GuildObject guildObject = new Gson().fromJson(readPrettyJsonFile(guildFile), GuildObject.class);
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
                cachePlayer(UUID.fromString(GuildTextUtils.getFileBaseName(file)),null);
            }
        }
    }

    public static void cachePlayer(UUID player,String guild) {
        try {
            File playerFile = new File(PLAYER_DATA_FOLDER + "/" + player + ".json");
            GuildPlayerObject guildPlayerObject = Main.getGsonInstance().fromJson(readPrettyJsonFile(playerFile), GuildPlayerObject.class);//new GuildPlayerObject(player,GuildTextUtils.isPlayerOnline(player), (String) jsonData.get("name"),guild);
            guildPlayerObject.setGuild(guild);
            guildPlayerObject.setOnline(false);
            guildPlayerObject.setPlayer(UUID.fromString(GuildTextUtils.getFileBaseName(playerFile)));
            Main.getPlayerCache().put(player, guildPlayerObject);
        }catch (Exception e){
            Main.log("Failed to cache player " + player + "! Caused by: " + e.getClass().getSimpleName() + "(" + e.getMessage() + ")", Main.LogLevel.WARNING);
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
        File personalPlayerFile = new File(PLAYER_DATA_FOLDER + "/" + player + ".json");
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
        FileWriter fileWriter = new FileWriter(GUILD_DATA_FOLDER + "/" + guildObject.getName() + ".json");
        if (!guildObject.getPlayers().contains(guildPlayerObject.getUniqueId())){
            guildObject.getPlayers().add(guildPlayerObject.getUniqueId());
        }
        fileWriter.write(Main.getGsonInstance().toJson(guildObject));
        fileWriter.close();
    }
    
}
