package at.theduggy.duckguilds.objects;

import at.theduggy.duckguilds.other.GuildTextUtils;
import at.theduggy.duckguilds.other.Utils;
import org.bukkit.ChatColor;

public class GuildColor {

    String color;

    public GuildColor(String color){
        this.color=color;
    }

    public GuildColor(ChatColor color){
        this.color = GuildTextUtils.chatColorToString(color);
    }

    public ChatColor getChatColor(){
        return GuildTextUtils.translateFromReadableStringToChatColorAllColors(color);
    }

    public String toString(){
        return color;
    }

}
