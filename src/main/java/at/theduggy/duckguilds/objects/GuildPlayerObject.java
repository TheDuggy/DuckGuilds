package at.theduggy.duckguilds.objects;

import com.google.gson.annotations.Expose;
import org.json.simple.JSONObject;

import java.util.UUID;

public class GuildPlayerObject {

    private transient UUID player;

    private String name;

    private transient String guild;

    private transient Boolean online;

    public GuildPlayerObject(UUID player, Boolean online, String name, String guild){
        this.player=player;
        this.name=name;
        this.guild=guild;
        this.online=online;
    }

    public UUID getUniqueId() {
        return player;
    }

    public String getName() {
        return name;
    }

    public String getGuild() {
        return guild;
    }

    public boolean isOnline() {
        return online;
    }

    public void setGuild(String guild) {
        this.guild = guild;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPlayer(UUID player) {
        this.player = player;
    }

    public String toString(){
        return player.toString() + " (" + name + ")";
    }

}
