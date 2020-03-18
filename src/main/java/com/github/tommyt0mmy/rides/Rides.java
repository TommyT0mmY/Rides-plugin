package com.github.tommyt0mmy.rides;

import com.github.tommyt0mmy.rides.commands.RidesCommand;
import com.github.tommyt0mmy.rides.events.RidesGUIEvents;
import com.github.tommyt0mmy.rides.storing.RidesDatabase;
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

        database = new RidesDatabase();

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
