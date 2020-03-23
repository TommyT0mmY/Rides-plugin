package com.github.tommyt0mmy.rides.storing;

import java.util.ArrayList;
import java.util.UUID;

public class OwnerData
{
    private UUID uuid;
    private ArrayList<Integer> horses;

    public OwnerData(UUID uuid, ArrayList<Integer> horses)
    {
        this.uuid = uuid;
        this.horses = horses;
    }

    public void setUuid(UUID uuid)
    {
        this.uuid = uuid;
    }

    public void setHorses(ArrayList<Integer> horses)
    {
        this.horses = horses;
    }

    public UUID getUuid()
    {
        return uuid;
    }

    public ArrayList<Integer> getHorses()
    {
        return horses;
    }
}
