package at.theduggy.duckguilds.objects;

import org.json.simple.JSONObject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class GuildMetadata {

    public String creationDate;

    public GuildMetadata(String creationDate) {
        this.creationDate = creationDate;
    }

    public GuildMetadata(LocalDateTime creationDate) {
        DateTimeFormatter formatCreationDate = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        this.creationDate = formatCreationDate.format(creationDate);
    }

    public String getCreationDate() {
        return creationDate;
    }

    public String toString(){
        JSONObject data = new JSONObject();
        data.put("creationDate",creationDate);
        return data.toJSONString();
    }
}
