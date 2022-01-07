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

import at.theduggy.duckguilds.config.GuildsConfig;
import at.theduggy.duckguilds.files.GuildFiles;
import at.theduggy.duckguilds.startUp.CachePlayers;
import at.theduggy.duckguilds.startUp.IndexGuilds;
import at.theduggy.duckguilds.startUp.StartGuildSystemOnReload;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public final class Main extends JavaPlugin {

public static HashMap<UUID,HashMap<String,Object>> cachedPlayers = new HashMap<>();
public static Scoreboard scoreboard;
public static HashMap<String,ArrayList<String>> guildInvites = new HashMap<>();
public static HashMap<String,HashMap<String,Object>> cachedGuilds = new HashMap<>();
public static String prefix = ChatColor.GRAY + "[" + ChatColor.YELLOW + "Guild-System" + ChatColor.GRAY + "] ";
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
public static Path guildRootFolder;

    @Override
    public void onEnable(){
        this.saveDefaultConfig();
        this.addColorsTolIst();
        scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        try {
            guildRootFolder = GuildsConfig.getGuildRootFolder(this.getConfig());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            commandRegistration();
            listenerRegistration();
            if (!GuildFiles.guildFolderStructureExists()) {
                GuildFiles.createGuildFiles();
                if (!GuildFiles.checkForIndex()) {
                    GuildFiles.createIndexFile();
                }
            }
            IndexGuilds.indexGuilds();
            CachePlayers.cachePlayers();
            StartGuildSystemOnReload.startOnReload();
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
        getCommand("temp").setExecutor(new TempExecutor());
    }

    public void listenerRegistration(){
        Bukkit.getPluginManager().registerEvents(new CachePlayers(),this);
        Bukkit.getPluginManager().registerEvents(new PlayerJoinEventGuild(),this);
    }

    public void addColorsTolIst(){
        GuildCommand.allColorsForColor.add("Blue");
        GuildCommand.allColorsForColor.add("White");
        GuildCommand.allColorsForColor.add("Aqua");
        GuildCommand.allColorsForColor.add("Gold");
        GuildCommand.allColorsForColor.add("Green");
        GuildCommand.allColorsForColor.add("Red");
        GuildCommand.allColorsForColor.add("Yellow");

        GuildCommand.allColorsForColorAndDark.add("Blue");
        GuildCommand.allColorsForColorAndDark.add("White");
        GuildCommand.allColorsForColorAndDark.add("Aqua");
        GuildCommand.allColorsForColorAndDark.add("Gold");
        GuildCommand.allColorsForColorAndDark.add("Green");
        GuildCommand.allColorsForColorAndDark.add("Red");
        GuildCommand.allColorsForColorAndDark.add("Yellow");
        GuildCommand.allColorsForColorAndDark.add("Dark_Blue");
        GuildCommand.allColorsForColorAndDark.add("Dark_Purple");
        GuildCommand.allColorsForColorAndDark.add("Dark_Aqua");
        GuildCommand.allColorsForColorAndDark.add("Dark_Green");
        GuildCommand.allColorsForColorAndDark.add("Dark_Red");
    }
}