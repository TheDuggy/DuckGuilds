package at.theduggy.duckguilds.storage.systemTypes;

import at.theduggy.duckguilds.Main;

import java.io.IOException;
import java.nio.file.Files;

import static at.theduggy.duckguilds.storage.systemTypes.GuildFileSystem.GUILD_DATA_FOLDER;
import static at.theduggy.duckguilds.storage.systemTypes.GuildFileSystem.PLAYER_DATA_FOLDER;

public class MySqlSystem {

    public static void initTables() throws IOException {
        if (!GUILD_DATA_FOLDER.exists()){
            Files.createDirectory(GUILD_DATA_FOLDER.toPath());
        }
        if (!PLAYER_DATA_FOLDER.exists()){
            Files.createDirectory(PLAYER_DATA_FOLDER.toPath());
        }
    }

    /*

    private static String executeStatement(String statement){

    } */
}
