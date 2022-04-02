package at.theduggy.duckguilds.storage.systemTypes;

import at.theduggy.duckguilds.Main;
import at.theduggy.duckguilds.files.GuildFiles;
import at.theduggy.duckguilds.objects.GuildMetadata;
import at.theduggy.duckguilds.objects.GuildObject;
import at.theduggy.duckguilds.objects.GuildPlayerObject;
import at.theduggy.duckguilds.other.JsonUtils;
import at.theduggy.duckguilds.other.Utils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;
import org.json.simple.JSONObject;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.UUID;

public class GuildFileSystem {
    //TODO Create files if not exists independent
    //TODO Test Gson!!!!
    public static File PLAYER_DATA_FOLDER = new File(Main.guildRootFolder.toAbsolutePath() + "/playerData");

    public static boolean personalGuildPlayerFileExists(UUID player){
        return Files.exists(Path.of(PLAYER_DATA_FOLDER + "/" + player + ".json"));
    }

    public static void createPersonalPlayerFile(Player player) throws IOException {
        Files.createFile(Path.of(PLAYER_DATA_FOLDER + "/"+ player.getUniqueId() + ".json"));
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setPrettyPrinting();
        Gson gson = gsonBuilder.create();
        JsonObject rawJsonData = new JsonObject();
        rawJsonData.addProperty("name", player.getName());
        FileWriter writeJsonData = new FileWriter(PLAYER_DATA_FOLDER + "/" + player.getUniqueId() + ".json");
        writeJsonData.write(Main.getGsonInstance().toJson(rawJsonData));
        writeJsonData.close();
    }
    
    public static void createGuildFile(GuildObject guildObject, String name) throws IOException {
        try {
            Path guildGuildsFolder = Paths.get(Main.guildRootFolder + "/guilds");
            Path guildFile = Paths.get(guildGuildsFolder + "/" + name + ".json");
            Files.createFile(guildFile);
            BufferedWriter fileWriter = new BufferedWriter(new FileWriter(String.valueOf(guildFile), StandardCharsets.UTF_8));
            System.out.println(guildObject.toString());
            fileWriter.write(JsonUtils.toPrettyJsonString(guildObject.toString()));
            fileWriter.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }



    public static void cacheGuildsFiles() throws IOException{
        Path guildGuildsFolder = Paths.get(Main.guildRootFolder + "/guilds");
        for (File file : guildGuildsFolder.toFile().listFiles()) {
            if (Utils.getFileExtension(file).equals(".json")) {
                System.out.println(readPrettyJsonFile(file));
                GuildObject guildObject = Main.getGsonInstance().fromJson(readPrettyJsonFile(file), GuildObject.class);
                Team team;
                //TODO Put head on first place on player-list!!!!
                try {
                    team = Main.getScoreboard().registerNewTeam(Utils.getFileBaseName(file));
                }catch (IllegalArgumentException e){
                    team = Main.getScoreboard().getTeam(Utils.getFileBaseName(file));
                }
                team.setColor(guildObject.getColor().getChatColor());
                team.setSuffix(ChatColor.GRAY + "[" + guildObject.getTagColor() + guildObject.getTag() + ChatColor.GRAY + "]");
                team.setDisplayName(Utils.getFileBaseName(file));
                Main.getGuildCache().put(Utils.getFileBaseName(file), guildObject); // guild indexed to HasMap
            }
        }
    }

    public static void deleteGuildFile(String name){
        Path guild = Path.of(GuildFiles.guildGuildsFolder + "/" + name + ".json");
        try {
            Files.delete(guild);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void removePlayerFromGuildFile(UUID player, String guildName) throws IOException {
        File guildFile = new File(GuildFiles.guildGuildsFolder + "/" + guildName + ".json");
        GuildObject guildObject = new Gson().fromJson(readPrettyJsonFile(guildFile), GuildObject.class);
        guildObject.getPlayers().remove(player);
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
        for (File file:GuildFiles.guildPlayerFolder.toFile().listFiles()){
            if (Utils.isStringUUID(Utils.getFileBaseName(file))) {
                if (!Main.getPlayerCache().containsKey(UUID.fromString(Utils.getFileBaseName(file)))) {
                    cachePlayer(UUID.fromString(Utils.getFileBaseName(file)),"");
                }
            }
        }
    }

    public static void cachePlayer(UUID player,String guild) throws IOException{
        File playerFile = new File(GuildFiles.guildPlayerFolder + "/" + player + ".json");
        GuildPlayerObject guildPlayerObject = new Gson().fromJson(readPrettyJsonFile(playerFile), GuildPlayerObject.class);//new GuildPlayerObject(player,Utils.isPlayerOnline(player), (String) jsonData.get("name"),guild);
        guildPlayerObject.setGuild(guild);
        guildPlayerObject.setOnline(Utils.isPlayerOnline(player));
        guildPlayerObject.setPlayer(UUID.fromString(Utils.getFileBaseName(playerFile)));
        Main.getPlayerCache().put(player, guildPlayerObject);
    }

    public static String getPlayerDataFromFile(UUID player) throws IOException {
        File personalPlayerFile = new File(PLAYER_DATA_FOLDER + "/" + player + ".json");
        GuildPlayerObject guildPlayerObject = new Gson().fromJson(readPrettyJsonFile(personalPlayerFile),GuildPlayerObject.class);
        return guildPlayerObject.getName();
    }

    public static void updatePlayerData(UUID player, GuildPlayerObject guildPlayerObject) throws IOException {
        File personalPlayerFile = new File(PLAYER_DATA_FOLDER + "/" + player + ".json");
        FileWriter writeNewData = new FileWriter(personalPlayerFile, StandardCharsets.UTF_8);
        writeNewData.write(JsonUtils.toPrettyJsonString(Main.getGsonInstance().toJson(guildPlayerObject)));
        writeNewData.close();
    }
    
    
}
