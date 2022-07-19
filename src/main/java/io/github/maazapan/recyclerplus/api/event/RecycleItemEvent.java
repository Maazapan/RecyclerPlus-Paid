package io.github.maazapan.recyclerplus.api.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;

public class RecycleItemEvent extends Event implements Cancellable {

    private final Player player;

    private final ItemStack itemStack;
    private Collection<ItemStack> ingredients;

    private boolean cancel;
    private static final HandlerList handlerList = new HandlerList();

    public RecycleItemEvent(Player player, ItemStack itemStack, Collection<ItemStack> ingredients) {
        this.player = player;
        this.ingredients = ingredients;
        this.itemStack = itemStack;
        this.cancel = false;
    }

    public Player getPlayer() {
        return player;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public Collection<ItemStack> getIngredients() {
        return ingredients;
    }

    public void setIngredients(Collection<ItemStack> ingredients) {
        this.ingredients = ingredients;
    }

    @Override
    public boolean isCancelled() {
        return cancel;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
}

