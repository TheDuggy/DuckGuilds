package at.theduggy.duckguilds.utils;

import at.theduggy.duckguilds.Main;
import at.theduggy.duckguilds.config.GuildConfigHandler;
import org.bukkit.ChatColor;

import java.io.File;
import java.util.ArrayList;
import java.util.UUID;

public class GuildTextUtils {

    public static String prefix = ChatColor.AQUA + "[" + ChatColor.GOLD + "DuckGuilds" + ChatColor.AQUA + "] ",
    prefixWithoutColor =  "[DuckGuilds] ",
    wrongUsage = prefix + ChatColor.RED + "Wrong usage! Use /guild help to see all options!",
    wrongUsageConsole = GuildTextUtils.prefixWithoutColor + "Wrong usage! Use /guild help for more information!",
    playerAlreadyInGuild = prefix + ChatColor.RED + "You are already in a guild! use /guild leave to leave yor current guild. Use /guild leave -y to leave the guild if you are the head, but your guild would be lost for ever!",
    guildDoesntExist = prefix + ChatColor.RED + "That guild doesn't exist. Use /guild list <page> to see all guilds!",
    youArentInThatGuild = prefix + ChatColor.RED + "Your aren't in that guild.",
    guildHeadLeftGuild = prefix +  ChatColor.RED + "Your guild-head had left the guild and the guild was deleted!",
    youAreNotInAGuild = prefix + ChatColor.RED + "You are not in a guild!",
    youAreTheHeadOfThatGuild = prefix + ChatColor.RED + "You are the head of that guild! You can't leave it, but delete it with /guild delete -y!",
    youAreNotTheHeadOfThatGuild = prefix + ChatColor.RED + "You are not the head of that guild!",
    forbiddenArgument = prefix + ChatColor.RED + "This command do not take this argument!",
    playerDoesntExists = prefix +ChatColor.RED + "That player doesn't exist!",
    playerIsntOnline = prefix + ChatColor.RED + "This player isn't online!",
    pageIndexMustBeNumeric = prefix + ChatColor.RED + "The page-index must be numeric!",
    pageIndexCantBe0 = prefix + ChatColor.RED + "The page-index can't be 0!",
    maxServerGuildsReached = prefix + ChatColor.RED + "The servers max guild-level was reached, which is " + ChatColor.YELLOW + GuildConfigHandler.getMaxGuildSize() + ChatColor.RED + " and the amount of guilds on this server is " + ChatColor.YELLOW + Main.getGuildCache().size() + ChatColor.RED + " ! You can't create guilds till a minimum of 1 is deleted!",
    forbiddenTag = prefix + "The tag contains forbidden symbols!",
    guildNameToLong = prefix + ChatColor.RED + "The name of a guild can't be longer that 25 characters!";

    //Utility-Methods
    public static boolean isStringInteger(String toCheck){
        try {
            int i = Integer.parseInt(toCheck);
            return true;
        }catch (NumberFormatException e){
            return false;
        }
    }

    public static String chatColorToString(ChatColor color){
        if (color.equals(ChatColor.BLUE)){
            return "Blue";
        }else if (color.equals(ChatColor.DARK_BLUE)){
            return "Dark_Blue";
        }else if (color.equals(ChatColor.DARK_PURPLE)){
            return "Dark_Purple";
        }else if (color.equals(ChatColor.LIGHT_PURPLE)){
            return "Light_Purple";
        }else if (color.equals(ChatColor.AQUA)){
            return "Aqua";
        }else if (color.equals(ChatColor.DARK_AQUA)) {
            return "Dark_Aqua";
        }else if (color.equals(ChatColor.DARK_GREEN)){
            return "Dark_Green";
        }else if (color.equals(ChatColor.DARK_RED)){
            return "Dark_Red";
        }else if (color.equals(ChatColor.GOLD)){
            return "Gold";
        }else if (color.equals(ChatColor.GREEN)){
            return "Green";
        }else if (color.equals(ChatColor.RED)){
            return "Red";
        }else if (color.equals(ChatColor.YELLOW)){
            return "Yellow";
        }else if (color.equals(ChatColor.WHITE)){
            return "White";
        }
        return null;
    }

