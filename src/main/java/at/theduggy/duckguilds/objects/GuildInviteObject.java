package at.theduggy.duckguilds.objects;

import at.theduggy.duckguilds.Main;

import java.util.UUID;

public class GuildInviteObject {

    private String guildName;
    private UUID sender;
    private UUID receiver;

    public GuildInviteObject(String guildName, UUID sender, UUID receiver){
        this.guildName=guildName;
        this.receiver=receiver;
        this.sender=sender;
    }

    public GuildPlayerObject getSender() {
        return Main.getPlayerCache().get(sender);
    }

    public GuildPlayerObject getReceiver() {
        return Main.getPlayerCache().get(receiver);
    }

    public GuildObject getGuild() {
        return Main.getGuildCache().get(guildName);
    }
}
