package at.theduggy.duckguilds.utils;

import at.theduggy.duckguilds.Main;
import at.theduggy.duckguilds.objects.GuildObject;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

public class ScoreboardHandler {

    public static void addGuild(GuildObject guildObject){
        Team team;
        try {
            team = Main.getScoreboard().registerNewTeam(guildObject.getName());
        }catch (IllegalArgumentException e){
            team = Main.getScoreboard().getTeam(guildObject.getName());
        }

        team.setColor(guildObject.getGuildColor().getChatColor());
        team.setSuffix(ChatColor.GRAY + "[" + guildObject.getTagColor().getChatColor() + guildObject.getTag() + ChatColor.GRAY + "]");
        team.setDisplayName(guildObject.getName());
    }

    public static void updateScoreboardAddPlayer(Player player, GuildObject guildObject){
        Team team;
        try {
            team = Main.getScoreboard().registerNewTeam(guildObject.getName());
            team.setColor(guildObject.getGuildColor().getChatColor());
            team.setSuffix(ChatColor.GRAY + "[" + guildObject.getTagColor().getChatColor() + guildObject.getTag() + ChatColor.GRAY + "]" + ChatColor.WHITE);
            team.setDisplayName(guildObject.getName());
            team.addEntry(player.getName());
        }catch (IllegalArgumentException exception){
            team = Main.getScoreboard().getTeam(guildObject.getName());
        }
        String newPlayerName = guildObject.getGuildColor().getChatColor() + player.getName() + ChatColor.GRAY + "[" + guildObject.getTagColor().getChatColor() + guildObject.getTag() + ChatColor.GRAY + "]" + ChatColor.WHITE;
        for (Player otherPlayer: Bukkit.getOnlinePlayers()){
            otherPlayer.setScoreboard(Main.getScoreboard());
        }
        player.setDisplayName(newPlayerName);
        player.setCustomName(newPlayerName);
    }

}
