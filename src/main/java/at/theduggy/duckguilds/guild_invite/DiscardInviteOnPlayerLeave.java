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
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;

public class DiscardInviteOnPlayerLeave implements Listener {

    @EventHandler
    public void playerLeaveEvent(PlayerQuitEvent e) throws ParseException {
        Player player = e.getPlayer();
        if (Utils.getPlayerGuildInvites(player).size()!=0){
            ArrayList<String> guildInvitesOfPlayer = Utils.getPlayerGuildInvites(player);
            for (int i =0;i!= guildInvitesOfPlayer.size();i++){
                Main.guildInvites.get(guildInvitesOfPlayer.get(i)).remove(player.getName());
                if (Bukkit.getPlayer(Utils.getHeadOfGuild(guildInvitesOfPlayer.get(i))).isOnline()) {
                    Bukkit.getPlayer(Utils.getHeadOfGuild(guildInvitesOfPlayer.get(i))).sendMessage(Main.prefix + ChatColor.RED + player.getName() + "has left the server and his invite was deleted!");
                }
            }
        }
    }
}
