package io.github.maazapan.recyclerplus.listener;

import de.tr7zw.changeme.nbtapi.NBTBlock;
import de.tr7zw.changeme.nbtapi.NBTItem;
import io.github.maazapan.recyclerplus.Recycler;
import io.github.maazapan.recyclerplus.api.event.RecycleItemEvent;
import io.github.maazapan.recyclerplus.api.event.RecyclerOpenEvent;
import io.github.maazapan.recyclerplus.inventory.RecyclerGUI;
import io.github.maazapan.recyclerplus.manager.RecyclerManager;
import io.github.maazapan.recyclerplus.utils.InventoryUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.List;

public class PlayerListener implements Listener {

    private final Recycler plugin;

    public PlayerListener(Recycler plugin) {
        this.plugin = plugin;
    }

    /**
     * Check player is place recycler and set nbt data.
     *
     * @param event BlockPlaceEvent
     */
    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        if (event.getItemInHand().getType() != Material.AIR) {
            NBTItem nbtItem = new NBTItem(event.getItemInHand());

            if (nbtItem.hasKey("recycler-item")) {
                NBTBlock block = new NBTBlock(event.getBlock());
                block.getData().setString("recycler-block", "block");
            }
        }
    }

    /**
     * Check player is interacted at recycler block and open inventory.
     *
     * @param event PlayerInteractEvent
     */
    @EventHandler
    public void onInteractBlock(PlayerInteractEvent event) {
        FileConfiguration config = plugin.getConfig();
        Player player = event.getPlayer();

        if (event.getClickedBlock() != null) {
            NBTBlock nbtBlock = new NBTBlock(event.getClickedBlock());

            if (nbtBlock.getData().hasKey("recycler-block") && event.getAction() == Action.RIGHT_CLICK_BLOCK) {

                if (config.getBoolean("config.open-recycler-permission.enable")) {
                    if (!player.hasPermission(config.getString("config.open-recycler-permission.permission"))) {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("config.messages.nopermission")));
                        return;
                    }
                }

                RecyclerOpenEvent recyclerOpenEvent = new RecyclerOpenEvent(player, false);
                Bukkit.getPluginManager().callEvent(recyclerOpenEvent);

                if (!recyclerOpenEvent.isCancelled()) {
                    event.setCancelled(true);
                    new RecyclerGUI(plugin).open(player);
                }
            }
        }
    }

    /**
     * Check player is break recycler if the player is break recycler
     * drop new recycler item.
     *
     * @param event BlockBreakEvent
     */
    @EventHandler
    public void onBreakBlock(BlockBreakEvent event) {
        InventoryUtils inventoryUtils = new InventoryUtils(plugin);
        NBTBlock nbtBlock = new NBTBlock(event.getBlock());

        Block block = event.getBlock();

        if (nbtBlock.getData().hasKey("recycler-block")) {
            event.setDropItems(false);
            block.getWorld().dropItem(block.getLocation().add(0.5, 0, 0.5), inventoryUtils.getRecyclerItem());
        }
    }

    /**
     * Check player is close inventory and if slot is not empty
     * add item-stack at player inventory.
     *
     * @param event InventoryCloseEvent
     */
    @EventHandler
    public void onCloseInventory(InventoryCloseEvent event) {
        FileConfiguration config = plugin.getConfig();
        Player player = (Player) event.getPlayer();

        if (event.getView().getTitle().equals(ChatColor.translateAlternateColorCodes('&', config.getString("config.inventory.title")))) {
            for (ItemStack itemStack : event.getInventory().getContents()) {

                if (itemStack != null) {
                    NBTItem nbtItem = new NBTItem(itemStack);

                    if (!nbtItem.hasCustomNbtData() && itemStack.getType() != Material.AIR) {
                        player.getInventory().addItem(itemStack);
                    }
                }
            }
        }
    }


    /**
     * Cancel event if the player don't have permission.
     *
     * @param e Check item craft is a recycler block
     */
    @EventHandler
    public void onItemCraft(CraftItemEvent e) {
        FileConfiguration config = plugin.getConfig();
        Player player = (Player) e.getWhoClicked();

        if (e.getRecipe().getResult().getType() != Material.AIR) {
            NBTItem nbt = new NBTItem(e.getRecipe().getResult());

            if (nbt.hasKey("recycler-item")) {
                // Check if is craft permission is an enabled
                player.discoverRecipe(new NamespacedKey(plugin, "recycler_block"));

                if (!config.getBoolean("config.crafting-recipe.recycler-permission.enable")) {
                    return;
                }

                // Check the player have permission.
                if (!player.hasPermission(config.getString("config.crafting-recipe.recycler-permission.permission"))) {
                    e.setCancelled(true);
                }
            }
        }
    }

    /**
     * Check player is recycler item and check config if the item
     * is on config path change item.
     *
     * @param event RecycleItemEvent
     */
    @EventHandler
    public void onRecycleItem(RecycleItemEvent event) {
        RecyclerManager manager = new RecyclerManager(plugin);
        FileConfiguration config = plugin.getConfig();

        if (!event.isCancelled() && config.getBoolean("config.change-result.enable")) {
            if (manager.changeResult(event.getItemStack()) != null) {
                event.setIngredients(manager.changeResult(event.getItemStack()));
            }
        }
    }

    /**
     * Check player is clicking on the recycler inventory.
     *
     * @param event InventoryClickEvent
     */
    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        FileConfiguration config = plugin.getConfig();
        RecyclerManager manager = new RecyclerManager(plugin);

        if (event.getCurrentItem() != null && event.getClickedInventory() != null) {
            if (event.getView().getTitle().equals(ChatColor.translateAlternateColorCodes('&', config.getString("config.inventory.title")))) {

                NBTItem nbtItem = new NBTItem(event.getCurrentItem());

                if (nbtItem.hasKey("recycler-item")) {
                    event.setCancelled(true);
                    return;
                }

                if (event.getClick().equals(ClickType.SHIFT_RIGHT) || event.getClick().equals(ClickType.SHIFT_LEFT)) {
                    if (event.getClickedInventory() instanceof PlayerInventory) {
                        int recyclerSlot = config.getInt("config.inventory.recycler-slot");

                        event.getInventory().setItem(recyclerSlot, event.getCurrentItem());
                        event.setCurrentItem(new ItemStack(Material.AIR));
                        event.setCancelled(true);
                    }
                }

                if (nbtItem.hasKey("recycler-actions")) {
                    event.setCancelled(true);

                    for (String actions : (List<String>) nbtItem.getObject("recycler-actions", List.class)) {
                        String[] a = actions.split(": ");

                        switch (a[0].toUpperCase()) {
                            case "[ACCEPT]":
                                manager.accept(player, event.getInventory());
                                break;

                            case "[CLOSE]":
                                manager.close(player);
                                event.setCancelled(true);
                                break;

                            case "[CONSOLE]":
                                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), a[1]);
                                break;

                            case "[PLAYER]":
                                player.performCommand(a[1]);
                                break;

                            case "[SOUND]":
                                String[] b = a[1].split(";");
                                player.playSound(player.getLocation(), Sound.valueOf(b[0]), Float.parseFloat(b[1]), Float.parseFloat(b[2]));
                                break;

                            default:
                                plugin.getLogger().warning("Error action " + a[0] + " not found.");
                                break;
                        }
                    }
                }
            }
        }
    }
}

