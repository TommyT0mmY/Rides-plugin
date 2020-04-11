package com.github.tommyt0mmy.rides.storing;

import com.github.tommyt0mmy.rides.Rides;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.sql.*;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

/** This class defines the plugin's database behaviour. */
public class SQLiteDatabase
{
    static Rides RidesClass = Rides.getInstance();

    static private String url = "jdbc:sqlite:" + RidesClass.getDataFolder().getAbsolutePath() + "\\database.db";

    /** Creates a new database connection */
    public SQLiteDatabase()
    {
        clearTable("spawned_horses");
    }


    /// setup methods ///

    /** Used to get a connection to the database */
    static Connection c;
    public static Connection getConn() throws SQLException {
        if(c == null || c.isClosed()) {
            c = DriverManager.getConnection(url);
        }
        return c;
    }

    /** A method that creates every necessary table and should be only called by the constructor.
     * <p>
     * Created tables:
     * <ul>
     *     <li>horses: HorseData is stored in this table
     *     <li>spawned_horses: Every spawned horse by the plugin is stored in this table
     *     <li>stables: StableData is stored in this table
     * </ul>
     */
    private void initTables()
    {
        // Horses table
        String sql = "CREATE TABLE IF NOT EXISTS horses("
                   + "id INT NOT NULL PRIMARY KEY,"
                   + "owner_uuid VARCHAR(255) NOT NULL,"
                   + "name VARCHAR(255) NOT NULL,"
                   + "speed FLOAT NOT NULL,"
                   + "health TINYINT NOT NULL,"
                   + "skin TINYINT NOT NULL"
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
        sql = "CREATE TABLE IF NOT EXISTS stables("
            + "id INT NOT NULL PRIMARY KEY,"
            + "curr_preview_state INT NOT NULL,"
            + "preview_location VARCHAR(255) NOT NULL,"
            + "next_preview_sign_location VARCHAR(255) NOT NULL,"
            + "previous_preview_sign_location VARCHAR(255) NOT NULL,"
            + "buy_horse_sign_location VARCHAR(255) NOT NULL"
            + ");";

        executeStatement(sql);
    }

    /** This method clears a given table
     *
     * @param  table_name the table name
     */
    public void clearTable(String table_name)
    {
        String sql = String.format("DELETE FROM %s;", table_name);
        executeStatement(sql);
    }



    /// horses table methods ///

    /** Gets the stored HorseData corresponding to the horse id.
     * <p>
     * If the id is not registered the method will return
     * Optional.empty, otherwise Optional&lt;HorseData&gt;.
     *
     * @param  horse_id the horse id
     * @return  the HorseData or Optional.empty
     * @see HorseData
     * @see java.util.Optional
     */
    public Optional<HorseData> getHorseData(int horse_id)
    {
        String sql = "SELECT owner_uuid, name, speed, health, skin "
                   + "FROM horses "
                   + "WHERE id = ?";


        try (Connection connection = getConn(); PreparedStatement pstmt = connection.prepareStatement(sql))
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

            connection.close();

            return Optional.of(new HorseData(name, horse_id, owner_uuid, speed, health, skin));
        } catch(SQLException e) {RidesClass.console.severe(e.getMessage() + " (getHorseData)");}

