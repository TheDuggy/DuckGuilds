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
package at.theduggy.duckguilds.other;

import at.theduggy.duckguilds.Main;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

public class Utils {

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

    public static ChatColor translateFromStringToChatColor(String toTranslate){
        ChatColor color;

        switch (toTranslate) {
            case "&9":
                color = ChatColor.BLUE;
                return color;
            case "&1":
                color = ChatColor.DARK_BLUE;
                return color;
            case "&5":
                color = ChatColor.DARK_PURPLE;
                return color;
            case "&d":
                color = ChatColor.LIGHT_PURPLE;
                return color;
            case "&b":
                color = ChatColor.AQUA;
                return color;
            case "&3":
                color = ChatColor.DARK_AQUA;
                return color;
            case "&2":
                color = ChatColor.DARK_GREEN;
                return color;
            case "&4":
                color = ChatColor.DARK_RED;
                return color;
            case "&6":
                color = ChatColor.GOLD;
                return color;
            case "&a":
                color = ChatColor.GREEN;
                return color;
            case "&c":
                color = ChatColor.RED;
                return color;
            case "&e":
                color = ChatColor.YELLOW;
                return color;
            case "&f":
                color = ChatColor.WHITE;
                return color;
        }
        return null;
    }

    public static String getPlayerGuild(Player player){
        return (String) Main.getPlayerCache().get(player.getUniqueId()).getGuild();
    }

    public static boolean getIfPlayerIsHeadOfGuild(String name, Player player) {
        boolean isTrue = false;
        if (Main.getGuildCache().containsKey(name)){
            if (Main.getGuildCache().get(name).getHead().equals(player.getUniqueId())){
                isTrue = true;
            }
        }
        return isTrue;
    }

    public static boolean guildExists(String name) throws IOException, ParseException {
        return Main.getGuildCache().containsKey(name);
    }

    public static UUID getHeadOfGuild(String guildName){
        return Main.getGuildCache().get(guildName).getHead();
    }

    public static ArrayList<String> getPlayerGuildInvites(Player player){
        ArrayList<String> keys = new ArrayList<>(Main.guildInvites.keySet());
        ArrayList<String> guilds = new ArrayList<>();
        for (int i = 0;i!=keys.size();i++){
            if (Main.guildInvites.get(keys.get(i)).contains(player.getName())){
                guilds.add(keys.get(i));
            }
        }
        return guilds;
    }

    public static ArrayList<String> getPlayersThatArentInAGuild(){
        ArrayList<String> players = new ArrayList<>();
        for (Player player:Bukkit.getOnlinePlayers()){
            if (!Utils.isPlayerInGuild(player)){
                players.add(player.getName());
            }
        }
        return players;
    }

    //TODO Check if all for-loops use the correct player!!!


    public static String centerText(String text) {
        int maxWidth = 80,
                spaces = (int) Math.round((maxWidth-1.4*ChatColor.stripColor(text).length())/2);
        return  StringUtils.repeat(" ", spaces)+text + ChatColor.WHITE ;
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

    public static ArrayList<String> getAllPlayerGuildInvitesForAGuild(String guildName){
        ArrayList<String> invites = new ArrayList<>();
        if  (Main.guildInvites.get(guildName)!=null) {
            for (int i = 0; i != Main.guildInvites.get(guildName).size(); i++) {
                invites.add(Main.guildInvites.get(guildName).get(i));
            }
        }
        return invites;
    }

    public static String getChatColorCode(ChatColor color){
        String chatColor = null;
        if (color.equals(ChatColor.BLUE)){
            chatColor="&9";
        }else if (color.equals(ChatColor.DARK_BLUE)){
            chatColor ="&1";
        }else if (color.equals(ChatColor.DARK_PURPLE)){
            chatColor = "&5";
        }else if (color.equals(ChatColor.LIGHT_PURPLE)){
            chatColor="&d";
        }else if (color.equals(ChatColor.AQUA)){
            chatColor="&b";
        }else if (color.equals(ChatColor.DARK_AQUA)) {
            chatColor = "&3";
        }else if (color.equals(ChatColor.DARK_GREEN)){
            chatColor= "&2";
        }else if (color.equals(ChatColor.DARK_RED)){
            chatColor="&4";
        }else if (color.equals(ChatColor.GOLD)){
            chatColor="&6";
        }else if (color.equals(ChatColor.GREEN)){
            chatColor="&a";
        }else if (color.equals(ChatColor.RED)){
            chatColor="&c";
        }else if (color.equals(ChatColor.YELLOW)){
            chatColor="&e";
        }else if (color.equals(ChatColor.WHITE)){
            chatColor="&f";
        }
        return chatColor;
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

    public static boolean isPlayerInGuild(Player player){
        return !Main.getPlayerCache().get(player.getUniqueId()).getGuild().equals("");
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

    public static boolean isPlayerOnline(UUID uuid){
        ArrayList<UUID> onlineUUIDS = new ArrayList<>();
        for (Player player: Bukkit.getOnlinePlayers()){
            onlineUUIDS.add(player.getUniqueId());
        }
        return onlineUUIDS.contains(uuid);
    }


    public static String getFileBaseName(File file){
        return file.getName().substring(0,file.getName().lastIndexOf('.'));
    }

    public static String getFileExtension(File file) {
        return file.getName().substring(file.getName().lastIndexOf('.'));
    }

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

    public static int getOnlinePlayersOfGuild(String guildName){
        int onlinePlayers =0;
        for (UUID player: Main.getGuildCache().get(guildName).getPlayers()){
            if (Bukkit.getPlayer(player)!=null){
                onlinePlayers+=1;
            }
        }
        return onlinePlayers;
    }
}
