package com.github.tommyt0mmy.rides.storing;

import com.github.tommyt0mmy.rides.Rides;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

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


    /// setup methods ///

    private void initDatabase()
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

    private void initTables()
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

        //Stables table
        sql += "CREATE TABLE IF NOT EXISTS stables("
             + "id INT NOT NULL PRIMARY KEY,"
             + "curr_preview_state INT NOT NULL,"
             + "preview_location VARCHAR(255) NOT NULL,"
             + "next_preview_sign_location VARCHAR(255) NOT NULL,"
             + "previous_preview_sign_location VARCHAR(255) NOT NULL,"
             + "buy_horse_sign_location VARCHAR(255) NOT NULL"
             + ");";

        executeStatement(sql);
    }

    public void clearTable(String table_name)
    {
        String sql = String.format("DELETE FROM %s;", table_name);
        executeStatement(sql);
    }



    /// horses table methods ///

    public Optional<HorseData> getHorseData(int horse_id)
    {
        String sql = "SELECT owner_uuid, name, speed, health, skin "
                   + "FROM horses "
                   + "WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql))
        {
            pstmt.setInt(1, horse_id);
            ResultSet rs = pstmt.executeQuery();
            if (!rs.next())
                return Optional.empty();

            String name = rs.getString("name");
            UUID owner_uuid = UUID.fromString(rs.getString("owner_uuid"));
            float speed = rs.getFloat("speed");
            byte health = rs.getByte("health");
            byte skin = rs.getByte("skin");

            return Optional.of(new HorseData(name, horse_id, owner_uuid, speed, health, skin));
        } catch(SQLException e) {RidesClass.console.severe(e.getMessage() + " (getHorseData)");}

        return Optional.empty();
    }

    public void addHorseData(HorseData horseData)
    {
        String sql  = "INSERT INTO horses(" +
                      "id, " +
                      "owner_uuid, " +
                      "name, speed, " +
                      "health, " +
                      "skin) " +
                      "VALUES(?, ?, ?, ?, ?, ?)";

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

    public void removeHorseData(Integer id)
    {
        String sql = "DELETE FROM horses " +
                     "WHERE id = ?";

        try(PreparedStatement pstmt = connection.prepareStatement(sql))
        {
            pstmt.setInt(1, id);

            pstmt.execute();
        } catch(SQLException e) {RidesClass.console.severe(e.getMessage() + " (removeHorseData)");}
    }

    public Optional<OwnerData> getOwnerData(UUID ownerUuid)
    {
        ArrayList<Integer> horses = new ArrayList<>();

        String sql = "SELECT id " +
                     "FROM horses " +
                     "WHERE owner_uuid = ?";

        try (PreparedStatement pstmt  = connection.prepareStatement(sql))
        {
            pstmt.setString(1, ownerUuid.toString());

            ResultSet rs = pstmt.executeQuery();
            // loop through the result set
            while (rs.next())
            {
                horses.add(rs.getInt("id"));
            }
            return Optional.of(new OwnerData(ownerUuid, horses));
        } catch (SQLException e) {RidesClass.console.severe(e.getMessage() + " (getOwnerData)");}
        return Optional.empty();
    }

    /// spawned_horses table methods ///

    public void addSpawnedHorse(UUID owner, Integer horse_id, UUID horse_uuid)
    {
        String sql = "INSERT INTO spawned_horses(" +
                     "owner_uuid, " +
                     "horse_id, " +
                     "horse_uuid) " +
                     "VALUES(?, ?, ?)";

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
        String sql = "DELETE FROM spawned_horses " +
                     "WHERE owner_uuid = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql))
        {
            pstmt.setString(1, owner.toString());

            pstmt.execute();
        } catch (SQLException e) {RidesClass.console.severe(e.getMessage() + " (removeSpawnedHorse)");}
    }

    public Optional<UUID> getSpawnedHorseFromOwner(UUID owner)
    {
        String sql = "SELECT horse_uuid " +
                     "FROM spawned_horses " +
                     "WHERE owner_uuid = ?";

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
        String sql = "SELECT horse_id " +
                     "FROM spawned_horses " +
                     "WHERE owner_uuid = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql))
        {
            preparedStatement.setString(1, owner.toString());
            ResultSet resultSet = preparedStatement.executeQuery();

            if(!resultSet.next())
                return Optional.empty();

            return getHorseData(resultSet.getInt("horse_id"));

        } catch(SQLException e) {RidesClass.console.severe(e.getMessage() + " (getSpawnedHorseDataFromOwner)");}

        return Optional.empty();
    }



    /// stables table methods ///

    public Optional<StableData> addStable(Location preview_location, Location next_preview_sign_location, Location previous_preview_sign_location, Location buy_horse_sign_location)
    {
        String sql = "INSERT INTO stables(" +
                     "id, " +
                     "curr_preview_state, " +
                     "preview_location, " +
                     "next_preview_sign_location, " +
                     "previous_preview_sign_location, " +
                     "buy_horse_sign_location) " +
                     "VALUES(?, ?, ?, ?, ?, ?)";

        try(PreparedStatement pstmt = connection.prepareStatement(sql))
        {
            //Generate new id
            String sql2 = "SELECT MAX(id) FROM stables AS maxId";
            Statement stmt2 = connection.createStatement();
            stmt2.execute(sql2);
            ResultSet maxId_rs = stmt2.getResultSet();
            if (!maxId_rs.next())
                return Optional.empty();
            int newId = maxId_rs.getInt("maxId") + 1;

            pstmt.setInt(1, newId);
            pstmt.setInt(2, 0);
            pstmt.setString(3, SQLiteDatabase.LocationToString(preview_location));
            pstmt.setString(4, SQLiteDatabase.LocationToString(next_preview_sign_location));
            pstmt.setString(5, SQLiteDatabase.LocationToString(previous_preview_sign_location));
            pstmt.setString(6, SQLiteDatabase.LocationToString(buy_horse_sign_location));

            pstmt.execute();

            return getStable(newId);
        } catch(SQLException e) {RidesClass.console.severe(e.getMessage() + " (addStable)");}
        return Optional.empty();
    }

    public void removeStable(Integer stableId)
    {
        String sql = "DELETE FROM stables " +
                     "WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql))
        {
            pstmt.setInt(1, stableId);

            pstmt.execute();
        } catch (SQLException e) {RidesClass.console.severe(e.getMessage() + " (removeStable)");}
    }

    public Optional<StableData> getStable(Integer stableId)
    {
        String sql = "SELECT curr_preview_state, " +
                     "preview_location, " +
                     "next_preview_sign_location, " +
                     "previous_preview_sign_location, " +
                     "buy_horse_sign_location " +
                     "FROM stables " +
                     "WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql))
        {
            pstmt.setInt(1, stableId);
            pstmt.execute();

            ResultSet resultSet = pstmt.getResultSet();
            if (!resultSet.next())
                return Optional.empty();

            Integer curr_preview_state = resultSet.getInt("curr_preview_state");
            Optional<Location> preview_location = SQLiteDatabase.StringToLocation(resultSet.getString("preview_location"));
            Optional<Location> next_preview_sign_location = SQLiteDatabase.StringToLocation(resultSet.getString("next_preview_sign_location"));
            Optional<Location> previous_preview_sign_location = SQLiteDatabase.StringToLocation(resultSet.getString("previous_preview_sign_location"));
            Optional<Location> buy_horse_sign_location = SQLiteDatabase.StringToLocation(resultSet.getString("previous_preview_sign_location"));

            if (!(preview_location.isPresent() &&
                    next_preview_sign_location.isPresent() &&
                    previous_preview_sign_location.isPresent() &&
                    buy_horse_sign_location.isPresent()))
                return Optional.empty();

            return Optional.of(new StableData(stableId,
                    curr_preview_state,
                    preview_location.get(),
                    next_preview_sign_location.get(),
                    previous_preview_sign_location.get(),
                    buy_horse_sign_location.get()));

        } catch (SQLException e) {RidesClass.console.severe(e.getMessage() + " (getStable)");}

        return Optional.empty();
    }

    public void setStablePreviewState(Integer stableId, Integer newState)
    {
        String sql = "UPDATE stables " +
                     "SET curr_preview_state = ? " +
                     "WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql))
        {
            pstmt.setInt(1, newState);
            pstmt.setInt(2, stableId);

            pstmt.execute();
        } catch (SQLException e) {RidesClass.console.severe(e.getMessage() + " (setStablePreviewState)");}
    }



    /// general purpose methods ///

    private void executeStatement(String sql)
    {
        try (Statement stmt = connection.createStatement())
        {
            stmt.execute(sql);
        } catch (SQLException e) {RidesClass.console.severe(e.getMessage() + " (executeStatement)");}
    }

    private static String LocationToString(Location loc)
    {
        return loc.getWorld().getName() + ";" + loc.getBlockX() + ";" + loc.getBlockY() + ";" + loc.getBlockZ();
    }

    private static Optional<Location> StringToLocation(String string)
    {
        String[] split = string.split(";");
        World world = Bukkit.getWorld(split[0]);
        if (world == null || split.length != 4)
            return Optional.empty();
        int x = Integer.parseInt(split[1]);
        int y = Integer.parseInt(split[2]);
        int z = Integer.parseInt(split[3]);
        return Optional.of(new Location(world, x, y, z));
    }
}
