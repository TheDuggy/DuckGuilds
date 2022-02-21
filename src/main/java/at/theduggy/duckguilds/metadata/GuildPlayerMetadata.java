package at.theduggy.duckguilds.metadata;

import org.json.simple.JSONObject;

import java.util.UUID;

public class GuildPlayerMetadata {

    private UUID player;
    private String name;
    private String guild;
    private Boolean online;

    public GuildPlayerMetadata(UUID player, Boolean online, String name, String guild){
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

    public String toString(){
        JSONObject data = new JSONObject();
        data.put("uuid",player.toString());
        data.put("name",name);
        data.put("guild",guild);//TODO Remove !!!! Only temp for debugging!
        data.put("online",online);//TODO Remove !!!! Only temp for debugging!
        return data.toJSONString();
    }

}
