package at.theduggy.duckguilds.logging;

import at.theduggy.duckguilds.Main;
import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import java.nio.file.Path;

public class DuckGuildsLogger {

    private static Logger duckGuildsLogger;

    public static Logger getLogger(){
        if (duckGuildsLogger==null){
            initLogger();
        }
        return duckGuildsLogger;
    }

    private static void initLogger(){
        DailyRollingFileAppender duckGuildAppender = new DailyRollingFileAppender();
        duckGuildAppender.setDatePattern("'.'yyyy-MM-dd'.log'");//TODO Make roll on maxFileSize!
        duckGuildAppender.setFile(Main.loggingFolder + "");
        duckGuildAppender.setFile(Main.loggingFolder + "/latest.log");
        duckGuildAppender.setLayout(new PatternLayout("[%d{HH:mm:dd}]-[%p] %m%n"));
        duckGuildAppender.activateOptions();
        duckGuildsLogger = Logger.getLogger(DuckGuildsLogger.class);
        duckGuildsLogger.addAppender(duckGuildAppender);
        duckGuildsLogger.setLevel(Level.ALL);
    }
}
