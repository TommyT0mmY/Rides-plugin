package com.github.tommyt0mmy.rides;

import com.github.tommyt0mmy.rides.commands.RidesCommand;
import com.github.tommyt0mmy.rides.events.RidesGUIEvents;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class Rides extends JavaPlugin {

    private static Rides instance;
    public Logger console = getLogger();

    public static Rides getInstance() {
        return instance;
    }

    public void setInstance(Rides instance) {
        Rides.instance = instance;
    }

    public void onEnable() {
        setInstance(this);

        getDataFolder().mkdir();
        loadCommands();
        loadEvents();
    }

    public void onDisable() {

    }

    private void loadCommands() {
        getCommand("rides").setExecutor(new RidesCommand());
    }

    private void loadEvents() {
        getServer().getPluginManager().registerEvents(new RidesGUIEvents(), this);
    }

}
