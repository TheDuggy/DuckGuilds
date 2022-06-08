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
package at.theduggy.duckguilds.commands.help;

import at.theduggy.duckguilds.utils.GuildTextUtils;
import org.bukkit.ChatColor;

public class GuildHelpCommand {

    public static String help() {
        StringBuilder msg = new StringBuilder();
        msg.append(GuildTextUtils.prefix + ChatColor.GREEN + "Help for command" + ChatColor.YELLOW + " 'help'" + ChatColor.GREEN + ":\n" + ChatColor.WHITE + "-".repeat(31));
        msg.append(ChatColor.GOLD + "\n→" + ChatColor.GRAY + " Get help to all commands! Usage: '/help <commands>'");
        return msg.toString();
    }

    public static String create() {
        StringBuilder msg = new StringBuilder();
        String nl = "\n" + " ".repeat(3);
        msg.append(GuildTextUtils.prefix + ChatColor.GREEN + "Help for command" + ChatColor.YELLOW + " 'create'" + ChatColor.GREEN + ":\n" + ChatColor.WHITE + "-".repeat(33));
        msg.append(ChatColor.GOLD + "\n→ " + ChatColor.GRAY + "Usage:" + ChatColor.YELLOW + " '/guild create <name> <color> <tag> <tagColor>'" + nl);
        //TODO Use maxGuildNameLengthVariable
        msg.append(ChatColor.GRAY + "This command creates a guild, of which you are automatically" + nl + "the head. The color must be light, the tagColor can be a" + nl + "dark color too! The tag has a max-length of 4, the name 20.");
        return msg.toString();
    }

    public static String delete() {
        StringBuilder msg = new StringBuilder();
        String nl = "\n" + " ".repeat(3);
        msg.append(GuildTextUtils.prefix + ChatColor.GREEN + "Help for command" + ChatColor.YELLOW + " 'delete'" + ChatColor.GREEN + ":\n" + ChatColor.WHITE + "-".repeat(32));
        msg.append(ChatColor.GOLD + "\n→ " + ChatColor.GRAY + "Usage:" + ChatColor.YELLOW + " '/guild delete <name> <conformation>'" + nl);
        msg.append(ChatColor.GRAY + "This command deletes your guild! You have to confirm it, so" + nl + "you don't delete your guild by mistake. In order to delete it," + nl + "you have to be the head of that guild.");
        return msg.toString();
    }

    public static String deleteInvite() {
        StringBuilder msg = new StringBuilder();
        String nl = "\n" + " ".repeat(3);
        msg.append(GuildTextUtils.prefix + ChatColor.GREEN + "Help for command" + ChatColor.YELLOW + " 'deleteInvite'" + ChatColor.GREEN + ":\n" + ChatColor.WHITE + "-".repeat(32));
        msg.append(ChatColor.GOLD + "\n→ " + ChatColor.GRAY + "Usage:" + ChatColor.YELLOW + " '/guild deleteInvite <nameOfInvitedPlayer>'" + nl);
        msg.append(ChatColor.GRAY + "With this command, you can delete a invite you sent. Logical," + nl + "this only works if you have already sent an invite to that" + nl + "player!");//TODO finished
        return msg.toString();
    }

    public static String discardInvite() {
        StringBuilder msg = new StringBuilder();
        String nl = "\n" + " ".repeat(3);
        msg.append(GuildTextUtils.prefix + ChatColor.GREEN + "Help for command" + ChatColor.YELLOW + " 'discardInvite'" + ChatColor.GREEN + ":\n" + ChatColor.WHITE + "-".repeat(38));
        msg.append(ChatColor.GOLD + "\n→ " + ChatColor.GRAY + "Usage:" + ChatColor.YELLOW + " '/guild discardInvite <nameOfGuild>'" + nl);
        msg.append(ChatColor.GRAY + "With this command, you can discard a invite you became." + nl + "Logical, this only works if you have already got an invite to" + nl + "that guild!");//TODO finished
        return msg.toString();
    }

    public static String list() {
        StringBuilder msg = new StringBuilder();
        String nl = "\n" + " ".repeat(3);
        msg.append(GuildTextUtils.prefix + ChatColor.GREEN + "Help for command" + ChatColor.YELLOW + " 'list'" + ChatColor.GREEN + ":\n" + ChatColor.WHITE + "-".repeat(30));
        msg.append(ChatColor.GOLD + "\n→ " + ChatColor.GRAY + "Usage:" + ChatColor.YELLOW + " '/guild list <page>'" + nl);
        msg.append(ChatColor.GRAY + "With this command, you can see all guilds that exist on this" + nl + "server. You can also see their size and there placement." + nl + "If you don't specify a page, it automatically takes the" + nl + "first page!");

        return msg.toString();
    }

