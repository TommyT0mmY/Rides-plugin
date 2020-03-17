package com.github.tommyt0mmy.rides.storing;

import com.google.gson.JsonObject;

import java.util.UUID;

public class HorseData
{
    private String name;
    private UUID owner;
    private UUID uuid;
    private float speed;
    private byte skin;

    public HorseData(String name, UUID owner, float speed, byte skin)
    {
        this.name = name;
        this.owner = owner;
        this.speed = speed;
        this.skin = skin;
        this.uuid = UUID.randomUUID();
    }

    public HorseData(JsonObject jsonobject)
    {
        uuid = UUID.fromString(jsonobject.get("uuid").getAsString());
        owner = UUID.fromString(jsonobject.get("owner").getAsString());
        name = jsonobject.get("name").getAsString();
        speed = jsonobject.get("speed").getAsFloat();
        skin = jsonobject.get("skin").getAsByte();
    }

    public UUID getUuid()
    {
        return uuid;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void setOwner(UUID owner)
    {
        this.owner = owner;
    }

    public void setSpeed(float speed)
    {
        this.speed = speed;
    }

    public void setSkin(byte skin)
    {
        this.skin = skin;
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
        result.addProperty("skin", skin);

        return result;
    }
}
