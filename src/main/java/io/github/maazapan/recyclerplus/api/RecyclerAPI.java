package io.github.maazapan.recyclerplus.api;

import io.github.maazapan.recyclerplus.Recycler;
import io.github.maazapan.recyclerplus.inventory.RecyclerGUI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;

import java.util.Collection;

public class RecyclerAPI {

    public static Collection<ItemStack> getIngredients(ItemStack recycleItemStack) {
        ItemStack itemStack = new ItemStack(recycleItemStack.getType());

        for (Recipe recipes : Bukkit.getRecipesFor(itemStack)) {
            if (recipes instanceof ShapedRecipe) {
                return ((ShapedRecipe) recipes).getIngredientMap().values();
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
