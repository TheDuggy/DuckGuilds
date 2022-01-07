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
import org.bukkit.configuration.file.FileConfiguration;

import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class GuildsConfig{



    public static long getTimeTillInviteIsDeleted(FileConfiguration f){
        if (f.getLong("inviteDeleteTime")>108000){
            return 18000;
        }else if (f.getLong("inviteDeleteTime")<6000){
            return 18000;
        }else {
            return f.getLong("inviteDeleteTime");
        }
    }

    public static Path getGuildRootFolder(FileConfiguration f) throws FileNotFoundException {
        if (f.getString("guildDirRootPath").equals("default")){
            return Paths.get(Main.getPlugin(Main.class).getDataFolder() + "/guilds");
        }else if (!f.getString("guildDirRootPath").equals("default")){
            if (Files.exists(Path.of(f.getString("guildDirRootPath")))){
                return Path.of(f.getString("guildDirRootPath")+ "/guilds");
            }else {
                return Paths.get(Main.getPlugin(Main.class).getDataFolder() + "/guilds");

            }
        }
        return null;
    }
}
