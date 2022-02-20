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
import at.theduggy.duckguilds.leaveGuild.PlayerLeaveGuild;
import at.theduggy.duckguilds.other.Utils;
import at.theduggy.duckguilds.storage.Storage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.json.simple.parser.ParseException;

import java.io.*;

public class GuildDelete {

    public static void removeGuild(String name, Player player) throws IOException, ParseException, InterruptedException {
        if (Utils.guildExists(name)) {
            if (Utils.isPlayerInGuild(player)) {
                if (Utils.getIfPlayerIsHeadOfGuild(name,player)) {
                    for (Player playerFromServer : Bukkit.getOnlinePlayers()) {
                        if (Utils.getPlayerGuild(playerFromServer).equals(name)) {
                            if (playerFromServer.isOnline()) {
                                if (Utils.getIfPlayerIsHeadOfGuild(name, playerFromServer)) {
                                    playerFromServer.sendMessage(Main.prefix + ChatColor.GREEN + "Your guild with the name " + ChatColor.YELLOW + name + ChatColor.GREEN + " has been deleted!");
                                } else {
                                    playerFromServer.sendMessage(Main.guildHeadLeftGuild);
                                }
                            }
                            PlayerLeaveGuild.leaveGuild(playerFromServer, name);
                            playerFromServer.setDisplayName(ChatColor.WHITE  + playerFromServer.getName() );
                        }
                    }
                    Storage.deleteGuildField(name);
                    Main.getGuildCache().remove(name);
                } else {
                    player.sendMessage(Main.youAreNotTheHeadOfThatGuild);
                }
            }
        }else {
            player.sendMessage(Main.guildDoesntExists);
        }
    }
}