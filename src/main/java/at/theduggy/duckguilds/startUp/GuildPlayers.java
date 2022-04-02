package at.theduggy.duckguilds.startUp;

import at.theduggy.duckguilds.Main;
import at.theduggy.duckguilds.objects.GuildPlayerObject;
import at.theduggy.duckguilds.storage.Storage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scoreboard.Team;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.HashMap;

public class GuildPlayers implements Listener {

    public static void handlePlayersOnReload() throws IOException, ParseException {
        for (Player player:Bukkit.getServer().getOnlinePlayers()){
            if (!Storage.personalGuildPlayerStorageSectionExists(player.getUniqueId())){
                Storage.createPersonalPlayerStorageSection(player);//TODO Remo IO createFile!!!!
                GuildPlayerObject guildPlayerObject = new GuildPlayerObject(player.getUniqueId(),true,player.getName(),"");
                Main.getPlayerCache().put(player.getUniqueId(), guildPlayerObject);
                Bukkit.getLogger().warning(Main.getPlayerCache().toString());
            }else if (Main.getPlayerCache().containsKey(player.getUniqueId())){
                Main.getPlayerCache().get(player.getUniqueId()).setOnline(true);
            }else {
                Storage.cachePlayer(player.getUniqueId(),"");
            }
        }
    }

    @EventHandler
    public void handlePlayersOnJoin(PlayerJoinEvent e) throws IOException, ParseException {
        Player player = e.getPlayer();
        if (!Storage.personalGuildPlayerStorageSectionExists(player.getUniqueId())){
            Storage.createPersonalPlayerStorageSection(player);
            GuildPlayerObject guildPlayerObject = new GuildPlayerObject(player.getUniqueId(),true,player.getName(),"");
            Main.getPlayerCache().put(player.getUniqueId(), guildPlayerObject);
            Team team;
            String guildName =  Main.getPlayerCache().get(player.getUniqueId()).getGuild();
            try {
                team = Main.getScoreboard().registerNewTeam(guildName);
            }catch (IllegalArgumentException exception){
                team = Main.getScoreboard().getTeam(guildName);
            }
            String newPlayerName = Main.getGuildCache().get(guildName).getColor() + player.getName() + ChatColor.GRAY + "[" + Main.getGuildCache().get(guildName).getTagColor()+ Main.getGuildCache().get(guildName).getTag() + ChatColor.GRAY + "]" + ChatColor.WHITE;
            team.setColor( Main.getGuildCache().get(guildName).getColor().getChatColor());
            team.setSuffix(ChatColor.GRAY + "[" + Main.getGuildCache().get(guildName).getTagColor() + Main.getGuildCache().get(guildName).getTag() + ChatColor.GRAY + "]" + ChatColor.WHITE);
            team.setDisplayName(guildName);
            team.addEntry(player.getName());
            player.setDisplayName(newPlayerName);
            player.setCustomName(newPlayerName);
            for (Player playerFromServer:Bukkit.getServer().getOnlinePlayers()){
                playerFromServer.setScoreboard(Main.getScoreboard());
            }
        }else if (!Main.getPlayerCache().get(player.getUniqueId()).getGuild().equals("")){
            String oldName = Storage.getPlayerDataFromStorage(player.getUniqueId());
            if (!oldName.equals(player.getName())) {
                HashMap<String, String> newPlayerData = new HashMap<>();
                newPlayerData.put("name", player.getName());
                Storage.updatePlayerData(player.getUniqueId(), Main.getPlayerCache().get(player.getUniqueId()));
                Main.getPlayerCache().get(player.getUniqueId()).setName(player.getName());
            }
            Main.getPlayerCache().get(player.getUniqueId()).setOnline(true);
            Team team;
            String guildName =  Main.getPlayerCache().get(player.getUniqueId()).getGuild();
            try {
                team = Main.getScoreboard().registerNewTeam(guildName);
            }catch (IllegalArgumentException exception){
                team = Main.getScoreboard().getTeam(guildName);
            }
            String newPlayerName = Main.getGuildCache().get(guildName).getColor() + player.getName() + ChatColor.GRAY + "[" + Main.getGuildCache().get(guildName).getTagColor()+ Main.getGuildCache().get(guildName).getTag() + ChatColor.GRAY + "]" + ChatColor.WHITE;
            team.setColor( Main.getGuildCache().get(guildName).getColor().getChatColor());
            team.setSuffix(ChatColor.GRAY + "[" + Main.getGuildCache().get(guildName).getTagColor() + Main.getGuildCache().get(guildName).getTag() + ChatColor.GRAY + "]" + ChatColor.WHITE);
            team.setDisplayName(guildName);
            team.addEntry(player.getName());
            player.setDisplayName(newPlayerName);
            player.setCustomName(newPlayerName);
            for (Player playerFromServer:Bukkit.getServer().getOnlinePlayers()){
                playerFromServer.setScoreboard(Main.getScoreboard());
            }
            System.out.println("Breakpoint 2!");
        }else {
            HashMap<String,Object> playerData = new HashMap<>();
            playerData.put("name",player.getName());
            playerData.put("online",true);
            playerData.put("guild","");
            String oldName = Storage.getPlayerDataFromStorage(player.getUniqueId());
            if (!oldName.equals(player.getName())) {
                HashMap<String, String> newPlayerData = new HashMap<>();
                newPlayerData.put("name", player.getName());
                Storage.updatePlayerData(player.getUniqueId(), Main.getPlayerCache().get(player.getUniqueId()));
                Main.getPlayerCache().get(player.getUniqueId()).setName(player.getName());
            }
            Main.getPlayerCache().get(player.getUniqueId()).setOnline(true);
            Bukkit.getLogger().warning("Breakpoint 3: " + Main.getPlayerCache().get(player.getUniqueId()).getGuild());
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e){
        Player player = e.getPlayer();
        Main.getPlayerCache().get(player.getUniqueId()).setOnline(true);
        Bukkit.getLogger().warning(Main.getPlayerCache().toString());
    }
}
