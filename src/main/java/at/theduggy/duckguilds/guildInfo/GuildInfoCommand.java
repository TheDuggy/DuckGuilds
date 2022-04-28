package at.theduggy.duckguilds.guildInfo;

import at.theduggy.duckguilds.Main;
import at.theduggy.duckguilds.other.Utils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class GuildInfoCommand {
/*
creationDate,

 */
    public static void guildInfoCommandGeneral(Player player,String guildName){
        if (Main.getGuildCache().containsKey(guildName)){
            StringBuilder msg = new StringBuilder();
            msg.append(Main.prefix + ChatColor.GREEN + "General information about " + ChatColor.YELLOW + guildName + ChatColor.GREEN + ": \n");
            msg.append(ChatColor.WHITE + "-".repeat(35) + "\n");
            ChatColor color = Main.getGuildCache().get(guildName).getGuildColor().getChatColor();
            ChatColor tagColor =  Main.getGuildCache().get(guildName).getTagColor().getChatColor();
            msg.append(ChatColor.GREEN + "Name: " + color + guildName + " (" + Utils.chatColorToString(color) + ")\n");
            msg.append(ChatColor.GREEN + "Tag: " + tagColor + Main.getGuildCache().get(guildName).getTag() + " (" +Utils.chatColorToString(tagColor) + ")\n");
            msg.append(ChatColor.GREEN + "Head: " + color + Main.getPlayerCache().get(Main.getGuildCache().get(guildName).getHead()).getName() + "\n");
            ArrayList<UUID> players =  Main.getGuildCache().get(guildName).getPlayers();
            msg.append(ChatColor.GREEN  + "Players: " + color + players.size() + "\n");
            msg.append(ChatColor.GREEN + "└ See /guild info " + guildName + " playerList <page> !\n");
            msg.append(ChatColor.GREEN + "Online-Players: " + color + Utils.getOnlinePlayersOfGuild(guildName) + "\n");
            msg.append(ChatColor.GREEN + "Creation-Date: " + color + Main.getGuildCache().get(guildName).getGuildMetadata().getCreationDate());
            player.sendMessage(msg.toString());
        }else {
            player.sendMessage(Main.guildDoesntExists);
        }
    }

    public static void listPlayersOfGuild(Player player,String guildName,int page){
        if (page>0) {
            ArrayList<UUID> players = Main.getGuildCache().get(guildName).getPlayers();
            int pageCount = (int) Math.ceil((double) players.size() / 8.0);
            if (page<=pageCount) {
                HashMap<Integer,ArrayList<String>> playerPages = new HashMap<>();
                int pages = (int) Math.ceil((double) players.size()/8.0);
                int lastCheckPoint = 0;
                for (int i = 1; i<=pages; i++,lastCheckPoint+=8){
                    playerPages.put(i,new ArrayList<>());
                    for (int i2 = lastCheckPoint; i2 !=lastCheckPoint+8&&i2!=players.size(); i2++){
                        playerPages.get(i).add(Main.getPlayerCache().get(players.get(i2)).getName());
                    }
                }
                System.out.println(playerPages);
                String headName = Main.getPlayerCache().get(Main.getGuildCache().get(guildName).getHead()).getName();
                StringBuilder msg = new StringBuilder();
                msg.append(Main.prefix  + ChatColor.GREEN + "List of all players in guild " + ChatColor.YELLOW + guildName + ChatColor.GRAY + "[" + ChatColor.AQUA + page + ChatColor.GRAY + "/"+ pages + "]\n");
                msg.append(ChatColor.WHITE + "-".repeat(40) + "\n");//60
                putHeadToFirstPosition(playerPages,headName);
                for (String name:playerPages.get(page)){
                    if (name.equals(headName)) {
                        msg.append(ChatColor.GRAY + "   - " + ChatColor.YELLOW + name +  ChatColor.GRAY + " [" + ChatColor.AQUA+ "HEAD" + ChatColor.GRAY + "]\n");
                    }else {
                        msg.append(ChatColor.GRAY + "   - " + ChatColor.YELLOW + name +"\n");
                    }
                }
                player.sendMessage(msg.toString());
            }else {
                player.sendMessage(Main.pageIndexOutOfBounds);
            }
        }else {
            player.sendMessage(Main.pageIndexOutOfBounds);
        }
    }

    public static HashMap<Integer,ArrayList<String>> putHeadToFirstPosition(HashMap<Integer,ArrayList<String>> listToWork,String headName){
        int headPage= 0;
        for (Integer key:listToWork.keySet()){
            for (String name:listToWork.get(key)){
                if (name.equals(headName)) {
                    headPage = key;
                    break;
                }
            }
        }
        String nameOfPlayerOnFirstPlace = listToWork.get(1).get(0);
        listToWork.get(1).set(0,headName);
        listToWork.get(headPage).set(listToWork.get(headPage).indexOf(headName),nameOfPlayerOnFirstPlace);
        return listToWork;
    }
}