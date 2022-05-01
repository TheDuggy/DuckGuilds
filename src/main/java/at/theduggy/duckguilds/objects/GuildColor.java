package at.theduggy.duckguilds.objects;

import at.theduggy.duckguilds.other.Utils;
import org.bukkit.ChatColor;

public class GuildColor {

    String color;

    public GuildColor(String color){
        this.color=color;
    }

    public GuildColor(ChatColor color){
        this.color = Utils.chatColorToString(color);
    }

    public ChatColor getChatColor(){
        return Utils.translateFromReadableStringToChatColorAllColors(color);
    }

    public String toString(){
        return color;
    }

}
