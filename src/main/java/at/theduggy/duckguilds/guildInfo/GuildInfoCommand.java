package at.theduggy.duckguilds.guildInfo;

import at.theduggy.duckguilds.Main;
import at.theduggy.duckguilds.other.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

public class GuildInfoCommand {
/*
creationDate,

 */
    public static void guildInfoCommandGeneral(Player player,String guildName){
        if (Main.getGuildCache().containsKey(guildName)){
            StringBuilder msg = new StringBuilder();
            msg.append(Main.prefix + ChatColor.GREEN + "General information about " + ChatColor.YELLOW + guildName + ChatColor.GREEN + ": \n");
            msg.append(ChatColor.WHITE + "-".repeat(35) + "\n");
            ChatColor color = (ChatColor) Main.getGuildCache().get(guildName).get("color");
            ChatColor tagColor = (ChatColor) Main.getGuildCache().get(guildName).get("tagColor");
            msg.append(ChatColor.GREEN + "Name: " + color + guildName + " (" + Utils.chatColorToString(color) + ")\n");
            msg.append(ChatColor.GREEN + "Tag: " + tagColor + Main.getGuildCache().get(guildName).get("tag") + " (" +Utils.chatColorToString(tagColor) + ")\n");
            msg.append(ChatColor.GREEN + "Head: " + color + Main.getPlayerCache().get(Main.getGuildCache().get(guildName).get("head")).get("name") + "\n");
            ArrayList<UUID> players = (ArrayList<UUID>) Main.getGuildCache().get(guildName).get("players");
            msg.append(ChatColor.GREEN  + "Players: " + color + players.size() + "\n");
            msg.append(ChatColor.GREEN + "└ See /guild info " + guildName + " playerList <page> !\n");
            msg.append(ChatColor.GREEN + "Online-Players: " + color + Utils.getOnlinePlayersOfGuild(guildName) + "\n");
            player.sendMessage(msg.toString());
        }else {
            player.sendMessage(Main.guildDoesntExists);
        }
    }

    public static void listPlayersOfGuild(Player player,String guildName,int page){
        if (page>0) {
            ArrayList<UUID> players = (ArrayList<UUID>) Main.getGuildCache().get(guildName).get("players");
            int pageCount = (int) Math.ceil((double) players.size() / 8.0);
            if (page<=pageCount) {
                ArrayList<String> names = new ArrayList<>();
                names.add((String) Main.getPlayerCache().get(Main.getGuildCache().get(guildName).get("head")).get("name"));
                HashMap<Integer,ArrayList<String>> playerPages = new HashMap<>();
                int pages = (int) Math.ceil((double) players.size()/8.0);
                int lastCheckPoint = 0;
                for (int i = 1; i<=pages; i++,lastCheckPoint+=8){
                    playerPages.put(i,new ArrayList<>());
                    for (int i2 = lastCheckPoint; i2 !=lastCheckPoint+8&&i2!=players.size(); i2++){
                        playerPages.get(i).add((String) Main.getPlayerCache().get(players.get(i2)).get("name"));
                    }
                }
                //TODO put head on first place!
                System.out.println(playerPages);
                String headName = (String) Main.getPlayerCache().get(Main.getGuildCache().get(guildName).get("head")).get("name");
                StringBuilder msg = new StringBuilder();
                msg.append(Main.prefix  + ChatColor.GREEN + "List of all players in guild " + ChatColor.YELLOW + guildName + ChatColor.GRAY + "[" + ChatColor.AQUA + page + ChatColor.GRAY + "/"+ pages + "]\n");
                msg.append(ChatColor.WHITE + "-".repeat(40) + "\n");//60
                Bukkit.broadcastMessage(String.valueOf(names.size()));
                msg.append(ChatColor.GRAY + "   - " + ChatColor.YELLOW + headName + ChatColor.GRAY +" [" + ChatColor.AQUA + "HEAD" + ChatColor.GRAY + "]\n");
                for (String name:playerPages.get(page)){
                    if (!name.equals(headName)) {
                        msg.append(ChatColor.GRAY + "   - " + ChatColor.YELLOW + name + "\n");
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
}