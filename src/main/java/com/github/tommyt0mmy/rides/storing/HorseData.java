package com.github.tommyt0mmy.rides.storing;

import java.util.UUID;

/** This class is used to hold basic horse variables. */
public class HorseData
{
    private String name;
    private UUID owner;
    private int id;
    private float speed;
    private byte health;
    private byte skin;

    /** Constructor for HorseData.
     *
     * @param name   the display name of the horse
     * @param id     the database id
     * @param owner  the UUID of the owner
     * @param speed  the natural horse speed
     * @param health the max health of the horse
     * @param skin   the skin id of the horse
     */
    public HorseData(String name, int id, UUID owner, float speed, byte health, byte skin)
    {
        this.name = name;
        this.owner = owner;
        this.speed = speed;
        this.health = health;
        this.skin = skin;
        this.id = id;
    }

    /** Gets the numeric id.
     *
     * @return the numeric id.
     */
    public int getId()
    {
        return id;
    }

    /** Gets the display name.
     *
     * @return the display name
     */
    public String getName()
    {
        return name;
    }

    /** Gets the UUID of the owner.
     *
     * @return the UUID
     */
    public UUID getOwner()
    {
        return owner;
    }

    /** Gets the walking speed.
     *
     * @return the walking speed
     */
    public float getSpeed()
    {
        return speed;
    }

    /** Gets the max health.
     *
     * @return the max health
     */
    public int getHealth()
    {
        return health;
    }

    /** Gets the skin id.
     *
     * @return the skin id
     */
    public byte getSkin()
    {
        return skin;
    }
}
