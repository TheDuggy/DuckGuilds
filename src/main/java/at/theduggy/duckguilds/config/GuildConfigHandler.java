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
import at.theduggy.duckguilds.storage.StorageHandler;
import com.zaxxer.hikari.HikariConfig;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.*;

public class GuildConfigHandler {

    public static Object getMaxGuildSize(){
        FileConfiguration f = Main.mainFileConfiguration;
        if (f.get("maxGuilds")instanceof Boolean){
            return f.get("maxGuilds");
        }else if (f.get("maxGuilds") instanceof Integer){
            if (f.getInt("maxGuilds")>0){
                if (f.getInt("maxGuilds")<=Main.getGuildCache().size()){
                    return f.getInt("maxGuilds");
                }else {
                    return false;
                }
            }else {
                return false;
            }
        }else {
            return false;
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

    public static File getGuildRootFolder() throws FileNotFoundException {
        FileConfiguration f = Main.mainFileConfiguration;
        if (f.getString("guildDirRootPath").equals("default")){
            return new File(Main.getPlugin(Main.class).getDataFolder() + "/guildStorage");
        }else if (!f.getString("guildDirRootPath").equals("default")){
            if (Files.exists(Paths.get(f.getString("guildDirRootPath")))){
                return new File(f.getString("guildDirRootPath")+ "/guildStorage");
            }else {
                return new File(Main.getPlugin(Main.class).getDataFolder() + "/guildStorage");

            }
        }
        return null;
    }

    public static LoggingType getLoggingType(){
        FileConfiguration f = Main.mainFileConfiguration;
        String loggingType =f.getString("logging");
        switch (loggingType){
            case "ALL": return LoggingType.ALL;
            case "NOTHING": return LoggingType.NOTHING;
            default: return LoggingType.WARNINGS_ONLY;
        }
    }


    public static HikariConfig getDataBase() throws FileNotFoundException {
        ArrayList<String> fileNames = new ArrayList<>();
        for (File currentFile : Main.plugin.getDataFolder().listFiles()){
            fileNames.add(currentFile.getName());
        }
        if (fileNames.contains("database.yml")){
            InputStream inputStream = new FileInputStream(Main.plugin.getDataFolder() + "/database.yml");
            Yaml yaml = new Yaml();
            HashMap<String,String> mysqlData = yaml.load(inputStream);
            if (mysqlData.size()==3&&mysqlData.containsKey("password")&&mysqlData.containsKey("username")&&mysqlData.containsKey("url")){
                if (mysqlData.get("password")!=null&&mysqlData.get("username")!=null&& mysqlData.get("url")!=null){
                    HikariConfig hikariConfig = new HikariConfig();
                    hikariConfig.setPassword(String.valueOf(mysqlData.get("password")));
                    hikariConfig.setUsername(String.valueOf(mysqlData.get("username")));
                    hikariConfig.setJdbcUrl(String.valueOf("jdbc:" + mysqlData.get("url")));
                    return hikariConfig;
                }else {
                    Main.shutDown("Empty database-file!");
                }
            }else {
                Main.shutDown("Corrupted database-file!");
            }
        }else {
            Main.shutDown("No database-data-file found!");
        }
        return null;
    }

    public static StorageHandler.StorageType getStorageType(){
        FileConfiguration fileConfiguration = Main.mainFileConfiguration;
        String storageType = fileConfiguration.getString("storageType");
        switch (storageType) {
            case "File":
                return StorageHandler.StorageType.File;
            case "MySQL":
                return StorageHandler.StorageType.MySQL;
            default:
                return null;
        }
    }

    public static boolean useFileSystemOnInvalidConnection() throws FileNotFoundException, SQLException {
        if (getStorageType()== StorageHandler.StorageType.MySQL){
            return Main.mainFileConfiguration.getBoolean("useFileSystemOnInvalidConnection");
        }else {
            return false;
        }
    }

    public static boolean deleteOldStorageSectionsWhileMigration(){
        return Main.mainFileConfiguration.getBoolean("deleteOldStorageSectionsWhileMigration");
    }

    public enum LoggingType{
        WARNINGS_ONLY, ALL, NOTHING
    }

}
