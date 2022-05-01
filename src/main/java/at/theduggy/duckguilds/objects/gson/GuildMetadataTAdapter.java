package at.theduggy.duckguilds.objects.gson;

import at.theduggy.duckguilds.objects.GuildMetadata;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import org.apache.log4j.helpers.DateTimeDateFormat;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class GuildMetadataTAdapter extends TypeAdapter<GuildMetadata> {
    @Override
    public void write(JsonWriter jsonWriter, GuildMetadata guildMetadata) throws IOException {
        jsonWriter.beginObject();
        jsonWriter.name("creationDate").jsonValue("\"" + guildMetadata.getCreationDate().toString() + "\"");
        jsonWriter.name("creatorName").jsonValue("\"" + guildMetadata.getCreatorName() + "\"");
        jsonWriter.endObject();
    }

    @Override
    public GuildMetadata read(JsonReader jsonReader) throws IOException {
        String fieldName;
        String creatorName = "";
        LocalDateTime creationDate = null;
        jsonReader.beginObject();
        while (jsonReader.hasNext()){
            if (jsonReader.peek()== JsonToken.NAME){
                fieldName = jsonReader.nextName();
                switch (fieldName){
                    case "creatorName":  creatorName=jsonReader.nextString();break;
                    case "creationDate": creationDate = LocalDateTime.parse(jsonReader.nextString());break;
                }
            }
        }
        jsonReader.endObject();
        return new GuildMetadata(creationDate, creatorName);
    }
}
