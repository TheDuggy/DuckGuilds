package at.theduggy.duckguilds.storage.systemTypes;

import at.theduggy.duckguilds.Main;
import at.theduggy.duckguilds.config.GuildConfig;
import at.theduggy.duckguilds.files.GuildFiles;
import at.theduggy.duckguilds.other.JsonUtils;
import at.theduggy.duckguilds.other.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.nio.Buffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.UUID;

public class GuildFileSystem {

    //TODO Use only uuid named player file, instead of dir with data.json
    public static File PLAYER_DATA_FOLDER = new File(Main.guildRootFolder.toAbsolutePath() + "/playerData");

    public static void createGuildFile(HashMap<String,Object> guildData,String name) throws IOException {
        JSONObject jsonData = new JSONObject();
        ArrayList<String> players = new ArrayList<>();
        for (UUID uuid:(ArrayList<UUID>)guildData.get("players")){
            players.add(uuid.toString());
        }
        System.out.println(guildData.get("name"));
        jsonData.put("name", guildData.get("name"));
        jsonData.put("color", guildData.get("color"));
        jsonData.put("tag", guildData.get("tag"));
        jsonData.put("tagColor", guildData.get("tagColor"));
        jsonData.put("players", players);
        jsonData.put("head", guildData.get("head").toString());
        try {
            Path guildGuildsFolder = Paths.get(Main.guildRootFolder + "/guilds");
            Path guildFile = Paths.get(guildGuildsFolder + "/" + name + ".json");
            Files.createFile(guildFile);
            BufferedWriter fileWriter = new BufferedWriter(new FileWriter(String.valueOf(guildFile), StandardCharsets.UTF_8));
            fileWriter.write(JsonUtils.toPrettyJsonString(jsonData.toJSONString()));
            fileWriter.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }



    public static void cacheGuildsFiles() throws IOException, ParseException {
        try {
            Path guildGuildsFolder = Paths.get(Main.guildRootFolder + "/guilds");
            for (File file : guildGuildsFolder.toFile().listFiles()) {
                if (Utils.getFileExtension(file).equals(".json")) {
                    JSONObject jsonStringComponents = (JSONObject) new JSONParser().parse(readPrettyJsonFile(file));
                    HashMap<String, Object> guildDetails = new HashMap<>();
                    ArrayList<UUID> players = new ArrayList<>();
                    for (String playerUUID : (ArrayList<String>) jsonStringComponents.get("players")) {
                        players.add(UUID.fromString(playerUUID));
                        cachePlayer(UUID.fromString(playerUUID), Utils.getFileBaseName(file));
                    }
                    guildDetails.put("head", UUID.fromString((String) jsonStringComponents.get("head")));
                    guildDetails.put("color", Utils.translateFromStringToChatColor((String) jsonStringComponents.get("color")));//TODO Redesign everything to use uuid.json instant!
                    guildDetails.put("tagColor",  Utils.translateFromStringToChatColor((String) jsonStringComponents.get("tagColor")));
                    guildDetails.put("players", players);
                    guildDetails.put("tag", jsonStringComponents.get("tag"));
                    Main.getGuildCache().put(Utils.getFileBaseName(file), guildDetails); // guild indexed to HasMap
                }
            }
        }catch (IOException|ParseException e){
            e.printStackTrace();
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

    public static void removePlayerFromGuildFile(UUID player, String guildName) throws IOException, ParseException {
        try {
            Path guildFile = Path.of(GuildFiles.guildGuildsFolder + "/" + guildName + ".json");
            JSONParser jsonParser = new JSONParser();
            JSONObject guildFileJsonString = (JSONObject) jsonParser.parse(readPrettyJsonFile(new File(String.valueOf(guildFile))));
            ArrayList<String> members = (ArrayList<String>) guildFileJsonString.get("players");
            for (int i = 0; i <= members.size(); i++) {
                if (members.get(i).equals(player.toString())) {
                    members.remove(i);
                    break;
                }
            }
            guildFileJsonString.remove("players");
            guildFileJsonString.put("players", members);
            FileWriter fileWriter = new FileWriter(guildFile.toFile(), StandardCharsets.UTF_8);
            fileWriter.write(guildFileJsonString.toJSONString());
            fileWriter.close();

        }catch (IOException|ParseException e){
            e.printStackTrace();
        }
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

    public static void cachePlayers() throws IOException, ParseException {
        for (File file:GuildFiles.guildPlayerFolder.toFile().listFiles()){
            if (Utils.isStringUUID(Utils.getFileBaseName(file))) {
                if (!Main.getPlayerCache().containsKey(UUID.fromString(Utils.getFileBaseName(file)))) {
                    cachePlayer(UUID.fromString(Utils.getFileBaseName(file)),"");
                }
            }
        }
    }

    public static void cachePlayer(UUID player,String guild) throws IOException, ParseException {
        Path playerFile = Path.of(GuildFiles.guildPlayerFolder + "/" + player + ".json");
        JSONObject jsonData = (JSONObject) new JSONParser().parse(readPrettyJsonFile(playerFile.toFile()));
        HashMap<String,Object> playerData = new HashMap<>();
        playerData.put("name",jsonData.get("name"));
        playerData.put("guild",guild);
        if (Utils.isPlayerOnline(player)){
            playerData.put("online",true);
        }else {
            playerData.put("online",false);
        }
        Main.getPlayerCache().put(player,playerData);
        Bukkit.getLogger().warning(Main.getPlayerCache().toString());
    }

    public static String getPlayerDataFromFile(UUID player) throws IOException, ParseException {
        File personalPlayerFile = new File(PLAYER_DATA_FOLDER + "/" + player + ".json");
        JSONObject playerData = (JSONObject) new JSONParser().parse(readPrettyJsonFile(personalPlayerFile));
        return (String) playerData.get("name");
    }

    public static void updatePlayerData(UUID player,HashMap<String,String> newPlayerData) throws IOException {
        File personalPlayerFile = new File(PLAYER_DATA_FOLDER + "/" + player + ".json");
        JSONObject newPlayerDataInJson = new JSONObject();
        newPlayerDataInJson.put("name",newPlayerData.get("name"));
        FileWriter writeNewData = new FileWriter(personalPlayerFile, StandardCharsets.UTF_8);
        writeNewData.write(JsonUtils.toPrettyJsonString(newPlayerDataInJson.toJSONString()));
        writeNewData.close();
    }
}
