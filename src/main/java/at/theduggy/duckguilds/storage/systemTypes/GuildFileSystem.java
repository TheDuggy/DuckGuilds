package at.theduggy.duckguilds.storage.systemTypes;

import at.theduggy.duckguilds.Main;
import at.theduggy.duckguilds.config.GuildConfig;
import at.theduggy.duckguilds.files.GuildFiles;
import at.theduggy.duckguilds.metadata.GuildMetadata;
import at.theduggy.duckguilds.metadata.GuildPlayerMetadata;
import at.theduggy.duckguilds.other.JsonUtils;
import at.theduggy.duckguilds.other.Utils;
import jdk.jshell.execution.Util;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Team;
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

    public static void createGuildFile(GuildMetadata guildMetadata, String name) throws IOException {
        try {
            Path guildGuildsFolder = Paths.get(Main.guildRootFolder + "/guilds");
            Path guildFile = Paths.get(guildGuildsFolder + "/" + name + ".json");
            Files.createFile(guildFile);
            BufferedWriter fileWriter = new BufferedWriter(new FileWriter(String.valueOf(guildFile), StandardCharsets.UTF_8));
            System.out.println(guildMetadata.toString());
            fileWriter.write(JsonUtils.toPrettyJsonString(guildMetadata.toString()));
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
                    ArrayList<UUID> players = new ArrayList<>();
                    for (String playerUUID : (ArrayList<String>) jsonStringComponents.get("players")) {
                        players.add(UUID.fromString(playerUUID));
                        cachePlayer(UUID.fromString(playerUUID), Utils.getFileBaseName(file));
                    }
                    GuildMetadata guildMetadata = new GuildMetadata();
                    guildMetadata.setHead(UUID.fromString((String) jsonStringComponents.get("head")));
                    guildMetadata.setColor(Utils.translateFromStringToChatColor((String) jsonStringComponents.get("color")));
                    guildMetadata.setTagColor(Utils.translateFromStringToChatColor((String) jsonStringComponents.get("tagColor")));
                    guildMetadata.setPlayers(players);
                    guildMetadata.setTag((String) jsonStringComponents.get("tag"));
                    guildMetadata.setName(Utils.getFileBaseName(file));
                    Team team;
                    try {
                        team = Main.getScoreboard().registerNewTeam(Utils.getFileBaseName(file));
                    }catch (IllegalArgumentException e){
                        team = Main.getScoreboard().getTeam(Utils.getFileBaseName(file));
                    }
                    team.setColor(guildMetadata.getColor());
                    team.setSuffix(ChatColor.GRAY + "[" + guildMetadata.getTagColor() + guildMetadata.getTag() + ChatColor.GRAY + "]");
                    team.setDisplayName(Utils.getFileBaseName(file));
                    Main.getGuildCache().put(Utils.getFileBaseName(file), guildMetadata); // guild indexed to HasMap
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
        GuildPlayerMetadata guildPlayerMetadata = new GuildPlayerMetadata(player,Utils.isPlayerOnline(player), (String) jsonData.get("name"),guild);
        guildPlayerMetadata.setOnline(Utils.isPlayerOnline(player));
        Main.getPlayerCache().put(player,guildPlayerMetadata);
    }

    public static String getPlayerDataFromFile(UUID player) throws IOException, ParseException {
        File personalPlayerFile = new File(PLAYER_DATA_FOLDER + "/" + player + ".json");
        JSONObject playerData = (JSONObject) new JSONParser().parse(readPrettyJsonFile(personalPlayerFile));
        return (String) playerData.get("name");
    }

    public static void updatePlayerData(UUID player,GuildPlayerMetadata guildPlayerMetadata) throws IOException {
        File personalPlayerFile = new File(PLAYER_DATA_FOLDER + "/" + player + ".json");
        JSONObject newPlayerDataInJson = new JSONObject();
        newPlayerDataInJson.put("name",guildPlayerMetadata.getName());
        FileWriter writeNewData = new FileWriter(personalPlayerFile, StandardCharsets.UTF_8);
        writeNewData.write(JsonUtils.toPrettyJsonString(newPlayerDataInJson.toJSONString()));
        writeNewData.close();
    }
}
