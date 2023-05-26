package io.github.maazapan.recyclerplus.recycler.incredients;

import org.bukkit.inventory.ItemStack;

public class ItemIngredients {

    private ItemStack itemStack;
    private String command;

    private boolean customItem = false;
    private boolean customCommand = false;

    public ItemIngredients(ItemStack itemStack) {
        this.itemStack = itemStack;
        this.customItem = true;
    }

    public ItemIngredients(String command) {
        this.command = command;
        this.customCommand = true;
    }

    public boolean isCommand() {
        return customCommand;
    }

    public boolean isCustomItem() {
        return customItem;
    }
}
