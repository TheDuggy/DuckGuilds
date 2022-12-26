package at.theduggy.duckguilds.storage.systemTypes;

import at.theduggy.duckguilds.Main;
import at.theduggy.duckguilds.config.GuildConfigHandler;
import at.theduggy.duckguilds.objects.GuildObject;
import at.theduggy.duckguilds.objects.GuildPlayerObject;
import at.theduggy.duckguilds.storage.StorageHandler;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.UUID;

public abstract class StorageType {

    public abstract boolean personalGuildPlayerStorageSectionExists(UUID player);
    public abstract void createPersonalPlayerStorageSection(GuildPlayerObject player, boolean inNewThread);
    public abstract void createGuildStorageSection(GuildObject guildData);
    public abstract void deleteGuildSection(GuildObject guildObject, boolean inNewThread);
    public abstract void removePlayerFromGuildSection(GuildPlayerObject player, GuildObject guild);
    public abstract void deleteRootStorageSection();
    public abstract String getPlayerNameFromPlayerSection(GuildPlayerObject player);
    public abstract void updatePlayerSection(GuildPlayerObject guildPlayerObject);
    public abstract void loadStorageWithoutCaching(StorageHandler.StorageType storageType);
    public abstract void loadStorage();
    public abstract void deletePlayerStorageSection(GuildPlayerObject playerObject);
    public abstract void addPlayerToGuildField(GuildObject guild, GuildPlayerObject player);
}
