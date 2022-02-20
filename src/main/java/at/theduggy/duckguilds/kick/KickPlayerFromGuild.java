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
package at.theduggy.duckguilds.kick;


import at.theduggy.duckguilds.Main;
import at.theduggy.duckguilds.leaveGuild.PlayerLeaveGuild;
import at.theduggy.duckguilds.other.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.json.simple.parser.ParseException;

import java.io.IOException;

public class KickPlayerFromGuild {

    public static void kickPlayerFromGuild( Player sender, Player player) throws IOException, ParseException {
        if (Utils.isPlayerInGuild(sender)) {
            String guildName = Utils.getPlayerGuild(sender);
            String guildNameOfPlayerToKick = Utils.getPlayerGuild(player);
            if (Utils.getIfPlayerIsHeadOfGuild(guildName, sender)) {
                if (sender.getName().equals(player.getName())) {
                    if (guildName.equals(guildNameOfPlayerToKick)) {
                        PlayerLeaveGuild.leaveGuild(player, guildName);
                        player.sendMessage(Main.prefix  + ChatColor.RED + "You were kicked from the guild " + ChatColor.YELLOW + guildNameOfPlayerToKick + ChatColor.RED + " by " + ChatColor.YELLOW + sender.getName());
                        for (Player playerFromServer : Bukkit.getOnlinePlayers()) {
                            if (Utils.getPlayerGuild(playerFromServer).equals(guildNameOfPlayerToKick)) {
                                if (Utils.getIfPlayerIsHeadOfGuild(guildNameOfPlayerToKick, sender)) {
                                    playerFromServer.sendMessage(Main.prefix + ChatColor.RED + "The player " + ChatColor.YELLOW + playerFromServer.getName() + ChatColor.RED + " has been kicked from your guild!");
                                } else {
                                    playerFromServer.sendMessage(Main.prefix + ChatColor.RED + "The head of your guild, " + ChatColor.YELLOW + sender.getName() + ChatColor.RED + " has kicked " + ChatColor.YELLOW + player.getName() + ChatColor.RED + " from your guild!");
                                }
                            }
                        }
                    } else {
                        sender.sendMessage(Main.prefix + ChatColor.RED + "You are not in the same guild as this player!");
                    }
                }else {
                    sender.sendMessage(Main.prefix + ChatColor.RED + "You can't kick your self!");
                }

            } else {
                sender.sendMessage(Main.youAreNotTheHeadOfThatGuild);
            }
        }else {
            sender.sendMessage(Main.youAreNotInAGuild);
        }
    }
}
