package io.github.maazapan.recyclerplus.utils;

import de.tr7zw.changeme.nbtapi.NBTItem;
import io.github.maazapan.recyclerplus.Recycler;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

public class InventoryUtils {

    private final Recycler plugin;

    public InventoryUtils(Recycler plugin) {
        this.plugin = plugin;
    }

    public ItemStack getRecyclerItem() {
        FileConfiguration config = plugin.getConfig();

        ItemBuilder itemBuilder = new ItemBuilder(Material.valueOf(config.getString("config.item-recycler.id")));

        if(config.isSet("config.item-recycler.owner")){
            itemBuilder.setSkullOwner(config.getString("config.item-recycler.owner"));
        }

        if(config.isSet("config.item-recycler.texture")){
            itemBuilder.setSkullBase64(config.getString("config.item-recycler.texture"));
        }

        if (config.isSet("config.item-recycler.display_name")) {
            itemBuilder.setName(config.getString("config.item-recycler.display_name"));
        }

        if (config.isSet("config.item-recycler.lore")) {
            itemBuilder.setLore(config.getStringList("config.item-recycler.lore"));
        }

        NBTItem nbtItem = new NBTItem(itemBuilder.toItemStack());
        nbtItem.setString("recycler-item", "item");
        nbtItem.applyNBT(itemBuilder.toItemStack());

        return itemBuilder.toItemStack();
    }
}
