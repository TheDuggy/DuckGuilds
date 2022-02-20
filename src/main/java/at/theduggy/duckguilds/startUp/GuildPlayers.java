package at.theduggy.duckguilds.startUp;

import at.theduggy.duckguilds.Main;
import at.theduggy.duckguilds.files.GuildFiles;
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
            if (!GuildFiles.checkForPersonalPlayerFile(player.getUniqueId())){
                GuildFiles.createPersonalPlayerFile(player);
                HashMap<String,Object> playerData = new HashMap<>();
                playerData.put("name",player.getName());
                playerData.put("guild","");
                playerData.put("online",true);
                Main.getPlayerCache().put(player.getUniqueId(),playerData);
                Bukkit.getLogger().warning(Main.getPlayerCache().toString());
            }else if (Main.getPlayerCache().containsKey(player.getUniqueId())){
                Main.getPlayerCache().get(player.getUniqueId()).replace("online",true);
            }else {
                Storage.cachePlayer(player.getUniqueId(),"");
            }
        }
    }

    @EventHandler
    public void handlePlayersOnJoin(PlayerJoinEvent e) throws IOException, ParseException {
        Player player = e.getPlayer();
        if (!GuildFiles.checkForPersonalPlayerFile(player.getUniqueId())){
            GuildFiles.createPersonalPlayerFile(player);
            HashMap<String,Object> playerData = new HashMap<>();
            playerData.put("name",player.getName());
            playerData.put("guild","");
            playerData.put("online",true);
            Main.getPlayerCache().put(player.getUniqueId(),playerData);
            Bukkit.getLogger().warning("Breakpoint 1: " +  Main.getPlayerCache().toString());
        }else if (!Main.getPlayerCache().get(player.getUniqueId()).get("guild").equals("")){
            String oldName = Storage.getPlayerDataFromStorage(player.getUniqueId());
            if (!oldName.equals(player.getName())) {
                HashMap<String, String> newPlayerData = new HashMap<>();
                newPlayerData.put("name", player.getName());
                Storage.updatePlayerData(player.getUniqueId(), newPlayerData);
                Main.getPlayerCache().get(player.getUniqueId()).replace("name","TestName");
                Main.getPlayerCache().get(player.getUniqueId()).replace("online",true);
            }
            Team team;
            String guildName = (String) Main.getPlayerCache().get(player.getUniqueId()).get("guild");
            try {
                team = Main.getScoreboard().registerNewTeam(guildName);
            }catch (IllegalArgumentException exception){
                team = Main.getScoreboard().getTeam(guildName);
            }
            String newPlayerName = Main.getGuildCache().get(guildName).get("color") + player.getName() + ChatColor.GRAY + "[" + Main.getGuildCache().get(guildName).get("tagColor") + Main.getGuildCache().get(guildName).get("tag") + ChatColor.GRAY + "]" + ChatColor.WHITE;
            team.setColor((ChatColor) Main.getGuildCache().get(guildName).get("color"));
            team.setSuffix(ChatColor.GRAY + "[" + Main.getGuildCache().get(guildName).get("tagColor") + Main.getGuildCache().get(guildName).get("tag") + ChatColor.GRAY + "]" + ChatColor.WHITE);
            team.setDisplayName(guildName);
            team.addEntry(player.getName());
            player.setDisplayName(newPlayerName);
            player.setCustomName(newPlayerName);
            for (Player playerFromServer:Bukkit.getServer().getOnlinePlayers()){
                playerFromServer.setScoreboard(Main.getScoreboard());
            }


        }else {
            HashMap<String,Object> playerData = new HashMap<>();
            playerData.put("name",player.getName());
            playerData.put("online",true);
            playerData.put("guild","");
            Bukkit.getLogger().warning("Breakpoint 3: " + Main.getPlayerCache().toString());
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e){
        Player player = e.getPlayer();
        Main.getPlayerCache().get(player.getUniqueId()).replace("online",false);
        Bukkit.getLogger().warning(Main.getPlayerCache().toString());
    }
}
