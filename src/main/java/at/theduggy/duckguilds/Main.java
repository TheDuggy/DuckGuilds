/*DuckGuilds: a plugin for creating/managing guilds
  Copyright (C) 2021 Georg Kollegger (or TheDuggy/CoderTheDuggy)

  This program is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.

  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with this program.  If not, see <http://www.gnu.org/licenses/>.*/
package at.theduggy.duckguilds;

import at.theduggy.duckguilds.commands.basCommand.GuildCommand;
import at.theduggy.duckguilds.commands.invite.GuildDeleteInviteOnPlayerLeave;
import at.theduggy.duckguilds.config.GuildConfigHandler;
import at.theduggy.duckguilds.objects.GuildColor;
import at.theduggy.duckguilds.objects.GuildMetadata;
import at.theduggy.duckguilds.objects.GuildObject;
import at.theduggy.duckguilds.objects.GuildPlayerObject;
import at.theduggy.duckguilds.objects.gson.GuildColorTAdapter;
import at.theduggy.duckguilds.objects.gson.GuildMetadataTAdapter;
import at.theduggy.duckguilds.startUp.GuildPlayerHandler;
import at.theduggy.duckguilds.storage.StorageHandler;
import at.theduggy.duckguilds.storage.systemTypes.MySql.MySqlSystem;
import at.theduggy.duckguilds.utils.GuildTextUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import org.json.simple.parser.ParseException;


import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;

public final class Main extends JavaPlugin {

private static final HashMap<UUID, GuildPlayerObject> cachedPlayers = new HashMap<>();
public static FileConfiguration mainFileConfiguration;
private static Scoreboard scoreboard;
public static Plugin plugin;
private static final HashMap<String, GuildObject> cachedGuilds = new HashMap<>();
public static File guildRootFolder;
private static StorageHandler mainStorageHandler;
private static boolean isStorageBusy = false;
public static Path loggingFolder;

    @Override
    public void onLoad(){
        this.saveDefaultConfig();
        plugin = this;
        mainFileConfiguration = this.getConfig();
        logIgnoreConfig("---------------config values---------------", LogLevel.WARNING);
        logIgnoreConfig("               ---Server---", LogLevel.WARNING);
        logIgnoreConfig("inviteDeleteTime: " + GuildConfigHandler.getTimeTillInviteIsDeleted(), LogLevel.WARNING);
        logIgnoreConfig("maxGuilds: " + GuildConfigHandler.getMaxGuildSize(), LogLevel.WARNING );
        logIgnoreConfig("logging: " + GuildConfigHandler.getLoggingType(),LogLevel.WARNING);
        logIgnoreConfig("               ---Storage---", LogLevel.WARNING);
        logIgnoreConfig("storageType: " + GuildConfigHandler.getStorageType(), LogLevel.WARNING);
        logIgnoreConfig("useFileSystemOnInvalidConnection: " + mainFileConfiguration.getBoolean("useFileSystemOnInvalidConnection"), LogLevel.WARNING);
        logIgnoreConfig("guildDirRootPath: " + mainFileConfiguration.getString("guildDirRootPath"), LogLevel.WARNING);
        logIgnoreConfig("deleteOldStorageSectionsWhileMigration: " + mainFileConfiguration.getBoolean("deleteOldStorageSectionsWhileMigration"),LogLevel.WARNING);

    }

    @Override
    public void onEnable(){
        Bukkit.getPluginManager().registerEvents(new GuildDeleteInviteOnPlayerLeave(), this);
        try {
            if (!Files.exists(Paths.get(this.getDataFolder() + "/database.yml"))){
                Files.copy(this.getClassLoader().getResourceAsStream("database.yml"), Paths.get(this.getDataFolder() + "/database.yml"));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.saveDefaultConfig();

        plugin = this;
        mainFileConfiguration = this.getConfig();
        if (GuildConfigHandler.getStorageType()!=null) {
            mainStorageHandler = new StorageHandler(GuildConfigHandler.getStorageType());

            /*
            try {
                FakePlayerData.initFakePlayer();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
             */


            try {
                guildRootFolder = GuildConfigHandler.getGuildRootFolder();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
            try {
                commandRegistration();
                listenerRegistration();
                mainStorageHandler.loadStorage();
                GuildPlayerHandler.handlePlayersOnReload();
            } catch (IOException | ParseException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }else {
            shutDown("Invalid storage-type!");
        }

    }
    
    @Override
    public void onDisable(){
        this.saveDefaultConfig();
        try {
            if (GuildConfigHandler.getStorageType()!=null&&GuildConfigHandler.getStorageType().equals(StorageHandler.StorageType.MySQL)&&MySqlSystem.connectionAvailable()){
                MySqlSystem.close();
            }
        } catch (FileNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void commandRegistration(){
        getCommand("guild").setExecutor(new GuildCommand());
        getCommand("guild").setTabCompleter(new GuildCommand());
        /*
        getCommand("guildFakePlayer").setExecutor(new FakeGuildPlayer());
        getCommand("guildFakePlayer").setTabCompleter(new FakeGuildPlayer());
         */
    }

    public void listenerRegistration(){
        Bukkit.getPluginManager().registerEvents(new GuildPlayerHandler(), this);
    }


    public static HashMap<String, GuildObject> getGuildCache(){
        return cachedGuilds;
    }

    public static HashMap<UUID, GuildPlayerObject> getPlayerCache(){
        return cachedPlayers;
    }

    public static Scoreboard getScoreboard(){
        return scoreboard;
    }


    private static Gson gson;
    public static Gson getGsonInstance(){
        if (gson==null) {
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.setPrettyPrinting();
            gsonBuilder.registerTypeAdapter(GuildColor.class, new GuildColorTAdapter());
            gsonBuilder.registerTypeAdapter(GuildMetadata.class, new GuildMetadataTAdapter());
            gson=gsonBuilder.create();
        }
        return gson;
    }

    public static void shutDown(String shutdownMessage){
        log(shutdownMessage, LogLevel.WARNING);
        log("Disabling...", LogLevel.WARNING);
        Bukkit.getPluginManager().disablePlugin(plugin);
    }

    public static StorageHandler getMainStorage(){
        return mainStorageHandler;
    }

    public static void log(String msg, LogLevel logLevel){
        if (GuildConfigHandler.getLoggingType()==GuildConfigHandler.LoggingType.ALL){
            switch (logLevel){
                case WARNING: Bukkit.getLogger().warning(GuildTextUtils.prefixWithoutColor + msg); break;
                case DEFAULT: Bukkit.getLogger().info(GuildTextUtils.prefixWithoutColor + msg); break;
            }
        }else if (GuildConfigHandler.getLoggingType()== GuildConfigHandler.LoggingType.WARNINGS_ONLY){
            if (logLevel==LogLevel.WARNING){
                Bukkit.getLogger().warning(GuildTextUtils.prefixWithoutColor + msg);
            }
        }

    }

    public static void logIgnoreConfig(String msg, LogLevel logLevel){
        switch (logLevel){
            case DEFAULT: Bukkit.getLogger().info(GuildTextUtils.prefixWithoutColor + msg);break;
            case WARNING: Bukkit.getLogger().warning(GuildTextUtils.prefixWithoutColor + msg);break;
        }
    }

    public enum LogLevel{
        DEFAULT,
        WARNING
    }

    public static boolean isIsStorageBusy(){
        return isStorageBusy;
    }

    public static void setStorageBusy(boolean busy){
        isStorageBusy=busy;
    }

}