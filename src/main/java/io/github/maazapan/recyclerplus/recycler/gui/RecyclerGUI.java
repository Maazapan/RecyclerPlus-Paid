package io.github.maazapan.recyclerplus.recycler.gui;

import de.tr7zw.changeme.nbtapi.NBTItem;
import io.github.maazapan.recyclerplus.Recycler;
import io.github.maazapan.recyclerplus.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.List;

public class RecyclerGUI implements InventoryHolder {

    private final Recycler plugin;
    private Inventory inventory;

    public RecyclerGUI(Recycler plugin) {
        this.plugin = plugin;
        this.create();
    }

    public void open(Player player) {
        if (inventory != null) {
            player.openInventory(inventory);
        }
    }

    public void create() {
        FileConfiguration config = plugin.getConfig();

        try {
            inventory = Bukkit.createInventory(this, config.getInt("config.inventory.size"), ChatColor.translateAlternateColorCodes('&', config.getString("config.inventory.title")));

            for (String path : config.getConfigurationSection("config.inventory.items").getKeys(false)) {
                ItemBuilder itemBuilder = new ItemBuilder(Material.valueOf(config.getString("config.inventory.items." + path + ".id")));

                if (config.isSet("config.inventory.items." + path + ".owner")) {
                    itemBuilder.setSkullOwner(config.getString("config.inventory.items." + path + ".owner"));
                }

                if (config.isSet("config.inventory.items." + path + ".texture")) {
                    itemBuilder.setSkullBase64(config.getString("config.inventory.items." + path + ".texture"));
                }

                if (config.isSet("config.inventory.items." + path + ".display_name")) {
                    itemBuilder.setName(config.getString("config.inventory.items." + path + ".display_name"));
                }

                if (config.isSet("config.inventory.items." + path + ".lore")) {
                    itemBuilder.setLore(config.getStringList("config.inventory.items." + path + ".lore"));
                }

                if (config.isSet("config.inventory.items." + path + ".model_data")) {
                    itemBuilder.setModelData(config.getInt("config.inventory.items." + path + ".model_data"));
                }

                NBTItem nbtItem = new NBTItem(itemBuilder.toItemStack());

                if (config.isSet("config.inventory.items." + path + ".actions")) {
                    nbtItem.setObject("recycler-actions", config.getStringList("config.inventory.items." + path + ".actions"));

                } else {
                    nbtItem.setString("recycler-item", "recycler");
                }

                nbtItem.applyNBT(itemBuilder.toItemStack());

                if (config.isSet("config.inventory.items." + path + ".slots")) {
                    for (Integer slots : config.getIntegerList("config.inventory.items." + path + ".slots")) {
                        inventory.setItem(slots, itemBuilder.toItemStack());
                    }
                } else if (config.isSet("config.inventory.items." + path + ".except-slots")) {
                    for (int i = 0; i < inventory.getSize(); i++) {
                        List<Integer> list = config.getIntegerList("config.inventory.items." + path + ".except-slots");

                        if (!list.contains(i)) {
                            inventory.setItem(i, itemBuilder.toItemStack());
                        }
                    }
                } else {
                    inventory.setItem(config.getInt("config.inventory.items." + path + ".slot"), itemBuilder.toItemStack());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
