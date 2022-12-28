package io.github.maazapan.recyclerplus.hooks.worldguard;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class WorldGuardHook {

    private static WorldGuardPlugin worldGuard;

    public static void init(Plugin plugin) {
        worldGuard = (WorldGuardPlugin) plugin;
    }

    public static boolean hasWorldGuard() {
        return worldGuard != null;
    }

    public static boolean canPlace(Player player, Location location) {
        LocalPlayer localPlayer = worldGuard.wrapPlayer(player);

        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionQuery query = container.createQuery();

        if (WorldGuard.getInstance().getPlatform().getSessionManager().hasBypass(localPlayer, BukkitAdapter.adapt(location.getWorld()))) {
            return true;
        }

        return query.testBuild(BukkitAdapter.adapt(location), localPlayer, Flags.BLOCK_PLACE);
    }

    public static boolean canBreak(Player player, Location location) {
        LocalPlayer localPlayer = worldGuard.wrapPlayer(player);

        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionQuery query = container.createQuery();

        if (WorldGuard.getInstance().getPlatform().getSessionManager().hasBypass(localPlayer, BukkitAdapter.adapt(location.getWorld()))) {
            return true;
        }
        return query.testBuild(BukkitAdapter.adapt(location), localPlayer, Flags.BLOCK_BREAK);
    }

    public static boolean canInteract(Player player, Location location) {
        LocalPlayer localPlayer = worldGuard.wrapPlayer(player);

        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionQuery query = container.createQuery();

        if (WorldGuard.getInstance().getPlatform().getSessionManager().hasBypass(localPlayer, BukkitAdapter.adapt(location.getWorld()))) {
            return true;
        }
        return query.testBuild(BukkitAdapter.adapt(location), localPlayer, Flags.INTERACT);
    }
}