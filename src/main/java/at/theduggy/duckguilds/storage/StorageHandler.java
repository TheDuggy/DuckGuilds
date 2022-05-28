package at.theduggy.duckguilds.storage;

import at.theduggy.duckguilds.Main;
import at.theduggy.duckguilds.config.GuildConfigHandler;
import at.theduggy.duckguilds.exceptions.GuildDatabaseException;
import at.theduggy.duckguilds.objects.GuildObject;
import at.theduggy.duckguilds.objects.GuildPlayerObject;
import at.theduggy.duckguilds.storage.systemTypes.GuildFileSystem;
import at.theduggy.duckguilds.storage.systemTypes.MySql.MySqlSystem;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.sql.SQLException;
import java.util.UUID;

public class StorageHandler {

    public StorageType storageType;

    public StorageHandler(StorageType storageType){
        this.storageType=storageType;
    }


    public boolean personalGuildPlayerStorageSectionExists(UUID player) throws SQLException {
        if (storageType.equals(StorageType.File)){
            return GuildFileSystem.personalGuildPlayerFileExists(player);
        }else if(storageType.equals(StorageType.MySQL)){
            return MySqlSystem.personalPlayerTableExists(player);
        }
        return false;
    }

    public void createPersonalPlayerStorageSection(Player player) throws IOException, SQLException, GuildDatabaseException {
        new Thread(() -> {
            if (storageType.equals(StorageType.File)){
                try {
                    GuildFileSystem.createPersonalPlayerFile(player);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }else if (storageType == StorageType.MySQL){
                try {
                    MySqlSystem.createPersonalPlayerTable(player);
                } catch (SQLException | GuildDatabaseException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    public void createGuildStorageField(GuildObject guildData) throws IOException, SQLException, GuildDatabaseException {
        if (storageType.equals(StorageType.File)){
            GuildFileSystem.createGuildFile(guildData);
        }if (storageType.equals(StorageType.MySQL)){
            MySqlSystem.createGuildRecord(guildData);
        }
    }


    public void deleteGuildField(String guildName) throws IOException {
        new Thread(() -> {
            if (storageType.equals(StorageType.File)){
                GuildFileSystem.deleteGuildFile(guildName);
            }
        }).start();
    }

    public void removePlayerFromGuildField(UUID player,String guildName) throws IOException, ParseException {
        new Thread(() -> {
            if (storageType.equals(StorageType.File)){
                try {
                    GuildFileSystem.removePlayerFromGuildFile(player, guildName);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    public String getPlayerDataFromStorage(UUID player) throws IOException, ParseException {
        if (storageType.equals(StorageType.File)){
            return GuildFileSystem.getPlayerDataFromFile(player);
        }
        return null;
    }

    public void updatePlayerData(GuildPlayerObject guildPlayerObject) throws IOException {
        new Thread(() -> {
            if (storageType.equals(StorageType.File)){
                try {
                    GuildFileSystem.updatePlayerData(guildPlayerObject);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }


    public void loadStorage() throws IOException, SQLException, GuildDatabaseException {
        if (storageType.equals(StorageType.File)){
            GuildFileSystem.init();
        }else if (storageType.equals(StorageType.MySQL)){
            if (MySqlSystem.connectionAvailable()){
                MySqlSystem.init();
            }else if (GuildConfigHandler.useFileSystemOnInvalidConnection()){
                GuildFileSystem.init();
            }else {
                Main.shutDown("Failed to connect to " + GuildConfigHandler.getDataBase().getJdbcUrl() + "!");
            }
        }
    }

    public void addPlayerToGuildField(GuildObject player) {
        new Thread(() -> {
            if (storageType.equals(StorageType.File)){
                try {
                    GuildFileSystem.addPlayerToGuildFile(player);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();

    }

    public enum StorageType{
        File,MySQL

    }
}