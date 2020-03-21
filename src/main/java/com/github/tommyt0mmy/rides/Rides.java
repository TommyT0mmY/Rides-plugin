package com.github.tommyt0mmy.rides;

import com.github.tommyt0mmy.rides.commands.RidesCommand;
import com.github.tommyt0mmy.rides.events.RidesGUIEvents;
import com.github.tommyt0mmy.rides.storing.HorseData;
import com.github.tommyt0mmy.rides.storing.RidesDatabase;
import com.github.tommyt0mmy.rides.storing.SQLiteDatabase;
import com.github.tommyt0mmy.rides.storing.customizables.Messages;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

public class Rides extends JavaPlugin {

    private static Rides instance;
    public Logger console = getLogger();
    public RidesDatabase database;
    public SQLiteDatabase sqlite;
    public Messages messages;
    public Map<Player, UUID> spawnedHorses = new HashMap<>();

    public static Rides getInstance() {
        return instance;
    }

    public void setInstance(Rides instance) {
        Rides.instance = instance;
    }

    public void onEnable()
    {
        setInstance(this);
        getDataFolder().mkdir();

        messages = new Messages();
        database = new RidesDatabase();
        sqlite = new SQLiteDatabase();

        // sqlite.addHorseData(new HorseData("nigga", UUID.randomUUID(), UUID.randomUUID(), 1, (byte) 10, (byte) 10));


        loadCommands();
        loadEvents();
    }

    public void onDisable(){

    }

    private void loadCommands() {
        getCommand("rides").setExecutor(new RidesCommand());
    }

    private void loadEvents() {
        getServer().getPluginManager().registerEvents(new RidesGUIEvents(), this);
    }

}
