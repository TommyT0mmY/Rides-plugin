package com.github.tommyt0mmy.rides.storing;

import com.github.tommyt0mmy.rides.Rides;

import java.sql.*;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

public class SQLiteDatabase
{
    Rides RidesClass = Rides.getInstance();

    private final String url = "jdbc:sqlite:" + RidesClass.getDataFolder().getAbsolutePath() + "\\database.db";
    private Connection connection;

    public SQLiteDatabase()
    {
        initDatabase();
        clearTable("spawned_horses");
    }

    public void initDatabase()
    {
        try
        {
            connection = DriverManager.getConnection(url);

            if (connection != null)
            {
                DatabaseMetaData meta = connection.getMetaData();
                RidesClass.console.info("The driver name is " + meta.getDriverName());
                initTables();
            }
        } catch (SQLException e)
        {
            RidesClass.console.severe(e.getMessage() + " (initDatabase)");
        }
    }

    public void initTables()
    {
        // Horses table
        String sql = "CREATE TABLE IF NOT EXISTS horses("
                   + "uuid VARCHAR(255) NOT NULL PRIMARY KEY,"
                   + "owner_uuid VARCHAR(255) NOT NULL,"
                   + "name VARCHAR(255) NOT NULL,"
                   + "speed float NOT NULL,"
                   + "health tinyint NOT NULL,"
                   + "skin tinyint NOT NULL"
                   + ");";

        executeStatement(sql);

        //Ingame spawned horses table
        sql = "CREATE TABLE IF NOT EXISTS spawned_horses("
            + "owner_uuid VARCHAR(255) NOT NULL PRIMARY KEY,"
            + "horsedata_uuid VARCHAR(255) NOT NULL,"
            + "horseentity_uuid VARCHAR(255) NOT NULL"
            + ");";

        executeStatement(sql);
    }

    public void clearTable(String table_name)
    {
        String sql = String.format("DELETE FROM %s;", table_name);
        executeStatement(sql);
    }

    public HorseData getHorseData(UUID horseUuid)
    {
        String sql = "SELECT owner_uuid, name, speed, health, skin "
                   + "FROM horses "
                   + "WHERE uuid = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql))
        {
            pstmt.setString(1, horseUuid.toString());
            ResultSet rs = pstmt.executeQuery();
            rs.next();

            String name = rs.getString("name");
            UUID owner_uuid = UUID.fromString(rs.getString("owner_uuid"));
            float speed = rs.getFloat("speed");
            byte health = rs.getByte("health");
            byte skin = rs.getByte("skin");

            return new HorseData(name, horseUuid, owner_uuid, speed, health, skin);
        } catch(SQLException e) {RidesClass.console.severe(e.getMessage() + " (getHorseData)");}

        return null;
    }

    public void addHorseData(HorseData horseData)
    {
        String sql  = "INSERT INTO horses(uuid, owner_uuid, name, speed, health, skin) VALUES(?, ?, ?, ?, ?, ?)";

        try(PreparedStatement pstmt = connection.prepareStatement(sql))
        {
            pstmt.setString(1, horseData.getUuid().toString());
            pstmt.setString(2, horseData.getOwner().toString());
            pstmt.setString(3, horseData.getName());
            pstmt.setFloat(4, horseData.getSpeed());
            pstmt.setInt(5, horseData.getHealth());
            pstmt.setByte(6, horseData.getSkin());

            pstmt.execute();
        } catch (SQLException e) {RidesClass.console.severe(e.getMessage() + " (addHorseData)");}
    }

    public OwnerData getOwnerData(UUID ownerUuid)
    {
        ArrayList<UUID> horses = new ArrayList<>();

        String sql = "SELECT uuid FROM horses WHERE owner_uuid = ?";

        try (PreparedStatement pstmt  = connection.prepareStatement(sql))
        {
            pstmt.setString(1, ownerUuid.toString());

            ResultSet rs = pstmt.executeQuery();
            // loop through the result set
            while (rs.next())
            {
                horses.add(UUID.fromString(rs.getString("uuid")));
            }
        } catch (SQLException e) {RidesClass.console.severe(e.getMessage() + " (getOwnerData)");}

        return new OwnerData(ownerUuid, horses);
    }

    public void addSpawnedHorse(UUID owner, UUID horsedata_uuid, UUID horseentity_uuid)
    {
        String sql = "INSERT INTO spawned_horses(owner_uuid, horsedata_uuid, horseentity_uuid) VALUES(?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql))
        {
            pstmt.setString(1, owner.toString());
            pstmt.setString(2, horsedata_uuid.toString());
            pstmt.setString(3, horseentity_uuid.toString());

            pstmt.execute();
        } catch (SQLException e) {RidesClass.console.severe(e.getMessage() + " (addSpawnedHorse)");}
    }

    public void removeSpawnedHorse(UUID owner)
    {
        String sql = "DELETE FROM spawned_horses WHERE owner_uuid = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql))
        {
            pstmt.setString(1, owner.toString());

            pstmt.execute();
        } catch (SQLException e) {RidesClass.console.severe(e.getMessage() + " (removeSpawnedHorse)");}
    }

    public Optional<UUID> getSpawnedHorseFromOwner(UUID owner)
    {
        String sql = "SELECT horseentity_uuid FROM spawned_horses WHERE owner_uuid = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql))
        {
            preparedStatement.setString(1, owner.toString());
            ResultSet resultSet = preparedStatement.executeQuery();

            if(!resultSet.next())
                return Optional.empty();

            return Optional.of(UUID.fromString(resultSet.getString("horseentity_uuid")));

        }
        catch(SQLException e)
        {
            RidesClass.console.severe(e.getMessage() + " (getSpawnedHorseFromOwner)");
        }

        return Optional.empty();
    }

    public HorseData getSpawnedHorseDataFromOwner(UUID owner)
    {
        String sql = "SELECT horsedata_uuid FROM spawned_horses WHERE owner_uuid = ?";

        try (PreparedStatement pstmt  = connection.prepareStatement(sql))
        {
            pstmt.setString(1, owner.toString());
            ResultSet rs = pstmt.executeQuery();
            rs.next();

            return getHorseData(UUID.fromString(rs.getString("horsedata_uuid")));
        }catch(SQLException e) {RidesClass.console.severe(e.getMessage() + " (getSpawnedHorseDataFromOwner)");}

        return null;
    }

    private void executeStatement(String sql)
    {
        try (Statement stmt = connection.createStatement())
        {
            stmt.execute(sql);
        } catch (SQLException e) {RidesClass.console.severe(e.getMessage() + " (executeStatement)");}
    }
}
