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
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class GuildFiles {
    public static Path guildPlayerFolder = Paths.get(Main.guildRootFolder.toString() + "/playerData");
    public static Path guildGuildsFolder = Paths.get(Main.guildRootFolder.toString() + "/guilds");

    public static boolean guildFolderStructureExists(){
        boolean exists = false;

        if (Files.exists(Main.guildRootFolder)&&Files.exists(guildPlayerFolder)&&Files.exists(guildGuildsFolder)){
            exists = true;
        }

        return exists;
    }

    public static void createGuildFiles() throws IOException {
        Files.createDirectories(guildPlayerFolder);
        Files.createDirectory(guildGuildsFolder);
    }
    public static boolean checkForPersonalPlayerGuildFolder(Player p){
        boolean exists = false;
        Path personalPlayerGuildFolder = Paths.get(guildPlayerFolder + "/" + p.getUniqueId());
        if (Files.exists(personalPlayerGuildFolder)){
            exists = true;
        }
        return exists;
    }

    public static boolean checkForPersonalPlayerGuildTeamsFile(Player p){
        boolean exists = false;
        Path personalPlayerGuildFolder = Paths.get(guildPlayerFolder + "/" + p.getUniqueId());
        Path personalPlayerGuildTeamsFile = Paths.get(personalPlayerGuildFolder + "/guild.json");
        if (Files.exists(personalPlayerGuildTeamsFile)){
            exists = true;
        }
        return exists;
    }

    public static boolean checkForPlayerNameFile(Player p){
        boolean exists = false;
        Path personalPlayerGuildFolder = Paths.get(guildPlayerFolder + "/" + p.getUniqueId());
        Path playerNameFile = Paths.get(personalPlayerGuildFolder + "/name.json");
        if (Files.exists(playerNameFile)){
         exists = true;
        }
        return exists;
    }
    public static void createPersonalPlayerGuildFolder(Player p) throws IOException {
        Path personalPlayerGuildFolder = Paths.get(guildPlayerFolder + "/" + p.getUniqueId());
        Files.createDirectory(personalPlayerGuildFolder);
    }

    public static void createPersonalPlayerGuildTeamsFile(Player p) throws IOException {
        Path personalPlayerGuildFolder = Paths.get(guildPlayerFolder + "/" + p.getUniqueId());
        Path personalPlayerGuildTeamsFile = Paths.get(personalPlayerGuildFolder + "/guild.json");
        Files.createFile(personalPlayerGuildTeamsFile);
        JSONObject guildForGuildFile = new JSONObject();
        guildForGuildFile.put("guild","");
        FileWriter guildFileWriter = new FileWriter(String.valueOf(personalPlayerGuildTeamsFile),StandardCharsets.UTF_8);
        guildFileWriter.write(guildForGuildFile.toJSONString());
        guildFileWriter.close();
    }

    public static void createPlayerNameFile(Player p) throws IOException {
        Path personalPlayerGuildFolder = Paths.get(guildPlayerFolder + "/" + p.getUniqueId());
        Path playerNameFile = Paths.get(personalPlayerGuildFolder + "/name.json");
        Files.createFile(playerNameFile);
        FileWriter nameFileWriter = new FileWriter(String.valueOf(playerNameFile), StandardCharsets.UTF_8);
        JSONObject name = new JSONObject();
        name.put("name",p.getName());
        nameFileWriter.write(name.toJSONString());
        nameFileWriter.close();
    }

    public static boolean checkForIndex(){
        boolean exists = false;
        Path guildIndexFile = Paths.get(Main.guildRootFolder + "/index.json");
        if (Files.exists(guildIndexFile)){
            exists = true;
        }
        return exists;
    }

    public static void createIndexFile() throws IOException {
        Path guildIndexFile = Paths.get(Main.guildRootFolder + "/index.json");
        ArrayList<String> dummyList = new ArrayList<>();
        Files.createFile(guildIndexFile);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("guilds",dummyList);
        FileWriter fileWriter = new FileWriter(String.valueOf(guildIndexFile));
        fileWriter.write(jsonObject.toJSONString());
        fileWriter.close();
    }
}
