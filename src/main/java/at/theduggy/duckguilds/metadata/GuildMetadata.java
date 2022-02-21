package at.theduggy.duckguilds.metadata;

import at.theduggy.duckguilds.other.Utils;
import org.bukkit.ChatColor;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.UUID;

public class GuildMetadata {

    private ArrayList<UUID> players;
    private ChatColor color;
    private ChatColor tagColor;
    private String name;
    private String tag;
    private UUID head;



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

    public void setColor(ChatColor color) {
        this.color = color;
    }

    public void setTagColor(ChatColor tagColor) {
        this.tagColor = tagColor;
    }

    public void addPlayer(UUID playerToAdd){
        players.add(playerToAdd);
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

    public ChatColor getColor() {
        return color;
    }

    public ChatColor getTagColor() {
        return tagColor;
    }

    public String toString(){
        JSONObject data = new JSONObject();
        data.put("name",name);
        data.put("tag",tag);
        data.put("head",head.toString());
        ArrayList<String> newUUIDs = new ArrayList<>();
        for (UUID uuid:players){
            newUUIDs.add(uuid.toString());
        }
        data.put("players", newUUIDs);
        data.put("color", Utils.getChatColorCode(color));
        data.put("tagColor",Utils.getChatColorCode(tagColor));
        return data.toJSONString();
    }
}
