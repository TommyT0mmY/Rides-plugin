package com.github.tommyt0mmy.rides.storing;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

public class OwnerData
{
    private UUID uuid;
    private ArrayList<UUID> horses;

    public OwnerData(UUID uuid, ArrayList<UUID> horses)
    {
        this.uuid = uuid;
        this.horses = horses;
    }

    public void setUuid(UUID uuid)
    {
        this.uuid = uuid;
    }

    public void setHorses(ArrayList<UUID> horses)
    {
        this.horses = horses;
    }

    public UUID getUuid()
    {
        return uuid;
    }

    public ArrayList<UUID> getHorses()
    {
        return horses;
    }
}
