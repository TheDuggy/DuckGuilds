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
package at.theduggy.duckguilds.startUp;

import at.theduggy.duckguilds.Main;
import org.bukkit.Bukkit;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class IndexGuilds {

    public static void indexGuilds() throws IOException, ParseException {
        Path guildIndexFile = Paths.get(Main.guildRootFolder + "/index.json");
        Path guildGuildsFolder = Paths.get(Main.guildRootFolder + "/guilds");
        JSONParser jsonParser = new JSONParser();
        FileReader allGuildsReader = new FileReader(guildIndexFile.toFile(), StandardCharsets.UTF_8);
        JSONObject allGuilds= (JSONObject) jsonParser.parse(allGuildsReader);
        allGuildsReader.close();
        ArrayList<String> allGuildsByIndex = (ArrayList<String>) allGuilds.get("guilds"); //Get all indexed guilds
        for (String s:allGuildsByIndex){
            JSONParser jsonParser1 = new JSONParser();
            FileReader fileReader = new FileReader(guildGuildsFolder + "/" + s+ ".json",StandardCharsets.UTF_8);
            JSONObject jsonStringComponents = (JSONObject) jsonParser1.parse(fileReader);
            fileReader.close();
            HashMap<String,Object> guildDetails = new HashMap<>();
            ArrayList<UUID> players = new ArrayList<>();
            for (String playerUUID: (ArrayList<String>) jsonStringComponents.get("players")){
                players.add(UUID.fromString(playerUUID));
            }
            guildDetails.put("head", UUID.fromString((String) jsonStringComponents.get("head")));
            guildDetails.put("color", jsonStringComponents.get("color"));
            guildDetails.put("tagColor", jsonStringComponents.get("tagColor"));
            guildDetails.put("players", players);
            guildDetails.put("name", jsonStringComponents.get("name"));
            guildDetails.put("tag", jsonStringComponents.get("tag"));
            Main.cachedGuilds.put(s, guildDetails); // guild indexed to HasMap

        }

    }
}
