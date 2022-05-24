package at.theduggy.duckguilds.storage.systemTypes.MySql;

import at.theduggy.duckguilds.Main;
import at.theduggy.duckguilds.config.GuildConfigHandler;
import at.theduggy.duckguilds.exceptions.GuildDatabaseException;
import at.theduggy.duckguilds.objects.GuildColor;
import at.theduggy.duckguilds.objects.GuildMetadata;
import at.theduggy.duckguilds.objects.GuildObject;
import at.theduggy.duckguilds.objects.GuildPlayerObject;
import at.theduggy.duckguilds.utils.GuildTextUtils;
import at.theduggy.duckguilds.utils.ScoreboardTeamUtils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.entity.Player;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;


public class MySqlSystem {

    private static HikariDataSource dataSource;

    public static void init() throws IOException, SQLException, GuildDatabaseException {
        HikariConfig hikariConfig = GuildConfigHandler.getDataBase();
        if (hikariConfig!=null) {
            dataSource = new HikariDataSource(hikariConfig);
        }
        initTables();
        cacheGuilds();
        cachePlayers();
    }


    public static boolean connectionAvailable() throws FileNotFoundException, SQLException {
        try {
            DriverManager.getConnection(GuildConfigHandler.getDataBase().getJdbcUrl(), GuildConfigHandler.getDataBase().getUsername(), GuildConfigHandler.getDataBase().getPassword());
            return true;
        }catch (SQLException e){
            return false;
        }
    }

    private static void execute(String statementString) throws SQLException, GuildDatabaseException {
        dataSource.getConnection().prepareStatement(statementString).execute();
    }


    private static ResultSet getDataFromDatabase(String statementString) throws SQLException {
        return dataSource.getConnection().createStatement().executeQuery(statementString);
    }

    public static void cacheGuilds() throws SQLException {
       ArrayList<String> columns = getColumns("name" ,"guilds","");
       for (String column:columns){
           ResultSet resultSet = getDataFromDatabase("SELECT * FROM guilds WHERE name='" + column + "';");
           GuildObject guildObject = new GuildObject();
           while (resultSet.next()){
               guildObject.setGuildColor(new GuildColor(resultSet.getString("color")));
               guildObject.setTagColor(new GuildColor(resultSet.getString("tagColor")));
               guildObject.setName(resultSet.getString("name"));
               guildObject.setHead(UUID.fromString(resultSet.getString("head")));
               guildObject.setPlayers(GuildTextUtils.uuidPlayerStringListToUUIDS(Main.getGsonInstance().fromJson(new JsonObject().getAsJsonObject(resultSet.getString("players")).get("players"), ArrayList.class)));
               guildObject.setGuildMetadata(new GuildMetadata(LocalDateTime.parse(resultSet.getString("creationDate")), resultSet.getString("creatorName")));
           }
           ScoreboardTeamUtils.addGuild(guildObject);
           for (UUID player : guildObject.getPlayers()){
               cachePlayerColumn(player, guildObject.getName());
           }
           Main.getGuildCache().put(guildObject.getName(), guildObject);
       }
    }

    public static void cachePlayers() throws SQLException {
        ArrayList<String> players = getColumns("uuid","guildplayers","");
        for (String uuidString : players){
            UUID uuid = UUID.fromString(uuidString);
            if (!Main.getPlayerCache().containsKey(uuid)){
                String name = "";
                ResultSet resultSet = getDataFromDatabase("SELECT name FROM guildplayers WHERE uuid='" + uuid + "';");
                while (resultSet.next()){
                    name=resultSet.getString("name");
                }
                GuildPlayerObject guildPlayerObject = new GuildPlayerObject(uuid,false,name,"");
                Main.getPlayerCache().put(uuid, guildPlayerObject);
            }
        }
    }

    private static void cachePlayerColumn(UUID player, String guildName) throws SQLException {
        ResultSet resultSet = getDataFromDatabase("SELECT * FROM guilds WHERE uuid=" + player.toString() +  ";");
        GuildPlayerObject guildPlayerObject = null;
        while (resultSet.next()){
            guildPlayerObject.setName(resultSet.getString("name"));
        }
        guildPlayerObject.setPlayer(player);
        guildPlayerObject.setGuild(guildName);
        guildPlayerObject.setOnline(false);
        Main.getPlayerCache().put(player,guildPlayerObject);
    }

    private static ArrayList<String> getColumns(String columnName, String table, String condition) throws SQLException {
        ArrayList<String> columns = new ArrayList<>();
        ResultSet resultSet = !Objects.equals(condition, "") ?getDataFromDatabase("SELECT " + columnName + " FROM " + table + " WHERE " + condition +  ";"):getDataFromDatabase("SELECT " + columnName + " FROM " + table + ";");
        while (resultSet.next()){
            columns.add(resultSet.getString(1));
        }
        return columns;
    }

    public static void initTables() throws IOException, SQLException, GuildDatabaseException {
        if (!tableExists("guilds")){
            execute("CREATE TABLE guilds(name TINYTEXT, tag TINYTEXT,players LONGTEXT, color TINYTEXT, tagColor TINYTEXT, head TINYTEXT, creationDate TINYTEXT, creatorName TINYTEXT)");
        }
        if (!tableExists("guildplayers")){
            execute("CREATE TABLE guildPlayers(uuid TINYTEXT, name TINYTEXT);");
        }
    }

    public static boolean tableExists(String tableName) throws SQLException {
        ArrayList<String> tables = new ArrayList<>();
        ResultSet resultSet = dataSource.getConnection().createStatement().executeQuery("Show tables;");
        while (resultSet.next()){
            tables.add(resultSet.getString(1));
        }
        return tables.contains(tableName);
    }

    public static void close(){
        dataSource.close();
    }

    public static void createPersonalPlayerTable(Player player) throws SQLException, GuildDatabaseException {
        execute("INSERT INTO guildplayers VALUES ('" + player.getUniqueId() + "','" + player.getName() + "')");
    }

    public static void createGuildTable(GuildObject guildObject) throws SQLException, GuildDatabaseException {
        JsonObject players = new JsonObject();
        players.addProperty("players", guildObject.getPlayers().toString());
        execute("INSERT INTO guilds VALUES ('" + guildObject.getName() + "', '" + guildObject.getTag() + "', '" + new Gson().toJson(players) + "', '" + guildObject.getGuildColor().toString() + "', '" + guildObject.getTagColor().toString() + "', '" + guildObject.getHead().toString() + "', '" + guildObject.getGuildMetadata().getFormattedCreationDate() + "', '" + guildObject.getGuildMetadata().getCreatorName() + "')");
    }

    public static boolean personalPlayerTableExists(UUID player) throws SQLException {
        ResultSet allPlayers = getDataFromDatabase("SELECT uuid FROM guildPlayers WHERE uuid IS NOT NULL AND name IS NOT NULL;");
        ArrayList<String> allPlayerUUIDs = new ArrayList<>();
        while (allPlayers.next()){
            allPlayerUUIDs.add(allPlayers.getString(1));
        }
        return allPlayerUUIDs.contains(player.toString());
    }
}
