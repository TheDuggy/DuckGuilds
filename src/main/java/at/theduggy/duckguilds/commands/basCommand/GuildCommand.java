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
package at.theduggy.duckguilds.commands.basCommand;

import at.theduggy.duckguilds.Main;
import at.theduggy.duckguilds.commands.kick.KickPlayerFromGuild;
import at.theduggy.duckguilds.commands.creatGuild.GuildCreate;
import at.theduggy.duckguilds.commands.deletSystem.GuildDelete;
import at.theduggy.duckguilds.commands.info.GuildInfoCommand;
import at.theduggy.duckguilds.commands.invite.GuildDeleteInvite;
import at.theduggy.duckguilds.commands.invite.GuildInviteCommand;
import at.theduggy.duckguilds.commands.invite.GuildInviteDiscardCommand;
import at.theduggy.duckguilds.commands.invite.GuildJoinCommand;
import at.theduggy.duckguilds.commands.leave.PlayerLeaveGuild;
import at.theduggy.duckguilds.commands.list.GuildListCommand;
import at.theduggy.duckguilds.commands.versionInfo.GuildVersionInfoCommand;
import at.theduggy.duckguilds.objects.GuildInviteObject;
import at.theduggy.duckguilds.storage.StorageHandler;
import at.theduggy.duckguilds.storage.systemTypes.GuildFileSystem;
import at.theduggy.duckguilds.storage.systemTypes.MySqlSystem;
import at.theduggy.duckguilds.utils.GuildTextUtils;
import at.theduggy.duckguilds.utils.Utils;
import at.theduggy.duckguilds.commands.help.GuildHelpCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.*;

public class GuildCommand implements TabExecutor {

