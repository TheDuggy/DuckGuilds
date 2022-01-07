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
package at.theduggy.duckguilds.help;

import at.theduggy.duckguilds.other.Utils;
import org.bukkit.ChatColor;

public class GuildHelpCommand {

    public static String page1 (){
        StringBuilder msg = new StringBuilder();
        msg.append(Utils.centerText(ChatColor.GRAY + "        [" + ChatColor.YELLOW + "Guild-System" + ChatColor.GRAY  + "]"+ ChatColor.WHITE + "\n"));
        msg.append(Utils.centerText(ChatColor.BLUE + "" + ChatColor.BOLD + ""  + ChatColor.YELLOW + "" + ChatColor.BOLD + "" + ChatColor.UNDERLINE +"Help" + ChatColor.BLUE + "" + ChatColor.BOLD ) + "\n    \n");
        msg.append(Utils.centerText(ChatColor.GRAY + "" + ChatColor.ITALIC + "Usage: /guild help <page> (There are 3 pages!)" + "\n"));
        msg.append(ChatColor.RED + "/guild help: " + ChatColor.WHITE + "   Show this list!\n");
        msg.append(ChatColor.RED + "/guild create: " + ChatColor.WHITE + "This command creates a guild! You need to use it                  like in this example:\n "  +ChatColor.YELLOW +  "/guild create <guildName> <guildColor> <guildTag> <tagColor>\n" +ChatColor.WHITE  + "                  You are automatically the head.The tag can't                        contain unicode-symbols!\n");
        return msg.toString();
    }
    public static String page2(){
        StringBuilder msg = new StringBuilder();
        msg.append(Utils.centerText(ChatColor.GRAY + "[" + ChatColor.YELLOW + "2" + ChatColor.GRAY + "]" + "\n"));
        msg.append(ChatColor.RED + "/guild list: " + ChatColor.WHITE + "    Lists all guilds on this server!\n");
        msg.append(ChatColor.RED + "/guild leave: " + ChatColor.WHITE + " Command to leave a guild! If you are the head,                      you can only delete it!\n");
        msg.append(ChatColor.RED + "/guild delete: " + ChatColor.WHITE + "Deletes a guild! All players would be kicked!\n");
        msg.append(ChatColor.RED + "/guild invite: " + ChatColor.WHITE + " Command to invite a player! This player would get                  a clickable message!\n");
        msg.append(ChatColor.RED + "/guild join: " + ChatColor.WHITE + "  There are 2 ways to join a guild if you are                         invited: click on the message(join) or run this                       command!\n");//"               "
        return msg.toString();
    }
    public static String page3(){
        StringBuilder msg = new StringBuilder();
        msg.append(Utils.centerText(ChatColor.GRAY + "[" + ChatColor.YELLOW + "3" + ChatColor.GRAY + "]" + "\n"));
        msg.append(ChatColor.RED+ "/guild kick: " + ChatColor.WHITE + "            To kick a player from your guild, if you                               are the head, run this command!\n");
        msg.append(ChatColor.RED + "/guild deleteInvite: " + ChatColor.WHITE + "  If you want to delete an invite you sent to                           a player, run this command!\n");
        msg.append(ChatColor.RED + "/guild discardInvite: " + ChatColor.WHITE + "There are 2 ways to discard a                                         guild-invite: click on the message(discard)                            or run this command!\n   \n   \n");
        return msg.toString();
    }
}