        return Optional.empty();
    }

    /** Use this method to create a new HorseData and adding it to the database.
     * The method could return Optional.empty if something goes wrong, remember to check.
     *
     * @param name   the display name of the horse
     * @param owner  the UUID of the owner
     * @param speed  the natural horse speed
     * @param health the max health of the horse
     * @param skin   the skin id of the horse
     */
    public Optional<HorseData> addHorseData(String name, UUID owner, float speed, byte health, byte skin)
    {
        String sql  = "INSERT INTO horses(" +
                      "id, " +
                      "owner_uuid, " +
                      "name, speed, " +
                      "health, " +
                      "skin) " +
                      "VALUES(?, ?, ?, ?, ?, ?)";

        try(Connection connection = getConn(); PreparedStatement pstmt = connection.prepareStatement(sql))
        {
            //Generate new id
            String getMaxIdQuery = "SELECT MAX(id) AS id FROM horses";
            Statement stmt2 = connection.createStatement();
            ResultSet maxId_rs = stmt2.executeQuery(getMaxIdQuery);
            int newId = 0;
            if (maxId_rs.next())
                newId = maxId_rs.getInt("id") + 1;

            pstmt.setInt(1, newId);
            pstmt.setString(2, owner.toString());
            pstmt.setString(3, name);
            pstmt.setFloat(4, speed);
            pstmt.setInt(5, health);
            pstmt.setByte(6, skin);

            pstmt.execute();
            pstmt.close();
            connection.close();

            return getHorseData(newId);
        } catch (SQLException e) {RidesClass.console.severe(e.getMessage() + " (addHorseData)");}
        return Optional.empty();
    }

    /** Removes an HorseData from the database by the horse id
     *
     * @param  id the horse id
     * @see HorseData
     */
    public void removeHorseData(Integer id)
    {
        String sql = "DELETE FROM horses " +
                     "WHERE id = ?";

        try(Connection connection = getConn(); PreparedStatement pstmt = connection.prepareStatement(sql))
        {
            pstmt.setInt(1, id);

            pstmt.execute();
            pstmt.close();
            connection.close();

        } catch(SQLException e) {RidesClass.console.severe(e.getMessage() + " (removeHorseData)");}
    }

    /** Gets the stored OwnerData corresponding to the owner UUID.
     * <p>
     * If the UUID is not registered the method will return
     * Optional.empty, otherwise Optional&lt;OwnerData&gt;.
     *
     * @param  ownerUuid the owner's UUID
     * @return  the HorseData or Optional.empty
     * @see OwnerData
     * @see java.util.Optional
     */
    public Optional<OwnerData> getOwnerData(UUID ownerUuid)
    {
        ArrayList<Integer> horses = new ArrayList<>();

        String sql = "SELECT id " +
                     "FROM horses " +
                     "WHERE owner_uuid = ?";

        try (Connection connection = getConn(); PreparedStatement pstmt  = connection.prepareStatement(sql))
        {
            pstmt.setString(1, ownerUuid.toString());

            ResultSet rs = pstmt.executeQuery();
            // loop through the result set
            while (rs.next())
            {
                horses.add(rs.getInt("id"));
            }

            rs.close();
            pstmt.close();
            connection.close();

            return Optional.of(new OwnerData(ownerUuid, horses));
        } catch (SQLException e) {RidesClass.console.severe(e.getMessage() + " (getOwnerData)");}
        return Optional.empty();
    }

    /// spawned_horses table methods ///

    /** When a new horse is spawned, it should be saved in the database with this method.
     *
     * @param owner  the owner's UUID
     * @param horse_id  the horse id
     * @param horse_uuid  the horse's UUID
     */
    public void addSpawnedHorse(UUID owner, Integer horse_id, UUID horse_uuid)
    {
        String sql = "INSERT INTO spawned_horses(" +
                     "owner_uuid, " +
                     "horse_id, " +
                     "horse_uuid) " +
                     "VALUES(?, ?, ?)";

        try (Connection connection = getConn(); PreparedStatement pstmt = connection.prepareStatement(sql))
        {
            pstmt.setString(1, owner.toString());
            pstmt.setInt(2, horse_id);
            pstmt.setString(3, horse_uuid.toString());

            pstmt.execute();

            pstmt.close();
            connection.close();

        } catch (SQLException e) {RidesClass.console.severe(e.getMessage() + " (addSpawnedHorse)");}
    }

    /** When an horse is de-spawned, it should be removed from the database with this method.
     *
     * @param  owner the owner's UUID
     */
    public void removeSpawnedHorse(UUID owner)
    {
        String sql = "DELETE FROM spawned_horses " +
                     "WHERE owner_uuid = ?";

        try (Connection connection = getConn(); PreparedStatement pstmt = connection.prepareStatement(sql))
        {
            pstmt.setString(1, owner.toString());

            pstmt.execute();

            pstmt.close();
            connection.close();

        } catch (SQLException e) {RidesClass.console.severe(e.getMessage() + " (removeSpawnedHorse)");}
    }

    /** Returns the UUID of an alive horse linked to an owner's UUID.
     * If there isn't any spawned horse the method will return Optional.empty,
     * otherwise it will return Optional&lt;UUID&gt;
     *
     * @param  owner the owner's UUID
     * @return  the horse's UUID or Optional.empty
     * @see java.util.Optional
     */
    public Optional<UUID> getSpawnedHorseFromOwner(UUID owner)
    {
        String sql = "SELECT horse_uuid " +
                     "FROM spawned_horses " +
                     "WHERE owner_uuid = ?";

        try (Connection connection = getConn(); PreparedStatement preparedStatement = connection.prepareStatement(sql))
        {
            preparedStatement.setString(1, owner.toString());
            ResultSet resultSet = preparedStatement.executeQuery();

            if(!resultSet.next())
                return Optional.empty();

            UUID horse_uuid = UUID.fromString(resultSet.getString("horse_uuid"));

            resultSet.close();
            connection.close();

            return Optional.of(horse_uuid);
        }
        catch(SQLException e)
        {
            RidesClass.console.severe(e.getMessage() + " (getSpawnedHorseFromOwner)");
        }

        return Optional.empty();
    }

    /** Returns the HorseData of an alive horse linked to an owner's UUID.
     * If there isn't any spawned horse the method will return Optional.empty,
     * otherwise it will return Optional&lt;HorseData&gt;
     *
     * @param  owner the owner's UUID
     * @return  the HorseData or Optional.empty
     * @see HorseData
     * @see java.util.Optional
     */
    public Optional<HorseData> getSpawnedHorseDataFromOwner(UUID owner)
    {
        String sql = "SELECT horse_id " +
                     "FROM spawned_horses " +
                     "WHERE owner_uuid = ?";

        try (Connection connection = getConn(); PreparedStatement preparedStatement = connection.prepareStatement(sql))
        {
            preparedStatement.setString(1, owner.toString());
            ResultSet resultSet = preparedStatement.executeQuery();

            if(!resultSet.next())
                return Optional.empty();

            int horse_id = resultSet.getInt("horse_id");

            resultSet.close();
            connection.close();

            return getHorseData(horse_id);

        } catch(SQLException e) {RidesClass.console.severe(e.getMessage() + " (getSpawnedHorseDataFromOwner)");}

        return Optional.empty();
    }



    /// stables table methods ///

    /** Use this method to create a new StableData and adding it to the database.
     * The method could return Optional.empty if something goes wrong, remember to check.
     *
     * @param  preview_location the preview horse spawn location
     * @param  next_preview_sign_location the location of the in-game sign used to look at the next preview
     * @param  previous_preview_sign_location the location of the in-game sign used to look at the previous preview
     * @param  buy_horse_sign_location the location of the in-game sign used to buy the previewed horse
     * @return  the StableData or Optional.empty
     * @see StableData
     * @see java.util.Optional
     */
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

        try(Connection connection = getConn(); PreparedStatement pstmt = connection.prepareStatement(sql))
        {
            //Generate new id
            String getMaxIdQuery = "SELECT MAX(id) AS id FROM stables";
            Statement stmt2 = connection.createStatement();
            ResultSet maxId_rs = stmt2.executeQuery(getMaxIdQuery);
            int newId = 0;
            if (maxId_rs.next())
                newId = maxId_rs.getInt("id") + 1;

            pstmt.setInt(1, newId);
            pstmt.setInt(2, 0);
            pstmt.setString(3, SQLiteDatabase.LocationToString(preview_location));
            pstmt.setString(4, SQLiteDatabase.LocationToString(next_preview_sign_location));
            pstmt.setString(5, SQLiteDatabase.LocationToString(previous_preview_sign_location));
            pstmt.setString(6, SQLiteDatabase.LocationToString(buy_horse_sign_location));

            pstmt.execute();

            pstmt.close();
            maxId_rs.close();
            connection.close();

            return getStable(newId);
        } catch(SQLException e) {RidesClass.console.severe(e.getMessage() + " (addStable)");}
        return Optional.empty();
    }

    /** Removes a StableData from the database by the stable id.
     *
     * @param  stableId the stable id
     * @see StableData
     */
    public void removeStable(Integer stableId)
    {
        String sql = "DELETE FROM stables " +
                     "WHERE id = ?";

        try (Connection connection = getConn(); PreparedStatement pstmt = connection.prepareStatement(sql))
        {
            pstmt.setInt(1, stableId);

            pstmt.execute();

            pstmt.close();
            connection.close();
        } catch (SQLException e) {RidesClass.console.severe(e.getMessage() + " (removeStable)");}
    }

    /** Gets a StableData from the database by the stable id.
     *
     * @param  stableId the stable id
     * @return  the StableData or Optional.empty
     * @see StableData
     * @see java.util.Optional
     */
    public Optional<StableData> getStable(Integer stableId)
    {
        String sql = "SELECT curr_preview_state, " +
                     "preview_location, " +
                     "next_preview_sign_location, " +
                     "previous_preview_sign_location, " +
                     "buy_horse_sign_location " +
                     "FROM stables " +
                     "WHERE id = ?";

        try (Connection connection = getConn(); PreparedStatement pstmt = connection.prepareStatement(sql))
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

    /** Changes the current horse skin previewed in the stable.
     *
     * @param stableId the stable id
     * @param newState the new skin id
     */
    public void setStablePreviewState(Integer stableId, Integer newState)
    {
        String sql = "UPDATE stables " +
                     "SET curr_preview_state = ? " +
                     "WHERE id = ?";

        try (Connection connection = getConn(); PreparedStatement pstmt = connection.prepareStatement(sql))
        {
            pstmt.setInt(1, newState);
            pstmt.setInt(2, stableId);

            pstmt.execute();

            pstmt.close();
            connection.close();

        } catch (SQLException e) {RidesClass.console.severe(e.getMessage() + " (setStablePreviewState)");}
    }



    /// general purpose methods ///

    /** This method executes a simple sql query that doesn't expect an output
     *
     * @param sql the sql query
     */
    private void executeStatement(String sql)
    {
        try (Connection connection = getConn(); Statement stmt = connection.createStatement())
        {
            stmt.execute(sql);
            stmt.close();
            connection.close();
        } catch (SQLException e) {RidesClass.console.severe(e.getMessage() + " (" + sql + ")");}
    }

    /** This method converts a location to a String.
     * To convert back the String to a Location use {@link #StringToLocation(String string)}.
     * <p>
     * Output example: "world;100;50;-180", where the world name and x, y and z coordinates are separated by a semicolon.
     *
     * @param location the Location
     * @return the String
     */
    private static String LocationToString(Location location)
    {
        return location.getWorld().getName() + ";" + location.getBlockX() + ";" + location.getBlockY() + ";" + location.getBlockZ();
    }

    /** This method converts a String (possibly generated by {@link #LocationToString(Location location)}).
     * If the string is in the wrong format, Optional.empty will return otherwise Optional&lt;Location&gt;.
     *
     * @param string the String
     * @return the Location
     * @see java.util.Optional
     */
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
