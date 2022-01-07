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
package at.theduggy.duckguilds.guild_invite;


import at.theduggy.duckguilds.Main;
import at.theduggy.duckguilds.other.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.json.simple.parser.ParseException;

import java.io.IOException;

public class DeleteGuildInvite {
    public static void deleteInvite(Player sender, String playerName) throws IOException, ParseException {
        if (Utils.isPlayerInGuild(sender)){
            if (Utils.getIfPlayerIsHeadOfGuild(Utils.getPlayerGuild(sender), sender)){
                if (Bukkit.getPlayerExact(playerName)!=null) {
                    if (Main.guildInvites.containsKey(Utils.getPlayerGuild(sender))) {
                        if (Main.guildInvites.get(Utils.getPlayerGuild(sender)).contains(playerName)) {
                            int index = Main.guildInvites.get(Utils.getPlayerGuild(sender)).indexOf(playerName);
                            Main.guildInvites.get(Utils.getPlayerGuild(sender)).remove(index);
                            Bukkit.getPlayerExact(playerName).sendMessage(Main.prefix + ChatColor.RED + "The guild-invite to " + ChatColor.YELLOW + Utils.getPlayerGuild(sender) + ChatColor.RED + " was deleted by " + ChatColor.YELLOW + sender.getName() + ChatColor.RED + "!");
                            sender.sendMessage(Main.prefix + ChatColor.RED + "The guild-invite for " + ChatColor.YELLOW + playerName + ChatColor.RED + " was deleted!");
                        } else {
                            sender.sendMessage(Main.prefix + ChatColor.RED + "There is no guild-invite for " + ChatColor.YELLOW + playerName + ChatColor.RED + "!");
                        }
                    } else {
                        sender.sendMessage(Main.prefix + ChatColor.RED + "There is no guild-invite for " + ChatColor.YELLOW + playerName + ChatColor.RED + "!");
                    }
                }else {
                    sender.sendMessage(Main.playerDoesntExists);
                }
            }else {
                sender.sendMessage(Main.youAreNotTheHeadOfThatGuild);
            }
        }else {
            sender.sendMessage(Main.youAreNotInAGuild);
        }
    }
}
