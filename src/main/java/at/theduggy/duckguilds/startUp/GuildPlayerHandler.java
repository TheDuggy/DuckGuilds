package at.theduggy.duckguilds.startUp;

import at.theduggy.duckguilds.Main;
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

public class GuildPlayerHandler implements Listener {

    public static void handlePlayersOnReload() throws IOException, ParseException, SQLException {
        for (Player player:Bukkit.getServer().getOnlinePlayers()){
            addPlayerToTeam(player);
            Main.getPlayerCache().get(player.getUniqueId()).setOnline(true);
        }
    }

    @EventHandler
    public void handlePlayersOnJoin(PlayerJoinEvent e){
        Player player = e.getPlayer();
        addPlayerToTeam(player);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e){
        Player player = e.getPlayer();
        Main.getPlayerCache().get(player.getUniqueId()).setOnline(true);
    }

    private static void addPlayerToTeam(Player player) {
        if (!Main.getMainStorage().personalGuildPlayerStorageSectionExists(player.getUniqueId())){
            GuildPlayerObject guildPlayerObject = new GuildPlayerObject(player.getUniqueId(),true,player.getName(),"");
            Main.getMainStorage().createPersonalPlayerStorageSection(guildPlayerObject,true);
            Main.getPlayerCache().put(player.getUniqueId(), guildPlayerObject);
        }else{
            String oldName = Main.getMainStorage().getPlayerNameFromPlayerSection(Main.getPlayerCache().get(player.getUniqueId()));
            if (!oldName.equals(player.getName())) {
                Main.getMainStorage().updatePlayerSection(Main.getPlayerCache().get(player.getUniqueId()));
                Main.getPlayerCache().get(player.getUniqueId()).setName(player.getName());
            }
            if (!Main.getPlayerCache().get(player.getUniqueId()).getGuild().equals("")){
                ScoreboardHandler.updateScoreboardAddPlayer(player, Main.getGuildCache().get(Main.getPlayerCache().get(player.getUniqueId()).getGuild()));
            }
            Main.getPlayerCache().get(player.getUniqueId()).setOnline(true);
        }
    }
}
