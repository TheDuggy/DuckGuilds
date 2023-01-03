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
package at.theduggy.duckguilds.commands.deletSystem;

import at.theduggy.duckguilds.Main;
import at.theduggy.duckguilds.commands.leave.PlayerLeaveGuild;
import at.theduggy.duckguilds.logging.GuildLogger;
import at.theduggy.duckguilds.objects.GuildObject;
import at.theduggy.duckguilds.objects.GuildPlayerObject;
import at.theduggy.duckguilds.utils.GuildTextUtils;
import at.theduggy.duckguilds.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.UUID;

public class GuildDelete {

    public static void removeGuild(String name, Player player) throws IOException, ParseException, InterruptedException {
        if (Main.getGuildCache().keySet().contains(name)) {
            if (Utils.isPlayerInGuild(player)) {
                if (Utils.getIfPlayerIsHeadOfGuild(name,player)) {
                    GuildPlayerObject head = Main.getPlayerCache().get(Main.getGuildCache().get(name).getHead());
                    GuildObject guild = Main.getGuildCache().get(name);
                    for (Player playerFromServer : Bukkit.getOnlinePlayers()) {
                        if (Main.getPlayerCache().get(player.getUniqueId()).getGuild().equals(name)) {
                            PlayerLeaveGuild.leaveGuild(playerFromServer, name, true);
                            playerFromServer.setDisplayName(ChatColor.WHITE  + playerFromServer.getName() );
                            playerFromServer.sendMessage(GuildTextUtils.prefix +  ChatColor.RED + Main.getPlayerCache().get(Main.getGuildCache().get(name).getHead()).getName() + " deleted " + ChatColor.GOLD + name + ChatColor.RED + "!");
                        }
                    }
                    Main.getMainStorage().deleteGuildSection(Main.getGuildCache().get(name), true);
                    Main.getGuildCache().remove(name);
                    GuildLogger.getLogger().info(head.toString() + " deleted guild " + guild.toString() + "!");
                    if (head.isOnline()){
                        Bukkit.getPlayer(head.getUniqueId()).sendMessage(GuildTextUtils.prefix + ChatColor.RED + "Your guild with the name " + ChatColor.YELLOW + name + ChatColor.RED + " has been deleted!");
                    }
                } else {
                    player.sendMessage(GuildTextUtils.youAreNotTheHeadOfThatGuild);
                }
            }else {
                player.sendMessage(GuildTextUtils.youAreNotInAGuild);
            }
        }else {
            player.sendMessage(GuildTextUtils.guildDoesntExist);
        }
    }
}