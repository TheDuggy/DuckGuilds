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
package at.theduggy.duckguilds.commands.invite;

import at.theduggy.duckguilds.Main;
import at.theduggy.duckguilds.config.GuildConfigHandler;
import at.theduggy.duckguilds.objects.GuildInviteObject;
import at.theduggy.duckguilds.objects.GuildObject;
import at.theduggy.duckguilds.utils.GuildTextUtils;
import at.theduggy.duckguilds.utils.Utils;
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
import java.util.HashMap;
import java.util.UUID;

public class GuildInviteCommand {

    public static void guildInviteCommand(Player sender, String playerNameToInvite, String guildName) throws IOException, ParseException {
        if (Utils.guildExists(guildName)) {
            if (Utils.getIfPlayerIsHeadOfGuild(guildName,sender)) {
                if (Bukkit.getPlayerExact(playerNameToInvite)!=null) {
                    if (!Main.getPlayerCache().get(Bukkit.getPlayerExact(playerNameToInvite).getUniqueId()).getGuild().equals(Utils.getPlayerGuild(sender))) {
                        if (Bukkit.getPlayerExact(playerNameToInvite) != null) {
                            Player invitedPlayer = Bukkit.getPlayerExact(playerNameToInvite);
                            if (invitedPlayer != null) {
                                GuildObject guildObject = Main.getGuildCache().get(guildName);
                                HashMap<UUID, GuildInviteObject> allInvites = guildObject.getAllInvites();
                                System.out.println(allInvites);
                                if (!allInvites.containsKey(Bukkit.getPlayerExact(playerNameToInvite).getUniqueId())) {
                                    System.out.println("Inivted!");
                                    guildObject.addInvite(new GuildInviteObject(guildObject.getName(),sender.getUniqueId(), invitedPlayer.getUniqueId()));
                                    invitedPlayer.spigot().sendMessage(new TextComponent(GuildTextUtils.prefix + " " + ChatColor.YELLOW + sender.getName() + ChatColor.GREEN + " has invited you to " + ChatColor.GOLD + guildName + ChatColor.GREEN + "!\n"), new TextComponent(" ".repeat(15) + ChatColor.GRAY + "-".repeat(5)), clickableMsgJoin(guildName), new TextComponent("  "), clickableMsgDiscard(sender, guildName), new TextComponent(ChatColor.GRAY + "-".repeat(5)));
                                    sender.sendMessage(GuildTextUtils.prefix + ChatColor.RED + "You invited " + ChatColor.YELLOW + playerNameToInvite + ChatColor.RED + " to your guild!");
                                } else {
                                    sender.sendMessage(GuildTextUtils.prefix + ChatColor.RED + "You already sent an invite to this player!");
                                }
                            } else {
                                sender.sendMessage(GuildTextUtils.playerIsntOnline);
                            }
                        } else {
                            sender.sendMessage(GuildTextUtils.playerDoesntExists);
                        }
                    } else {
                        sender.sendMessage(GuildTextUtils.prefix + ChatColor.RED + "That player is already in your guild!");
                    }
                }
            }else {
                sender.sendMessage(GuildTextUtils.youAreNotTheHeadOfThatGuild);
            }
        }else {
            sender.sendMessage(GuildTextUtils.guildDoesntExist);
        }
    }

    public static TextComponent clickableMsgJoin( String guildName){
        TextComponent textComponent =new TextComponent( ChatColor.GRAY + "[" +  ChatColor.GREEN + "" + ChatColor.BOLD + "join" + ChatColor.GRAY + "] ");
        textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/guild join " + guildName));
        textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Join " + guildName).color(net.md_5.bungee.api.ChatColor.GREEN).italic(true).create()));
        return textComponent;
    }

    public static TextComponent clickableMsgDiscard(Player sender, String guildName){
        TextComponent textComponent = new TextComponent(ChatColor.GRAY + "[" + ChatColor.RED + "" + ChatColor.BOLD + "discard" + ChatColor.GRAY + "]");
        textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/guild discardInvite " + guildName));
        textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Discard invite from " + sender.getName()).color(net.md_5.bungee.api.ChatColor.RED).italic(true).create()));
        return textComponent;
    }
}
