package com.github.tommyt0mmy.rides.storing.customizables;

import com.github.tommyt0mmy.rides.Rides;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;

public class Messages
{

    public Messages()
    {
        loadMessagesFile();
    }

    private Rides RidesClass = Rides.getInstance();

    private FileConfiguration messagesConfig;
    private File messagesConfigFile;
    private final String fileName = "messages.yml";

    private HashMap<String, String> messagesMap = new HashMap<String, String>()
    {
        {
            put("ingame_prefix", "[Rides]");

            //CHAT MESSAGES
            put("messages.only_players_command", "Only players can execute this command");
            put("messages.invalid_permissions", "Invalid Permissions!");

            //GUI BUTTONS

        }
    };

    private void loadMessagesFile()
    { //loading messages.yml
        messagesConfigFile = new File(RidesClass.getDataFolder(), fileName);
        if (!messagesConfigFile.exists())
        {
            messagesConfigFile.getParentFile().mkdirs();
            RidesClass.saveResource(fileName, false);
            RidesClass.console.info("Created messages.yml");
            RidesClass.console.info("To modify ingame messages edit messages.yml and reload the plugin");
        }

        messagesConfig = new YamlConfiguration();
        try
        {
            messagesConfig.load(messagesConfigFile);
            loadMessages();
        } catch (Exception e) {
            RidesClass.console.severe("Couldn't load messages.yml file properly!");
        }
    }

    private void loadMessages()
    {
        boolean needsRewrite = false; //A rewrite is needed when loaded on the server there is a older version of messages.yml, without newer messages

        for (String messageKey : messagesMap.keySet())
        {
            boolean result = loadMessage(messageKey);
            needsRewrite = needsRewrite || result;
        }

        //Once every message is loaded on the messagesMap, if needsRewrite is true, messages.yml gets closed, deleted, and rewritten with every message
        if (needsRewrite)
        {
            try
            {
                if (messagesConfigFile.delete())
                { //deleting file
                    messagesConfigFile.getParentFile().mkdirs(); //creating file
                    messagesConfigFile.createNewFile();
                    messagesConfig.load(messagesConfigFile);
                    for (String messageKey : messagesMap.keySet())
                    { //writing file
                        messagesConfig.set(messageKey, messagesMap.get(messageKey));
                    }
                    messagesConfig.save(messagesConfigFile);
                }
                else
                {
                    RidesClass.console.severe("Couldn't load messages.yml file properly!");
                }
            }
            catch (Exception e)
            {
                RidesClass.console.severe("Couldn't load messages.yml file properly!");
            }
        }

        RidesClass.console.info("Loaded custom messages");
    }

    private boolean loadMessage (String messageName)
    { //returns true if the message is not found, letting loadMessages() know if a rewrite of the file is needed or not
        boolean returnValue = false;

        String path = messageName;
        if (messagesConfig.getString(path, null) == null)
        { //message not found, returns true
            returnValue = true;
        }

        if (messagesConfig.getString(path) == null)
        {
            return true;
        }

        messagesMap.put(messageName, messagesConfig.getString(messageName)); //loading messages into messagesMap
        return returnValue;
    }

    public String getChatMessage(String messageName)
    {
        return messagesMap.get("messages." + messageName);
    }

    public String formattedChatMessage(String color, String messageName)
    { //Automatically puts the prefix and the color to the message
        return String.format("%s%s %s", color, messagesMap.get("ingame_prefix"), getChatMessage(messageName));
    }
}
