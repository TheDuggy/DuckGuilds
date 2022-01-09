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
package at.theduggy.duckguilds.listGuilds;

import at.theduggy.duckguilds.Main;
import at.theduggy.duckguilds.config.GuildsConfig;
import at.theduggy.duckguilds.files.GuildFiles;
import at.theduggy.duckguilds.other.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class ListGuilds {

    public static void listGuilds(Player p) throws IOException, ParseException {
        if (Main.cachedGuilds.size()==0){
            p.sendMessage(Main.prefix + ChatColor.RED + "There are no guilds on this server!" );
        }else { ;
            ArrayList<String> keys = new ArrayList<>(Main.cachedGuilds.keySet());
            StringBuilder msg = new StringBuilder();
            msg.append(Utils.centerText(ChatColor.GRAY + "        [" + ChatColor.YELLOW + "Guild-System" + ChatColor.GRAY  + "]"+ ChatColor.WHITE + "\n"));
            msg.append(Utils.centerText(ChatColor.BLUE + "" + ChatColor.BOLD + ""  + ChatColor.MAGIC+ "wa" + ChatColor.YELLOW + "" + ChatColor.BOLD + "" + ChatColor.UNDERLINE +"Guilds" + ChatColor.BLUE + "" + ChatColor.BOLD + ""  + ChatColor.MAGIC + "wa")).append("\n  \n");
            for (String s : keys) {
                HashMap<String,Object> guildInfos = Main.cachedGuilds.get(s);
                Bukkit.broadcastMessage(guildInfos.toString());
                ChatColor color = Utils.translateFromStringToChatColor((String) guildInfos.get("color"));
                String line = Utils.centerText(  color  + "" + ChatColor.BOLD+ s + ":");
                String optionalPlayerRole = "";
                if (Utils.getPlayerGuild(p).equals(s)) {
                    optionalPlayerRole = Utils.centerText(ChatColor.RED + "" + ChatColor.BOLD + "YOU");
                }
                String players = Utils.centerText(ChatColor.GREEN + "Player-count: " + ChatColor.YELLOW + Utils.getGuildSize(s));
                ChatColor tagColor = Utils.translateFromStringToChatColor((String) guildInfos.get("tagColor"));
                String tag = Utils.centerText(ChatColor.GREEN + "Tag: " + tagColor + guildInfos.get("tag"));
                Path guildPlayerFolder = Paths.get(Main.guildRootFolder + "/playerData");
                Path headPlayerGuildFolder = Paths.get(guildPlayerFolder + "/" + guildInfos.get("head"));
                Path playerNameData = Paths.get(headPlayerGuildFolder + "/data.json");
                String name;
                if (Files.exists(playerNameData)){
                    name = (String) Main.cachedPlayers.get(guildInfos.get("head")).get("name");//TODO Change all UUID-objects to strings
                }else{
                    name = ChatColor.RED + "NOT FOUND";
                }
                String head = Utils.centerText(ChatColor.GREEN + "Head: " + ChatColor.YELLOW + name);

                msg.append(line).append("\n");
                if (!optionalPlayerRole.equals("")){
                    msg.append(optionalPlayerRole).append("\n");
                }
                msg.append(players).append("\n");
                msg.append(tag).append("\n");
                msg.append(head).append("\n\n");
            }
            p.sendMessage(msg.toString());
        }

        }

    }

