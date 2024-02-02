package io.github.maazapan.recyclerplus;

import com.google.gson.Gson;
import io.github.maazapan.recyclerplus.manager.LoaderManager;
import io.github.maazapan.recyclerplus.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public final class Recycler extends JavaPlugin {

    private LoaderManager manager;
    private static Recycler instance;
    @Override
    public void onEnable() {
        // Plugin startup logic}
        manager = new LoaderManager(this);
        instance = this;
        manager.load();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        manager.disable();
    }

    public LoaderManager getManager() {
        return manager;
    }

    // Only use for api.
    public static Recycler getInstance() {
        return instance;
    }

    public String getPrefix() {
        return getConfig().getString("config.prefix");
    }

    public LoaderManager getLoader() {
        return manager;
    }
}
