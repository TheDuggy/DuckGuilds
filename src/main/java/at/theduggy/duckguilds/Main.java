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

import at.theduggy.duckguilds.config.GuildConfig;
import at.theduggy.duckguilds.logging.AutoLogger;
import at.theduggy.duckguilds.objects.GuildColor;
import at.theduggy.duckguilds.objects.GuildMetadata;
import at.theduggy.duckguilds.objects.GuildObject;
import at.theduggy.duckguilds.objects.GuildPlayerObject;
import at.theduggy.duckguilds.objects.gson.GuildColorTAdapter;
import at.theduggy.duckguilds.objects.gson.GuildMetadataTAdapter;
import at.theduggy.duckguilds.startUp.GuildPlayers;
import at.theduggy.duckguilds.storage.Storage;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.log4j.Level;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public final class Main extends JavaPlugin {

private static HashMap<UUID, GuildPlayerObject> cachedPlayers = new HashMap<>();
public static ArrayList<String> guildInfo = new ArrayList<>();
public static FileConfiguration mainFileConfiguration;
private static Scoreboard scoreboard;
public static Plugin plugin;
public static HashMap<String,ArrayList<String>> guildInvites = new HashMap<>();
private static HashMap<String, GuildObject> cachedGuilds = new HashMap<>();
public static String prefix = ChatColor.AQUA + "[" + ChatColor.GOLD + "DuckGuilds" + ChatColor.AQUA + "] ";
public static String wrongUsage = prefix + ChatColor.RED + "Wrong usage! Use /guild help to see all options!";
public static String playerAlreadyInGuild = prefix + ChatColor.RED + "You are already in a guild! use /guild leave to leave yor current guild. Use /guild leave -y to leave the guild if you are the head, but your guild would be lost for ever!";
public static String guildDoesntExists = prefix + ChatColor.RED + "That guild doesn't exist. Use /guild list to see all guilds!";
public static String youArentInThatGuild = prefix + ChatColor.RED + "Your aren't in that guild.";
public static String guildHeadLeftGuild = prefix +  ChatColor.RED + "Your guild-head had left the guild and the guild was deleted!";
public static String youAreNotInAGuild = prefix + ChatColor.RED + "You are not in a guild!";
public static String youAreTheHeadOfThatGuild = prefix + ChatColor.RED + "You are the head of that guild! You can't leave it, but delete it with /guild delete -y!";
public static String youAreNotTheHeadOfThatGuild = prefix + ChatColor.RED + "You are not the head of that guild!";
public static String forbiddenArgument = prefix + ChatColor.RED + "This command do not take this argument!";
public static String playerDoesntExists = prefix + ChatColor.RED + "That player doesn't exist!";
public static String playerInstOnline = prefix + ChatColor.RED + "This player isn't online!";
public static String pageIndexMustBeNumeric = prefix + ChatColor.RED + "The page-index must be numeric!";
public static String pageIndexOutOfBounds = prefix + ChatColor.RED + "The page-index must be valid!";
public static File guildRootFolder;
public static Path loggingFolder;

    @Override
    public void onEnable(){
        this.saveDefaultConfig();
        plugin = this;
        mainFileConfiguration = this.getConfig();
        try {
            FakePlayerData.initFakePlayer();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            guildRootFolder = GuildConfig.getGuildRootFolder();
            System.out.println(guildRootFolder.toPath());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        addLogFolderPath();
        scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        try {
            commandRegistration();
            listenerRegistration();
            Storage.loadStorage();
            GuildPlayers.handlePlayersOnReload();
        }catch (IOException|ParseException e){
                e.printStackTrace();
        }
    }
    
    @Override
    public void onDisable(){
        this.saveDefaultConfig();
    }

    public void commandRegistration(){
        getCommand("guild").setExecutor(new GuildCommand());
        getCommand("guild").setTabCompleter(new GuildCommand());
        getCommand("guildFakePlayer").setExecutor(new FakeGuildPlayer());
        getCommand("guildFakePlayer").setTabCompleter(new FakeGuildPlayer());
    }

    public void listenerRegistration(){
        Bukkit.getPluginManager().registerEvents(new GuildPlayers(), this);
    }


    public static void addLogFolderPath(){
        if (GuildConfig.getCustomLogging() instanceof Path){
            loggingFolder = (Path) GuildConfig.getCustomLogging();
        }else {
            loggingFolder = Paths.get(plugin.getDataFolder().toPath() + "/logs/");
        }
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

    public static ArrayList<String> getGuildInfo() {
        return guildInfo;
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

}