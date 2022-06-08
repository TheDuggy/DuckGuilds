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
import at.theduggy.duckguilds.objects.GuildObject;
import at.theduggy.duckguilds.utils.GuildTextUtils;
import at.theduggy.duckguilds.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.json.simple.parser.ParseException;

import java.util.UUID;

public class GuildInviteDiscardCommand {

    public static void discardInvite(Player player, String guildName) throws ParseException {
        if (Main.getGuildCache().containsKey(guildName)){
            if (Main.getGuildCache().get(guildName).getAllInvites().containsKey(player.getUniqueId())){
                GuildInviteObject guildInvite = Main.getGuildCache().get(guildName).getAllInvites().get(player.getUniqueId());
                GuildObject guild = Main.getGuildCache().get(guildName);
                Main.getGuildCache().get(guildName).getAllInvites().remove(player.getUniqueId());
                if (Main.getPlayerCache().get(Main.getGuildCache().get(guildName).getHead()).isOnline()){
                    Bukkit.getPlayer(guild.getHead()).sendMessage(GuildTextUtils.prefix + ChatColor.RED + ChatColor.YELLOW + player.getName() + ChatColor.GREEN + " has discarded your guild-invite!");
                }
                player.sendMessage(GuildTextUtils.prefix + ChatColor.RED + "You successfully discarded the invite of " + ChatColor.YELLOW + Main.getPlayerCache().get(guildInvite.getSender().getUniqueId()).getName() + ChatColor.RED + " to " + guildName + "!" );
            }else {
                player.sendMessage(GuildTextUtils.prefix + ChatColor.RED + "You are not invited to this guild!");
            }
        }else {
            player.sendMessage(GuildTextUtils.guildDoesntExist);
        }
    }
}
