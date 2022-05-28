package at.theduggy.duckguilds.startUp;

import at.theduggy.duckguilds.Main;
import at.theduggy.duckguilds.exceptions.GuildDatabaseException;
import at.theduggy.duckguilds.objects.GuildPlayerObject;
import at.theduggy.duckguilds.utils.ScoreboardHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.sql.SQLException;

public class GuildPlayers implements Listener {

    public static void handlePlayersOnReload() throws IOException, ParseException, SQLException, GuildDatabaseException {
        for (Player player:Bukkit.getServer().getOnlinePlayers()){
            addPlayerToTeam(player);
        }
    }

    @EventHandler
    public void handlePlayersOnJoin(PlayerJoinEvent e) throws IOException, ParseException, SQLException, GuildDatabaseException {
        Player player = e.getPlayer();
        addPlayerToTeam(player);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e){
        Player player = e.getPlayer();
        Main.getPlayerCache().get(player.getUniqueId()).setOnline(true);
    }

    private static void addPlayerToTeam(Player player) throws IOException, ParseException, SQLException, GuildDatabaseException {
        if (!Main.getMainStorage().personalGuildPlayerStorageSectionExists(player.getUniqueId())){
            Main.getMainStorage().createPersonalPlayerStorageSection(player);
            GuildPlayerObject guildPlayerObject = new GuildPlayerObject(player.getUniqueId(),true,player.getName(),"");
            Main.getPlayerCache().put(player.getUniqueId(), guildPlayerObject);
        }else if (Main.getPlayerCache().containsKey(player.getUniqueId())){
            String oldName = Main.getMainStorage().getPlayerDataFromStorage(player.getUniqueId());
            if (!oldName.equals(player.getName())) {
                Main.getMainStorage().updatePlayerData(Main.getPlayerCache().get(player.getUniqueId()));
                Main.getPlayerCache().get(player.getUniqueId()).setName(player.getName());
            }
            if (!Main.getPlayerCache().get(player.getUniqueId()).getGuild().equals("")){
                ScoreboardHandler.updateScoreboardAddPlayer(player, Main.getGuildCache().get(Main.getPlayerCache().get(player.getUniqueId()).getGuild()));
            }
            System.out.println(Main.getGuildCache().size());
            Main.getPlayerCache().get(player.getUniqueId()).setOnline(true);
            System.out.println("Breakpoint 2!");
        }else {
            GuildPlayerObject guildPlayerObject = new GuildPlayerObject(player.getUniqueId(),true, player.getName(), "");
            Main.getPlayerCache().put(player.getUniqueId(),guildPlayerObject);
            Bukkit.getLogger().warning("Breakpoint 3: " + Main.getPlayerCache().size());
        }
    }
}
