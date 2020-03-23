package com.github.tommyt0mmy.rides.storing;

import com.google.gson.JsonObject;

import java.util.UUID;

public class HorseData
{
    private String name;
    private UUID owner;
    private int id;
    private float speed;
    private byte health;
    private byte skin;

    public HorseData(String name, int id, UUID owner, float speed, byte health, byte skin)
    {
        this.name = name;
        this.owner = owner;
        this.speed = speed;
        this.health = health;
        this.skin = skin;
        this.id = id;
    }

    public int getId()
    {
        return id;
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
}
