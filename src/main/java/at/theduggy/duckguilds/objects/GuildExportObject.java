package at.theduggy.duckguilds.objects;

import java.util.ArrayList;
import java.util.UUID;

public class GuildExportObject {

    private final ArrayList<GuildObject> guildObjects = new ArrayList<>();

    private final ArrayList<String[]> guildPlayers = new ArrayList<>();

    public void addGuild(GuildObject guildObject){
        guildObjects.add(guildObject);
    }

    public void addGuildPlayer(GuildPlayerObject guildPlayerObject){
        guildPlayers.add(new String[]{guildPlayerObject.getName(), guildPlayerObject.getUniqueId().toString()});
    }

    public ArrayList<GuildObject> getGuildObjects() {
        return guildObjects;
    }

    public ArrayList<GuildPlayerObject> getGuildPlayers() {
        ArrayList<GuildPlayerObject> players = new ArrayList<>();
        for (String[] data : guildPlayers){
            players.add(new GuildPlayerObject(UUID.fromString(data[1]), false, data[0], ""));
        }
        return players;
    }
}
