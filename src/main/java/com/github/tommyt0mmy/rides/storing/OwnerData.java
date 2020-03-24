package com.github.tommyt0mmy.rides.storing;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

/**
 * This class is used to hold owners data.
 *
 * <p>
 * To add an horse to an owner use {@link SQLiteDatabase#addHorseData(HorseData)}
 * then use {@link SQLiteDatabase#getOwnerData(UUID ownerUuid)} to get a new OwnerData object with the new horse.
 * <p>
 * To remove an horse from an owner use {@link SQLiteDatabase#removeHorseData(Integer horseId)}
 * then use {@link SQLiteDatabase#getOwnerData(UUID ownerUuid)} to get a new OwnerData object without the removed horse,
 * if every horse gets removed, {@link Optional#empty()} will be returned.
 */
public class OwnerData
{
    private UUID uuid;
    private ArrayList<Integer> horses;

    /** Constructor of OwnerData.
     *
     * @param uuid the owner UUID
     * @param horses an array composed of every owned horse's HorseData id
     */
    public OwnerData(UUID uuid, ArrayList<Integer> horses)
    {
        this.uuid = uuid;
        this.horses = horses;
    }

    /** Gets the owner UUID.
     *
     * @return the UUID
     */
    public UUID getUuid()
    {
        return uuid;
    }

    /** Gets an array composed of every owned horse's HorseData id.
     *
     * @return the array
     */
    public ArrayList<Integer> getHorses()
    {
        return horses;
    }
}
