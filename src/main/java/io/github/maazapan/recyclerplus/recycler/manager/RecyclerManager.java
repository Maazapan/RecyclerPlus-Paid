package io.github.maazapan.recyclerplus.recycler.manager;

import de.tr7zw.changeme.nbtapi.NBTItem;
import io.github.maazapan.recyclerplus.Recycler;
import io.github.maazapan.recyclerplus.recycler.api.RecyclerAPI;
import io.github.maazapan.recyclerplus.recycler.api.event.RecycleItemEvent;
import io.github.maazapan.recyclerplus.hooks.compatibles.VaultHook;
import io.github.maazapan.recyclerplus.utils.InventoryUtils;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class RecyclerManager {

    private final Recycler plugin;
    private final FileConfiguration config;

    public RecyclerManager(Recycler plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
    }

    /**
     * This code is execute when player click at item.
     *
     * @param player    Player
     * @param inventory Inventory
     */

    public void accept(Player player, Inventory inventory) {
        FileConfiguration config = plugin.getConfig();

        int slot = config.getInt("config.inventory.recycler-slot");
        ItemStack itemStack = inventory.getItem(slot);

        if (itemStack != null && itemStack.getType() != Material.AIR) {
            /*
            - This is call custom event RecycleItemEvent
            */
            RecycleItemEvent event = new RecycleItemEvent(player, itemStack, RecyclerAPI.getIngredients(itemStack));
            Bukkit.getPluginManager().callEvent(event);

            if (event.isCancelled()) {
                this.errorInfo(player, inventory);
                return;
            }

            if (!event.isCustomRecipe()) {
                if (RecyclerAPI.getShapedRecipe(itemStack) == null && RecyclerAPI.getIngredients(itemStack) == null) {
                    this.errorInfo(player, inventory);
                    return;
                }
            }

            /*
             - Check material is not blacklisted.
             */
            for (String type : config.getStringList("config.blacklist-items")) {
                if (itemStack.getType().toString().equals(type)) {
                    this.errorInfo(player, inventory);
                    return;
                }
            }
            /*
             - Check item-stack is correct amount at recipe amount
             */
            int amount = event.isCustomRecipe() ? 1 : RecyclerAPI.getShapedRecipe(itemStack).getResult().getAmount();

            if (itemStack.getAmount() < amount) {
                this.errorInfo(player, inventory);
                return;
            }
            /*
             - Check if the inventory is full.
             */
            if (!this.hasAvaliableSlot(inventory)) {
                this.errorInfo(player, inventory);
                return;
            }

            if (VaultHook.getEconomy() != null) {
                double cost = config.getDouble("config.plugin-hooks.vault-economy.cost");

                if (!VaultHook.getEconomy().has(player, cost)) {
                    player.closeInventory();
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getPrefix() +
                            config.getString("config.messages.no-money").replaceAll("%money%", String.valueOf(cost))));
                    return;
                }
                VaultHook.getEconomy().withdrawPlayer(player, cost);
            }
            /*
             - Probability fail recycler percentage.
             */
            if (config.getBoolean("config.fail-recycler.enable")) {
                if ((int) (Math.random() * 100) < config.getInt("config.fail-recycler.percentage")) {
                    this.errorInfo(player, inventory);
                    this.removeItemStack(inventory, itemStack, slot, amount);
                    return;
                }
            }

            // List of all ingredients of itemstack.
            List<ItemStack> ingredients = event.getIngredients().stream()
                    .filter(ingredient -> ingredient != null && ingredient.getType() != Material.AIR)
                    .collect(Collectors.toList());

            /*
             - Check if the items tack is enchantment and add enchanted book at ingredient list.
             */
            if (itemStack.getEnchantments().size() > 0 && config.getBoolean("config.enchanted-craft")) {
                for (Enchantment enchantment : itemStack.getEnchantments().keySet()) {
                    int level = itemStack.getEnchantments().get(enchantment);

                    ItemStack enchantedBook = new ItemStack(Material.ENCHANTED_BOOK);
                    EnchantmentStorageMeta enchantmentMeta = (EnchantmentStorageMeta) enchantedBook.getItemMeta();

                    if (enchantmentMeta == null) return;
                    enchantmentMeta.addStoredEnchant(Enchantment.getByKey(enchantment.getKey()), level, false);

                    enchantedBook.setItemMeta(enchantmentMeta);
                    ingredients.add(enchantedBook);
                }
            }

            /*
             - Check durability at item.
             */
            if (config.getBoolean("config.item-durability")) {
                Damageable damageable = (Damageable) itemStack.getItemMeta();

                if (damageable != null && damageable.getDamage() > 0) {
                    int durability = itemStack.getType().getMaxDurability() - damageable.getDamage();
                    int percentage = (durability * 100) / itemStack.getType().getMaxDurability();

                    ingredients.removeIf(a -> (int) (Math.random() * 100) > percentage);
                }
            }

            ingredients.forEach(inventory::addItem);

            this.removeItemStack(inventory, itemStack, slot, amount);
            this.successInfo(player, inventory);

        } else {
            this.errorInfo(player, inventory);
        }
    }

    /**
     * Close player custom inventory.
     *
     * @param player Player
     */
    public void close(Player player) {
        player.closeInventory();

    }

    /**
     * Remove itemstack on inventory recycler slot.
     *
     * @param inventory Bench Inventory
     * @param itemStack Item-stack to recycler
     * @param slot      Get Recycler slot
     * @param amount    Amount
     */
    private void removeItemStack(Inventory inventory, ItemStack itemStack, int slot, int amount) {
        if (itemStack.getAmount() > amount) {
            inventory.setItem(slot, new ItemStack(itemStack.getType(), itemStack.getAmount() - amount));

        } else {
            inventory.setItem(slot, new ItemStack(Material.AIR));
        }
    }

    /**
     * Error info if the itemstack is not compatible.
     *
     * @param player    Player
     * @param inventory Inventory
     */
    private void errorInfo(Player player, Inventory inventory) {
        ItemStack itemStack = new ItemStack(Material.RED_STAINED_GLASS_PANE);

        NBTItem nbtItem = new NBTItem(itemStack);
        nbtItem.setString("recycler-item", "recycler");
        nbtItem.applyNBT(itemStack);

        String[] slots = config.getString("config.inventory.info-slot").split(",");

        if (!slots[0].equals("none")) {
            for (int i = 0; i < slots.length; i++) {
                inventory.setItem(Integer.parseInt(slots[i]), itemStack);
            }
        }

        try {
            for (String s : config.getStringList("config.sounds.error-info")) {
                String[] a = s.split(";");
                player.playSound(player.getLocation(), Sound.valueOf(a[0]), Float.parseFloat(a[1]), Float.parseFloat(a[2]));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Success info if the itemstack is compatible.
     *
     * @param player    Player
     * @param inventory Inventory
     */
    private void successInfo(Player player, Inventory inventory) {
        ItemStack itemStack = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
        FileConfiguration config = plugin.getConfig();

        NBTItem nbtItem = new NBTItem(itemStack);
        nbtItem.setString("recycler-item", "recycler");
        nbtItem.applyNBT(itemStack);

        String[] slots = config.getString("config.inventory.info-slot").split(",");

        if (!slots[0].equals("none")) {
            for (int i = 0; i < slots.length; i++) {
                inventory.setItem(Integer.parseInt(slots[i]), itemStack);
            }
        }

        if (config.getBoolean("config.success-message")) {
            String prefix = config.getString("config.prefix");
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + config.getString("config.messages.success-recycler")));
        }

        try {
            for (String s : config.getStringList("config.sounds.success-info")) {
                String[] a = s.split(";");
                player.playSound(player.getLocation(), Sound.valueOf(a[0]), Float.parseFloat(a[1]), Float.parseFloat(a[2]));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Change result at item-stack recycler.
     *
     * @param itemStack ItemStack
     */
    public Collection<ItemStack> changeResult(ItemStack itemStack) {
        Collection<ItemStack> itemStacks = new ArrayList<>();
        FileConfiguration config = plugin.getConfig();

        for (String key : config.getConfigurationSection("config.change-result.materials").getKeys(false)) {
            Material material = Material.valueOf(config.getString("config.change-result.materials." + key + ".id"));

            if (itemStack.getType() != material) continue;
            for (String s : config.getStringList("config.change-result.materials." + key + ".result")) {
                String[] split = s.split(";");
                ItemStack result = new ItemStack(Material.valueOf(split[0]));

                if (split.length > 1) {
                    result.setAmount(Integer.parseInt(split[1]));
                }
                itemStacks.add(result);
            }
        }
        return itemStacks.isEmpty() ? null : itemStacks;
    }

    /**
     * Create new recipe of recycler block.
     */
    public void registerRecipe() {
        FileConfiguration config = plugin.getConfig();
        InventoryUtils utils = new InventoryUtils(plugin);

        // Check if this option is enabled.
        if (!config.getBoolean("config.crafting-recipe.enable")) return;
        try {

            // Creating new mailbox recipe.
            NamespacedKey key = new NamespacedKey(plugin, "recycler_block");
            ShapedRecipe recipe = new ShapedRecipe(key, utils.getRecyclerItem());

            List<String> pattern = config.getStringList("config.crafting-recipe.pattern");
            pattern.replaceAll(s -> s.replaceAll(" ", ""));

            recipe.shape(pattern.get(0), pattern.get(1), pattern.get(2));

            for (String path : config.getConfigurationSection("config.crafting-recipe.recipe").getKeys(false)) {
                recipe.setIngredient(path.charAt(0), Material.valueOf(config.getString("config.crafting-recipe.recipe." + path)));
            }
            Bukkit.addRecipe(recipe);

        } catch (Exception e) {
            plugin.getLogger().warning("Han error occurred on register custom recipe.");
            e.printStackTrace();
        }
    }


    /**
     * Check if the inventory is available slot.
     *
     * @param inv Inventory
     * @return boolean
     */
    public boolean hasAvaliableSlot(Inventory inv) {
        for (ItemStack item : inv.getContents()) {
            if (item == null) {
                return true;
            }
        }
        return false;
    }
}
