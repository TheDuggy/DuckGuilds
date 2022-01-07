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
  along with this program.  If not, see <http://www.gnu.org/licenses/>.
  You can contact me on my e-mail-address: theduggy@outlook.com*///TODO add a valid e-mail-address
package at.theduggy.duckguilds;

import at.theduggy.duckguilds.files.GuildFiles;
import at.theduggy.duckguilds.other.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scoreboard.Team;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

public class PlayerJoinEventGuild implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e) throws IOException, ParseException {
        Player p = e.getPlayer();
        Path guildPlayerFolder = Paths.get(Main.guildRootFolder + "/playerData");
        Path personalPlayerGuildFolder = Paths.get(guildPlayerFolder + "/" + p.getUniqueId());
        Path playerNameFile = Paths.get(personalPlayerGuildFolder + "/name.json");

        JSONParser getPlayerNameParser = new JSONParser();
        FileReader fileReader = new FileReader(playerNameFile.toFile());
        JSONObject getPlayerName= (JSONObject) getPlayerNameParser.parse(fileReader);
        fileReader.close();
        String oldName = (String) getPlayerName.get("name");
        if (!oldName.equals(p.getName())){
            JSONObject newPlayerName= new JSONObject();
            newPlayerName.put("name", p.getName());
            FileWriter changePlayerNameInFile = new FileWriter(String.valueOf(playerNameFile),StandardCharsets.UTF_8);
            changePlayerNameInFile.write(newPlayerName.toJSONString());
            changePlayerNameInFile.close();
        }
        addToOldGuild(p);

        }

        public static void addToOldGuild(Player p) throws IOException, ParseException {
            Path guildPlayerFolder = Paths.get(Main.guildRootFolder + "/playerData");
            Path personalPlayerGuildFolder = Paths.get(guildPlayerFolder + "/" + p.getUniqueId());
            Path personalPlayerGuildTeamsFile = Paths.get(personalPlayerGuildFolder + "/guild.json");
            JSONParser jsonParser = new JSONParser();
            FileReader fileReader = new FileReader(personalPlayerGuildTeamsFile.toFile(),StandardCharsets.UTF_8);
            JSONObject getPlayerGuild = (JSONObject) jsonParser.parse(fileReader);
            fileReader.close();
            byte[] guildNameBytes = String.valueOf(getPlayerGuild.get("guild")).getBytes(StandardCharsets.UTF_8);
            String guild = new String(guildNameBytes,StandardCharsets.UTF_8);
            if (!guild.equals("")){
                //TODO Check on join if player is in guild file
                HashMap<String,Object> indexedGuild = Main.cachedGuilds.get(guild);
                try {
                    Team guildTeam = Main.scoreboard.registerNewTeam(guild);
                    ChatColor tagColor =  Utils.translateFromStringToChatColor((String) indexedGuild.get("tagColor"));
                    String tag = (String) indexedGuild.get("tag");
                    ChatColor color = Utils.translateFromStringToChatColor((String) indexedGuild.get("color"));
                    guildTeam.setSuffix(ChatColor.GRAY + "[" + tagColor+ tag + ChatColor.GRAY + "]");
                    guildTeam.setDisplayName(guild);
                    guildTeam.setColor(color);
                    guildTeam.addEntry(p.getName());
                    p.setDisplayName(color + p.getName() + ChatColor.GRAY + "[" + tagColor + tag + ChatColor.GRAY + "]" + ChatColor.WHITE);
                    for (Player player:Bukkit.getOnlinePlayers()){
                        player.setScoreboard(Main.scoreboard);
                    }
                } catch (IllegalArgumentException e){
                    ChatColor tagColor =  Utils.translateFromStringToChatColor((String) indexedGuild.get("tagColor"));
                    String tag = (String) indexedGuild.get("tag");
                    ChatColor color = Utils.translateFromStringToChatColor((String) indexedGuild.get("color"));
                    p.setDisplayName(color + p.getName() + ChatColor.GRAY + "[" + tagColor + tag + ChatColor.GRAY + "]" + ChatColor.WHITE);
                    Team guildTeam= Main.scoreboard.getTeam(guild);
                    guildTeam.addEntry(p.getName());
                    for (Player player:Bukkit.getOnlinePlayers()){
                        player.setScoreboard(Main.scoreboard);
                    }
                }
            }else {
                p.setScoreboard(Main.scoreboard);
            }

    }

}