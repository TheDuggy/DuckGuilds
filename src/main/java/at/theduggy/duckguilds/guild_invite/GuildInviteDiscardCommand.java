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

public class GuildInviteDiscardCommand {

    public static void discardInvite(Player player, String guildName) throws ParseException {
        if (Main.guildInvites.containsKey(guildName)){
            if (Main.guildInvites.get(guildName).contains(player.getName())){
                Main.guildInvites.get(guildName).remove(player.getName());
                Player head = Bukkit.getPlayer(Utils.getHeadOfGuild(guildName));
                head.sendMessage(Main.prefix + ChatColor.RED  + player.getName() + "has discarded your guild-invite!");
                player.sendMessage(Main.prefix + ChatColor.RED + "You discarded the invite of " + head.getName() + " to " + guildName + "!" );
            }else {
                player.sendMessage(Main.prefix + ChatColor.RED + "You are not invited to this guild!");
            }
        }else {
            player.sendMessage(Main.guildDoesntExists);
        }
    }
}