    public static boolean isStringUUID(String s){
        boolean canBeParsed = true;
        try {
            UUID uuid = UUID.fromString(s);
        }catch (IllegalArgumentException e){
            canBeParsed = false;
        }
        return canBeParsed;
    }

    public static String getFileBaseName(File file){
        return file.getName().substring(0,file.getName().lastIndexOf('.'));
    }

    public static String getFileExtension(File file) {
        return file.getName().substring(file.getName().lastIndexOf('.'));
    }

    public static ChatColor translateFromReadableStringToChatColorAllColors(String toTranslate){
        ChatColor color = null;
        switch (toTranslate) {
            case "Blue":
                color = ChatColor.BLUE;
                break;
            case "Dark_Blue":
                color = ChatColor.DARK_BLUE;
                break;
            case "Dark_Purple":
                color = ChatColor.DARK_PURPLE;
                break;
            case "Light_Purple":
                color = ChatColor.LIGHT_PURPLE;
                break;
            case "Aqua":
                color = ChatColor.AQUA;
                break;
            case "Dark_Aqua":
                color = ChatColor.DARK_AQUA;
                break;
            case "Dark_Green":
                color = ChatColor.DARK_GREEN;
                break;
            case "Dark_Red":
                color = ChatColor.DARK_RED;
                break;
            case "Gold":
                color = ChatColor.GOLD;
                break;
            case "Green":
                color = ChatColor.GREEN;
                break;
            case "Red":
                color = ChatColor.RED;
                break;
            case "Yellow":
                color = ChatColor.YELLOW;
                break;
            case "White":
                color = ChatColor.WHITE;
                break;
        }
        return color;
    }
    public static ChatColor translateFromReadableStringToChatColorLightColors(String toTranslate){
        ChatColor color = null;
        switch (toTranslate) {
            case "Blue":
                color = ChatColor.BLUE;
                break;
            case "Light_Purple":
                color = ChatColor.LIGHT_PURPLE;
                break;
            case "Aqua":
                color = ChatColor.AQUA;
                break;
            case "Gold":
                color = ChatColor.GOLD;
                break;
            case "Green":
                color = ChatColor.GREEN;
                break;
            case "Red":
                color = ChatColor.RED;
                break;
            case "Yellow":
                color = ChatColor.YELLOW;
                break;
            case "White":
                color = ChatColor.WHITE;
                break;
        }
        return color;
    }

    public static boolean isStringReadyToUse(String string){
        boolean isTrue = false;
        char[] digits = string.toCharArray();
        for (int i =0; i!= digits.length;i++){
            char digit = digits[i];
            if (Character.isDigit(digit)){//TODO Test#
                isTrue = true;
            }else if (Character.isAlphabetic(digit)){
                isTrue = true;
            }
        }
        return isTrue;
    }


    public static boolean isReadyForCreate(String stringToCheck){
        boolean isTrue = false;
        char[] chars = stringToCheck.toCharArray();
        for (int i = 0; i!= chars.length;i++){
            if (Character.isAlphabetic(chars[i])){
                if (chars.length<=4){
                    isTrue = true;
                }
            }else if (Character.isDigit(chars[i])){
                if (chars.length<=4){
                    isTrue = true;
                }
            }
        }

        return isTrue;
    }

    public static ArrayList<UUID> uuidPlayerStringListToUUIDS(ArrayList<String> stringList){
        ArrayList<UUID> uuids = new ArrayList<>();
        for (String s:stringList){
            uuids.add(UUID.fromString(s));
        }
        return uuids;
    }

    public static String trimUUID(UUID uuid){
        return uuid.toString().replace("-","");
    }

    public static UUID untrimUUID(String trimmedUUID){
        StringBuilder uuidString = new StringBuilder();
        char[] chars = trimmedUUID.toCharArray();
        for (int i = 0; i<chars.length;i++){
            if (i==8||i==12||i==16||i==20) {
                uuidString.append("-" + chars[i]);
            }else {
                uuidString.append(chars[i]);
            }
        }
        return UUID.fromString(uuidString.toString());
    }

}
