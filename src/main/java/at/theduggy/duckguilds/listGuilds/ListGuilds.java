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
import at.theduggy.duckguilds.other.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.json.simple.parser.ParseException;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class ListGuilds {

    public static void listGuilds(Player player, int page) throws IOException, ParseException {
        if (Main.getGuildCache().size()==0){
            player.sendMessage(Main.prefix + ChatColor.RED + "There are no guilds on this server!" );
        }else {
            if (page>0) {
                int pageCount = (int) Math.ceil((double) Main.getGuildCache().size() / 8.0);
                if (page<=pageCount) {
                    HashMap<Integer,ArrayList<String>> guildPages = new HashMap<>();
                    ArrayList<String> keys = new ArrayList<>(Main.getGuildCache().keySet());
                    ArrayList<Integer> values = new ArrayList<>();
                    for (String key : keys) {
                        ArrayList<UUID> players = (ArrayList<UUID>) Main.getGuildCache().get(key).get("players");
                        values.add(players.size());
                    }
                    for (int i =0;i<values.size();i++){
                        for (int j = values.size()-1;j>=i;j--){
                            if (values.get(i)<values.get(j)){
                                int temp = values.get(j);
                                values.set(j,values.get(i));
                                values.set(i,temp);
                                String tempString = keys.get(j);
                                keys.set(j,keys.get(i));
                                keys.set(i,tempString);
                            }
                        }
                    }
                    int pages = (int) Math.ceil((double) Main.getGuildCache().keySet().size()/8.0);
                    int lastCheckPoint = 0;
                    for (int i = 1; i<=pages; i++,lastCheckPoint+=8){
                        guildPages.put(i,new ArrayList<>());
                        for (int i2 = lastCheckPoint; i2 !=lastCheckPoint+8&&i2!=keys.size(); i2++){
                            guildPages.get(i).add(keys.get(i2));
                        }
                    }
                    //TODO Sort guilds after size!
                    StringBuilder msg = new StringBuilder();
                    msg.append(Main.prefix  + ChatColor.GREEN + "List of all guilds from server " + ChatColor.GRAY + "[" + ChatColor.AQUA + page + ChatColor.GRAY + "/"+ pages + "]\n");
                    msg.append(ChatColor.WHITE + "-".repeat(39) + "\n");//60
                    for (String guildName:guildPages.get(page)){
                        ChatColor color = (ChatColor) Main.getGuildCache().get(guildName).get("color");
                        ChatColor tagColor = (ChatColor) Main.getGuildCache().get(guildName).get("tagColor");
                        String tag = (String) Main.getGuildCache().get(guildName).get("tag");
                        Bukkit.getLogger().warning(tag);
                        ArrayList<UUID> guildPlayers = (ArrayList<UUID>) Main.getGuildCache().get(guildName).get("players");
                        msg.append(ChatColor.GRAY + "   - " + color + guildName + ChatColor.GRAY + " [" + tagColor + tag + ChatColor.GRAY + "] " +  guildPlayers.size() + "\n");//TODO Test sorting after guild-size!
                    }
                    player.sendMessage(msg.toString());
                }else {
                    player.sendMessage(Main.pageIndexOutOfBounds);
                }
            }else {
                player.sendMessage(Main.pageIndexOutOfBounds);
            }
        }
    }
}

