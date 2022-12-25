package at.theduggy.duckguilds.objects.gson;

import at.theduggy.duckguilds.Main;
import at.theduggy.duckguilds.objects.GuildColor;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public class GuildColorTAdapter extends TypeAdapter<GuildColor> {
    @Override
    public void write(JsonWriter jsonWriter, GuildColor guildColor) throws IOException {
        jsonWriter.beginObject();
        jsonWriter.name("color").jsonValue("\"" + guildColor.toString() + "\"");
        jsonWriter.endObject();
    }

    @Override
    public GuildColor read(JsonReader jsonReader) throws IOException {
        String fieldName = "";
        GuildColor color = null;
        jsonReader.beginObject();
        while (jsonReader.hasNext()){
            if (jsonReader.peek()== JsonToken.NAME){
                fieldName=jsonReader.nextName();
                if (fieldName.equals("color")){
                    color=new GuildColor(jsonReader.nextString());
                }
            }
        }
        jsonReader.endObject();
        return color;
    }
}
