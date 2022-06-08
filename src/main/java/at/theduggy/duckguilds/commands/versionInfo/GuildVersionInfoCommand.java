package at.theduggy.duckguilds.commands.versionInfo;

import at.theduggy.duckguilds.Main;
import at.theduggy.duckguilds.utils.GuildTextUtils;
import net.md_5.bungee.api.chat.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.PluginDescriptionFile;


public class GuildVersionInfoCommand {

    public static BaseComponent[] guildVersionInfo(){

        TextComponent line1 = new TextComponent(GuildTextUtils.prefix + ChatColor.GREEN + "DuckGuilds-version-info: ");
        TextComponent line2 = new TextComponent(ChatColor.WHITE + "-".repeat(line1.getText().length()));
        PluginDescriptionFile pluginDescriptionFile = Main.getPlugin(Main.class).getDescription();
        TextComponent line3 =  new TextComponent(ChatColor.YELLOW + "Version: " + ChatColor.GRAY + pluginDescriptionFile.getVersion() + "(" + ChatColor.RED + "latest: " + newerVersion() + ")");

        TextComponent githubLink = new TextComponent(ChatColor.GREEN + "GitHub");
        githubLink.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.GRAY + "Go to the GitHub-page!").italic(true).create()));
        githubLink.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL,"https://github.com/CoderTheDuggy"));

        TextComponent spigotLink = new TextComponent(ChatColor.GREEN + "Spigot");
        spigotLink.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.GRAY + "Go to the Spigot-page (download)!").italic(true).create()));
        spigotLink.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL,"https://www.spigotmc.org/"));

        TextComponent line4String = new TextComponent(ChatColor.YELLOW + "Links: ");
        TextComponent line4 = new TextComponent(line4String,githubLink,new TextComponent(ChatColor.GRAY + ","),spigotLink);

        ComponentBuilder msg = new ComponentBuilder(new TextComponent(line1));
        System.out.println(line1.getText());
        msg.append(line1);
        msg.append(line2);
        msg.append(line3);
        msg.append(line4);

        return msg.create();
    }


    private static String newerVersion(){
        return "CURRENT";
    }
}
