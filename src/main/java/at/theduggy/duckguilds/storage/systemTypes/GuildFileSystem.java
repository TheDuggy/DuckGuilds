package at.theduggy.duckguilds.storage.systemTypes;

import at.theduggy.duckguilds.Main;
import at.theduggy.duckguilds.objects.GuildObject;
import at.theduggy.duckguilds.objects.GuildPlayerObject;
import at.theduggy.duckguilds.utils.GuildTextUtils;
import at.theduggy.duckguilds.utils.ScoreboardHandler;
import com.google.gson.JsonObject;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.UUID;

public class GuildFileSystem extends StorageType{
    private final File PLAYER_DATA_FOLDER = new File(Main.guildRootFolder.getPath() + "/playerData");
    private final File GUILD_DATA_FOLDER = new File(Main.guildRootFolder.getPath() + "/guilds");

    @Override
    public boolean personalPlayerSectionExists(UUID player){
        return Files.exists(Path.of(PLAYER_DATA_FOLDER + "/" + player + ".json"));
    }

    @Override
    public void load() throws IOException {
        initFolders();
        cacheGuildFiles();
        cachePlayerFiles();
        applyGuildsOnPlayers();
    }

    @Override
    public void loadWithoutCaching() {
        initFolders();
    }

    @Override
    public void deletePlayerSection(GuildPlayerObject player) throws IOException {
        Files.delete(Path.of(PLAYER_DATA_FOLDER + "/" + player.getUniqueId() + ".json"));
    }

    @Override
    public void deleteRootSections() throws IOException {
        Files.delete(PLAYER_DATA_FOLDER.toPath());
        Main.log("Deleted player-data-folder " + PLAYER_DATA_FOLDER.toPath() + "!", Main.LogLevel.DEFAULT);
        Files.delete(GUILD_DATA_FOLDER.toPath());
        Main.log("Deleted guild-data-folder " + GUILD_DATA_FOLDER.toPath() + "!", Main.LogLevel.DEFAULT);
        Files.delete(Main.guildRootFolder.toPath());
        Main.log("Deleted root-guild-folder " + Main.guildRootFolder.toPath() + "!", Main.LogLevel.DEFAULT);
    }

    @Override
    public void createPersonalPlayerSection(GuildPlayerObject player) throws IOException {
        Files.createFile(Path.of(PLAYER_DATA_FOLDER + "/"+ player.getUniqueId() + ".json"));
        FileWriter writeJsonData = new FileWriter(PLAYER_DATA_FOLDER + "/" + player.getUniqueId() + ".json");
        writeJsonData.write(Main.getGsonInstance().toJson(player));
        writeJsonData.close();
    }

    @Override
    public void createGuildSection(GuildObject guildObject) throws IOException {
        Path guildGuildsFolder = Paths.get(Main.guildRootFolder + "/guilds");
        Path guildFile = Paths.get(guildGuildsFolder + "/" + guildObject.getName() + ".json");
        Files.createFile(guildFile);
        BufferedWriter fileWriter = new BufferedWriter(new FileWriter(String.valueOf(guildFile), StandardCharsets.UTF_8));
        fileWriter.write(Main.getGsonInstance().toJson(guildObject));
        fileWriter.close();
    }


    private void cacheGuildFiles() {
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

    @Override
    public void deleteGuildSection(GuildObject guildObject) throws IOException {
        Path guild = Path.of(GUILD_DATA_FOLDER + "/" + guildObject.getName() + ".json");
        Files.delete(guild);

    }

    @Override
    public void removePlayerFromGuildSection(GuildPlayerObject player, GuildObject guildName) throws IOException {
        File guildFile = new File(GUILD_DATA_FOLDER + "/" + guildName.getName() + ".json");
        GuildObject guildObject = Main.getGsonInstance().fromJson(readPrettyJsonFile(guildFile), GuildObject.class);
        guildObject.getPlayers().remove(player.getUniqueId());
        FileWriter fileWriter = new FileWriter(guildFile, StandardCharsets.UTF_8);
        fileWriter.write(Main.getGsonInstance().toJson(guildObject));
        fileWriter.close();
    }

    private String readPrettyJsonFile(File fileToRead) throws IOException {
        StringBuilder rawJsonData = new StringBuilder();
        Scanner getRawJsonData = new Scanner(new FileReader(fileToRead,StandardCharsets.UTF_8));
        while (getRawJsonData.hasNext()){
            rawJsonData.append(getRawJsonData.nextLine());
        }
        getRawJsonData.close();
        return rawJsonData.toString();
    }

    private void cachePlayerFiles() throws IOException {
        for (File file:PLAYER_DATA_FOLDER.listFiles()){
            if (GuildTextUtils.isStringUUID(GuildTextUtils.getFileBaseName(file))) {
                try {
                    cachePlayerFile(UUID.fromString(GuildTextUtils.getFileBaseName(file)));
                }catch (Exception e){
                    Main.log("Failed to cache player " + GuildTextUtils.getFileBaseName(file) + "! Caused by: " + e.getClass().getSimpleName() + " (" + e.getMessage() + ")", Main.LogLevel.WARNING);
                }

            }
        }
    }

    private void cachePlayerFile(UUID player) {
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

    private void initFolders()  {
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

    @Override
    public String getPlayerNameFromPlayerSection(GuildPlayerObject player) throws IOException {
        File personalPlayerFile = new File(PLAYER_DATA_FOLDER + "/" + player.getUniqueId() + ".json");
        GuildPlayerObject guildPlayerObject = Main.getGsonInstance().fromJson(readPrettyJsonFile(personalPlayerFile),GuildPlayerObject.class);
        return guildPlayerObject.getName();
    }

    @Override
    public void updatePlayerSection(GuildPlayerObject guildPlayerObject) throws IOException {
        File personalPlayerFile = new File(PLAYER_DATA_FOLDER + "/" + guildPlayerObject.getUniqueId() + ".json");
        guildPlayerObject.setName(guildPlayerObject.getName());
        FileWriter writeNewData = new FileWriter(personalPlayerFile, StandardCharsets.UTF_8);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("name", guildPlayerObject.getGuild());
        writeNewData.write(Main.getGsonInstance().toJson(jsonObject));
        writeNewData.close();
    }

    @Override
    public void addPlayerToGuildSection(GuildObject guildObject, GuildPlayerObject guildPlayerObject) throws IOException {
        FileWriter fileWriter = new FileWriter(GUILD_DATA_FOLDER + "/" + guildObject.getName() + ".json", StandardCharsets.UTF_8);
        if (!guildObject.getPlayers().contains(guildPlayerObject.getUniqueId())){
            guildObject.getPlayers().add(guildPlayerObject.getUniqueId());
        }
        fileWriter.write(Main.getGsonInstance().toJson(guildObject));
        fileWriter.close();
    }
}
