package io.github.maazapan.recyclerplus.hooks;

import io.github.maazapan.recyclerplus.Recycler;
import io.github.maazapan.recyclerplus.hooks.bstats.Metrics;
import io.github.maazapan.recyclerplus.hooks.compatibles.VaultHook;
import io.github.maazapan.recyclerplus.hooks.update.UpdateChecker;
import io.github.maazapan.recyclerplus.hooks.worldguard.WorldGuardHook;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

public class HookManager {

    private final Recycler plugin;

    public HookManager(Recycler plugin) {
        this.plugin = plugin;
    }

    public void loadHooks() {
        FileConfiguration config = plugin.getConfig();

        // Setup WorldGuard Integration.
        if (Bukkit.getPluginManager().getPlugin("WorldGuard") != null) {
            Plugin worldGuard = Bukkit.getPluginManager().getPlugin("WorldGuard");
            WorldGuardHook.init(worldGuard);
        }

        // Setup Vault Economy hook.
        if (config.getBoolean("config.plugin-hooks.vault-economy.enable")) {
            VaultHook vaultHook = new VaultHook(plugin);

            if (!vaultHook.setupEconomy()) {
                plugin.getLogger().warning("Han occurred error Vault economy not hooked.");
                return;
            }
            plugin.getLogger().info("Success Vault economy hooked.");
        }

        // Check plugin is available update.
        new UpdateChecker(plugin, 102495).getVersion(version -> {

            if (!plugin.getDescription().getVersion().equals(version)) {
                plugin.getLogger().info("There is a new update available https://www.spigotmc.org/resources/102495");
                return;
            }
            plugin.getLogger().info("There is not a new update available.");
        });

        Metrics metrics = new Metrics(plugin, 14791);
    }
}
