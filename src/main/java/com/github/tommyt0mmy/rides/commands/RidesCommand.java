package com.github.tommyt0mmy.rides.commands;

import com.github.tommyt0mmy.rides.Rides;
import com.github.tommyt0mmy.rides.enums.RidesItemKey;
import com.github.tommyt0mmy.rides.storing.HorseData;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

public class RidesCommand implements CommandExecutor
{

    Rides RidesClass = Rides.getInstance();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {

        if (!(sender instanceof Player))
        {
            //TODO send only players message
            return true;
        }

        Player p = (Player) sender;

        if (args.length != 0)
        {
            //TODO send usage message
            return true;
        }

        if (!p.hasPermission("rides.rides"))
        {
            //TODO send error message
            return true;
        }

        //Opening GUI
        Inventory inv = Bukkit.createInventory(p, 27, "§eRides GUI");
        inv.setItem(10, customGUIitem(Material.HORSE_SPAWN_EGG, RidesItemKey.HORSE_LIST_BUTTON, "§aSpawn Horse", "Lists every horse", "possessed by you"));
        inv.setItem(16, customGUIitem(Material.BOOK, RidesItemKey.HELP_BUTTON, "§aHelp", "For help digit", "/rideshelp"));
        if (RidesClass.spawnedHorses.get(p) != null)
        {
            NamespacedKey uuidkey = new NamespacedKey(RidesClass, "rides_uuid");
            Horse horse = (Horse) Bukkit.getEntity(RidesClass.spawnedHorses.get(p));
            if (horse == null)
            {
                RidesClass.spawnedHorses.remove(p);
            }
            else
            {
                String UuidString = horse.getPersistentDataContainer().get(uuidkey, PersistentDataType.STRING);
                if (UuidString == null)
                {
                    RidesClass.spawnedHorses.remove(p);
                } else
                {
                    UUID rides_uuid = UUID.fromString(UuidString);
                    HorseData horsedata = RidesClass.database.getHorseByUUID(rides_uuid);
                    inv.setItem(13, customGUIitem(Material.BARRIER, RidesItemKey.REMOVE_HORSE, "§cSend back", "send " + horsedata.getName(), "to the stable"));
                }
            }
        }
        p.openInventory(inv);

        return true;
    }

    private ItemStack customGUIitem(Material material, RidesItemKey code, String name, String...lore)
    {
        ItemStack itemstack = new ItemStack(material);
        ItemMeta meta = itemstack.getItemMeta();
        NamespacedKey key = new NamespacedKey(RidesClass, "rides-key");

        ArrayList<String> loreList = new ArrayList<>(Arrays.asList(lore));

        if (meta != null)
        {
            meta.getPersistentDataContainer().set(key, PersistentDataType.STRING , code.toString());
            meta.setLore(loreList);
            meta.setDisplayName(name);
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_DESTROYS, ItemFlag.HIDE_POTION_EFFECTS);
        }

        itemstack.setItemMeta(meta);
        return itemstack;
    }
}