    public static ArrayList<String> allColorsForColor = new ArrayList<>(Arrays.asList("Blue","White","Aqua","Gold","Green","Red","Yellow"));
    public static ArrayList<String> allColorsForColorAndDark = new ArrayList<>(Arrays.asList("Blue","White","Aqua","Gold","Green","Red","Yellow","Dark_Blue","Dark_Purple","Dark_Aqua","Dark_Green","Dark_Red"));

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!Main.isIsStorageBusy()) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (args.length != 0) {

                    switch (args[0]) {
                        case "help":
                            if ((args.length == 2 && args[1].equals("help"))) {
                                player.sendMessage(GuildHelpCommand.help());
                            } else if (args.length == 2 && args[1].equals("create")) {
                                player.sendMessage(GuildHelpCommand.create());
                            } else if (args.length == 2 && args[1].equals("delete")) {
                                player.sendMessage(GuildHelpCommand.delete());
                            } else if (args.length == 2 && args[1].equals("deleteInvite")) {
                                player.sendMessage(GuildHelpCommand.deleteInvite());
                            } else if (args.length == 2 && args[1].equals("discardInvite")) {
                                player.sendMessage(GuildHelpCommand.discardInvite());
                            } else if (args.length == 2 && args[1].equals("list")){
                                player.sendMessage(GuildHelpCommand.list());
                            }else if (args.length == 2 && args[1].equals("leave")){
                                player.sendMessage(GuildHelpCommand.leave());
                            }else if (args.length == 2 && args[1].equals("invite")){
                                player.sendMessage(GuildHelpCommand.invite());
                            }else if (args.length == 2 && args[1].equals("join")){
                                player.sendMessage(GuildHelpCommand.join());
                            }else if (args.length == 2 && args[1].equals("kick")){
                                player.sendMessage(GuildHelpCommand.kick());
                            }else if (args.length == 2 && args[1].equals("info")){
                                player.sendMessage(GuildHelpCommand.info());
                            }else if (args.length == 2 && args[1].equals("versionInfo")){
                                player.sendMessage(GuildHelpCommand.versionInfo());
                            } else {
                                player.sendMessage(GuildTextUtils.wrongUsage);
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
                                                    GuildCreate.createGuild(player, GuildTextUtils.translateFromReadableStringToChatColorLightColors(color), name, tag, GuildTextUtils.translateFromReadableStringToChatColorAllColors(tagColor));

                                                } catch (IOException | ParseException e) {
                                                    e.printStackTrace();
                                                } catch (SQLException e) {
                                                    throw new RuntimeException(e);
                                                }

                                            } else {
                                                player.sendMessage(GuildTextUtils.wrongUsage);
                                            }
                                        } else {
                                            player.sendMessage(GuildTextUtils.wrongUsage);
                                        }
                                    } else {
                                        player.sendMessage(GuildTextUtils.wrongUsage);
                                    }
                                } else {
                                    player.sendMessage(GuildTextUtils.wrongUsage);
                                }
                            } else {
                                player.sendMessage(GuildTextUtils.wrongUsage);
                            }
                            break;
                        case "list":
                            try {
                                if (args.length == 2) {
                                    if (GuildTextUtils.isStringInteger(args[1])) {
                                        GuildListCommand.listGuilds(player, Integer.parseInt(args[1]));
                                    } else {
                                        player.sendMessage(GuildTextUtils.pageIndexMustBeNumeric);
                                    }
                                } else if (args.length == 1) {
                                    GuildListCommand.listGuilds(player, 1);
                                } else {
                                    player.sendMessage(GuildTextUtils.wrongUsage);
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
                                        player.sendMessage(GuildTextUtils.youAreTheHeadOfThatGuild);
                                    }
                                } else {
                                    player.sendMessage(GuildTextUtils.wrongUsage);
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
                                        player.sendMessage(GuildTextUtils.prefix + ChatColor.RED + "To delete your guild use /guild delete -y!");
                                    } else {
                                        player.sendMessage(GuildTextUtils.forbiddenArgument);
                                    }
                                } else {
                                    player.sendMessage(GuildTextUtils.wrongUsage);
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
                                                    player.sendMessage(GuildTextUtils.prefix + ChatColor.RED + "You can't invite yourself!");
                                                }
                                            } else {
                                                player.sendMessage(GuildTextUtils.prefix + ChatColor.RED + "This player doesn't exist or isn't online!");
                                            }
                                        } else {
                                            player.sendMessage(GuildTextUtils.youAreNotTheHeadOfThatGuild);
                                        }
                                    } catch (IOException | ParseException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    player.sendMessage(GuildTextUtils.youAreNotInAGuild);
                                }
                            } else {
                                player.sendMessage(GuildTextUtils.wrongUsage);
                            }
                            break;
                        case "info":
                            if (args.length == 4) {
                                if (args[2].equals("general")) {
                                    GuildInfoCommand.guildInfoCommandGeneral(player, args[1], args[3]);
                                    break;
                                } else if (args[2].equals("playerList")) {
                                    if (GuildTextUtils.isStringInteger(args[3])) {
                                        GuildInfoCommand.listPlayersOfGuild(player, args[1], args[3]);
                                    } else {
                                        player.sendMessage(GuildTextUtils.pageIndexMustBeNumeric);
                                    }
                                }
                            } else if (args.length == 3) {
                                if (args[2].equals("general")) {
                                    GuildInfoCommand.guildInfoCommandGeneral(player, args[1], "");
                                    break;
                                } else if (args[2].equals("playerList")) {
                                    GuildInfoCommand.listPlayersOfGuild(player, args[1], "");
                                }
                            } else {
                                player.sendMessage(GuildTextUtils.wrongUsage);
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
                                player.sendMessage(GuildTextUtils.wrongUsage);
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
                                player.sendMessage(GuildTextUtils.wrongUsage);
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
                                    player.sendMessage(GuildTextUtils.wrongUsage);
                                    //TODO Change msg!
                                }
                            } else {
                                player.sendMessage(GuildTextUtils.wrongUsage);
                            }
                            break;
                        case "deleteInvite":
                            if (args.length == 2) {
                                try {
                                    GuildDeleteInvite.deleteInvite(player, args[1]);
                                } catch (IOException | ParseException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                player.sendMessage(GuildTextUtils.wrongUsage);
                            }
                            break;
                        case "versionInfo":
                            if (args.length==1){
                                player.spigot().sendMessage(GuildVersionInfoCommand.guildVersionInfo());
                            }else {
                                player.sendMessage(GuildTextUtils.wrongUsage);
                            }
                    }
                } else {
                    player.sendMessage(GuildTextUtils.wrongUsage);
                }
                //
            } else {
                if (args[0].equals("storage")) {
                    if (args.length==3){
                        if (args[1].equals("migrate")) {
                            switch (args[2]) {
                                case "File_To_MySql":
                                    if (!Main.getMainStorage().getStorageSystemID().equals("MySQL")) {
                                        try {
                                            Main.getMainStorage().migrateStorage(new MySqlSystem());
                                        } catch (SQLException | IOException e) {
                                            throw new RuntimeException(e);
                                        }
                                    } else {
                                        sender.sendMessage(GuildTextUtils.prefixWithoutColor + "The current storage-type is already MySQL!");
                                    }
                                    break;
                                case "MySql_To_File":
                                    if (!Main.getMainStorage().getStorageSystemID().equals("File")) {
                                        try {
                                            Main.getMainStorage().migrateStorage(new GuildFileSystem());
                                        } catch (SQLException | IOException e) {
                                            throw new RuntimeException(e);
                                        }
                                    } else {
                                        sender.sendMessage(GuildTextUtils.prefixWithoutColor + "The current storage-type is already File!");
                                    }
                                    break;
                                default:
                                    sender.sendMessage(GuildTextUtils.wrongUsageConsole);
                            }
                        }else if(args[1].equals("import")){
                            if (Files.exists(Path.of(args[2]))){
                                File fileToLoad = new File(args[2]);
                                StorageHandler.importStorage(fileToLoad);
                            }else {
                                sender.sendMessage(GuildTextUtils.prefixWithoutColor + " The file " + args[2] + " doesn't exist!");
                            }
                        }else {
                            sender.sendMessage(GuildTextUtils.wrongUsageConsole);
                        }
                    } else if (args.length==2){
                        if (args[1].equals("export")) {
                            StorageHandler.exportStorage();
                        }else {
                            sender.sendMessage(GuildTextUtils.wrongUsageConsole);
                        }
                    }else {
                        sender.sendMessage(GuildTextUtils.wrongUsageConsole);
                    }

                } else {
                    sender.sendMessage(GuildTextUtils.wrongUsageConsole);
                }
            }
        }else {
            if (sender instanceof Player){
                sender.sendMessage(GuildTextUtils.prefix + ChatColor.RED + "Failed to perform action! Wait a bit and try again! If this doesn't go away, report it to an admin!");
            }else {
                sender.sendMessage(GuildTextUtils.prefixWithoutColor + "Failed to perform action! Storage is busy!");
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (args.length == 1) {
                List<String> commands = new ArrayList<>();
                commands.add("help");
                commands.add("create");
                commands.add("list");
                commands.add("leave");
                commands.add("delete");
                commands.add("invite");
                commands.add("join");
                commands.add("discardInvite");
                commands.add("deleteInvite");
                commands.add("kick");
                commands.add("info");
                commands.add("versionInfo");
                Collections.sort(commands);
                return commands;
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
                                    if (Utils.getIfPlayerIsHeadOfGuild(Main.getGuildCache().get(Main.getPlayerCache().get(player.getUniqueId()).getGuild()).getName(),player)) {
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
                                        return Utils.getAllPlayerNamesOfInvitedPlayers(Utils.getPlayerGuild(player));
                                    }
                                }
                            default:
                                return new ArrayList<>();
                        }
                    case "discardInvite":
                        switch (args.length){
                            case 2:
                                ArrayList<String> guildInvites = new ArrayList<>();
                                for (GuildInviteObject guildInviteObject : Utils.getPlayerGuildInvites(player)){
                                    guildInvites.add(guildInviteObject.getGuild().getName());
                                }
                                return guildInvites;
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
                                ArrayList<String> commands = new ArrayList<>();
                                commands.add("help");
                                commands.add("create");
                                commands.add("list");
                                commands.add("leave");
                                commands.add("delete");
                                commands.add("invite");
                                commands.add("join");
                                commands.add("discardInvite");
                                commands.add("deleteInvite");
                                commands.add("kick");
                                commands.add("info");
                                commands.add("versionInfo");
                                return commands;
                            default:
                                return new ArrayList<>();
                        }
                    case "invite":
                        switch (args.length){
                            case 2:

                                return new ArrayList<>(Utils.getPlayersThatArentInAGuild());
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
                                }else if (args[2].equals("general")){
                                    ArrayList<String> pageIndexes = new ArrayList<>();
                                    pageIndexes.add("1");
                                    pageIndexes.add("2");
                                    return pageIndexes;
                                } else {
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
                                ArrayList<String> guildInvites = new ArrayList<>();
                                for (GuildInviteObject guildInviteObject : Utils.getPlayerGuildInvites(player)){
                                    guildInvites.add(guildInviteObject.getGuild().getName());
                                }
                                return guildInvites;
                            default:
                                return new ArrayList<>();
                        }
                    case "kick":
                        switch (args.length){
                            case 2:
                                if (Utils.isPlayerInGuild(player)) {
                                    if (Utils.getIfPlayerIsHeadOfGuild(Utils.getPlayerGuild(player),player)) {
                                        return Utils.getAllPlayerNamesOfGuildWithoutHead(Utils.getPlayerGuild(player));
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
                    default:
                        return new ArrayList<>();
                }
                //TODO Add completion on discardInvite
            }
        }else {
            if (args.length==1){
                return new ArrayList<>(Arrays.asList("storage"));
            }else {
                switch (args[0]){
                    case "storage":
                        switch (args.length){
                            case 2: return new ArrayList<>(Arrays.asList("migrate","export", "import"));
                            case 3:
                                if (args[1].equals("migrate")){
                                    return new ArrayList<>(Arrays.asList("MySql_To_File","File_To_MySql"));
                                }
                            default:
                                return new ArrayList<>();
                        }
                }
            }
        }
        return null;
    }
}
