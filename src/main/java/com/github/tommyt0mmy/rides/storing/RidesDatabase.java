package com.github.tommyt0mmy.rides.storing;

import com.github.tommyt0mmy.rides.Rides;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

public class RidesDatabase
{
    private Rides RidesClass = Rides.getInstance();

    public RidesDatabase()
    {
        loadFile();
    }
    private File file;

    private ArrayList<HorseData> horsesList = new ArrayList<>();
    private ArrayList<OwnerData> ownersList = new ArrayList<>();

    private void loadFile()
    {
        file = new File(RidesClass.getDataFolder().getAbsolutePath() + "\\database.json");
        try
        {
            if (file.createNewFile())
            {
                JsonObject file_json_object = new JsonObject();
                file_json_object.add("horses", new JsonArray());
                file_json_object.add("owners", new JsonArray());

                BufferedWriter bw = new BufferedWriter(new FileWriter(file));
                bw.write(file_json_object.toString());
                bw.flush();
                bw.close();
            }
            else
            {
                JsonParser parser = new JsonParser();
                Reader reader = new FileReader(file);

                JsonObject file_json_object = (JsonObject) parser.parse(reader);
                JsonArray horses_json_array = file_json_object.getAsJsonArray("horses");
                JsonArray owners_json_array = file_json_object.getAsJsonArray("owners");

                Iterator iterator = horses_json_array.iterator();
                while(iterator.hasNext())
                {
                    JsonObject curr_horse_object = (JsonObject) iterator.next();

                    horsesList.add(new HorseData(curr_horse_object));
                }

                iterator = owners_json_array.iterator();
                while(iterator.hasNext())
                {
                    JsonObject curr_owner_object = (JsonObject) iterator.next();

                    ownersList.add(new OwnerData(curr_owner_object));
                }
                reader.close();
            }
        }
        catch (IOException e) {e.printStackTrace();}
    }

    public void addHorse(HorseData horsedata)
    {
        horsesList.add(horsedata);

        try
        {
            BufferedReader br = new BufferedReader(new FileReader(file));

            String readed = br.readLine();
            br.close();

            JsonObject file_json_object = new JsonParser().parse(readed).getAsJsonObject();

            JsonArray horses_array = file_json_object.getAsJsonArray("horses");
            JsonObject new_horse_object = horsedata.toJsonObject();
            horses_array.add(new_horse_object);
            file_json_object.add("horses", horses_array);

            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            bw.write(file_json_object.toString());
            bw.flush();
            bw.close();
        } catch(IOException e) {e.printStackTrace();}
    }

    public void addOwner(OwnerData ownerdata)
    {
        ownersList.add(ownerdata);

        try
        {
            BufferedReader br = new BufferedReader(new FileReader(file));

            String readed = br.readLine();
            br.close();

            JsonObject file_json_object = new JsonParser().parse(readed).getAsJsonObject();

            JsonArray owners_array = file_json_object.getAsJsonArray("owners");
            JsonObject new_owner_object = ownerdata.toJsonObject();
            owners_array.add(new_owner_object);
            file_json_object.add("owners", owners_array);

            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            bw.write(file_json_object.toString());
            bw.flush();
            bw.close();
        } catch(IOException e) {e.printStackTrace();}
    }

    public OwnerData getOwnerByUUID(UUID ownerUuid)
    {
        for (OwnerData currOwner : ownersList)
        {
            if (currOwner.getUuid().equals(ownerUuid))
            {
                return currOwner;
            }
        }
        return null;
    }
    public HorseData getHorseByUUID(UUID horseUuid)
    {
        for (HorseData currHorse : horsesList)
        {
            if (currHorse.getUuid().equals(horseUuid))
            {
                return currHorse;
            }
        }
        return null;
    }

}
