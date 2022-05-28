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

    //                complete.add("list");
    //                complete.add("leave");
    //                complete.add("invite");
    //                complete.add("join");
    //                complete.add("kick");
    //                complete.add("info");
    //                Collections.sort(complete);

    //TODO Finish help-command

    public static String help (){
        StringBuilder msg = new StringBuilder();
        msg.append(GuildTextUtils.prefix + ChatColor.GREEN + "Help for command"+ChatColor.YELLOW +" 'help'" + ChatColor.GREEN + ":\n" + ChatColor.WHITE + "-".repeat(31));
        msg.append( ChatColor.GOLD + "\n→" + ChatColor.GRAY + " Get help to all commands! Usage: '/help <commands>'");
        return msg.toString();
    }

    public static String create(){
        StringBuilder msg = new StringBuilder();
        String nl = "\n" + " ".repeat(3);
        msg.append(GuildTextUtils.prefix + ChatColor.GREEN + "Help for command"+ChatColor.YELLOW +" 'create'" + ChatColor.GREEN + ":\n" + ChatColor.WHITE + "-".repeat(33));
        msg.append(ChatColor.GOLD + "\n→ " + ChatColor.GRAY + "Usage:" + ChatColor.YELLOW + " '/guild create <name> <color> <tag> <tagColor>'" + nl);
        //TODO Use maxGuildNameLengthVariable
        msg.append(ChatColor.GRAY + "This command creates a guild, of which you are automatically" + nl + "the head. The color must be light, the tagColor can be a" + nl + "dark color too! The tag has a max-length of 4, the name 20.");
        return msg.toString();
    }

    public static String delete(){
        StringBuilder msg = new StringBuilder();
        String nl = "\n" + " ".repeat(3);
        msg.append(GuildTextUtils.prefix + ChatColor.GREEN + "Help for command"+ChatColor.YELLOW +" 'delete'" + ChatColor.GREEN + ":\n" + ChatColor.WHITE + "-".repeat(32));
        msg.append(ChatColor.GOLD + "\n→ " + ChatColor.GRAY + "Usage:" + ChatColor.YELLOW + " '/guild delete <name> <conformation>'" + nl);
        msg.append(ChatColor.GRAY + "This command deletes your guild! You have to confirm it, so" + nl + "you don't delete your guild by mistake. In order to delete it," + nl +"you have to be the head of that guild.");
        return msg.toString();
    }

    public static String deleteInvite(){
        StringBuilder msg = new StringBuilder();
        String nl = "\n" + " ".repeat(3);
        msg.append(GuildTextUtils.prefix + ChatColor.GREEN + "Help for command"+ChatColor.YELLOW +" 'deleteInvite'" + ChatColor.GREEN + ":\n" + ChatColor.WHITE + "-".repeat(32));
        msg.append(ChatColor.GOLD + "\n→ " + ChatColor.GRAY + "Usage:" + ChatColor.YELLOW + " '/guild deleteInvite <nameOfInvitedPlayer>'" + nl);
        msg.append(ChatColor.GRAY +  "With this command, you can delete a invite you sent. Logical," + nl +"this only works if you have already sent an invite to that" + nl + "player!");//TODO finished
        return msg.toString();
    }

    public static String discardInvite(){
        StringBuilder msg = new StringBuilder();
        String nl = "\n" + " ".repeat(3);
        msg.append(GuildTextUtils.prefix + ChatColor.GREEN + "Help for command"+ChatColor.YELLOW +" 'discardInvite'" + ChatColor.GREEN + ":\n" + ChatColor.WHITE + "-".repeat(38));
        msg.append(ChatColor.GOLD + "\n→ " + ChatColor.GRAY + "Usage:" + ChatColor.YELLOW + " '/guild discardInvite <nameOfGuild>'" + nl);
        msg.append(ChatColor.GRAY +  "With this command, you can discard a invite you became." + nl +"Logical, this only works if you have already got an invite to" + nl +"that guild!");//TODO finished
        return msg.toString();
    }
}
