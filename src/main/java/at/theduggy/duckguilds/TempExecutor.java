package at.theduggy.duckguilds;

import at.theduggy.duckguilds.other.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.json.simple.parser.ParseException;

import java.io.IOException;

public class TempExecutor implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage(Main.cachedPlayers.toString());
        sender.sendMessage(Main.cachedGuilds.toString());
        sender.sendMessage(String.valueOf(Main.cachedGuilds.get("ÜÜ").get("head").getClass()));
        try {
            sender.sendMessage(String.valueOf(Utils.isPlayerInGuild((Player) sender)));
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

}
