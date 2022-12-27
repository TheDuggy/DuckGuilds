package at.theduggy.duckguilds.storage.systemTypes;

import at.theduggy.duckguilds.Main;
import at.theduggy.duckguilds.objects.GuildObject;
import at.theduggy.duckguilds.objects.GuildPlayerObject;

import java.io.IOException;
import java.util.UUID;

public abstract class StorageType {

    public StorageType(String storageSystemID){
        this.storageSystemID = storageSystemID;
    }
    private final String storageSystemID;
    public String getStorageSystemID(){
        return storageSystemID;
    }
    public abstract boolean personalPlayerSectionExists(UUID player);
    public abstract void createPersonalPlayerSection(GuildPlayerObject player) throws IOException;
    public abstract void createGuildSection(GuildObject guildData) throws IOException;
    public abstract void deleteGuildSection(GuildObject guildObject) throws IOException;
    public abstract void removePlayerFromGuildSection(GuildPlayerObject player, GuildObject guild) throws IOException;
    public abstract void deleteRootSection() throws IOException;
    public abstract String getPlayerNameFromPlayerSection(GuildPlayerObject player) throws IOException;
    public abstract void updatePlayerSection(GuildPlayerObject guildPlayerObject) throws IOException;
    public abstract void loadWithoutCaching();
    public abstract void deletePlayerSection(GuildPlayerObject playerObject) throws IOException;
    public abstract void addPlayerToGuildSection(GuildObject guild, GuildPlayerObject player) throws IOException;
    public abstract void load() throws IOException;
    protected void applyGuildsOnPlayers(){
        for (GuildObject guildObject: Main.getGuildCache().values()){
            for (UUID uuid:guildObject.getPlayers()){
                Main.getPlayerCache().get(uuid).setGuild(guildObject.getName());
            }
        }
    }


}
