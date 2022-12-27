package at.theduggy.duckguilds.storage.systemTypes;

import at.theduggy.duckguilds.Main;
import at.theduggy.duckguilds.objects.GuildObject;
import at.theduggy.duckguilds.objects.GuildPlayerObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.UUID;

public abstract class StorageType {

    public StorageType(String storageSystemID){
        this.storageSystemID = storageSystemID;
    }
    private final String storageSystemID;
    public String getStorageSystemID(){
        return storageSystemID;
    }
    public abstract boolean personalPlayerSectionExists(UUID player) throws SQLException;
    public abstract void createPersonalPlayerSection(GuildPlayerObject player) throws IOException, SQLException;
    public abstract void createGuildSection(GuildObject guildData) throws IOException, SQLException;
    public abstract void deleteGuildSection(GuildObject guildObject) throws IOException, SQLException;
    public abstract void removePlayerFromGuildSection(GuildPlayerObject player, GuildObject guild) throws IOException, SQLException;
    public abstract void deleteRootSection() throws IOException, SQLException;
    public abstract String getPlayerNameFromPlayerSection(GuildPlayerObject player) throws IOException, SQLException;
    public abstract void updatePlayerSection(GuildPlayerObject guildPlayerObject) throws IOException, SQLException;
    public abstract void loadWithoutCaching() throws SQLException, FileNotFoundException;
    public abstract void deletePlayerSection(GuildPlayerObject playerObject) throws IOException, SQLException;
    public abstract void addPlayerToGuildSection(GuildObject guild, GuildPlayerObject player) throws IOException, SQLException;
    public abstract void load() throws IOException, SQLException;
    protected void applyGuildsOnPlayers(){
        for (GuildObject guildObject: Main.getGuildCache().values()){
            for (UUID uuid:guildObject.getPlayers()){
                Main.getPlayerCache().get(uuid).setGuild(guildObject.getName());
            }
        }
    }


}
