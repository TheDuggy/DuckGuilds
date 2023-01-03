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
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GuildAppender extends WriterAppender {

    private final Logger PLUGIN_LOGGER = Main.getPlugin(Main.class).getLogger();
    private final ErrorHandler errorHandler = new OnlyOnceErrorHandler();
    private final Path pathToLogs;
    private File logFile;
    private final long maxFileSize;

    private int currentEpochDay;

    public GuildAppender(String pathToLogs, long maxFileSize){
        super.name = "GuildAppender";
        this.maxFileSize = maxFileSize;
        this.pathToLogs = Path.of(pathToLogs);
        layout = new PatternLayout("[%d{HH:mm:ss}]-[%p]: %m%n");
    }

    @Override
    public void activateOptions() {
        logFile = new File(pathToLogs + "/latest.log");
        if (logFile.exists() && logFile.length() > 0){
            try {
                LocalDate lastModified = new Date(new File(pathToLogs + "/latest.log").lastModified()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                String nextValidFilename = getNextValidFile(lastModified);
                TarArchiveOutputStream tarArchiveOutputStream = new TarArchiveOutputStream(new FileOutputStream(pathToLogs + "/" + nextValidFilename + ".tar.gz"));
                TarArchiveEntry tarArchiveEntry = new TarArchiveEntry(logFile,  nextValidFilename + ".log");
                tarArchiveOutputStream.putArchiveEntry(tarArchiveEntry);
                Files.copy(logFile.toPath(), tarArchiveOutputStream);
                tarArchiveOutputStream.closeArchiveEntry();
                tarArchiveOutputStream.close();
                new FileOutputStream(logFile, false).close();
            }catch (IOException e){
                e.printStackTrace();
                LogLog.error("Failed to tar old latest.log content! Caused by: " + e.getClass().getSimpleName() + " (" + e.getMessage() + ")");
            }
        }
        initAppender();
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

    public String getNextValidFile(LocalDate date){
        int biggest = 0;
        String formattedDate = DateTimeFormatter.ofPattern("yyyy-MM-dd").format(date);
        for (File f : new File(pathToLogs.toUri()).listFiles()){
            Matcher m = Pattern.compile("guild-log_(\\d{4}-\\d{2}-\\d{2})(_(\\d*)\\.tar\\.gz)").matcher(f.getName());

            if (m.find() && m.groupCount() == 3){
                if (m.group(1).equals(formattedDate)){
                    int number = Integer.parseInt(m.group(3));
                    if (number > biggest){
                        biggest = number;
                    }
                }
            }
        }
        return "guild-log_" + formattedDate + "_" + (biggest + 1);
    }

    private void initAppender(){
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

    private void rollOver() throws IOException {
        LogLog.debug("Rolling over log-file in path " + pathToLogs + "!");
        closeFile();
        String nextValidFilename = getNextValidFile(LocalDate.now()) + ".tar.gz";
        TarArchiveOutputStream tout = new TarArchiveOutputStream(new FileOutputStream(pathToLogs + "/" + nextValidFilename, true));
        TarArchiveEntry tarArchiveEntry = new TarArchiveEntry(logFile, nextValidFilename + ".log");
        tout.putArchiveEntry(tarArchiveEntry);
        Files.copy(logFile.toPath(), tout);
        tout.closeArchiveEntry();
        tout.finish();
        FileOutputStream clearFile = new FileOutputStream(logFile, false);
        clearFile.close();
        initAppender();
        LogLog.debug("Rolling over done!");
    }

    protected void subAppend(LoggingEvent event) {
        long size = ((CountingQuietWriter) qw).getCount();
        if (event.getLevel() == Level.ERROR || event.getLevel() == Level.FATAL || event.getLevel() == Level.WARN) {
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
