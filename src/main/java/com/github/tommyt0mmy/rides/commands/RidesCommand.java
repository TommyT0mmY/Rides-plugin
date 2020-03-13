package com.github.tommyt0mmy.rides.commands;

import com.github.tommyt0mmy.rides.Rides;
import com.github.tommyt0mmy.rides.enums.RidesItemKey;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Arrays;

public class RidesCommand implements CommandExecutor {

    Rides RidesClass = Rides.getInstance();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) {
            //TODO send only players message
            return true;
        }

        Player p = (Player) sender;

        if (args.length != 0) {
            //TODO send usage message
            return true;
        }

        if (!p.hasPermission("rides.rides")) {
            //TODO send error message
            return true;
        }

        //Opening GUI
        Inventory inv = Bukkit.createInventory(p, 27, "Rides GUI");
        inv.setItem(10, customGUIitem(Material.HORSE_SPAWN_EGG, RidesItemKey.HORSE_LIST_BUTTON, "Spawn Horse", "Lists every horse", "possessed by you"));
        inv.setItem(16, customGUIitem(Material.BARRIER, RidesItemKey.HELP_BUTTON, "Help", "For help digit", "/rideshelp"));

        p.openInventory(inv);

        return true;
    }

    private ItemStack customGUIitem(Material material, RidesItemKey code, String name, String...lore) {
        ItemStack itemstack = new ItemStack(material);
        ItemMeta meta = itemstack.getItemMeta();
        NamespacedKey key = new NamespacedKey(RidesClass, "rides-key");

        ArrayList<String> loreList = new ArrayList<>(Arrays.asList(lore));

        if (meta != null) {
            meta.getPersistentDataContainer().set(key, PersistentDataType.STRING , code.toString());
            meta.setLore(loreList);
            meta.setDisplayName(name);
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_DESTROYS, ItemFlag.HIDE_POTION_EFFECTS);
        }

        itemstack.setItemMeta(meta);
        return itemstack;
    }
}
