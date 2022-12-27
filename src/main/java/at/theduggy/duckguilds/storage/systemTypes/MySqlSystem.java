package at.theduggy.duckguilds.storage.systemTypes;

import at.theduggy.duckguilds.Main;
import at.theduggy.duckguilds.config.GuildConfigHandler;
import at.theduggy.duckguilds.objects.GuildColor;
import at.theduggy.duckguilds.objects.GuildMetadata;
import at.theduggy.duckguilds.objects.GuildObject;
import at.theduggy.duckguilds.objects.GuildPlayerObject;
import at.theduggy.duckguilds.utils.GuildTextUtils;
import at.theduggy.duckguilds.utils.ScoreboardHandler;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;


public class MySqlSystem extends StorageType{

    private static HikariDataSource dataSource;
    private static Connection connection;

    public MySqlSystem() {
        super("MySQL");
    }

    @Override
    public void load() throws IOException, SQLException {
        HikariConfig hikariConfig = GuildConfigHandler.getDataBase();
        if (hikariConfig!=null) {
            hikariConfig.setPoolName("GuildConnectionPool");
            dataSource = new HikariDataSource(hikariConfig);
            connection=dataSource.getConnection();
        }

        initTables();
        cacheGuilds();
        cachePlayers();
        super.applyGuildsOnPlayers();
    }


    @Override
    public void loadWithoutCaching() throws SQLException, FileNotFoundException {
        HikariConfig hikariConfig = GuildConfigHandler.getDataBase();
        if (hikariConfig!=null) {
            hikariConfig.setPoolName("GuildConnectionPool");
            dataSource = new HikariDataSource(hikariConfig);
            connection=dataSource.getConnection();
        }
        initTables();
    }

    public static boolean connectionAvailable() throws FileNotFoundException, SQLException {
        try {
            DriverManager.getConnection(GuildConfigHandler.getDataBase().getJdbcUrl(), GuildConfigHandler.getDataBase().getUsername(), GuildConfigHandler.getDataBase().getPassword());
            return true;
        }catch (SQLException e){
            return false;
        }
    }


    private void cacheGuilds() throws SQLException {

       PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM guilds");
       ResultSet resultSet = preparedStatement.executeQuery();//TODO Add try-catch stuff to mysql

       boolean isFirst = true;

       while (resultSet.next()){
           if (isFirst){
               Main.log("--------------caching guilds--------------", Main.LogLevel.DEFAULT);
           }
           isFirst = false;
           String name = resultSet.getString("name");
           try {
               Main.log("Caching " + name + " with storage-type File!", Main.LogLevel.DEFAULT);
               GuildObject guildObject = new GuildObject();
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
           }catch (Exception e){
               Main.log("Failed to cache guild-record for guild " + name  + "! Caused by: " + e.getClass().getSimpleName() + "(" + e.getMessage()+")", Main.LogLevel.WARNING);
           }
       }
    }

