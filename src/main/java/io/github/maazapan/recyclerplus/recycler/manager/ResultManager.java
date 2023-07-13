package io.github.maazapan.recyclerplus.recycler.manager;

import io.github.maazapan.recyclerplus.Recycler;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ResultManager {

    private final Recycler plugin;

    public ResultManager(Recycler plugin) {
        this.plugin = plugin;
    }

    public boolean haveCustomResult(ItemStack itemStack) {
        CustomItems customItems = plugin.getManager().getCustomItems();
        FileConfiguration config = plugin.getConfig();

        for (String key : config.getConfigurationSection("config.change-result.materials").getKeys(false)) {
            String resultID = customItems.isCustomItem(itemStack)
                    ? "custom_item:" + customItems.getCustomItemID(itemStack)
                    : "material:" + itemStack.getType();

            if (config.getString("config.change-result.materials." + key + ".id").equalsIgnoreCase(resultID)) {
                return true;
            }
        }
        return false;
    }


    public boolean executeResult(Player player, ItemStack itemStack) {
        CustomItems customItems = plugin.getManager().getCustomItems();
        FileConfiguration config = plugin.getConfig();

        for (String key : config.getConfigurationSection("config.change-result.materials").getKeys(false)) {
            String id = config.getString("config.change-result.materials." + key + ".id");

            if (id == null) break;
            if (id.startsWith("custom_item:") || id.startsWith("material:")) {
                String resultID = customItems.isCustomItem(itemStack)
                        ? "custom_item:" + customItems.getCustomItemID(itemStack)
                        : "material:" + itemStack.getType();

                if (config.isSet("config.change-result.materials." + key + ".permission")) {
                    String permission = config.getString("config.change-result.materials." + key + ".permission");
                    if (!player.hasPermission(permission)) continue;
                }

                if (!id.equalsIgnoreCase(resultID)) continue;
                List<String> resultList = config.getStringList("config.change-result.materials." + key + ".result").stream()
                        .filter(s -> s.startsWith("[CONSOLE]") || s.startsWith("[PLAYER]")).collect(Collectors.toList());

                for (String result : resultList) {

                    // if the result is a player command, execute them.
                    if (result.startsWith("[PLAYER]")) {
                        String command = result.replace("[PLAYER]", "")
                                .replace("%player%", player.getName())
                                .trim();

                        player.performCommand(command);
                    }

                    // if the result is a console command, execute them.
                    if (result.startsWith("[CONSOLE]")) {
                        String command = result.replace("[CONSOLE]", "")
                                .replace("%player%", player.getName())
                                .trim();

                        plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), command);
                    }
                }
                return true;
            }
        }
        return false;
    }

    public List<ItemStack> getItemResult(ItemStack itemStack) {
        CustomItems customItems = plugin.getManager().getCustomItems();

        FileConfiguration config = plugin.getConfig();
        List<ItemStack> itemStacks = new ArrayList<>();

        for (String key : config.getConfigurationSection("config.change-result.materials").getKeys(false)) {
            String id = config.getString("config.change-result.materials." + key + ".id");

            if (id == null) break;
            if (id.startsWith("custom_item:") || id.startsWith("material:")) {
                String resultID = customItems.isCustomItem(itemStack)
                        ? "custom_item:" + customItems.getCustomItemID(itemStack)
                        : "material:" + itemStack.getType();

                if (!id.equalsIgnoreCase(resultID)) continue;
                List<String> resultList = config.getStringList("config.change-result.materials." + key + ".result").stream()
                        .filter(s -> s.startsWith("[ITEM]") || s.startsWith("[CUSTOM_ITEM]")).collect(Collectors.toList());

                for (String result : resultList) {

                    // If the result is a material add to list.
                    if (result.startsWith("[ITEM]")) {
                        String[] resultSplit = result.replace("[ITEM]", "")
                                .trim().split(":");

                        Material material = Material.valueOf(resultSplit[0]);
                        ItemStack materialResult = new ItemStack(material);

                        if (resultSplit.length > 1) {
                            materialResult.setAmount(Integer.parseInt(resultSplit[1]));
                        }
                        itemStacks.add(materialResult);
                    }

                    // If the result is a custom item add to list.
                    if (result.startsWith("[CUSTOM_ITEM]")) {
                        String customItemID = result.replace("[CUSTOM_ITEM]", "").trim();
                        itemStacks.add(customItems.getCustomItem(customItemID));
                    }
                }
            }
        }
        return itemStacks;
    }
}
