package io.github.maazapan.recyclerplus.hooks.compatibles;

import io.github.maazapan.recyclerplus.Recycler;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultHook {

    private final Recycler plugin;
    private static Economy econ = null;

    public VaultHook(Recycler plugin) {
        this.plugin = plugin;
    }

    /**
     * Setup vault economy.
     */
    public boolean setupEconomy() {
        RegisteredServiceProvider<Economy> rsp = plugin.getServer().getServicesManager().getRegistration(Economy.class);
        // create matrix

        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    public static Economy getEconomy() {
        return econ;
    }
}
