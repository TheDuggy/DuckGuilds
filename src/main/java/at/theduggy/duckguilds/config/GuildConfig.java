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
package at.theduggy.duckguilds.config;

import at.theduggy.duckguilds.Main;
import at.theduggy.duckguilds.storage.StorageType;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

public class GuildConfig {

    public static StorageType storageType;

    public static int getMaxGuildSize(){
        FileConfiguration f = Main.mainFileConfiguration;
        if (f.get("maxGuilds")instanceof Boolean){
            return 0;
        }else if (f.get("maxGuilds") instanceof Integer){
            if (f.getInt("maxGuilds")>0){
                if (f.getInt("maxGuilds")<=Main.getGuildCache().size()){
                    return f.getInt("maxGuilds");
                }else {
                    return 0;
                }
            }else {
                return 0;
            }
        }else {
            return 0;
        }
    }

    public static long getTimeTillInviteIsDeleted(){
        FileConfiguration f = Main.mainFileConfiguration;
        if (f.getLong("inviteDeleteTime")>108000){
            return 18000;
        }else if (f.getLong("inviteDeleteTime")<6000){
            return 18000;
        }else {
            return f.getLong("inviteDeleteTime");
        }
    }

    public static Path getGuildRootFolder() throws FileNotFoundException {
        FileConfiguration f = Main.mainFileConfiguration;
        if (f.getString("guildDirRootPath").equals("default")){
            return Paths.get(Main.getPlugin(Main.class).getDataFolder() + "/guilds");
        }else if (!f.getString("guildDirRootPath").equals("default")){
            if (Files.exists(Paths.get(f.getString("guildDirRootPath")))){
                return Paths.get(f.getString("guildDirRootPath")+ "/guilds");
            }else {
                return Paths.get(Main.getPlugin(Main.class).getDataFolder() + "/guilds");

            }
        }
        return null;
    }

    public static boolean getLogging(){
        FileConfiguration f = Main.mainFileConfiguration;
        return f.getBoolean("log");
    }

    public static Object getCustomLogging() {
        FileConfiguration f = Main.mainFileConfiguration;
        if (getLogging()) {
            if (f.get("customLogging") instanceof Boolean) {
                return false;
            } else if (f.get("customLogging") instanceof String) {
                if (Files.exists(Paths.get(f.getString("customLogging")))) {
                    if (Files.isDirectory(Paths.get(f.getString("customLogging")))) {
                        return Paths.get(f.getString("customLogging"));
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }else {
            return false;
        }
    }

    public static Enum<StorageType> getStorageType(){
        FileConfiguration fileConfiguration = Main.mainFileConfiguration;
        if (storageType==null) {
            switch (fileConfiguration.getString("storageType")) {
                case "FILE":
                    storageType=StorageType.FILE;
                    return StorageType.FILE;
                case "DATABASE":
                    storageType=StorageType.DATABASE;
                    return StorageType.DATABASE;
                default:
                    return StorageType.FILE;
            }
        }else {
            return storageType;
        }
    }

    public static boolean getIfCheckForPlayerInAllGuilds(){
        return Main.mainFileConfiguration.getBoolean("checkForPlayerInAllGuilds");
    }
}
