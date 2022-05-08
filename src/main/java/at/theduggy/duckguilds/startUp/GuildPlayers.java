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

public class GuildPlayers implements Listener {

    public static void handlePlayersOnReload() throws IOException, ParseException {
        for (Player player:Bukkit.getServer().getOnlinePlayers()){
            addPlayerToTeam(player);
        }
    }

    @EventHandler
    public void handlePlayersOnJoin(PlayerJoinEvent e) throws IOException, ParseException {
        Player player = e.getPlayer();
        addPlayerToTeam(player);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e){
        Player player = e.getPlayer();
        Main.getPlayerCache().get(player.getUniqueId()).setOnline(true);
    }

    private static void addPlayerToTeam(Player player) throws IOException, ParseException {
        if (!Main.getMainStorage().personalGuildPlayerStorageSectionExists(player.getUniqueId())){
            Main.getMainStorage().createPersonalPlayerStorageSection(player);
            GuildPlayerObject guildPlayerObject = new GuildPlayerObject(player.getUniqueId(),true,player.getName(),"");
            Main.getPlayerCache().put(player.getUniqueId(), guildPlayerObject);
            Team team;
            String guildName =  Main.getPlayerCache().get(player.getUniqueId()).getGuild();
            try {
                team = Main.getScoreboard().registerNewTeam(guildName);
            }catch (IllegalArgumentException exception){
                team = Main.getScoreboard().getTeam(guildName);
            }
            String newPlayerName = Main.getGuildCache().get(guildName).getGuildColor() + player.getName() + ChatColor.GRAY + "[" + Main.getGuildCache().get(guildName).getTagColor().getChatColor() + Main.getGuildCache().get(guildName).getTag() + ChatColor.GRAY + "]" + ChatColor.WHITE;
            team.setColor(Main.getGuildCache().get(guildName).getGuildColor().getChatColor());
            team.setSuffix(ChatColor.GRAY + "[" + Main.getGuildCache().get(guildName).getTagColor().getChatColor() + Main.getGuildCache().get(guildName).getTag() + ChatColor.GRAY + "]" + ChatColor.WHITE);
            team.setDisplayName(guildName);
            team.addEntry(player.getName());
            player.setDisplayName(newPlayerName);
            player.setCustomName(newPlayerName);
            for (Player playerFromServer:Bukkit.getServer().getOnlinePlayers()){
                playerFromServer.setScoreboard(Main.getScoreboard());
            }
        }else if (Main.getPlayerCache().containsKey(player.getUniqueId())){
            System.out.println(Main.getGuildCache().size());
            String oldName = Main.getMainStorage().getPlayerDataFromStorage(player.getUniqueId());
            if (!oldName.equals(player.getName())) {
                Main.getMainStorage().updatePlayerData(player.getUniqueId(), Main.getPlayerCache().get(player.getUniqueId()), player.getName());
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
            String newPlayerName = Main.getGuildCache().get(guildName).getGuildColor().getChatColor() + player.getName() + ChatColor.GRAY + "[" + Main.getGuildCache().get(guildName).getTagColor().getChatColor() + Main.getGuildCache().get(guildName).getTag() + ChatColor.GRAY + "]" + ChatColor.WHITE;
            team.setColor( Main.getGuildCache().get(guildName).getGuildColor().getChatColor());
            team.setSuffix(ChatColor.GRAY + "[" + Main.getGuildCache().get(guildName).getTagColor().getChatColor() + Main.getGuildCache().get(guildName).getTag() + ChatColor.GRAY + "]" + ChatColor.WHITE);
            team.setDisplayName(guildName);
            team.addEntry(player.getName());
            player.setDisplayName(newPlayerName);
            player.setCustomName(newPlayerName);
            for (Player playerFromServer:Bukkit.getServer().getOnlinePlayers()){
                playerFromServer.setScoreboard(Main.getScoreboard());
            }
            System.out.println("Breakpoint 2!");
        }else {
            GuildPlayerObject guildPlayerObject = new GuildPlayerObject(player.getUniqueId(),true, player.getName(), "");
            Main.getPlayerCache().put(player.getUniqueId(),guildPlayerObject);
            Bukkit.getLogger().warning("Breakpoint 3: " + Main.getPlayerCache().size());
        }
    }
}
