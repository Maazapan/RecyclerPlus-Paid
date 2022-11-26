package io.github.maazapan.recyclerplus.api;

import de.tr7zw.changeme.nbtapi.NBTItem;
import io.github.maazapan.recyclerplus.Recycler;
import io.github.maazapan.recyclerplus.inventory.RecyclerGUI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import java.util.Collection;

public class RecyclerAPI {

    public static Collection<ItemStack> getIngredients(ItemStack recycleItemStack) {
        ItemStack replaced = new ItemStack(recycleItemStack.getType());

        for (Recipe recipes : Bukkit.getRecipesFor(replaced)) {
            if (recipes instanceof ShapedRecipe) {
                Collection<ItemStack> itemStacks = ((ShapedRecipe) recipes).getIngredientMap().values();

                itemStacks.stream().filter(itemStack -> itemStack != null && itemStack.getType() != Material.AIR).forEach(itemStack -> {
                    NBTItem nbtItem = new NBTItem(itemStack);
                    nbtItem.getKeys().clear();
                    nbtItem.applyNBT(itemStack);
                });
                return itemStacks;
            }
        }
        return null;
    }

    public static ShapedRecipe getShapedRecipe(ItemStack recycleItemStack) {
        ItemStack itemStack = new ItemStack(recycleItemStack.getType());

        for (Recipe recipes : Bukkit.getRecipesFor(itemStack)) {
            if (recipes instanceof ShapedRecipe) {
                return (ShapedRecipe) recipes;
            }
        }
        return null;
    }

    public static void openRecycler(Player player) {
        new RecyclerGUI(Recycler.getInstance()).open(player);
    }
}
