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
package at.theduggy.duckguilds.commands.invite;


import at.theduggy.duckguilds.Main;
import at.theduggy.duckguilds.objects.GuildObject;
import at.theduggy.duckguilds.utils.GuildTextUtils;
import at.theduggy.duckguilds.utils.ScoreboardHandler;
import at.theduggy.duckguilds.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.UUID;

public class GuildJoinCommand {

    public static void inviteReceive(Player player, String guildName) throws IOException, ParseException {

        if (!Utils.isPlayerInGuild(player)){
            if (Main.getGuildCache().containsKey(guildName)){
                GuildObject guildObject = Main.getGuildCache().get(guildName);
                if (guildObject.getAllInvites().containsKey(player.getUniqueId())){
                    Main.getGuildCache().get(guildName).getPlayers().add(player.getUniqueId());
                    Main.getMainStorage().addPlayerToGuildSection(Main.getGuildCache().get(guildName), Main.getPlayerCache().get(player.getUniqueId()));
                    Main.getPlayerCache().get(player.getUniqueId()).setGuild(guildName);
                    ScoreboardHandler.updateScoreboardAddPlayer(player, Main.getGuildCache().get(guildName));
                    player.sendMessage(GuildTextUtils.prefix + ChatColor.GREEN + "You successfully accepted the guild-invite from " + guildObject.getAllInvites().get(player.getUniqueId()).getSender().getName() + " to " + ChatColor.YELLOW + guildName + ChatColor.GREEN + "!");
                    for (UUID guildPlayer:guildObject.getPlayers()){
                        if (Main.getPlayerCache().get(guildPlayer).isOnline()&&guildPlayer!=player.getUniqueId()){
                            Bukkit.getPlayer(guildPlayer).sendMessage(GuildTextUtils.prefix + ChatColor.GREEN + ">>> " + ChatColor.YELLOW + player.getName() + ChatColor.GREEN + " has joined your guild!");
                        }
                    }
                    Main.getGuildCache().get(guildName).getAllInvites().remove(player.getUniqueId());
                }else {
                    player.sendMessage(GuildTextUtils.prefix+ ChatColor.RED + "You aren't invited to this guild or the invite expired/was deleted!");
                }
            }else {
                player.sendMessage(GuildTextUtils.guildDoesntExist);
            }
        }else {
            player.sendMessage(GuildTextUtils.playerAlreadyInGuild);
        }
    }
}
