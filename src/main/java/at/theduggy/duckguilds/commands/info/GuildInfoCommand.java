package at.theduggy.duckguilds.commands.info;

import at.theduggy.duckguilds.Main;
import at.theduggy.duckguilds.commands.list.GuildListCommand;
import at.theduggy.duckguilds.objects.GuildObject;
import at.theduggy.duckguilds.utils.GuildTextUtils;
import at.theduggy.duckguilds.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class GuildInfoCommand {
/*
creationDate,

 */
    public static void guildInfoCommandGeneral(Player player,String guildName, String page){

        if (Main.getGuildCache().containsKey(guildName)) {
            if (page.equals("1")||page.equals("")) {
                StringBuilder msg = new StringBuilder();
                msg.append(GuildTextUtils.prefix + ChatColor.GREEN + "General information about " + ChatColor.YELLOW + guildName + ChatColor.GREEN + " [" + ChatColor.YELLOW + 1 + ChatColor.GREEN + "/2]: \n");
                msg.append(ChatColor.WHITE + "-".repeat(35) + "\n");
                ChatColor color = Main.getGuildCache().get(guildName).getGuildColor().getChatColor();
                ChatColor tagColor = Main.getGuildCache().get(guildName).getTagColor().getChatColor();
                msg.append(ChatColor.GREEN + "Name: " + color + guildName + " (" + GuildTextUtils.chatColorToString(color) + ")\n");
                msg.append(ChatColor.GREEN + "Tag: " + tagColor + Main.getGuildCache().get(guildName).getTag() + " (" + GuildTextUtils.chatColorToString(tagColor) + ")\n");
                msg.append(ChatColor.GREEN + "Head: " + color + Main.getPlayerCache().get(Main.getGuildCache().get(guildName).getHead()).getName() + "\n");
                ArrayList<UUID> players = Main.getGuildCache().get(guildName).getPlayers();
                msg.append(ChatColor.GREEN + "Players: " + color + players.size() + "\n");
                msg.append(ChatColor.GREEN + "└ See /guild info " + guildName + " playerList <page> !\n");
                msg.append(ChatColor.GREEN + "Online-Players: " + color + Utils.getOnlinePlayersOfGuild(guildName) + "\n");
                msg.append(ChatColor.GREEN + "Creation-Date: " + color + Main.getGuildCache().get(guildName).getGuildMetadata().getFormattedCreationDate());
                player.sendMessage(msg.toString());
            } else if (page.equals("2")) {
                ArrayList<String> keys = new ArrayList<>(Main.getGuildCache().keySet());
                ArrayList<Integer> values = new ArrayList<>();
                GuildObject guildObject = Main.getGuildCache().get(guildName);
                values.add(guildObject.getPlayers().size());
                for (int i = 0; i < values.size(); i++) {
                    for (int j = values.size() - 1; j >= i; j--) {
                        if (values.get(i) < values.get(j)) {
                            int temp = values.get(j);
                            values.set(j, values.get(i));
                            values.set(i, temp);
                            String tempString = keys.get(j);
                            keys.set(j, keys.get(i));
                            keys.set(i, tempString);
                        }
                    }
                }
                StringBuilder msg = new StringBuilder();
                msg.append(GuildTextUtils.prefix + ChatColor.GREEN + "General information about " + ChatColor.YELLOW + guildName + ChatColor.GREEN + " [" + ChatColor.YELLOW + 2 + ChatColor.GREEN + "/2]: \n");
                msg.append(ChatColor.WHITE + "-".repeat(35) + "\n");
                int amongWithOtherGuilds = 0; //Guilds with which that guild to get the info about is in the same position!
                for (String guildName1 : Main.getGuildCache().keySet()) {
                    if (GuildListCommand.calculatePositionOnServer(guildName1) == GuildListCommand.calculatePositionOnServer(guildName)&&!guildName1.equals(guildName)) {
                        amongWithOtherGuilds++;
                    }
                }


                String posColor;
                switch (GuildListCommand.calculatePositionOnServer(guildName)) {
                    case 1:
                        posColor = ChatColor.GOLD + "" + ChatColor.BOLD;
                        break;
                    case 2:
                        posColor = ChatColor.GRAY + "" + ChatColor.BOLD;
                        break;
                    case 3:
                        posColor = ChatColor.DARK_RED + "" + ChatColor.BOLD;
                        break;
                    default:
                        posColor = ChatColor.DARK_GRAY + "";
                        break;
                }
                msg.append(ChatColor.GREEN + "Server-Position: " + posColor + "" + GuildListCommand.calculatePositionOnServer(guildName) + " " + ChatColor.GREEN + "(among with " + amongWithOtherGuilds + " other guilds)\n");
                msg.append(ChatColor.GREEN + "List-Page-Index: " + Main.getGuildCache().get(guildName).getGuildColor().getChatColor() + calculatePagePosition(guildName));
                player.sendMessage(msg.toString());
            } else {
                player.sendMessage(GuildTextUtils.wrongUsage);
            }
        }else {
            player.sendMessage(GuildTextUtils.guildDoesntExist);
        }
    }

    private static int calculatePagePosition(String guildName){
        HashMap<Integer,ArrayList<String>> guildPages = new HashMap<>();
        ArrayList<String> keys = new ArrayList<>(Main.getGuildCache().keySet());
        ArrayList<Integer> values = new ArrayList<>();
        for (String key : keys) {
            ArrayList<UUID> players = Main.getGuildCache().get(key).getPlayers();
            values.add(players.size());
        }
        for (int i =0;i<values.size();i++){
            for (int j = values.size()-1;j>=i;j--){
                if (values.get(i)<values.get(j)){
                    int temp = values.get(j);
                    values.set(j,values.get(i));
                    values.set(i,temp);
                    String tempString = keys.get(j);
                    keys.set(j,keys.get(i));
                    keys.set(i,tempString);
                }
            }
        }
        int pages = (int) Math.ceil((double) Main.getGuildCache().keySet().size()/8.0);
        int lastCheckPoint = 0;
        for (int i = 1; i<=pages; i++,lastCheckPoint+=8){
            guildPages.put(i,new ArrayList<>());
            for (int i2 = lastCheckPoint; i2 !=lastCheckPoint+8&&i2!=keys.size(); i2++){
                guildPages.get(i).add(keys.get(i2));
            }
        }

        System.out.println(guildPages.keySet().size());
        for (int i = 1; i<=guildPages.keySet().size();i++){
            for (String guildNameFromList: guildPages.get(i)){
                if (guildNameFromList.equals(guildName)){
                    return i;
                }
            }
        }
        return 0;
    }



    public static void listPlayersOfGuild(Player player,String guildName,String page){
        int pageIndex = Integer.parseInt(page);
        if (pageIndex>0) {
            ArrayList<UUID> players = Main.getGuildCache().get(guildName).getPlayers();
            int pageCount = (int) Math.ceil((double) players.size() / 8.0);
            if (pageIndex<=pageCount) {
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
                msg.append(GuildTextUtils.prefix  + ChatColor.GREEN + "List of all players in guild " + ChatColor.YELLOW + guildName + ChatColor.GRAY + "[" + ChatColor.AQUA + page + ChatColor.GRAY + "/"+ pages + "]\n");
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
                player.sendMessage(GuildTextUtils.prefix + ChatColor.RED + "The last page is " + ChatColor.YELLOW + pageCount + ChatColor.RED + "!");
            }
        }else {
            player.sendMessage(GuildTextUtils.pageIndexCantBe0);
        }
    }

    private static void putHeadToFirstPosition(HashMap<Integer,ArrayList<String>> listToWork, String headName){
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
    }


}