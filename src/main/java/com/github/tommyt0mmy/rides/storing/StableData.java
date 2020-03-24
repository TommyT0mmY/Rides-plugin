package com.github.tommyt0mmy.rides.storing;

import org.bukkit.Location;

public class StableData
{
    private Integer stableId;
    private Integer currPreviewState;
    private Location previewLocation;
    private Location nextPreviewSignLocation;
    private Location previousPreviewSignLocation;
    private Location buyHorseSignLocation;

    public StableData(Integer stableId, Integer currPreviewState, Location previewLocation, Location nextPreviewSignLocation, Location previousPreviewSignLocation, Location buyHorseSignLocation)
    {
        this.stableId = stableId;
        this.currPreviewState = currPreviewState;
        this.previewLocation = previewLocation;
        this.nextPreviewSignLocation = nextPreviewSignLocation;
        this.previousPreviewSignLocation = previousPreviewSignLocation;
        this.buyHorseSignLocation = buyHorseSignLocation;
    }

    public Integer getStableId()
    {
        return stableId;
    }

    public Integer getCurrPreviewState()
    {
        return currPreviewState;
    }

    public Location getPreviewLocation()
    {
        return previewLocation;
    }

    public Location getNextPreviewSignLocation()
    {
        return nextPreviewSignLocation;
    }

    public Location getPreviousPreviewSignLocation()
    {
        return previousPreviewSignLocation;
    }

    public Location getBuyHorseSignLocation()
    {
        return buyHorseSignLocation;
    }

    public void setCurrPreviewState(Integer newPreviewState)
    {
        currPreviewState = newPreviewState;
    }
}
