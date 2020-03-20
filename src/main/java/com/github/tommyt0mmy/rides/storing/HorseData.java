package com.github.tommyt0mmy.rides.storing;

import com.google.gson.JsonObject;

import java.util.UUID;

public class HorseData
{
    private String name;
    private UUID owner;
    private UUID uuid;
    private double speed;
    private double health;
    private byte skin;

    public HorseData(String name, UUID owner, double speed, double health, byte skin)
    {
        this.name = name;
        this.owner = owner;
        this.speed = speed;
        this.health = health;
        this.skin = skin;
        this.uuid = UUID.randomUUID();
    }

    public HorseData(JsonObject jsonobject)
    {
        uuid = UUID.fromString(jsonobject.get("uuid").getAsString());
        owner = UUID.fromString(jsonobject.get("owner").getAsString());
        name = jsonobject.get("name").getAsString();
        speed = jsonobject.get("speed").getAsDouble();
        health = jsonobject.get("health").getAsDouble();
        skin = jsonobject.get("skin").getAsByte();
    }

    public UUID getUuid()
    {
        return uuid;
    }

    public String getName()
    {
        return name;
    }

    public UUID getOwner()
    {
        return owner;
    }

    public double getSpeed()
    {
        return speed;
    }

    public double getHealth()
    {
        return health;
    }

    public byte getSkin()
    {
        return skin;
    }

    JsonObject toJsonObject()
    {
        JsonObject result = new JsonObject();
        result.addProperty("name", name);
        result.addProperty("uuid", String.valueOf(uuid));
        result.addProperty("owner", String.valueOf(owner));
        result.addProperty("speed", speed);
        result.addProperty("health", health);
        result.addProperty("skin", skin);

        return result;
    }
}
