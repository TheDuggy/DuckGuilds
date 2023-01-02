package at.theduggy.duckguilds.logging;

import at.theduggy.duckguilds.Main;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.WriterAppender;
import org.apache.log4j.helpers.CountingQuietWriter;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.helpers.OnlyOnceErrorHandler;
import org.apache.log4j.spi.ErrorHandler;
import org.apache.log4j.spi.LoggingEvent;
import org.bukkit.Bukkit;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GuildAppender extends WriterAppender {

    private final Layout STANDARD_LAYOUT = new PatternLayout("[%d{HH:mm:ss}]-[%p]: %m%n");
    private final Logger PLUGIN_LOGGER = Main.getPlugin(Main.class).getLogger();
    private final ErrorHandler errorHandler = new OnlyOnceErrorHandler();
    private final Path pathToLogs;
    private File logFile;
    private final long maxFileSize;

    private int currentEpochDay;

    public GuildAppender(PatternLayout layout, String pathToLogs, long maxFileSize) {
        super.name = "GuildAppender";
        this.maxFileSize = maxFileSize;
        super.layout = layout;
        this.pathToLogs = new File(pathToLogs).toPath().toAbsolutePath();
    }

    public GuildAppender(String pathToLogs, long maxFileSize){
        super.name = "GuildAppender";
        this.maxFileSize = maxFileSize;
        this.pathToLogs = new File(pathToLogs).toPath().toAbsolutePath();
        layout = STANDARD_LAYOUT;
    }

    @Override
    public void activateOptions() {
        logFile = new File(pathToLogs + "/latest.log");
        currentEpochDay = (int) Math.floor(Instant.now().toEpochMilli() / 84_600_000d);
        setImmediateFlush(true);
        FileOutputStream fos = null;
        try {
            if (!Files.exists(pathToLogs)){
                Files.createDirectories(pathToLogs);
            }
            fos = new FileOutputStream(logFile.getAbsoluteFile(), true);
        } catch (IOException e) {
            errorHandler.error("Failed to create log-file!");
        }
        super.qw = new CountingQuietWriter(createWriter(fos), errorHandler);
        ((CountingQuietWriter) qw).setCount(logFile.length());
    }

    private void closeFile(){
        try {
            qw.close();
        } catch (IOException e) {
            LogLog.error("Failed to close log-file " + logFile + "!");
        }
    }

    @Override
    protected void reset() {
        closeFile();
        super.reset();
    }

    public String getNextValidFile(){
        int biggest = 0;
        for (File f : new File(pathToLogs.toUri()).listFiles()){
            Matcher m = Pattern.compile("guild-log_(\\d{4}-\\d{2}-\\d{2})(_(\\d*)\\.tar\\.gz)").matcher(f.getName());

            if (m.find() && m.groupCount() == 3){
                int number = Integer.parseInt(m.group(3));
                if (number > biggest){
                    biggest = number;
                }
            }
        }

        String now = DateTimeFormatter.ofPattern("yyyy-MM-dd").format(LocalDate.now());
        return "guild-log_" + now + "_" + (biggest + 1) + ".tar.gz";
    }

    private void rollOver() throws IOException {
        LogLog.debug("Rolling over log-file in path " + pathToLogs + "!");
        closeFile();
        String archiveName = getNextValidFile();
        System.out.println(archiveName);
        TarArchiveOutputStream tout = new TarArchiveOutputStream(new FileOutputStream(pathToLogs + "/" + archiveName, true));
        TarArchiveEntry tarArchiveEntry = new TarArchiveEntry(logFile, archiveName.substring(0, archiveName.lastIndexOf(".")) + ".log");
        FileInputStream in = new FileInputStream(logFile);
        tout.putArchiveEntry(tarArchiveEntry);
        Files.copy(logFile.toPath(), tout);
        tout.closeArchiveEntry();
        tout.finish();
        in.close();
        FileOutputStream clearFile = new FileOutputStream(logFile, false);
        clearFile.close();
        activateOptions();
        LogLog.debug("Rolling over done!");
    }

    protected void subAppend(LoggingEvent event) {
        long size = ((CountingQuietWriter) qw).getCount();
        if (event.getLevel() == Level.ERROR || event.getLevel() == Level.FATAL) {
            PLUGIN_LOGGER.warning(event.getRenderedMessage());
        } else if (event.getLevel() == Level.DEBUG) {
            PLUGIN_LOGGER.info(event.getRenderedMessage());
        } else {
                if (Main.getGuildConfigHandler().getLogLevel().equals("DEBUG")){
                    PLUGIN_LOGGER.info(event.getRenderedMessage());
                }
            }
        if ( size + layout.format(event).getBytes(StandardCharsets.UTF_8).length > maxFileSize || currentEpochDay < Math.floor(Instant.now().toEpochMilli() / 86_400_000d)){
            try {
                rollOver();
            } catch (IOException e) {
                e.printStackTrace();
                LogLog.error("Failed to roll-over log-file " + logFile + "!");
            }
        }
        super.subAppend(event);
    }
}
