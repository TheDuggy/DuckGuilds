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
package at.theduggy.duckguilds.utils;

import at.theduggy.duckguilds.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

public class Utils {

    public static String getPlayerGuild(Player player){
        return  Main.getPlayerCache().get(player.getUniqueId()).getGuild();
    }

    public static boolean getIfPlayerIsHeadOfGuild(String name, Player player) {
        boolean isTrue = false;
        if (Main.getGuildCache().containsKey(name)){
            if (Main.getGuildCache().get(name).getHead().equals(player.getUniqueId())){
                isTrue = true;
            }
        }
        return isTrue;
    }

    public static boolean guildExists(String name) throws IOException, ParseException {
        return Main.getGuildCache().containsKey(name);
    }

    public static UUID getHeadOfGuild(String guildName){
        return Main.getGuildCache().get(guildName).getHead();
    }

    public static ArrayList<String> getPlayerGuildInvites(Player player){
        ArrayList<String> keys = new ArrayList<>(Main.guildInvites.keySet());
        ArrayList<String> guilds = new ArrayList<>();
        for (int i = 0;i!=keys.size();i++){
            if (Main.guildInvites.get(keys.get(i)).contains(player.getName())){
                guilds.add(keys.get(i));
            }
        }
        return guilds;
    }

    public static ArrayList<String> getPlayersThatArentInAGuild(){
        ArrayList<String> players = new ArrayList<>();
        for (Player player:Bukkit.getOnlinePlayers()){
            if (!Utils.isPlayerInGuild(player)){
                players.add(player.getName());
            }
        }
        return players;
    }

    public static ArrayList<String> getAllPlayerGuildInvitesForAGuild(String guildName){
        ArrayList<String> invites = new ArrayList<>();
        if  (Main.guildInvites.get(guildName)!=null) {
            for (int i = 0; i != Main.guildInvites.get(guildName).size(); i++) {
                invites.add(Main.guildInvites.get(guildName).get(i));
            }
        }
        return invites;
    }

    public static boolean isPlayerInGuild(Player player){
        return !Main.getPlayerCache().get(player.getUniqueId()).getGuild().equals("");
    }

    public static int getOnlinePlayersOfGuild(String guildName){
        int onlinePlayers =0;
        for (UUID player: Main.getGuildCache().get(guildName).getPlayers()){
            if (Bukkit.getPlayerExact(Main.getPlayerCache().get(player).getName())!=null){
                onlinePlayers+=1;
            }
        }
        return onlinePlayers;
    }
}
