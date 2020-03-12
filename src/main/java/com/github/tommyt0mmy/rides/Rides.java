package com.github.tommyt0mmy.rides;

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

    }

    public void onDisable() {

    }

    private void loadCommands() {

    }

    private void loadEvents() {

    }

}
