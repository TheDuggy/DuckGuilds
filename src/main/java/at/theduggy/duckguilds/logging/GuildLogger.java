package at.theduggy.duckguilds.logging;

import at.theduggy.duckguilds.Main;
import at.theduggy.duckguilds.config.GuildConfigHandler;
import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

public class GuildLogger {

    private static Logger guildLogger;

    public static Logger getLogger(){
        if (guildLogger ==null){
            initLogger();
        }
        return guildLogger;
    }

    private static void initLogger(){
        guildLogger = Logger.getLogger(GuildLogger.class);
        GuildAppender guildAppender = new GuildAppender(Main.getGuildConfigHandler().getLoggingPath(), Main.getGuildConfigHandler().getMaxLogFileSize());
        guildAppender.activateOptions();
        guildLogger.addAppender(guildAppender);
        guildLogger.setLevel(Level.ALL);
    }
}
