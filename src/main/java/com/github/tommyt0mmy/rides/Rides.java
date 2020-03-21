package com.github.tommyt0mmy.rides;

import com.github.tommyt0mmy.rides.commands.RidesCommand;
import com.github.tommyt0mmy.rides.events.RidesGUIEvents;
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
    public SQLiteDatabase database;
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
        database = new SQLiteDatabase();

        /* DEBUG
        UUID DEBUGUUID = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");

        sqlite.addHorseData(new HorseData("Nigga Horse", UUID.randomUUID(), DEBUGUUID, 1, (byte) 10, (byte) 10));

        OwnerData DEBUGOWNERDATA = sqlite.getOwnerData(DEBUGUUID);
        console.info("\n\n\n\n");
        for (UUID currHorse : DEBUGOWNERDATA.getHorses())
        {
            console.info(currHorse.toString());
        }
        console.info("\n\n\n\n");
        */

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
