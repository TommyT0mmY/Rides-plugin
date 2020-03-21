package com.github.tommyt0mmy.rides.events;

import com.github.tommyt0mmy.rides.Rides;
import com.github.tommyt0mmy.rides.enums.RidesItemKey;
import com.github.tommyt0mmy.rides.storing.HorseData;
import com.github.tommyt0mmy.rides.storing.OwnerData;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.UUID;

public class RidesGUIEvents implements Listener
{
    private Rides RidesClass = Rides.getInstance();

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e)
    {
        Player p = (Player) e.getWhoClicked();
        ItemStack clickedItem = e.getCurrentItem();

        if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

        if (e.getView().getTitle().equals(RidesClass.messages.getGuiTitle("main_page")))
        {
            e.setCancelled(true);

            NamespacedKey key = new NamespacedKey(RidesClass, "rides-key");
            RidesItemKey itemKey = RidesItemKey.valueOf(clickedItem.getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING));

            switch (itemKey)
            {
                case HORSE_LIST_BUTTON:
                    Inventory inv = Bukkit.createInventory(p, 45, RidesClass.messages.getGuiTitle("select_horse"));
                    OwnerData ownerdata = RidesClass.database.getOwnerData(p.getUniqueId());
                    if (ownerdata == null)
                    {
                        p.closeInventory();
                        p.sendMessage(RidesClass.messages.formattedChatMessage(ChatColor.RED, "no_horse_possessed"));
                        return;
                    }

                    ArrayList<UUID> horsesUuid = ownerdata.getHorses();
                    if (horsesUuid == null)
                    {
                        p.closeInventory();
                        p.sendMessage(RidesClass.messages.formattedChatMessage(ChatColor.RED, "no_horse_possessed"));
                        return;
                    }

                    for (UUID currHorseUuid : horsesUuid)
                    {
                        HorseData currHorse = RidesClass.database.getHorseData(currHorseUuid);
                        if (currHorse == null)
                            break;
                        inv.addItem(getEgg(currHorse));
                    }
                    p.openInventory(inv);

                    break;
                case REMOVE_HORSE:
                    UUID horseUuid = RidesClass.spawnedHorses.get(p);
                    if (horseUuid == null)
                        return;
                    LivingEntity horse = (LivingEntity) Bukkit.getServer().getEntity(horseUuid);

                    horse.teleport(new Location(horse.getWorld(), 0, -10, 0));
                    horse.setSilent(true);
                    horse.setHealth(0);
                    RidesClass.spawnedHorses.remove(p);
                    Inventory newInv = p.getOpenInventory().getTopInventory();
                    newInv.setItem(13, new ItemStack(Material.AIR));
                    p.openInventory(newInv);
                    p.sendMessage(RidesClass.messages.formattedChatMessage(ChatColor.GREEN, "horse_sent_back"));
                    break;
                case HELP_BUTTON:
                    //TODO send message
                    break;
            }
        }
        else if (e.getView().getTitle().equals(RidesClass.messages.getGuiTitle("select_horse")))
        {
            e.setCancelled(true);
            NamespacedKey key = new NamespacedKey(RidesClass, "uuid");
            UUID selectedHorseUuid = UUID.fromString(clickedItem.getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING));
            HorseData horsedata = RidesClass.database.getHorseData(selectedHorseUuid);
            spawnHorse(horsedata);
        }
    }

    private ItemStack getEgg(HorseData horsedata)
    {
        final Material[] eggTypes =
        {
            Material.GHAST_SPAWN_EGG,
            Material.LLAMA_SPAWN_EGG,
            Material.RABBIT_SPAWN_EGG,
            Material.DONKEY_SPAWN_EGG,
            Material.ENDERMAN_SPAWN_EGG,
            Material.SILVERFISH_SPAWN_EGG,
            Material.MULE_SPAWN_EGG
        };

        Material eggType = eggTypes[horsedata.getSkin() % 7];


        ItemStack egg = new ItemStack(eggType);
        ItemMeta meta = egg.getItemMeta();
        meta.setDisplayName(horsedata.getName());
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_DESTROYS, ItemFlag.HIDE_POTION_EFFECTS);

        NamespacedKey key = new NamespacedKey(RidesClass, "uuid");
        meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, horsedata.getUuid().toString());

        egg.setItemMeta(meta);
        return egg;
    }

    private Horse spawnHorse(HorseData horsedata)
    {
        Player owner = Bukkit.getPlayer(horsedata.getOwner());
        World world = owner.getWorld();
        Location location = owner.getLocation();

        Horse.Color[] colors =
        {
            Horse.Color.WHITE,
            Horse.Color.CREAMY,
            Horse.Color.CHESTNUT,
            Horse.Color.BROWN,
            Horse.Color.BLACK,
            Horse.Color.GRAY,
            Horse.Color.DARK_BROWN
        };

        Horse.Style[] styles =
        {
            Horse.Style.NONE,
            Horse.Style.WHITE,
            Horse.Style.WHITEFIELD,
            Horse.Style.WHITE_DOTS,
            Horse.Style.BLACK_DOTS
        };

        Horse.Color color = colors[horsedata.getSkin() % 7];
        Horse.Style style = styles[horsedata.getSkin() / 7];

        Horse spawnedHorse = (Horse) world.spawnEntity(location, EntityType.HORSE);
        spawnedHorse.setColor(color);
        spawnedHorse.setStyle(style);
        spawnedHorse.setAdult();
        spawnedHorse.getInventory().setSaddle(new ItemStack(Material.SADDLE, 1));
        spawnedHorse.setTamed(true);
        spawnedHorse.setCustomName(horsedata.getName());
        spawnedHorse.setCustomNameVisible(true);
        spawnedHorse.setOwner(owner);
        spawnedHorse.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(horsedata.getSpeed());
        spawnedHorse.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(horsedata.getHealth());
        NamespacedKey uuidkey = new NamespacedKey(RidesClass, "rides_uuid");
        spawnedHorse.getPersistentDataContainer().set(uuidkey, PersistentDataType.STRING, horsedata.getUuid().toString());

        if (RidesClass.spawnedHorses.get(owner) != null)
        {
            UUID oldHorseUuid = RidesClass.spawnedHorses.get(owner);
            LivingEntity oldHorse = (LivingEntity) Bukkit.getServer().getEntity(oldHorseUuid);

            oldHorse.teleport(new Location(oldHorse.getWorld(), 0, -10, 0));
            oldHorse.setSilent(true);
            oldHorse.setHealth(0);
            owner.sendMessage(RidesClass.messages.formattedChatMessage(ChatColor.GREEN, "horse_replaced"));
        }
        RidesClass.spawnedHorses.put(owner, spawnedHorse.getUniqueId());

        return spawnedHorse;
    }
}

