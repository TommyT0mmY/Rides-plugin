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

    public OwnerData (JsonObject jsonobject)
    {
        uuid = UUID.fromString(jsonobject.get("uuid").getAsString());
        horses = new ArrayList<>();
        JsonArray horsesJsonArray = jsonobject.getAsJsonArray("horses");

        for (com.google.gson.JsonElement jsonElement : horsesJsonArray) {
            UUID currUuid = UUID.fromString(jsonElement.getAsString());
            horses.add(currUuid);
        }
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

    JsonObject toJsonObject() {
        JsonObject result = new JsonObject();
        JsonArray horses_array = new JsonArray();
        for (UUID currhorseuuid : horses)
        {
            horses_array.add(String.valueOf(currhorseuuid));
        }
        result.addProperty("uuid", String.valueOf(uuid));
        result.add("horses", horses_array);

        return result;
    }

}