    private void cachePlayers() throws SQLException {
        boolean isFirst = true;
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM guildPlayers WHERE uuid IS NOT NULL AND name IS NOT NULL");
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()){
            if (isFirst){
                Main.log("--------------caching players-------------", Main.LogLevel.DEFAULT);
            }

            isFirst = false;

            try {
                String name = resultSet.getString("name");
                GuildPlayerObject guildPlayerObject = new GuildPlayerObject(UUID.fromString(resultSet.getString("uuid")), false, name, "");
                Main.getPlayerCache().put(UUID.fromString(resultSet.getString("uuid")), guildPlayerObject);
            }catch (Exception e){
                Main.log("Failed to cache player " + resultSet.getString("uuid") + "(" + resultSet.getString("name") + ") ! Caused by: " + e.getClass().getSimpleName() + "(" + e.getMessage() + ")", Main.LogLevel.WARNING);
            }
        }
    }


    private void initTables() throws SQLException {
        PreparedStatement guildStatement =  connection.prepareStatement("CREATE TABLE IF NOT EXISTS guilds(name varchar(25), tag varchar(4),players LONGTEXT, color varchar(15), tagColor varchar(15), head varchar(36), creationDate TINYTEXT, creatorName TINYTEXT)");
        guildStatement.execute();
        PreparedStatement playerStatement =  connection.prepareStatement("CREATE TABLE IF NOT EXISTS guildPlayers(uuid varchar(36), name varchar(16))");
        playerStatement.execute();

    }

    public static void close(){
        dataSource.close();
    }

    @Override
    public void deletePlayerSection(GuildPlayerObject player) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM guildPlayers WHERE uuid=?");
        preparedStatement.setString(1, player.getUniqueId().toString());
        preparedStatement.execute();
    }

    @Override
    public void createPersonalPlayerSection(GuildPlayerObject player) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO guildPlayers VALUES (?,?)");
        preparedStatement.setString(1, player.getUniqueId().toString());
        preparedStatement.setString(2, player.getName());
        preparedStatement.execute();
    }

    @Override
    public void createGuildSection(GuildObject guildObject) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO guilds VALUES (?,?,?,?,?,?,?,?)");
        preparedStatement.setString(1, guildObject.getName());
        preparedStatement.setString(2, guildObject.getTag());
        preparedStatement.setString(3, uuidArrayListToTrimmedSQLList(guildObject.getPlayers()));
        preparedStatement.setString(4, guildObject.getGuildColor().toString());
        preparedStatement.setString(5, guildObject.getTagColor().toString());
        preparedStatement.setString(6, guildObject.getHead().toString());
        preparedStatement.setString(7, guildObject.getGuildMetadata().getCreationDate().toString());
        preparedStatement.setString(8, guildObject.getGuildMetadata().getCreatorName());
        preparedStatement.execute();
    }

    @Override
    public boolean personalPlayerSectionExists(UUID player) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT uuid FROM guildPlayers WHERE uuid IS NOT NULL AND name IS NOT NULL");
        ResultSet allPlayers = preparedStatement.executeQuery();
        ArrayList<String> allPlayerUUIDs = new ArrayList<>();
        while (allPlayers.next()){
            allPlayerUUIDs.add(allPlayers.getString(1));
        }
        return allPlayerUUIDs.contains(player.toString());
    }

    @Override
    public void addPlayerToGuildSection(GuildObject guildObject, GuildPlayerObject player) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("UPDATE guilds SET players=? WHERE name=");
        if (!guildObject.getPlayers().contains(player.getUniqueId())){
            guildObject.getPlayers().add(player.getUniqueId());
        }

        preparedStatement.setString(1, uuidArrayListToTrimmedSQLList(guildObject.getPlayers()));
        preparedStatement.setString(2, guildObject.getName());
        preparedStatement.execute();
    }

    @Override
    public void updatePlayerSection(GuildPlayerObject newGuildPlayerObject) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("UPDATE guildPlayers SET name=? WHERE uuid=");
        preparedStatement.setString(1,newGuildPlayerObject.getName());
        preparedStatement.setString(2, newGuildPlayerObject.getUniqueId().toString());
        preparedStatement.execute();
    }

    @Override
    public String getPlayerNameFromPlayerSection(GuildPlayerObject guildPlayerObject) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT name FROM guildPlayers WHERE uuid=?");
        preparedStatement.setString(1, guildPlayerObject.getUniqueId().toString());
        ResultSet resultSet = preparedStatement.executeQuery();
        String name = null;
        while (resultSet.next()){
            name = resultSet.getString("name");
        }
        return name;
    }

    @Override
    public void removePlayerFromGuildSection(GuildPlayerObject guildPlayerObject, GuildObject guildObject) throws SQLException {
        if (guildObject.getPlayers().contains(guildPlayerObject.getUniqueId())){
            guildObject.getPlayers().remove(guildPlayerObject.getUniqueId());
        }
        PreparedStatement preparedStatement = connection.prepareStatement("UPDATE guilds SET players=? WHERE name=?");
        preparedStatement.setString(1,uuidArrayListToTrimmedSQLList(guildObject.getPlayers()));
        preparedStatement.setString(2, guildObject.getName());
        preparedStatement.execute();
    }

    @Override
    public void deleteRootSection() throws IOException, SQLException {
        deleteTables();
    }

    @Override
    public void deleteGuildSection(GuildObject guildObject) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM guilds WHERE name=?");
        preparedStatement.setString(1, guildObject.getName());
        preparedStatement.execute();
    }

    private String uuidArrayListToTrimmedSQLList(ArrayList<UUID> uuids){
        StringBuilder players = new StringBuilder();
        for (int i = 0;i<uuids.size();i++){
            players.append(i!=uuids.size()-1?GuildTextUtils.trimUUID(uuids.get(i)) + ",":GuildTextUtils.trimUUID(uuids.get(i)));
        }
        return players.toString();
    }

    private void deleteTables() throws SQLException {
        PreparedStatement deleteGuildTable = connection.prepareStatement("DROP TABLE guilds");
        deleteGuildTable.execute();
        Main.log("Deleted guild-data-table!", Main.LogLevel.DEFAULT);
        PreparedStatement deletePlayerTable = connection.prepareStatement("DROP TABLE guildPlayers");
        deletePlayerTable.execute();
        Main.log("Deleted guild-player-data-table!", Main.LogLevel.DEFAULT);
    }
}
