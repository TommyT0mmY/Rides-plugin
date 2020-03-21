package com.github.tommyt0mmy.rides.storing;

import com.google.gson.JsonObject;

import java.util.UUID;

public class HorseData
{
    private String name;
    private UUID owner;
    private UUID uuid;
    private float speed;
    private byte health;
    private byte skin;

    public HorseData(String name, UUID uuid, UUID owner, float speed, byte health, byte skin)
    {
        this.name = name;
        this.owner = owner;
        this.speed = speed;
        this.health = health;
        this.skin = skin;
        this.uuid = uuid;
    }

    public HorseData(JsonObject jsonobject)
    {
        uuid = UUID.fromString(jsonobject.get("uuid").getAsString());
        owner = UUID.fromString(jsonobject.get("owner").getAsString());
        name = jsonobject.get("name").getAsString();
        speed = jsonobject.get("speed").getAsFloat();
        health = jsonobject.get("health").getAsByte();
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

    public float getSpeed()
    {
        return speed;
    }

    public int getHealth()
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
