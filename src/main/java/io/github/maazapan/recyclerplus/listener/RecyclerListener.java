package io.github.maazapan.recyclerplus.listener;

import io.github.maazapan.recyclerplus.Recycler;
import io.github.maazapan.recyclerplus.recycler.api.event.RecycleItemEvent;
import io.github.maazapan.recyclerplus.recycler.manager.RecyclerManager;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class RecyclerListener implements Listener {

    private final Recycler plugin;

    public RecyclerListener(Recycler plugin) {
        this.plugin = plugin;
    }

    /**
     * Check player is recycler item and check config if the item
     * is on config path change item.
     *
     * @param event RecycleItemEvent
     */
    @EventHandler
    public void onRecyclerItem(RecycleItemEvent event) {
        RecyclerManager manager = new RecyclerManager(plugin);
        FileConfiguration config = plugin.getConfig();

        if (!event.isCancelled() && config.getBoolean("config.change-result.enable")) {
            ItemStack recycleItem = event.getItemStack();

            if (manager.changeResult(event.getItemStack()) != null) {


                event.setCustomRecipe(true);
                event.setIngredients(manager.changeResult(event.getItemStack()));
            }
        }
    }
}
