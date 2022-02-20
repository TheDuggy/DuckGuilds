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
        sender.sendMessage(Main.getPlayerCache().toString());
        sender.sendMessage(Main.getGuildCache().toString());
        sender.sendMessage(String.valueOf(Main.getGuildCache().get("ÜÜ").get("head").getClass()));
        sender.sendMessage(String.valueOf(Utils.isPlayerInGuild((Player) sender)));
        return false;
    }

}
