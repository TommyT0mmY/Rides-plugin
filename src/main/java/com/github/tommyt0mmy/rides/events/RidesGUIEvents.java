package com.github.tommyt0mmy.rides.events;

import com.github.tommyt0mmy.rides.Rides;
import com.github.tommyt0mmy.rides.enums.RidesItemKey;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

public class RidesGUIEvents implements Listener {
    private Rides RidesClass = Rides.getInstance();

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (!e.getView().getTitle().equals("Rides GUI")) {
            return;
        }
        Player p = (Player) e.getWhoClicked();
        ItemStack clickedItem = e.getCurrentItem();

        if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

        e.setCancelled(true);

        NamespacedKey key = new NamespacedKey(RidesClass, "rides-key");
        RidesItemKey itemKey = RidesItemKey.valueOf(clickedItem.getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING));

        switch(itemKey) {
            case HORSE_LIST_BUTTON:
                p.sendMessage("DEBUG");
                //TODO implement database

                break;
            case HELP_BUTTON:
                //TODO send message
                break;
        }

    }
}

