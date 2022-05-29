package at.theduggy.duckguilds.storage;

import at.theduggy.duckguilds.Main;
import at.theduggy.duckguilds.config.GuildConfigHandler;
import at.theduggy.duckguilds.exceptions.GuildDatabaseException;
import at.theduggy.duckguilds.objects.GuildObject;
import at.theduggy.duckguilds.objects.GuildPlayerObject;
import at.theduggy.duckguilds.storage.systemTypes.GuildFileSystem;
import at.theduggy.duckguilds.storage.systemTypes.MySql.MySqlSystem;
import org.bukkit.entity.Player;
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


    public void deleteGuildField(GuildObject guildObject) throws IOException {
        new Thread(() -> {
            if (storageType.equals(StorageType.File)){
                GuildFileSystem.deleteGuildFile(guildObject);
            }else if (storageType.equals(StorageType.MySQL)){
                try {
                    MySqlSystem.deleteGuildRecord(guildObject);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    public void removePlayerFromGuildField(GuildPlayerObject player,GuildObject guild) throws IOException, ParseException {
        new Thread(() -> {
            if (storageType.equals(StorageType.File)){
                try {
                    GuildFileSystem.removePlayerFromGuildFile(player, guild);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }else if (storageType.equals(StorageType.MySQL)){
                try {
                    MySqlSystem.removePlayerFromGuildRecord(player,guild);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    public String getPlayerNameFromPlayerField(GuildPlayerObject player) throws IOException, SQLException {
        if (storageType.equals(StorageType.File)){
            return GuildFileSystem.getPlayerNameFromPlayerFile(player);
        }else if (storageType.equals(StorageType.MySQL)){
            return MySqlSystem.getPlayerNameFromPlayerRecord(player);
        }
        return null;
    }

    public void updatePlayerData(GuildPlayerObject guildPlayerObject) throws IOException {
        new Thread(() -> {
            if (storageType.equals(StorageType.File)){
                try {
                    GuildFileSystem.updatePlayerFile(guildPlayerObject);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }else if (storageType.equals(StorageType.MySQL)){
                try {
                    MySqlSystem.updatePlayerRecord(guildPlayerObject);
                } catch (SQLException e) {
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

    public void addPlayerToGuildField(GuildObject guild, GuildPlayerObject player) {
        new Thread(() -> {
            if (storageType.equals(StorageType.File)){
                try {
                    GuildFileSystem.addPlayerToGuildFile(guild,player);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }else if (storageType.equals(StorageType.MySQL)){
                try {
                    MySqlSystem.addPlayerToGuildRecord(guild,player);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();

    }

    public enum StorageType{
        File,MySQL

    }
}