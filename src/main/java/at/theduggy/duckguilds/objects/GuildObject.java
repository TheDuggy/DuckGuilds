package at.theduggy.duckguilds.objects;

import at.theduggy.duckguilds.Main;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.UUID;

public class GuildObject {

    private ArrayList<UUID> players;
    private GuildColor color;
    private GuildColor tagColor;
    private String name;
    private String tag;
    private UUID head;
    private GuildMetadata guildMetadata;

    public GuildMetadata getGuildMetadata() {
        return guildMetadata;
    }

    public void setGuildMetadata(GuildMetadata guildMetadata) {
        this.guildMetadata = guildMetadata;
    }

    public void setName(String name) {
        this.name = name;
    }


    public void setTag(String tag) {
        this.tag = tag;
    }

    public void setHead(UUID head) {
        this.head = head;
    }

    public void setPlayers(ArrayList<UUID> players) {
        this.players = players;
    }

    public void setGuildColor(GuildColor color) {
        this.color = color;
    }

    public void setTagColor(GuildColor tagColor) {
        this.tagColor = tagColor;
    }

    public String getName() {
        return name;
    }

    public String getTag() {
        return tag;
    }

    public UUID getHead() {
        return head;
    }

    public ArrayList<UUID> getPlayers() {
        return players;
    }

    public GuildColor getGuildColor() {
        return color;
    }

    public GuildColor getTagColor() {
        return tagColor;
    }

    public String toString(){
        return Main.getGsonInstance().toJson(this);
    }

}
