package io.github.maazapan.recyclerplus.recycler.api;

import de.tr7zw.changeme.nbtapi.NBTItem;
import io.github.maazapan.recyclerplus.Recycler;
import io.github.maazapan.recyclerplus.recycler.gui.RecyclerGUI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

import java.util.ArrayList;
import java.util.Collection;

public class RecyclerAPI {

    public static Collection<ItemStack> getIngredients(ItemStack recycleItemStack) {
        ItemStack replaced = new ItemStack(recycleItemStack.getType());

        for (Recipe recipe : Bukkit.getRecipesFor(replaced)) {
            Collection<ItemStack> ingredients = new ArrayList<>();

            if (recipe instanceof ShapelessRecipe) {
                ingredients = ((ShapelessRecipe) recipe).getIngredientList();
            }

            if (recipe instanceof ShapedRecipe) {
                ingredients = ((ShapedRecipe) recipe).getIngredientMap().values();

            }

            if (ingredients.isEmpty()) return null;
            for (ItemStack ingredient : ingredients) {
                if (ingredient != null && ingredient.getType() != Material.AIR) {
                    NBTItem nbtItem = new NBTItem(ingredient);
                    nbtItem.getKeys().clear();
                    nbtItem.applyNBT(ingredient);
                }
            }
            return ingredients;
        }
        return null;
    }

    public static Recipe getRecipe(ItemStack recycleItemStack) {
        ItemStack itemStack = new ItemStack(recycleItemStack.getType());

        for (Recipe recipe : Bukkit.getRecipesFor(itemStack)) {
            if (recipe instanceof ShapedRecipe) {
                return (ShapedRecipe) recipe;
            }

            if (recipe instanceof ShapelessRecipe) {
                return (ShapelessRecipe) recipe;
            }
        }
        return null;
    }

    public static void openRecycler(Player player) {
        new RecyclerGUI(Recycler.getInstance()).open(player);
    }
}
