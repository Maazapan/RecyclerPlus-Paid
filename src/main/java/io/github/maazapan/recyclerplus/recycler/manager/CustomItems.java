package io.github.maazapan.recyclerplus.recycler.manager;

import de.tr7zw.changeme.nbtapi.NBTItem;
import io.github.maazapan.recyclerplus.Recycler;
import io.github.maazapan.recyclerplus.utils.ItemBuilder;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class CustomItems {

    private final Map<String, ItemStack> customItemsMap;
    private final Recycler plugin;

    public CustomItems(Recycler plugin) {
        this.customItemsMap = new HashMap<>();
        this.plugin = plugin;
    }

    public void load() {
        FileConfiguration config = plugin.getConfig();
        customItemsMap.clear();

        try {
            // Load custom items from config.yml
            for (String id : config.getConfigurationSection("config.custom-items").getKeys(false)) {
                ItemBuilder itemBuilder = new ItemBuilder().fromConfig(config, "config.custom-items." + id);
                ItemStack itemStack = itemBuilder.toItemStack();

                NBTItem nbtItem = new NBTItem(itemStack);
                nbtItem.setString("custom_item_id", id);
                nbtItem.applyNBT(itemStack);

                customItemsMap.put(id, itemStack);
            }
            plugin.getLogger().info("Success load custom items.");

        } catch (Exception e) {
            plugin.getLogger().warning("Han ocurred error when load custom items, please check it.");
            e.printStackTrace();
        }
    }

    public boolean isCustomItem(ItemStack itemStack) {
        return new NBTItem(itemStack).hasTag("custom_item_id");
    }

    public ItemStack getCustomItem(String id) {
        return customItemsMap.get(id);
    }

    public String  getCustomItemID(ItemStack itemStack) {
        return new NBTItem(itemStack).getString("custom_item_id");
    }

    public boolean existItem(String id){
        return customItemsMap.containsKey(id);
    }

    public Map<String, ItemStack> getCustomItems() {
        return customItemsMap;
    }
}