    public static String leave() {
        StringBuilder msg = new StringBuilder();
        String nl = "\n" + " ".repeat(3);
        msg.append(GuildTextUtils.prefix + ChatColor.GREEN + "Help for command" + ChatColor.YELLOW + " 'leave'" + ChatColor.GREEN + ":\n" + ChatColor.WHITE + "-".repeat(32));
        msg.append(ChatColor.GOLD + "\n→ " + ChatColor.GRAY + "Usage:" + ChatColor.YELLOW + " '/guild leave <guild>'" + nl);
        msg.append(ChatColor.GRAY + "With this command, you can leave a guild. Logically you can" + nl + "only leave a guild you are currently in to!");
        return msg.toString();
    }

    public static String invite() {
        StringBuilder msg = new StringBuilder();
        String nl = "\n" + " ".repeat(3);
        msg.append(GuildTextUtils.prefix + ChatColor.GREEN + "Help for command" + ChatColor.YELLOW + " 'invite'" + ChatColor.GREEN + ":\n" + ChatColor.WHITE + "-".repeat(32));
        msg.append(ChatColor.GOLD + "\n→ " + ChatColor.GRAY + "Usage:" + ChatColor.YELLOW + " '/guild invite <player>'" + nl);
        msg.append(ChatColor.GRAY + "With this command, you can invite a player to your guild. This" + nl + "only works if you are the head and you haven't already" + nl + "invited the player. You receive a message if the invite gets" + nl + "discarded or is deleted after a specific amount of time/when" + nl + "the player you've invited leaves");
        return msg.toString();
    }

    public static String join() {
        StringBuilder msg = new StringBuilder();
        String nl = "\n" + " ".repeat(3);
        msg.append(GuildTextUtils.prefix + ChatColor.GREEN + "Help for command" + ChatColor.YELLOW + " 'join'" + ChatColor.GREEN + ":\n" + ChatColor.WHITE + "-".repeat(31));
        msg.append(ChatColor.GOLD + "\n→ " + ChatColor.GRAY + "Usage:" + ChatColor.YELLOW + " '/guild join <guildName>'" + nl);
        msg.append(ChatColor.GRAY + "With this command, you can join a guild! This only works if" + nl + "you've been invited to the guild and the the invited didn't get" + nl +"deleted!");
        return msg.toString();
    }

    public static String kick() {
        StringBuilder msg = new StringBuilder();
        String nl = "\n" + " ".repeat(3);
        msg.append(GuildTextUtils.prefix + ChatColor.GREEN + "Help for command" + ChatColor.YELLOW + " 'kick'" + ChatColor.GREEN + ":\n" + ChatColor.WHITE + "-".repeat(30));
        msg.append(ChatColor.GOLD + "\n→ " + ChatColor.GRAY + "Usage:" + ChatColor.YELLOW + " '/guild kick <player>'" + nl);
        msg.append(ChatColor.GRAY + "With this command, you can kick a player from your guild." + nl + "This only works if you are the head!");
        return msg.toString();
    }

    public static String info() {
        StringBuilder msg = new StringBuilder();
        String nl = "\n" + " ".repeat(3);
        msg.append(GuildTextUtils.prefix + ChatColor.GREEN + "Help for command" + ChatColor.YELLOW + " 'info'" + ChatColor.GREEN + ":\n" + ChatColor.WHITE + "-".repeat(30));
        msg.append(ChatColor.GOLD + "\n→ " + ChatColor.GRAY + "Usage:" + ChatColor.YELLOW + " '/guild info <guild> <infoType>'" + nl);
        msg.append(ChatColor.GRAY + "With this command, you can get general info/the player-list" + nl +"of a guild! To do so, specify the typ as argument!");
        return msg.toString();
    }

    public static String versionInfo() {
        StringBuilder msg = new StringBuilder();
        String nl = "\n" + " ".repeat(3);
        msg.append(GuildTextUtils.prefix + ChatColor.GREEN + "Help for command" + ChatColor.YELLOW + " 'versionInfo'" + ChatColor.GREEN + ":\n" + ChatColor.WHITE + "-".repeat(37));
        msg.append(ChatColor.GOLD + "\n→ " + ChatColor.GRAY + "Usage:" + ChatColor.YELLOW + " '/guild versionInfo'" + nl);
        msg.append(ChatColor.GRAY + "With this command, you can get the current version of the" + nl + "plugin and if it is up-to-date and a few links for download" + nl + "etc. (they don't work because the plugin is still under" + nl + "development)");
        return msg.toString();
    }


}
