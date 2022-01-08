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
package at.theduggy.duckguilds.deletSystem;

import at.theduggy.duckguilds.Main;
import at.theduggy.duckguilds.files.GuildFiles;
import at.theduggy.duckguilds.leaveGuild.PlayerLeaveGuild;
import at.theduggy.duckguilds.other.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class DeleteGuild {

    public static void removeGuild(String name, Player p) throws IOException, ParseException, InterruptedException {
        if (Utils.guildExists(name)) {
            if (Utils.isPlayerInGuild(p)) {
                if (Utils.getIfPlayerIsHeadOfGuild(name,p)) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        if (Utils.getPlayerGuild(player).equals(name)) {
                            if (player.isOnline()) {
                                if (Utils.getIfPlayerIsHeadOfGuild(name, player)) {
                                    player.sendMessage(Main.prefix + ChatColor.GREEN + "Your guild with the name " + ChatColor.YELLOW + name + ChatColor.GREEN + " has been deleted!");
                                } else {
                                    player.sendMessage(Main.guildHeadLeftGuild);
                                }
                            }
                            PlayerLeaveGuild.leaveGuild(player, name);
                            p.setDisplayName(ChatColor.WHITE  + p.getName() );
                        }
                    }
                    removeGuildFromFiles(name);
                    unindexGuild(name);
                } else {
                    p.sendMessage(Main.youAreNotTheHeadOfThatGuild);
                }
            }
        }else {
            p.sendMessage(Main.guildDoesntExists);
        }
    }

    public static void unindexGuild(String name) {
        Main.cachedGuilds.remove(name);
    }

    public static void removeGuildFromFiles(String name) throws IOException, ParseException {
        Path guildFile = Paths.get(GuildFiles.guildGuildsFolder + "/" + name + ".json");
        Path guildIndexFile = Paths.get(Main.guildRootFolder + "/index.json");
        JSONParser jsonParser = new JSONParser();
        FileReader fileReader = new FileReader(guildIndexFile.toFile(), StandardCharsets.UTF_8);
        JSONObject guildsByIndexFile = (JSONObject) jsonParser.parse(fileReader);
        fileReader.close();
        ArrayList<String> guilds = (ArrayList<String>) guildsByIndexFile.get("guilds");
        guilds.remove(name);
        guildsByIndexFile.remove("guilds");
        guildsByIndexFile.put("guilds",guilds);
        FileWriter fileWriter = new FileWriter(guildIndexFile.toFile(), StandardCharsets.UTF_8);
        fileWriter.write(guildsByIndexFile.toJSONString());
        fileWriter.close();
        Files.deleteIfExists(guildFile);
    }
}