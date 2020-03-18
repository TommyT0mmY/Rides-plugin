package com.github.tommyt0mmy.rides.enums;

public enum Permissions
{
    OPEN_GUI("rides"),
    OPEN_HELP("help");

    private String node;

    Permissions(String node)
    {
        this.node = node;
    }

    public String getNode() { return "rides." + node; }
}