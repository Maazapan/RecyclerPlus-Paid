package io.github.maazapan.recyclerplus;

import io.github.maazapan.recyclerplus.manager.LoaderManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class Recycler extends JavaPlugin {

    private LoaderManager manager;
    private static Recycler instance;

    @Override
    public void onEnable() {
        // Plugin startup logic
        manager = new LoaderManager(this);
        instance = this;
        manager.load();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        manager.disable();
    }

    // Only use for api.
    public static Recycler getInstance() {
        return instance;
    }

    public String getPrefix(){
        return getConfig().getString("config.prefix");
    }
}
