package at.theduggy.duckguilds.storage.systemTypes;

import at.theduggy.duckguilds.Main;
import at.theduggy.duckguilds.objects.GuildObject;
import at.theduggy.duckguilds.objects.GuildPlayerObject;
import at.theduggy.duckguilds.other.GuildTextUtils;
import at.theduggy.duckguilds.other.GuildTextUtils;
import at.theduggy.duckguilds.other.Utils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

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

    public static void createPersonalPlayerFile(Player player) throws IOException {
        Files.createFile(Path.of(PLAYER_DATA_FOLDER + "/"+ player.getUniqueId() + ".json"));
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
            fileWriter.write(Main.getGsonInstance().toJson(guildObject));
            fileWriter.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }



    public static void cacheGuildFiles() throws IOException{
        Path guildGuildsFolder = Paths.get(Main.guildRootFolder + "/guilds");
        for (File file : guildGuildsFolder.toFile().listFiles()) {
            if (GuildTextUtils.getFileExtension(file).equals(".json")) {
                System.out.println(readPrettyJsonFile(file));
                GuildObject guildObject = Main.getGsonInstance().fromJson(readPrettyJsonFile(file), GuildObject.class);
                Team team;
                try {
                    team = Main.getScoreboard().registerNewTeam(GuildTextUtils.getFileBaseName(file));
                }catch (IllegalArgumentException e){
                    team = Main.getScoreboard().getTeam(GuildTextUtils.getFileBaseName(file));
                }

                team.setColor(guildObject.getGuildColor().getChatColor());
                team.setSuffix(ChatColor.GRAY + "[" + guildObject.getTagColor().getChatColor() + guildObject.getTag() + ChatColor.GRAY + "]");
                team.setDisplayName(GuildTextUtils.getFileBaseName(file));
                for (UUID player : guildObject.getPlayers()){
                    cachePlayer(player, guildObject.getName());
                }
                Main.getGuildCache().put(GuildTextUtils.getFileBaseName(file), guildObject); // guild indexed to HasMap
            }
        }
    }

    public static void deleteGuildFile(String name){
        Path guild = Path.of(GUILD_DATA_FOLDER + "/" + name + ".json");
        try {
            Files.delete(guild);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void removePlayerFromGuildFile(UUID player, String guildName) throws IOException {
        File guildFile = new File(GUILD_DATA_FOLDER + "/" + guildName + ".json");
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
        for (File file:GUILD_DATA_FOLDER.listFiles()){
            if (GuildTextUtils.isStringUUID(GuildTextUtils.getFileBaseName(file))) {
                if (!Main.getPlayerCache().containsKey(UUID.fromString(GuildTextUtils.getFileBaseName(file)))) {
                    cachePlayer(UUID.fromString(GuildTextUtils.getFileBaseName(file)),"");
                }
            }
        }
    }

    public static void cachePlayer(UUID player,String guild) throws IOException{
        File playerFile = new File(PLAYER_DATA_FOLDER + "/" + player + ".json");
        GuildPlayerObject guildPlayerObject = Main.getGsonInstance().fromJson(readPrettyJsonFile(playerFile), GuildPlayerObject.class);//new GuildPlayerObject(player,GuildTextUtils.isPlayerOnline(player), (String) jsonData.get("name"),guild);
        guildPlayerObject.setGuild(guild);
        guildPlayerObject.setOnline(Utils.isPlayerOnline(player));
        guildPlayerObject.setPlayer(UUID.fromString(GuildTextUtils.getFileBaseName(playerFile)));
        Main.getPlayerCache().put(player, guildPlayerObject);

    }

    public static void initFolders() throws IOException {
        if (Main.guildRootFolder.exists()){
            if (!GUILD_DATA_FOLDER.exists()){
                Files.createDirectory(GUILD_DATA_FOLDER.toPath());
            }
            if (!PLAYER_DATA_FOLDER.exists()){
                Files.createDirectory(PLAYER_DATA_FOLDER.toPath());
            }
        }else {
            Files.createDirectory(Main.guildRootFolder.toPath());
            Files.createDirectory(PLAYER_DATA_FOLDER.toPath());
            Files.createDirectory(GUILD_DATA_FOLDER.toPath());
        }
    }

    public static String getPlayerDataFromFile(UUID player) throws IOException {
        File personalPlayerFile = new File(PLAYER_DATA_FOLDER + "/" + player + ".json");
        GuildPlayerObject guildPlayerObject = Main.getGsonInstance().fromJson(readPrettyJsonFile(personalPlayerFile),GuildPlayerObject.class);
        return guildPlayerObject.getName();
    }

    public static void updatePlayerData(UUID player, GuildPlayerObject guildPlayerObject, String name) throws IOException {
        File personalPlayerFile = new File(PLAYER_DATA_FOLDER + "/" + player + ".json");
        guildPlayerObject.setName(name);
        FileWriter writeNewData = new FileWriter(personalPlayerFile, StandardCharsets.UTF_8);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("name", name);
        writeNewData.write(Main.getGsonInstance().toJson(jsonObject));
        writeNewData.close();
    }
    
    
}
