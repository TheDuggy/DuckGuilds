/*DuckGuilds: a plugin for creating/managing guilds
  Copyright (C) 2021 Georg Kollegger (or TheDuggy/CoderTheDuggy)

  This program is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.

  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with this program.  If not, see <http://www.gnu.org/licenses/>.*/
package at.theduggy.duckguilds.files;

import at.theduggy.duckguilds.Main;;
import at.theduggy.duckguilds.other.JsonUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.UUID;

public class GuildFiles {
    public static Path guildPlayerFolder = Paths.get(Main.guildRootFolder.toAbsolutePath() + "/playerData");
    public static Path guildGuildsFolder = Paths.get(Main.guildRootFolder.toAbsolutePath()+ "/guilds");

    public static boolean guildFolderStructureExists(){
        boolean exists = false;

        if (Files.exists(Main.guildRootFolder)&&Files.exists(guildPlayerFolder)&&Files.exists(guildGuildsFolder)){
            exists = true;
        }

        return exists;
    }

    public static boolean logFolderExists(){
        if (Files.exists(Paths.get(Main.plugin.getDataFolder() + "/logs"))){
            return true;
        }else {
            return false;
        }
    }

    public static void createLogFolder() throws IOException {
        Files.createDirectory(Paths.get(Main.plugin.getDataFolder() + "/logs"));
    }

    public static void createGuildFiles() throws IOException {
        Files.createDirectories(guildPlayerFolder);
        Files.createDirectory(guildGuildsFolder);
    }
    public static boolean checkForPersonalPlayerFile(UUID player){
        return Files.exists(Path.of(GuildFiles.guildPlayerFolder + "/" + player + ".json"));
    }

    public static void createPersonalPlayerFile(Player player) throws IOException {
        Files.createFile(Path.of(guildPlayerFolder + "/"+ player.getUniqueId() + ".json"));
        JSONObject rawJsonData = new JSONObject();
        rawJsonData.put("name",player.getName());
        FileWriter writeJsonData = new FileWriter(guildPlayerFolder + "/" + player.getUniqueId() + ".json");
        writeJsonData.write(JsonUtils.toPrettyJsonString(rawJsonData.toJSONString()));
        writeJsonData.close();
    }

    public static boolean checkForPlayerDataFile(Player player){
        return Files.exists(Paths.get(guildPlayerFolder + "/" + player.getUniqueId() + "/data.json"));
    }

    public static void createPlayerDataFile(Player player) throws IOException {
        JSONObject playerBlankData = new JSONObject();
        playerBlankData.put("name", player.getName());
        Files.createFile(Paths.get(guildPlayerFolder + "/" + player.getUniqueId() +".json"));
        FileWriter blankDataWriter = new FileWriter(guildPlayerFolder + "/" + player.getUniqueId() + "/data.json", StandardCharsets.UTF_8);
        blankDataWriter.write(playerBlankData.toJSONString());
        blankDataWriter.close();
    }
}
