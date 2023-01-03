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
import com.zaxxer.hikari.HikariConfig;
import org.bukkit.configuration.file.FileConfiguration;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class GuildConfigHandler {

    private final FileConfiguration conf;
    
    public GuildConfigHandler(FileConfiguration conf){
        this.conf = conf;
    }

    public void set(String path, Object value){
        conf.set(path, value);
    }


    public Object getMaxGuildSize(){
        if (conf.get("max-guilds")instanceof Boolean){
            return conf.get("max-guilds");
        }else if (conf.get("max-guilds") instanceof Integer){
            if (conf.getInt("max-guilds")>0){
                if (conf.getInt("max-guilds")<=Main.getGuildCache().size()){
                    return conf.getInt("max-guilds");
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

    public long getTimeDeleteTime(){
        if (conf.getLong("invite-delete-time")>108000){
            return 18000;
        }else if (conf.getLong("invite-delete-time")<6000){
            return 18000;
        }else {
            return conf.getLong("invite-delete-time");
        }
    }

    public File getGuildRootPath(){
        if (conf.getString("guild-root-path").equals("default")){
            return new File(Main.getPlugin(Main.class).getDataFolder() + "/guildStorage");
        }else if (!conf.getString("guild-root-path").equals("default")){
            if (Files.exists(Paths.get(conf.getString("guild-root-path")))){
                return new File(conf.getString("guild-root-path")+ "/guildStorage");
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
        String storageType = conf.getString("storage-type");
        switch (storageType) {
            case "File":
            case "MySQL":
                return storageType;
            default:
                return null;
        }
    }

    public boolean fileOnConFail(){
        if (getStorageType().equals("MySQL")){
            return conf.getBoolean("file-on-con-fail");
        }else {
            return false;
        }
    }

    public boolean delOldStorage(){
        return conf.getBoolean("del-old-storage");
    }

    public boolean showConfBanner(){
        return conf.getBoolean("config-banner");
    }
}
