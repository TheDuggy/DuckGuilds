package at.theduggy.duckguilds.logging;

import org.apache.log4j.Level;

public class AutoLogger {

    public static void logMessage(String msg, Level level){
            if (level.equals(Level.ERROR)) {
                DuckGuildsLogger.getLogger().error(msg);
            } else if (level.equals(Level.WARN)) {
                DuckGuildsLogger.getLogger().warn(msg);
            } else if (level.equals(Level.INFO)) {
                DuckGuildsLogger.getLogger().info(msg);
            } else if (level.equals(Level.DEBUG)) {
                DuckGuildsLogger.getLogger().debug(msg);
            } else if (level.equals(Level.FATAL)) {
                DuckGuildsLogger.getLogger().debug(msg);
            } else if (level.equals(Level.TRACE)) {
                DuckGuildsLogger.getLogger().trace(msg);
            }

    }
}
