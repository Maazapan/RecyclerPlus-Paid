package io.github.maazapan.recyclerplus.manager;

import io.github.maazapan.recyclerplus.Recycler;
import io.github.maazapan.recyclerplus.commands.RecyclerCMD;
import io.github.maazapan.recyclerplus.hooks.HookManager;
import io.github.maazapan.recyclerplus.listener.PlayerListener;
import io.github.maazapan.recyclerplus.listener.RecyclerListener;
import io.github.maazapan.recyclerplus.recycler.manager.CustomItems;
import io.github.maazapan.recyclerplus.recycler.manager.RecyclerManager;

public class LoaderManager {

    private final Recycler plugin;
    private final CustomItems customItems;

    public LoaderManager(Recycler plugin) {
        this.customItems = new CustomItems(plugin);
        this.plugin = plugin;
    }

    public void load() {
        this.registerConfig();
        this.registerCommands();
        this.registerListener();
        this.registerHooks();
        this.registerRecipe();

        this.customItems.load();
    }

    private void registerRecipe() {
        RecyclerManager manager = new RecyclerManager(plugin);
        manager.registerRecipe();
    }

    public void disable() {

    }

    private void registerCommands() {
        plugin.getCommand("recycler").setExecutor(new RecyclerCMD(plugin));
    }

    private void registerListener() {
        plugin.getServer().getPluginManager().registerEvents(new PlayerListener(plugin), plugin);
        plugin.getServer().getPluginManager().registerEvents(new RecyclerListener(plugin), plugin);
    }

    private void registerConfig() {
        plugin.saveDefaultConfig();
    }

    private void registerHooks() {
        new HookManager(plugin).loadHooks();
    }

    public CustomItems getCustomItems() {
        return customItems;
    }
}
