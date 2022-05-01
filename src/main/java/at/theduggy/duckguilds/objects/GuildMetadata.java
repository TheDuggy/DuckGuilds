package at.theduggy.duckguilds.objects;

import at.theduggy.duckguilds.Main;
import com.google.gson.annotations.Expose;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class GuildMetadata {

    private final LocalDateTime creationDate;
    private final String creatorName;


    public GuildMetadata(LocalDateTime creationDate, String creatorName) {
        this.creationDate = creationDate;
        this.creatorName=creatorName;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public String getFormattedCreationDate(){
        return DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss").format(creationDate);
    }

    public String getCreatorName(){
        return creatorName;
    }

    public String toString(){
        return Main.getGsonInstance().toJson(this);
    }

}
