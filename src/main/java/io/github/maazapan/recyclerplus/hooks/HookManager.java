package io.github.maazapan.recyclerplus.hooks;

import io.github.maazapan.recyclerplus.Recycler;
import io.github.maazapan.recyclerplus.hooks.bstats.Metrics;
import io.github.maazapan.recyclerplus.hooks.compatibles.VaultHook;
import io.github.maazapan.recyclerplus.hooks.update.UpdateChecker;
import org.bukkit.configuration.file.FileConfiguration;

public class HookManager {

    private final Recycler plugin;

    public HookManager(Recycler plugin) {
        this.plugin = plugin;
    }

    public void loadHooks() {
        FileConfiguration config = plugin.getConfig();

        // Setup Vault Economy hook.
        if (config.getBoolean("config.plugin-hooks.vault-economy.enable")) {
            VaultHook vaultHook = new VaultHook(plugin);

            if (vaultHook.setupEconomy()) {
                plugin.getLogger().info("Success Vault economy hooked.");
            } else {
                plugin.getLogger().warning("Han occurred error Vault economy not hooked.");
            }
        }

        // Check plugin is available update.
        new UpdateChecker(plugin, 102495).getVersion(version -> {
            if (plugin.getDescription().getVersion().equals(version)) {
                plugin.getLogger().info("There is not a new update available.");

            } else {
                plugin.getLogger().info("There is a new update available https://www.spigotmc.org/resources/102495");
            }
        });

        Metrics metrics = new Metrics(plugin, 14791);
    }
}
