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
package at.theduggy.duckguilds.leaveGuild;

import at.theduggy.duckguilds.Main;
import at.theduggy.duckguilds.storage.Storage;
import at.theduggy.duckguilds.storage.systemTypes.GuildFileSystem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class PlayerLeaveGuild {

    public static void leaveGuild(Player player, String name) throws IOException, ParseException {
        if (Main.getGuildCache().containsKey(name)) {
            HashMap<String, Object> playersList = Main.getGuildCache().get(name);
            ArrayList<UUID> players = (ArrayList<UUID>) playersList.get("players");
            if (players.contains(player.getUniqueId())) {
                    removePlayerFromScoreboard(player, name);
                    updatePlayerCache(player);
                    Storage.removePlayerFromGuildField(player.getUniqueId(),name);
                    player.setDisplayName(ChatColor.WHITE + "<" + player.getName() + ">");
                    reindexAndChangeFile(name,player.getUniqueId());
                    player.sendMessage(Main.prefix + ChatColor.RED + "You left the guild " + ChatColor.YELLOW + name + ChatColor.RED + "!");
            } else {
                player.sendMessage(Main.youArentInThatGuild);

            }
        }else {
            player.sendMessage(Main.guildDoesntExists);
        }
    }

    public static void updatePlayerCache(Player player) throws IOException, ParseException {
        UUID uuidFromPlayer = player.getUniqueId();
        HashMap<String,Object> tempCachedPlayerData = new HashMap<>();
        tempCachedPlayerData.put("name", Main.getPlayerCache().get(uuidFromPlayer).get("name"));
        tempCachedPlayerData.put("guild","");
        tempCachedPlayerData.put("online", Main.getPlayerCache().get(uuidFromPlayer).get("online"));
        Main.getPlayerCache().remove(uuidFromPlayer);
        Main.getPlayerCache().put(uuidFromPlayer, tempCachedPlayerData);
    }

    public static void reindexAndChangeFile(String name,UUID player) throws IOException, ParseException {
        ArrayList<UUID> oldPlayers = (ArrayList<UUID>) Main.getGuildCache().get(name).get("players");
        for (int i=0; i!=oldPlayers.size();i++){
            if (oldPlayers.get(i).equals(player)){
                ((ArrayList<?>) Main.getGuildCache().get(name).get("players")).remove(i);
                break;
            }
        }
    }
    public static void removePlayerFromScoreboard(Player player, String name){
        Team team = Main.getScoreboard().getTeam(name);
        team.removeEntry(player.getName());
        for (Player playerFromServer:Bukkit.getOnlinePlayers()){
            playerFromServer.setScoreboard(Main.getScoreboard());
        }
    }
}
