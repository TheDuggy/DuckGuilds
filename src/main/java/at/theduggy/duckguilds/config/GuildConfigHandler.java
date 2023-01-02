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

    private final FileConfiguration conf;
    
    public GuildConfigHandler(FileConfiguration conf){
        this.conf = conf;
    }
    
    public Object getMaxGuildSize(){
        if (conf.get("maxGuilds")instanceof Boolean){
            return conf.get("maxGuilds");
        }else if (conf.get("maxGuilds") instanceof Integer){
            if (conf.getInt("maxGuilds")>0){
                if (conf.getInt("maxGuilds")<=Main.getGuildCache().size()){
                    return conf.getInt("maxGuilds");
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

    public long getTimeTillInviteIsDeleted(){
        if (conf.getLong("inviteDeleteTime")>108000){
            return 18000;
        }else if (conf.getLong("inviteDeleteTime")<6000){
            return 18000;
        }else {
            return conf.getLong("inviteDeleteTime");
        }
    }

    public File getGuildRootFolder(){
        if (conf.getString("guildDirRootPath").equals("default")){
            return new File(Main.getPlugin(Main.class).getDataFolder() + "/guildStorage");
        }else if (!conf.getString("guildDirRootPath").equals("default")){
            if (Files.exists(Paths.get(conf.getString("guildDirRootPath")))){
                return new File(conf.getString("guildDirRootPath")+ "/guildStorage");
            }else {
                return new File(Main.getPlugin(Main.class).getDataFolder() + "/guildStorage");
            }
        }
        return null;
    }

    public String getLoggingPath(){
        String path = conf.getString("logging-path");
        if ("default".equals(path)) {
            path = Main.getPlugin(Main.class).getDataFolder().getPath() + "/logs/";
        }
        return path;
    }

    public String getLogLevel(){
        String logLevel = conf.getString("log-level");
        if (logLevel.equals("IMPORTANT") || logLevel.equals("DEBUG")){
            return logLevel;
        }
        return "IMPORTANT";
    }

    public long getMaxLogFileSize(){
        return conf.getLong("max-log-file-size");
    }

    public HikariConfig getDataBase() throws FileNotFoundException {
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

    public String getStorageType(){
        String storageType = conf.getString("storageType");
        switch (storageType) {
            case "File":
            case "MySQL":
                return storageType;
            default:
                return null;
        }
    }

    public boolean useFileSystemOnInvalidConnection(){
        if (getStorageType().equals("MySQL")){
            return conf.getBoolean("useFileSystemOnInvalidConnection");
        }else {
            return false;
        }
    }

    public boolean deleteOldStorageSectionsWhileMigration(){
        return conf.getBoolean("deleteOldStorageSectionsWhileMigration");
    }
}
