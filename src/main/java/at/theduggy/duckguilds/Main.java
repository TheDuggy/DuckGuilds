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

import at.theduggy.duckguilds.commands.GuildCommand;
import at.theduggy.duckguilds.commands.invite.GuildDeleteInviteOnPlayerLeave;
import at.theduggy.duckguilds.config.GuildConfigHandler;
import at.theduggy.duckguilds.logging.GuildLogger;
import at.theduggy.duckguilds.objects.GuildColor;
import at.theduggy.duckguilds.objects.GuildMetadata;
import at.theduggy.duckguilds.objects.GuildObject;
import at.theduggy.duckguilds.objects.GuildPlayerObject;
import at.theduggy.duckguilds.objects.gson.GuildColorTAdapter;
import at.theduggy.duckguilds.objects.gson.GuildMetadataTAdapter;
import at.theduggy.duckguilds.startUp.GuildPlayerHandler;
import at.theduggy.duckguilds.storage.StorageHandler;
import at.theduggy.duckguilds.storage.systemTypes.MySqlSystem;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.log4j.LogManager;
import org.apache.log4j.helpers.LogLog;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;
import org.json.simple.parser.ParseException;


import java.io.*;
import java.nio.file.Files;
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
private static GuildConfigHandler guildConfigHandler;

    @Override
    public void onLoad(){
        this.saveDefaultConfig();
        plugin = this;

    }

    @Override
    public void onEnable(){
        guildConfigHandler = new GuildConfigHandler(this.getConfig());
        if (guildConfigHandler.showConfBanner()){
            GuildLogger.getLogger().debug("---------------config values---------------");
            GuildLogger.getLogger().debug("               ---General---");
            GuildLogger.getLogger().debug("config-banner: " + Main.getGuildConfigHandler().showConfBanner());
            GuildLogger.getLogger().debug("invite-delete-time: " + Main.getGuildConfigHandler().getTimeDeleteTime());
            GuildLogger.getLogger().debug("max-guilds: " + Main.getGuildConfigHandler().getMaxGuildSize());
            GuildLogger.getLogger().debug("               ---Logging---");
            GuildLogger.getLogger().debug("logging-path: " + guildConfigHandler.getLoggingPath());
            GuildLogger.getLogger().debug("logging-level: " + guildConfigHandler.getLogLevel());
            GuildLogger.getLogger().debug("max-log-file-size: " + guildConfigHandler.getMaxLogFileSize());
            GuildLogger.getLogger().debug("               ---Storage---");
            GuildLogger.getLogger().debug("storage-type: " + Main.getGuildConfigHandler().getStorageType());
            GuildLogger.getLogger().debug("file-on-con-fail: " + guildConfigHandler.fileOnConFail());
            GuildLogger.getLogger().debug("guild-root-path: " + guildConfigHandler.getGuildRootPath());
            GuildLogger.getLogger().debug("del-old-storage: " + guildConfigHandler.delOldStorage());
        }

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

        if (Main.getGuildConfigHandler().getStorageType()!=null) {
            guildRootFolder = Main.getGuildConfigHandler().getGuildRootPath();
            mainStorageHandler = new StorageHandler(Main.getGuildConfigHandler().getStorageType());

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
        LogManager.shutdown();
        try {
            if (Main.getGuildConfigHandler().getStorageType()!=null&&mainStorageHandler.getStorageType() instanceof MySqlSystem&&MySqlSystem.connectionAvailable()){
                ((MySqlSystem) mainStorageHandler.getStorageType()).close();
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
        GuildLogger.getLogger().error(shutdownMessage);
        GuildLogger.getLogger().error("Disabling...");
        Bukkit.getPluginManager().disablePlugin(plugin);
    }

    public static StorageHandler getMainStorage(){
        return mainStorageHandler;
    }

    public static boolean isIsStorageBusy(){
        return isStorageBusy;
    }

    public static void setStorageBusy(boolean busy){
        isStorageBusy=busy;
    }

    public static GuildConfigHandler getGuildConfigHandler() {
        return guildConfigHandler;
    }
}