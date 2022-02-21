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
import at.theduggy.duckguilds.deletSystem.GuildDelete;
import at.theduggy.duckguilds.guildInfo.GuildInfoCommand;
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

import javax.swing.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class GuildCommand implements TabExecutor {

    public static List<String> allColorsForColor = new ArrayList<>();
    public static List<String> allColorsForColorAndDark = new ArrayList<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (args.length != 0) {

                switch (args[0]) {
                    case "help":
                        if ((args.length == 2 && args[1].equals("1")) || args.length == 1) {
                            player.sendMessage(GuildHelpCommand.page1());
                        } else if (args.length == 2 && args[1].equals("2")) {
                            player.sendMessage(GuildHelpCommand.page2());
                        } else if (args.length == 2 && args[1].equals("3")) {
                            player.sendMessage(GuildHelpCommand.page3());
                        } else {
                            player.sendMessage(Main.wrongUsage);
                        }

                        break;
                    case "create":
                        if (args.length == 5) {
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
                                                GuildCreate.createGuild(player, Utils.translateFromReadableStringToChatColorLightColors(color), name, tag, Utils.translateFromReadableStringToChatColorAllColors(tagColor));

                                            } catch (IOException | ParseException e) {
                                                e.printStackTrace();
                                            }

                                        } else {
                                            player.sendMessage(Main.wrongUsage);
                                        }
                                    } else {
                                        player.sendMessage(Main.wrongUsage);
                                    }
                                } else {
                                    player.sendMessage(Main.wrongUsage);
                                }
                            } else {
                                player.sendMessage(Main.wrongUsage);
                            }
                        } else {
                            player.sendMessage(Main.wrongUsage);
                        }
                        break;
                    case "list":
                        try {
                            if (args.length==2) {
                                if (Utils.isStringInteger(args[1])){
                                    ListGuilds.listGuilds(player,Integer.parseInt(args[1]));
                                }else {
                                    player.sendMessage(Main.pageIndexMustBeNumeric);
                                }
                            } else {
                                player.sendMessage(Main.wrongUsage);
                            }
                        } catch (IOException | ParseException e) {
                            e.printStackTrace();
                        }
                        break;
                    case "leave":
                        try {
                            if (args.length == 2) {
                                if (!Utils.getIfPlayerIsHeadOfGuild(args[1], player)) {
                                    PlayerLeaveGuild.leaveGuild(player, args[1]);
                                } else if (Utils.getIfPlayerIsHeadOfGuild(args[1], player)) {
                                    player.sendMessage(Main.youAreTheHeadOfThatGuild);
                                }
                            } else {
                                player.sendMessage(Main.wrongUsage);
                            }
                        } catch (IOException | ParseException e) {
                            e.printStackTrace();
                        }
                        break;
                    case "delete":
                        try {
                            if (args.length == 3) {
                                if (args[2].equals("-y")) {
                                    GuildDelete.removeGuild(args[1], player);
                                } else if (args[2].equals("-n")) {
                                    player.sendMessage(Main.prefix + ChatColor.RED + "To delete your guild use /guild delete -y!");
                                } else {
                                    player.sendMessage(Main.forbiddenArgument);
                                }
                            } else {
                                player.sendMessage(Main.wrongUsage);
                            }

                        } catch (IOException | ParseException | InterruptedException e) {
                            e.printStackTrace();
                        }
                        break;
                    case "invite":
                        if (args.length == 2) {
                            if (Utils.isPlayerInGuild(player)) {
                                try {
                                    if (Utils.getIfPlayerIsHeadOfGuild(Utils.getPlayerGuild(player), player)) {
                                        if (Bukkit.getServer().getPlayerExact(args[1]) != null) {
                                            if (!Bukkit.getPlayerExact(args[1]).equals(player)) {
                                                GuildInviteCommand.guildInviteCommand(player, args[1], Utils.getPlayerGuild(player));
                                            } else {
                                                player.sendMessage(Main.prefix + ChatColor.RED + "You can't invite yourself!");
                                            }
                                        } else {
                                            player.sendMessage(Main.prefix + ChatColor.RED + "This player doesn't exist or isn't online!");
                                        }
                                    } else {
                                        player.sendMessage(Main.youAreNotTheHeadOfThatGuild);
                                    }
                                } catch (IOException | ParseException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                player.sendMessage(Main.youAreNotInAGuild);
                            }
                        } else {
                            player.sendMessage(Main.wrongUsage);
                        }
                        break;
                    case "info":
                            if(args.length==3) {
                                if (args[2].equals("general")) {
                                    GuildInfoCommand.guildInfoCommandGeneral(player, args[1]);
                                    break;
                                } else {
                                    player.sendMessage(Main.wrongUsage);
                                }
                            }else if (args.length==4){//TODO Fix info-command lit-players!
                                if (args[2].equals("playerList")){
                                    if (Utils.isStringInteger(args[3])){
                                        GuildInfoCommand.listPlayersOfGuild(player,args[1], Integer.parseInt(args[3]));
                                    }else {
                                        player.sendMessage(Main.pageIndexMustBeNumeric);
                                    }
                                }
                            }else {
                                player.sendMessage(Main.wrongUsage);
                            }
                            break;
                    case "join":
                        if (args.length == 2) {
                            try {
                                GuildJoinCommand.inviteReceive((Player) player, args[1]);
                            } catch (IOException | ParseException e) {
                                e.printStackTrace();
                            }
                        } else {
                            player.sendMessage(Main.wrongUsage);
                        }
                        break;
                    case "discardInvite":
                        if (args.length == 2) {
                            try {
                                GuildInviteDiscardCommand.discardInvite(player, args[1]);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        } else {
                            player.sendMessage(Main.wrongUsage);
                        }
                        break;
                    case "kick":
                        if (args.length == 3) {
                            if (args[2].equals("-y")) {
                                try {
                                    KickPlayerFromGuild.kickPlayerFromGuild(player, Bukkit.getPlayerExact(args[1]));
                                } catch (IOException | ParseException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                player.sendMessage(Main.wrongUsage);
                                //TODO Change msg!
                            }
                        } else {
                            player.sendMessage(Main.wrongUsage);
                        }
                        break;
                    case "deleteInvite":
                        if (args.length == 2) {
                            try {
                                DeleteGuildInvite.deleteInvite(player, args[1]);
                            } catch (IOException | ParseException e) {
                                e.printStackTrace();
                            }
                        } else {
                            player.sendMessage(Main.wrongUsage);
                        }
                        break;
                    default:
                        player.sendMessage(Main.wrongUsage);
                        break;
                }
            } else {
                player.sendMessage(Main.wrongUsage);
            }
        } else {
            sender.sendMessage("You are not a player! Use /guild help for all options!");
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (args.length == 1) {
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
                complete.add("info");
                Collections.sort(complete);
                return complete;
            } else {
                switch (args[0]){
                    case "create":
                        switch (args.length){
                            case 3:
                                List<String> colors = new ArrayList<>();
                                colors.add("Blue");
                                colors.add("White");
                                colors.add("Aqua");
                                colors.add("Gold");
                                colors.add("Green");
                                colors.add("Red");
                                colors.add("Yellow");
                                Collections.sort(colors);
                                return colors;
                            case 5:
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
                                Collections.sort(tagColors);
                                return tagColors;
                            default:
                                return new ArrayList<>();
                        }
                    case "delete":
                        switch (args.length){
                            case 2:
                                List<String> guilds = new ArrayList<>();
                                if (Utils.isPlayerInGuild(player)) {
                                    if (Utils.getIfPlayerIsHeadOfGuild(Utils.getPlayerGuild( player),player)) {
                                        guilds.add(Utils.getPlayerGuild(player));
                                    }

                                }
                                return guilds;
                            case 3:
                                if (Utils.isPlayerInGuild( player)) {
                                    if (Utils.getIfPlayerIsHeadOfGuild(Utils.getPlayerGuild( player),player)) {
                                        ArrayList<String> no_yes = new ArrayList<>();
                                        no_yes.add("-n");
                                        no_yes.add("-y");
                                        return no_yes;
                                    }
                                }
                            default:
                                return new ArrayList<>();
                        }
                    case "deleteInvite":
                        switch (args.length){
                            case 2:
                                if (Utils.isPlayerInGuild(player)) {
                                    if (Utils.getIfPlayerIsHeadOfGuild(Utils.getPlayerGuild( player), player)) {
                                        return Utils.getAllPlayerGuildInvitesForAGuild(Utils.getPlayerGuild(player));
                                    }
                                }
                            default:
                                return new ArrayList<>();
                        }
                    case "discardInvite":
                        switch (args.length){
                            case 2:
                                return Utils.getPlayerGuildInvites(player);
                            case 3:
                                ArrayList<String> no_yes = new ArrayList<>();
                                no_yes.add("no");
                                no_yes.add("yes");
                                return no_yes;
                            default:
                                return new ArrayList<>();
                        }
                    case "help":
                        switch (args.length){
                            case 2:
                                List<String> pages = new ArrayList<>();
                                pages.add("1");
                                pages.add("2");
                                pages.add("3");
                                return pages;
                        }
                        break;
                    case "invite":
                        switch (args.length){
                            case 2:
                                ArrayList<String> players = new ArrayList<>(Utils.getPlayersThatArentInAGuild());
                                return players;
                            default:
                                return new ArrayList<>();
                        }
                    case "info":
                        switch (args.length){
                            case 4:
                                if (args[2].equals("playerList")){
                                    ArrayList<String> pagesIndexes = new ArrayList<>();
                                    if (Main.getGuildCache().containsKey(args[1])) {
                                        ArrayList<UUID> players = Main.getGuildCache().get(args[1]).getPlayers();
                                        int pageCount = (int) Math.ceil((double) players.size() / 8.0);
                                        for (int i = 1; i <= pageCount; i++) {
                                            pagesIndexes.add(String.valueOf(i));
                                        }
                                        return pagesIndexes;
                                    }else {
                                        return new ArrayList<>();
                                    }
                                }else {
                                    return new ArrayList<>();
                                }
                            case 3:
                                ArrayList<String> options = new ArrayList<>();
                                options.add("general");
                                options.add("playerList");
                                return options;
                            case 2:
                                return new ArrayList<>(Main.getGuildCache().keySet());
                            default:
                                return new ArrayList<>();
                        }
                    case "join":
                        switch (args.length){
                            case 2:
                                ArrayList<String> guildInvites = new ArrayList<>(Utils.getPlayerGuildInvites((Player) player));
                                return guildInvites;
                            default:
                                return new ArrayList<>();
                        }
                    case "kick":
                        switch (args.length){
                            case 2:
                                if (Utils.isPlayerInGuild(player)) {
                                    if (Utils.getIfPlayerIsHeadOfGuild(Utils.getPlayerGuild(player),player)) {
                                        ArrayList<String> guildInvites = Utils.getAllPlayerGuildInvitesForAGuild(Utils.getPlayerGuild(player));
                                        return guildInvites;
                                    } else {
                                        return new ArrayList<>();
                                    }
                                } else {
                                    return new ArrayList<>();
                                }
                            case 3:
                                if (Utils.isPlayerInGuild(player)) {
                                    if (Utils.getIfPlayerIsHeadOfGuild(Utils.getPlayerGuild(player), player)) {
                                        List<String> no_yes = new ArrayList<>();
                                        no_yes.add("-n");
                                        no_yes.add("-y");
                                        return no_yes;
                                    } else {
                                        return new ArrayList<>();
                                    }
                                } else {
                                    return new ArrayList<>();
                                }
                            default:
                                return new ArrayList<>();

                        }
                    case "leave":
                        switch (args.length){
                            case 2:
                                ArrayList<String> guilds = new ArrayList<>();
                                guilds.add(Utils.getPlayerGuild( player));
                                return guilds;
                            default:
                                return new ArrayList<>();
                        }
                    case "list":
                        switch (args.length){
                            case 2:
                                ArrayList<String> pagesIndexes = new ArrayList<>();
                                int pageCount = (int) Math.ceil((double) Main.getGuildCache().size()/8.0);
                                for (int i = 1;i<=pageCount; i++){
                                    pagesIndexes.add(String.valueOf(i));
                                }
                                return pagesIndexes;
                            default:
                                return new ArrayList<>();
                        }
                }
                //TODO Add completion on discardInvite
            }
        }
        return null;
    }
}
