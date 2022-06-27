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
import at.theduggy.duckguilds.objects.GuildInviteObject;
import at.theduggy.duckguilds.utils.GuildTextUtils;
import at.theduggy.duckguilds.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class GuildDeleteInviteOnPlayerLeave implements Listener {

    @EventHandler
    public void playerLeaveEvent(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        for (GuildInviteObject guildInviteObject:Utils.getPlayerGuildInvites(player)){
            guildInviteObject.getGuild().getAllInvites().remove(guildInviteObject.getReceiver().getUniqueId());
            if (Main.getPlayerCache().get(guildInviteObject.getGuild().getHead()).isOnline()) {
                Bukkit.getPlayerExact(Main.getPlayerCache().get(guildInviteObject.getGuild().getHead()).getName()).sendMessage(GuildTextUtils.prefix + ChatColor.RED + player.getName() + " has left the server and his invite was deleted!");
            }
        }

    }
}
