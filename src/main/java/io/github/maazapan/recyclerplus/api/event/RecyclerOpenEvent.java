package io.github.maazapan.recyclerplus.api.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class RecyclerOpenEvent extends Event implements Cancellable {

    private final Player player;

    private final boolean command;
    private boolean cancel;

    private static HandlerList handlerList = new HandlerList();

    public RecyclerOpenEvent(Player player, boolean command) {
        this.player = player;
        this.command = command;
        this.cancel = false;
    }

    public Player getPlayer() {
        return player;
    }

    public boolean isCommand() {
        return command;
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
