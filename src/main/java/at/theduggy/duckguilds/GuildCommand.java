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
package at.theduggy.duckguilds;

import at.theduggy.duckguilds.creatGuild.GuildCreate;
import at.theduggy.duckguilds.deletSystem.DeleteGuild;
import at.theduggy.duckguilds.guild_invite.DeleteGuildInvite;
import at.theduggy.duckguilds.guild_invite.GuildInviteCommand;
import at.theduggy.duckguilds.guild_invite.GuildInviteDiscardCommand;
import at.theduggy.duckguilds.guild_invite.GuildJoinCommand;
import at.theduggy.duckguilds.kick.KickPlayerFromGuild;
import at.theduggy.duckguilds.leaveGuild.PlayerLeaveGuild;
import at.theduggy.duckguilds.listGuilds.ListGuilds;
import at.theduggy.duckguilds.other.Utils;
import at.theduggy.duckguilds.help.GuildHelpCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GuildCommand implements TabExecutor {

    public static List<String> allColorsForColor = new ArrayList<>();
    public static List<String> allColorsForColorAndDark = new ArrayList<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            if (args.length != 0) {
                if (args[0].equals("help")){
                    if ((args.length==2&&args[1].equals("1"))||args.length==1){
                        sender.sendMessage(GuildHelpCommand.page1());
                    }else if (args.length==2&&args[1].equals("2")) {
                        sender.sendMessage(GuildHelpCommand.page2());
                    }else if (args.length==2&&args[1].equals("3")) {
                            sender.sendMessage(GuildHelpCommand.page3());
                    }else {
                        sender.sendMessage(Main.wrongUsage);
                    }

                }else if (args[0].equals("create")){
                    if (args.length==5) {
                        String name;
                        String color;
                        String tag;
                        String tagColor;
                        if (!args[1].equals("")) {
                            if (!args[2].equals("") && allColorsForColor.contains(args[2])) {
                                if (!args[3].equals("")) {
                                    if (!args[4].equals("") && allColorsForColorAndDark.contains(args[4])) {

                                            try {

                                                name = args[1];
                                                color = args[2];
                                                tag = args[3];
                                                tagColor = args[4];
                                                    GuildCreate.createGuild((Player) sender,Utils.translateFromReadableStringToChatColorLightColors(color), name, tag, Utils.translateFromReadableStringToChatColorAllColors(tagColor));

                                            } catch (IOException | ParseException e) {
                                                e.printStackTrace();
                                            }

                                    } else {
                                        sender.sendMessage(Main.wrongUsage);
                                    }
                                } else {
                                    sender.sendMessage(Main.wrongUsage);
                                }
                            } else {
                                sender.sendMessage(Main.wrongUsage);
                            }
                        } else {
                            sender.sendMessage(Main.wrongUsage);
                        }
                    }else {
                        sender.sendMessage(Main.wrongUsage);
                    }
                }else if (args[0].equals("list")) {
                    try {
                        if (args.length==1) {
                            ListGuilds.listGuilds((Player) sender);
                        }else {
                            sender.sendMessage(Main.wrongUsage);
                        }
                    } catch (IOException | ParseException e) {
                        e.printStackTrace();
                    }
                }else if (args[0].equals("leave")){
                    try {
                        if (args.length==2) {
                            if (!Utils.getIfPlayerIsHeadOfGuild(args[1], (Player) sender)) {
                                PlayerLeaveGuild.leaveGuild((Player) sender, args[1]);
                            } else if (Utils.getIfPlayerIsHeadOfGuild(args[1], (Player) sender)) {
                                sender.sendMessage(Main.youAreTheHeadOfThatGuild);
                            }
                        }else {
                            sender.sendMessage(Main.wrongUsage);
                        }
                    } catch (IOException | ParseException e) {
                        e.printStackTrace();
                    }


                }else if (args[0].equals("delete")){
                    try {

                            if (args.length == 3) {


                                    if (args[2].equals("-y")) {
                                        DeleteGuild.removeGuild(args[1], (Player) sender);
                                    } else if (args[2].equals("-n")) {
                                        sender.sendMessage(Main.prefix + ChatColor.RED + "To delete your guild use /guild delete -y!");
                                    } else {
                                        sender.sendMessage(Main.forbiddenArgument);
                                    }


                            } else {
                                sender.sendMessage(Main.wrongUsage);
                            }

                    } catch (IOException | ParseException | InterruptedException e) {
                        e.printStackTrace();
                    }
                }else if (args[0].equals("invite")) {

                    try {
                        if (args.length == 2) {
                            if (Utils.isPlayerInGuild((Player) sender)) {
                                try {
                                    Player player = (Player) sender;
                                    if (Utils.getIfPlayerIsHeadOfGuild(Utils.getPlayerGuild(player), player)) {
                                        if (Bukkit.getServer().getPlayerExact(args[1]) != null) {
                                            if (!Bukkit.getPlayerExact(args[1]).equals(player)) {
                                                GuildInviteCommand.guildInviteCommand(player, args[1], Utils.getPlayerGuild(player));
                                            } else {
                                                sender.sendMessage(Main.prefix + ChatColor.RED + "You can't invite yourself!");
                                            }
                                        } else {
                                            sender.sendMessage(Main.prefix + ChatColor.RED + "This player doesn't exist or isn't online!");
                                        }
                                    } else {
                                        sender.sendMessage(Main.youAreNotTheHeadOfThatGuild);
                                    }
                                } catch (IOException | ParseException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                sender.sendMessage(Main.youAreNotInAGuild);
                            }
                        } else {
                            sender.sendMessage(Main.wrongUsage);
                        }
                    } catch (IOException | ParseException e) {
                        e.printStackTrace();
                    }
                }else if (args[0].equals("join")){
                    if (args.length==2){
                        try {
                            GuildJoinCommand.inviteReceive((Player) sender, args[1]);
                        } catch (IOException | ParseException e) {
                            e.printStackTrace();
                        }
                    }else {
                        sender.sendMessage(Main.wrongUsage);
                    }
                }else if (args[0].equals("discardInvite")){
                    if (args.length==2){
                        try {
                            GuildInviteDiscardCommand.discardInvite((Player) sender, args[1]);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }else {
                        sender.sendMessage(Main.wrongUsage);
                    }
                }else  if(args[0].equals("kick")){
                    if (args.length==3) {
                        if (args[2].equals("-y")) {
                            try {
                                KickPlayerFromGuild.kickPlayerFromGuild((Player) sender, Bukkit.getPlayerExact(args[1]));
                            } catch (IOException | ParseException e) {
                                e.printStackTrace();
                            }
                        }else {
                            sender.sendMessage(Main.wrongUsage);
                            //TODO Change msg!
                        }
                    }else {
                        sender.sendMessage(Main.wrongUsage);
                    }
                }else if (args[0].equals("deleteInvite")){
                    if (args.length==2) {
                        try {
                            DeleteGuildInvite.deleteInvite((Player) sender, args[1]);
                        } catch (IOException | ParseException e) {
                            e.printStackTrace();
                        }
                    }else {
                        sender.sendMessage(Main.wrongUsage);
                    }
                }else {
                    sender.sendMessage(Main.wrongUsage);
                }
            } else {
                sender.sendMessage(Main.wrongUsage);
            }
        } else {
            sender.sendMessage("You are not a player! Use /guild help for all options!");
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

        if (args.length==1){
            List<String> complete = new ArrayList<>();
            complete.add("help");
            complete.add("create");
            complete.add("list");
            complete.add("leave");
            complete.add("delete");
            complete.add("invite");
            complete.add("join");
            complete.add("discardInvite");
            complete.add("deleteInvite");
            complete.add("kick");
            return complete;
        }else if (args.length==2 &&args[0].equals("create")){
            List<String> complete2 = new ArrayList<>();
            return complete2;

        }else if (args.length==3&& args[0].equals("create")){
            List<String> colors = new ArrayList<>();
            colors.add("Blue");
            colors.add("White");
            colors.add("Aqua");
            colors.add("Gold");
            colors.add("Green");
            colors.add("Red");
            colors.add("Yellow");
            return colors;
        }else if (args.length==4){
            List<String> tag = new ArrayList<>();
            return tag;
        }else if (args.length==5&&args[0].equals("create")){
            List<String> tagColors = new ArrayList<>();
            tagColors.add("Blue");
            tagColors.add("White");
            tagColors.add("Aqua");
            tagColors.add("Gold");
            tagColors.add("Green");
            tagColors.add("Red");
            tagColors.add("Yellow");
            tagColors.add("Dark_Blue");
            tagColors.add("Dark_Purple");
            tagColors.add("Dark_Aqua");
            tagColors.add("Dark_Green");
            tagColors.add("Dark_Red");

            return tagColors;
        }else if (args.length==2&&args[0].equals("leave")){
            List<String> guilds = new ArrayList<>();
            try {
                guilds.add(Utils.getPlayerGuild((Player) sender));
            } catch (IOException | ParseException e) {
                e.printStackTrace();
            }
            return guilds;
        }else if (args.length>2&&args[0].equals("leave")){
            List<String> nothing = new ArrayList<>();
            return nothing;
        }else if(args.length==2&&args[0].equals("delete")) {
            List<String> guilds = new ArrayList<>();
            try {
                if (Utils.isPlayerInGuild((Player) sender)) {
                        if (Utils.getIfPlayerIsHeadOfGuild(Utils.getPlayerGuild((Player) sender), (Player) sender)) {
                            try {
                                guilds.add(Utils.getPlayerGuild((Player) sender));
                            } catch (IOException | ParseException e) {
                                e.printStackTrace();
                            }
                        }

                }
            } catch (IOException | ParseException e) {
                e.printStackTrace();
            }
            return guilds;
        }else if (args[0].equals("delete")&&args.length>3){
            List<String> nothing = new ArrayList<>();
            return nothing;
        }else if(args.length>5) {
            List<String> nothing = new ArrayList<>();
            return nothing;
        }else if(args[0].equals("invite")&&args.length==2) {
            try {
                List<String> players = new ArrayList<>(Utils.getPlayersThatArentInAGuild());
                return players;
            } catch (IOException | ParseException e) {
                e.printStackTrace();
            }
        }else if (args.length>2&&args[0].equals("invite")) {
            List<String> nothing = new ArrayList<>();
            return nothing;
        }else if(args[0].equals("join")&&args.length==2) {
            List<String> guildInvites = new ArrayList<>(Utils.getPlayerGuildInvites((Player) sender));
            return guildInvites;
        }else if (args.length>2&&args[0].equals("join")) {
            List<String> nothing = new ArrayList<>();
            return nothing;
        }else if (args.length==2&&args[0].equals("kick")) {
            try {
                if (Utils.isPlayerInGuild((Player) sender)) {
                    if (Utils.getIfPlayerIsHeadOfGuild(Utils.getPlayerGuild((Player) sender), (Player) sender)) {
                        ArrayList<String> instance = Utils.getAllPlayerGuildInvitesForAGuild(Utils.getPlayerGuild((Player) sender));

                        return instance;
                    }else {
                        List<String> nothing = new ArrayList<>();
                        return nothing;
                    }
                }else {
                    List<String> nothing = new ArrayList<>();
                    return nothing;
                }
            } catch (IOException | ParseException e) {
                e.printStackTrace();
            }
        }else if (args.length==3&&args[0].equals("kick")) {
            try {

                if (Utils.isPlayerInGuild((Player) sender)) {
                    if (Utils.getIfPlayerIsHeadOfGuild(Utils.getPlayerGuild((Player) sender), (Player) sender)) {
                        List<String> no_yes = new ArrayList<>();
                        no_yes.add("-n");
                        no_yes.add("-y");
                        return no_yes;
                    } else {
                        List<String> nothing = new ArrayList<>();
                        return nothing;
                    }
                } else {
                    List<String> nothing = new ArrayList<>();
                    return nothing;
                }
            } catch (IOException | ParseException e) {
                e.printStackTrace();
            }
        }else if (args[0].equals("help")){
            if (args.length==2){
                List<String> pages = new ArrayList<>();
                pages.add("1");
                pages.add("2");
                pages.add("3");
                return pages;
            }else if (args.length>2){
                List<String> nothing = new ArrayList<>();
                return nothing;
            }
        }else if(args[0].equals("list")){
            List<String> nothing = new ArrayList<>();
            if (args.length==2) {
                return nothing;
            }else if (args.length==3){
                return nothing;
            }else {
                return nothing;
            }
        }else if (args.length==2&&args[0].equals("deleteInvite")) {
            try {
                if (Utils.isPlayerInGuild((Player) sender)) {
                    if (Utils.getIfPlayerIsHeadOfGuild(Utils.getPlayerGuild((Player) sender), (Player) sender)) {
                        return Utils.getAllPlayerGuildInvitesForAGuild(Utils.getPlayerGuild((Player) sender));
                    } else {
                        List<String> nothing = new ArrayList<>();
                        return nothing;
                    }
                } else {
                    List<String> nothing = new ArrayList<>();
                    return nothing;
                }
            } catch (IOException | ParseException e) {
                e.printStackTrace();
            }
        }else if (args.length>2&&args[0].equals("deleteInvite")) {
            List<String> nothing = new ArrayList<>();
            return nothing;
        }else if (args.length==2&&args[0].equals("help")){
            List<String> nothing = new ArrayList<>();
            return nothing;
        }else {
            try {
                if (args.length == 3 && args[0].equals("delete")) {
                   if (Utils.isPlayerInGuild((Player) sender)){
                        if (Utils.getIfPlayerIsHeadOfGuild(Utils.getPlayerGuild((Player) sender), (Player) sender)){
                            List<String> no_yes = new ArrayList<>();
                            no_yes.add("-n");
                            no_yes.add("-y");
                            return no_yes;
                        }
                    }
                }
            } catch (IOException | ParseException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
