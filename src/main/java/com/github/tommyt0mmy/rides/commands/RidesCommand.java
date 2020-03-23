package com.github.tommyt0mmy.rides.commands;

import com.github.tommyt0mmy.rides.Rides;
import com.github.tommyt0mmy.rides.enums.RidesItemKey;
import com.github.tommyt0mmy.rides.enums.Permissions;
import com.github.tommyt0mmy.rides.storing.HorseData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
import sun.awt.image.IntegerComponentRaster;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

public class RidesCommand implements CommandExecutor
{

    Rides RidesClass = Rides.getInstance();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {

        if (!(sender instanceof Player))
        {
            sender.sendMessage(RidesClass.messages.getChatMessage("only_players_command"));
            return true;
        }

        Player p = (Player) sender;

        if (args.length != 0)
        {
            //TODO send usage message
            return true;
        }

        if (!p.hasPermission(Permissions.OPEN_GUI.getNode()))
        {
            p.sendMessage(RidesClass.messages.formattedChatMessage(ChatColor.RED, "invalid_permissions"));
            return true;
        }

        //Opening GUI
        Inventory inv = Bukkit.createInventory(p, 27, RidesClass.messages.getGuiTitle("main_page"));
        inv.setItem(10, customGUIitem(Material.HORSE_SPAWN_EGG, RidesItemKey.HORSE_LIST_BUTTON, RidesClass.messages.getGuiButtonName("select_horse"), RidesClass.messages.getGuiButtonLore("select_horse")));
        inv.setItem(16, customGUIitem(Material.BOOK, RidesItemKey.HELP_BUTTON, RidesClass.messages.getGuiButtonName("help"), RidesClass.messages.getGuiButtonLore("help")));
        if (RidesClass.database.getSpawnedHorseFromOwner(p.getUniqueId()).isPresent())
        {
            NamespacedKey uuidkey = new NamespacedKey(RidesClass, "horse_id");
            Optional<UUID> spawnedHorseUuid = RidesClass.database.getSpawnedHorseFromOwner(p.getUniqueId());
            if (spawnedHorseUuid.isPresent())
            {
                Horse horse = (Horse) Bukkit.getEntity(spawnedHorseUuid.get());
                if (horse == null)
                {
                    RidesClass.database.removeSpawnedHorse(p.getUniqueId());
                }
                else
                {
                    Integer horse_id = horse.getPersistentDataContainer().get(uuidkey, PersistentDataType.INTEGER);
                    if (horse_id == null)
                    {
                        RidesClass.database.removeSpawnedHorse(p.getUniqueId());
                    } else
                    {
                        HorseData horsedata = RidesClass.database.getHorseData(horse_id);

                        ArrayList<String> lore = RidesClass.messages.getGuiButtonLore("send_back_horse");
                        for (int i = 0; i < lore.size(); ++i)
                            lore.set(i, lore.get(i).replaceAll("<HORSE_NAME>", horsedata.getName()));

                        inv.setItem(13, customGUIitem(Material.BARRIER, RidesItemKey.REMOVE_HORSE, RidesClass.messages.getGuiButtonName("send_back_horse"), lore));
                    }
                }
            }
        }
        p.openInventory(inv);

        return true;
    }

    private ItemStack customGUIitem(Material material, RidesItemKey code, String name, ArrayList<String> lore)
    {
        ItemStack itemstack = new ItemStack(material);
        ItemMeta meta = itemstack.getItemMeta();
        NamespacedKey key = new NamespacedKey(RidesClass, "rides-key");

        if (meta != null)
        {
            meta.getPersistentDataContainer().set(key, PersistentDataType.STRING , code.toString());
            meta.setLore(lore);
            meta.setDisplayName(name);
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_DESTROYS, ItemFlag.HIDE_POTION_EFFECTS);
        }

        itemstack.setItemMeta(meta);
        return itemstack;
    }
}
