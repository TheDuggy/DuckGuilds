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
package at.theduggy.duckguilds.guild_invite;

import at.theduggy.duckguilds.Main;
import at.theduggy.duckguilds.config.GuildConfig;
import at.theduggy.duckguilds.other.Utils;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.simple.parser.ParseException;
import net.md_5.bungee.api.chat.TextComponent;

import java.io.IOException;
import java.util.ArrayList;

public class GuildInviteCommand {

    public static void guildInviteCommand(Player sender, String playerNameToInvite, String guildName) throws IOException, ParseException {
        if (Utils.guildExists(guildName)) {
            if (Utils.getIfPlayerIsHeadOfGuild(guildName,sender)) {
                if (!Utils.getPlayerGuild(Bukkit.getPlayerExact(playerNameToInvite)).equals(Utils.getPlayerGuild(sender))) {
                    if (Bukkit.getPlayerExact(playerNameToInvite) != null) {
                        Player invitedPlayer = Bukkit.getPlayerExact(playerNameToInvite);
                        if (invitedPlayer != null) {
                            if (!Main.guildInvites.containsKey(guildName)) {
                                ArrayList<String> playerInvites = new ArrayList<>();
                                Main.guildInvites.put(guildName, playerInvites);
                            }
                            if (!Main.guildInvites.get(guildName).contains(playerNameToInvite)) {
                                Main.guildInvites.get(guildName).add(playerNameToInvite);
                                invitedPlayer.spigot().sendMessage(new TextComponent(Main.prefix + " " + ChatColor.YELLOW + sender.getName() + ChatColor.RED + " has invited you to " + ChatColor.YELLOW + guildName + ChatColor.RED + "!"), clickableMsgJoin( invitedPlayer.getName()), new TextComponent(" "), clickableMsgDiscard(sender, guildName));
                                sender.sendMessage(Main.prefix + ChatColor.RED + "You invited " + ChatColor.YELLOW + playerNameToInvite + ChatColor.RED + " to your guild!");
                                autoDeleteGuildInvite(Bukkit.getPlayerExact(playerNameToInvite), guildName);

                            } else {
                                sender.sendMessage(Main.prefix + ChatColor.RED + "You already sent an invite to this player!");
                            }
                        } else {
                            sender.sendMessage(Main.playerInstOnline);
                        }
                    } else {
                        sender.sendMessage(Main.playerDoesntExists);
                    }
                }else {
                    sender.sendMessage(Main.prefix + ChatColor.RED + "That player is already in your guild!");
                }
            }else {
                sender.sendMessage(Main.youAreNotTheHeadOfThatGuild);
            }
        }else {
            sender.sendMessage(Main.guildDoesntExists);
        }
    }

    public static TextComponent clickableMsgJoin( String guildName){
        TextComponent textComponent =new TextComponent(ChatColor.GREEN + "" + ChatColor.BOLD + "join");
        textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/guild join " + guildName));
        textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Join " + guildName).color(net.md_5.bungee.api.ChatColor.GREEN).italic(true).create()));
        return textComponent;
    }

    public static TextComponent clickableMsgDiscard(Player sender, String guildName){
        TextComponent textComponent = new TextComponent(ChatColor.RED + "" + ChatColor.BOLD + "discard");
        textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/guild discardInvite " + guildName));
        textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Discard invite from " + sender).color(net.md_5.bungee.api.ChatColor.RED).italic(true).create()));
        return textComponent;
    }

    public static void autoDeleteGuildInvite(Player invited, String guildName){
        new BukkitRunnable(){

            @Override
            public void run() {
                if (Main.guildInvites.get(guildName).contains(invited.getName())){
                    Main.guildInvites.get(guildName).remove(invited.getName());
                    invited.sendMessage(Main.prefix + ChatColor.RED + "The invite from " + Bukkit.getPlayer(Utils.getHeadOfGuild(guildName)).getName() + " to " + guildName + " has expired!");
                    Bukkit.getPlayer(Utils.getHeadOfGuild(guildName)).sendMessage(Main.prefix + ChatColor.RED+ "The invite for " +invited.getName() + " has expired!");

                }
            }
        }.runTaskLater(Main.getPlugin(Main.class), GuildConfig.getTimeTillInviteIsDeleted());
    }
}
