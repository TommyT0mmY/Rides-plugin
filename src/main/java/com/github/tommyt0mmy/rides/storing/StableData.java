package com.github.tommyt0mmy.rides.storing;

import org.bukkit.Location;

/** This class is used to hold stables data. */
public class StableData
{
    private Integer stableId;
    private Integer currPreviewState;
    private Location previewLocation;
    private Location nextPreviewSignLocation;
    private Location previousPreviewSignLocation;
    private Location buyHorseSignLocation;

    /** Constructor for StableData.
     *
     * @param stableId  the stable id
     * @param currPreviewState  the current horse skin previewed in the stable
     * @param previewLocation  the preview horse spawn location
     * @param nextPreviewSignLocation  the location of the in-game sign used to look at the next preview
     * @param previousPreviewSignLocation  the location of the in-game sign used to look at the previous preview
     * @param buyHorseSignLocation  the location of the in-game sign used to buy the previewed horse
     */
    public StableData(Integer stableId, Integer currPreviewState, Location previewLocation, Location nextPreviewSignLocation, Location previousPreviewSignLocation, Location buyHorseSignLocation)
    {
        this.stableId = stableId;
        this.currPreviewState = currPreviewState;
        this.previewLocation = previewLocation;
        this.nextPreviewSignLocation = nextPreviewSignLocation;
        this.previousPreviewSignLocation = previousPreviewSignLocation;
        this.buyHorseSignLocation = buyHorseSignLocation;
    }

    /** Gets the stable id.
     *
     * @return the stable id
     */
    public Integer getStableId()
    {
        return stableId;
    }

    /** Gets the current horse skin previewed in the stable.
     *
     * @return the skin id
     */
    public Integer getCurrPreviewState()
    {
        return currPreviewState;
    }

    /** Gets the preview horse spawn location.
     *
     * @return the location
     */
    public Location getPreviewLocation()
    {
        return previewLocation;
    }

    /** Gets the location of the in-game sign used to look at the next preview.
     *
     * @return the location
     */
    public Location getNextPreviewSignLocation()
    {
        return nextPreviewSignLocation;
    }

    /** Gets the location of the in-game sign used to look at the previous preview.
     *
     * @return the location
     */
    public Location getPreviousPreviewSignLocation()
    {
        return previousPreviewSignLocation;
    }

    /** Gets the location of the in-game sign used to buy the previewed horse.
     *
     * @return the location
     */
    public Location getBuyHorseSignLocation()
    {
        return buyHorseSignLocation;
    }

    /** Sets a new preview state of the stable.
     * To fully implement the changes in the game, change the preview horse's skin
     * and change the stored value in the database.
     * To change the stored value in the database, use {@link SQLiteDatabase#setStablePreviewState(Integer stableId, Integer newPreviewState)}
     *
     * @param newPreviewState the new preview state
     */
    public void setCurrPreviewState(Integer newPreviewState)
    {
        currPreviewState = newPreviewState;
    }
}
