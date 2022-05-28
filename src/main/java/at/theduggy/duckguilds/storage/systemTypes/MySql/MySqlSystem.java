package at.theduggy.duckguilds.storage.systemTypes.MySql;

import at.theduggy.duckguilds.Main;
import at.theduggy.duckguilds.config.GuildConfigHandler;
import at.theduggy.duckguilds.exceptions.GuildDatabaseException;
import at.theduggy.duckguilds.objects.GuildColor;
import at.theduggy.duckguilds.objects.GuildMetadata;
import at.theduggy.duckguilds.objects.GuildObject;
import at.theduggy.duckguilds.objects.GuildPlayerObject;
import at.theduggy.duckguilds.utils.GuildTextUtils;
import at.theduggy.duckguilds.utils.ScoreboardHandler;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.entity.Player;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;


public class MySqlSystem {

    private static HikariDataSource dataSource;
    private static Connection connection;

    public static void init() throws IOException, SQLException, GuildDatabaseException {
        long begin = System.currentTimeMillis();
        HikariConfig hikariConfig = GuildConfigHandler.getDataBase();
        if (hikariConfig!=null) {
            hikariConfig.setPoolName("GuildConnectionPool");
            dataSource = new HikariDataSource(hikariConfig);
            connection=dataSource.getConnection();
        }

        initTables();
        cacheGuilds();
        System.out.println("Guilds: " + Main.getGuildCache().size());
        cachePlayers();
        System.out.println("Elapsed time: " + (double) ((System.currentTimeMillis()-begin)/1000)/60 + "Seconds: " +(double) ((System.currentTimeMillis()-begin)/1000) );
        System.out.println("Players: " + Main.getPlayerCache().size());
    }


    public static boolean connectionAvailable() throws FileNotFoundException, SQLException {
        try {
            DriverManager.getConnection(GuildConfigHandler.getDataBase().getJdbcUrl(), GuildConfigHandler.getDataBase().getUsername(), GuildConfigHandler.getDataBase().getPassword());
            return true;
        }catch (SQLException e){
            return false;
        }
    }


    public static void cacheGuilds() throws SQLException {
       PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM guilds");
       ResultSet resultSet = preparedStatement.executeQuery();
       while (resultSet.next()){
           GuildObject guildObject = new GuildObject();
           String name = resultSet.getString("name");
           System.out.println("Caching " + name);
           guildObject.setGuildColor(new GuildColor(resultSet.getString("color")));
           guildObject.setTagColor(new GuildColor(resultSet.getString("tagColor")));
           guildObject.setName(name);
           guildObject.setTag(resultSet.getString("tag"));
           guildObject.setHead(UUID.fromString(resultSet.getString("head")));
           String[] trimmedPlayerUUIDs = resultSet.getString("players").split(",");
           ArrayList<UUID> players = new ArrayList<>();
           for (String uuid:trimmedPlayerUUIDs){
               players.add(GuildTextUtils.untrimUUID(uuid));
           }//TODO Fix two missing guilds
           guildObject.setPlayers(players);
           guildObject.setGuildMetadata(new GuildMetadata(LocalDateTime.parse(resultSet.getString("creationDate")), resultSet.getString("creatorName")));
           ScoreboardHandler.addGuild(guildObject);
           Main.getGuildCache().put(guildObject.getName(), guildObject);
       }



    }

    public static void cachePlayers() throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM guildplayers WHERE uuid IS NOT NULL");
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()){
            GuildPlayerObject guildPlayerObject = new GuildPlayerObject(UUID.fromString(resultSet.getString("uuid")), false, resultSet.getString("name"), "");
            Main.getPlayerCache().put(UUID.fromString(resultSet.getString("uuid")), guildPlayerObject);

        }
    }

    private static void cachePlayerColumn(UUID player, String guildName) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM guildplayers WHERE uuid IS NOT NULL");
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()){
            GuildPlayerObject guildPlayerObject = new GuildPlayerObject(player,false,null,"");
            guildPlayerObject.setName(resultSet.getString("name"));
            guildPlayerObject.setPlayer(player);
            guildPlayerObject.setGuild(guildName);
            guildPlayerObject.setOnline(false);
            Main.getPlayerCache().put(player,guildPlayerObject);
        }

    }


    public static void initTables() throws IOException, SQLException, GuildDatabaseException {
        if (!tableExists("guilds")){
            System.out.println("creating!");
            PreparedStatement preparedStatement =  connection.prepareStatement("CREATE TABLE guilds(name varchar(25), tag varchar(4),players LONGTEXT, color varchar(15), tagColor varchar(15), head varchar(36), creationDate TINYTEXT, creatorName TINYTEXT)");
            preparedStatement.execute();
        }
        if (!tableExists("guildplayers")){
            PreparedStatement preparedStatement =  connection.prepareStatement("CREATE TABLE guildPlayers(uuid varchar(36), name varchar(16))");
            preparedStatement.execute();
        }
    }
    public static boolean tableExists(String tableName) throws SQLException {
        ArrayList<String> tables = new ArrayList<>();
        ResultSet resultSet = connection.prepareStatement("SHOW tables").executeQuery();
        while (resultSet.next()){
            tables.add(resultSet.getString(1));
        }
        System.out.println(tables);
        System.out.println(tables.contains(tableName));
        return tables.contains(tableName);
    }

    public static void close(){
        dataSource.close();
    }

    public static void createPersonalPlayerTable(Player player) throws SQLException, GuildDatabaseException {
        PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO guildplayers VALUES (?,?)");
        preparedStatement.setString(1, player.getUniqueId().toString());
        preparedStatement.setString(2, player.getName());
        preparedStatement.executeBatch();
    }

    public static void createGuildRecord(GuildObject guildObject) throws SQLException, GuildDatabaseException {
        StringBuilder players = new StringBuilder();
        for (int i = 0;i<guildObject.getPlayers().size();i++){
            players.append(i!=guildObject.getPlayers().size()-1?GuildTextUtils.trimUUID(guildObject.getPlayers().get(i)) + ",":GuildTextUtils.trimUUID(guildObject.getPlayers().get(i)));
        }
        PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO guilds VALUES (?,?,?,?,?,?,?,?)");
        preparedStatement.setString(1, guildObject.getName());
        preparedStatement.setString(2, guildObject.getTag());
        preparedStatement.setString(3, players.toString());
        preparedStatement.setString(4, guildObject.getGuildColor().toString());
        preparedStatement.setString(5, guildObject.getTagColor().toString());
        preparedStatement.setString(6, guildObject.getHead().toString());
        preparedStatement.setString(7, guildObject.getGuildMetadata().getCreationDate().toString());
        preparedStatement.setString(8, guildObject.getGuildMetadata().getCreatorName());
        preparedStatement.executeBatch();
    }

    public static boolean personalPlayerTableExists(UUID player) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT uuid FROM guildPlayers WHERE uuid IS NOT NULL AND name IS NOT NULL");
        ResultSet allPlayers = preparedStatement.executeQuery();
        ArrayList<String> allPlayerUUIDs = new ArrayList<>();
        while (allPlayers.next()){
            allPlayerUUIDs.add(allPlayers.getString(1));
        }
        return allPlayerUUIDs.contains(player.toString());
    }
}
