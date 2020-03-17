package com.github.tommyt0mmy.rides;

import com.github.tommyt0mmy.rides.commands.RidesCommand;
import com.github.tommyt0mmy.rides.events.RidesGUIEvents;
import com.github.tommyt0mmy.rides.storing.HorseData;
import com.github.tommyt0mmy.rides.storing.OwnerData;
import com.github.tommyt0mmy.rides.storing.RidesDatabase;
import org.bukkit.plugin.java.JavaPlugin;

import java.awt.image.AreaAveragingScaleFilter;
import java.util.ArrayList;
import java.util.UUID;
import java.util.logging.Logger;

public class Rides extends JavaPlugin {

    private static Rides instance;
    public Logger console = getLogger();
    public RidesDatabase database;

    public static Rides getInstance() {
        return instance;
    }

    public void setInstance(Rides instance) {
        Rides.instance = instance;
    }

    public void onEnable() {
        setInstance(this);
        getDataFolder().mkdir();

        database = new RidesDatabase();
        ArrayList<UUID> arl = new ArrayList<>();
        arl.add(UUID.randomUUID());
        arl.add(UUID.randomUUID());

        database.addOwner(new OwnerData(UUID.randomUUID(), arl));
        database.addHorse(new HorseData("John", UUID.randomUUID(), 10, (byte) 1));

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
