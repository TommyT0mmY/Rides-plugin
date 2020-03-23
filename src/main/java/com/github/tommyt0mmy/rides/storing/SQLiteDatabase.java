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
                   + "id INT NOT NULL PRIMARY KEY,"
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
            + "horse_id INT NOT NULL,"
            + "horse_uuid VARCHAR(255) NOT NULL"
            + ");";

        executeStatement(sql);
    }

    public void clearTable(String table_name)
    {
        String sql = String.format("DELETE FROM %s;", table_name);
        executeStatement(sql);
    }

    public HorseData getHorseData(int horse_id)
    {
        String sql = "SELECT owner_uuid, name, speed, health, skin "
                   + "FROM horses "
                   + "WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql))
        {
            pstmt.setInt(1, horse_id);
            ResultSet rs = pstmt.executeQuery();
            rs.next();

            String name = rs.getString("name");
            UUID owner_uuid = UUID.fromString(rs.getString("owner_uuid"));
            float speed = rs.getFloat("speed");
            byte health = rs.getByte("health");
            byte skin = rs.getByte("skin");

            return new HorseData(name, horse_id, owner_uuid, speed, health, skin);
        } catch(SQLException e) {RidesClass.console.severe(e.getMessage() + " (getHorseData)");}

        return null;
    }

    public void addHorseData(HorseData horseData)
    {
        String sql  = "INSERT INTO horses(id, owner_uuid, name, speed, health, skin) VALUES(?, ?, ?, ?, ?, ?)";

        try(PreparedStatement pstmt = connection.prepareStatement(sql))
        {
            pstmt.setInt(1, horseData.getId());
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
        ArrayList<Integer> horses = new ArrayList<>();

        String sql = "SELECT id FROM horses WHERE owner_uuid = ?";

        try (PreparedStatement pstmt  = connection.prepareStatement(sql))
        {
            pstmt.setString(1, ownerUuid.toString());

            ResultSet rs = pstmt.executeQuery();
            // loop through the result set
            while (rs.next())
            {
                horses.add(rs.getInt("id"));
            }
        } catch (SQLException e) {RidesClass.console.severe(e.getMessage() + " (getOwnerData)");}

        return new OwnerData(ownerUuid, horses);
    }

    public void addSpawnedHorse(UUID owner, Integer horse_id, UUID horse_uuid)
    {
        String sql = "INSERT INTO spawned_horses(owner_uuid, horse_id, horse_uuid) VALUES(?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql))
        {
            pstmt.setString(1, owner.toString());
            pstmt.setInt(2, horse_id);
            pstmt.setString(3, horse_uuid.toString());

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
        String sql = "SELECT horse_uuid FROM spawned_horses WHERE owner_uuid = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql))
        {
            preparedStatement.setString(1, owner.toString());
            ResultSet resultSet = preparedStatement.executeQuery();

            if(!resultSet.next())
                return Optional.empty();

            return Optional.of(UUID.fromString(resultSet.getString("horse_uuid")));

        }
        catch(SQLException e)
        {
            RidesClass.console.severe(e.getMessage() + " (getSpawnedHorseFromOwner)");
        }

        return Optional.empty();
    }

    public Optional<HorseData> getSpawnedHorseDataFromOwner(UUID owner)
    {
        String sql = "SELECT horse_id FROM spawned_horses WHERE owner_uuid = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql))
        {
            preparedStatement.setString(1, owner.toString());
            ResultSet resultSet = preparedStatement.executeQuery();

            if(!resultSet.next())
                return Optional.empty();

            return Optional.of(getHorseData(resultSet.getInt("horse_id")));

        }
        catch(SQLException e)
        {
            RidesClass.console.severe(e.getMessage() + " (getSpawnedHorseDataFromOwner)");
        }

        return Optional.empty();
    }

    private void executeStatement(String sql)
    {
        try (Statement stmt = connection.createStatement())
        {
            stmt.execute(sql);
        } catch (SQLException e) {RidesClass.console.severe(e.getMessage() + " (executeStatement)");}
    }
}
