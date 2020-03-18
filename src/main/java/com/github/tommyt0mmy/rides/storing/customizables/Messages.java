package com.github.tommyt0mmy.rides.storing.customizables;

import com.github.tommyt0mmy.rides.Rides;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.util.ChatPaginator;

import java.io.File;
import java.util.ArrayList;
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
            put("messages.no_horse_possessed", "You don't own any horses");
            put("messages.horse_sent_back", "You sent your horse to the stail");
            put("messages.horse_replaced", "Your horse has been sent back to the stail");

            //GUI TITLES
            put("gui.titles.main_page", "&eRides");
            put("gui.titles.select_horse", "&eSelect An Horse");

            //GUI BUTTONS
            put("gui.buttons.select_horse.title", "&eSelect An Horse");
            put("gui.buttons.send_back_horse.title", "&cSend Back");
            put("gui.buttons.help.title", "&eHelp");

            put("gui.buttons.select_horse.lore", "&aA list of every horse possessed by you");
            put("gui.buttons.send_back_horse.lore", "&aSend <HORSE_NAME> to the stable");
            put("gui.buttons.help.lore", "&aFor help digit /rideshelp");
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
        return ChatColor.translateAlternateColorCodes('&', String.format("%s%s %s", color, messagesMap.get("ingame_prefix"), getChatMessage(messageName)));
    }

    public String formattedChatMessage(ChatColor color, String messageName)
    { //Automatically puts the prefix and the color to the message
        return ChatColor.translateAlternateColorCodes('&', String.format("%s%s %s", color, messagesMap.get("ingame_prefix"), getChatMessage(messageName)));
    }

    public String getGuiButtonName(String buttonName)
    {
        return ChatColor.translateAlternateColorCodes('&', messagesMap.get("gui.buttons." + buttonName + ".title"));
    }

    public ArrayList<String> getGuiButtonLore(String buttonName)
    {
        String completeLore = ChatColor.translateAlternateColorCodes('&', messagesMap.get("gui.buttons." + buttonName + ".lore"));
        String[] words = completeLore.split("\\s+");
        ArrayList<String> lore = new ArrayList<>();
        StringBuilder currLine = new StringBuilder();
        int count = 0;
        for (String word : words)
        {
            if (currLine.length() + word.length() > 20)
            {
                lore.add(currLine.toString());
                currLine.setLength(0);
                currLine.append(word + " ");
                continue;
            }
            currLine.append(word + " ");
        }
        lore.add(currLine.toString());
        //coloring
        String lastColor = ChatColor.getLastColors(lore.get(0));
        count = 0;
        for (String line : lore)
        {
            lore.set(count, lastColor + line);
            lastColor = ChatColor.getLastColors(lore.get(count));
            count++;
        }

        return lore;
    }

    public String getGuiTitle(String pageName)
    {
        return ChatColor.translateAlternateColorCodes('&', messagesMap.get("gui.titles." + pageName));
    }
}
