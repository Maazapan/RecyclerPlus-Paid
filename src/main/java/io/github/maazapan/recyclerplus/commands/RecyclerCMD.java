package io.github.maazapan.recyclerplus.commands;

import io.github.maazapan.recyclerplus.Recycler;
import io.github.maazapan.recyclerplus.inventory.RecyclerGUI;
import io.github.maazapan.recyclerplus.utils.InventoryUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class RecyclerCMD implements CommandExecutor, TabCompleter {

    private final Recycler plugin;

    public RecyclerCMD(Recycler plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        FileConfiguration config = plugin.getConfig();
        InventoryUtils utils = new InventoryUtils(plugin);
        String prefix = config.getString("config.prefix");

        if (args.length > 0) {
            switch (args[0].toLowerCase()) {

                case "reload":
                    if (sender.hasPermission("recycler.cmd.reload")) {
                        plugin.reloadConfig();
                        plugin.saveDefaultConfig();
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + "&aSuccess reloaded config."));

                    } else {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + config.getString("config.messages.nopermission")));
                    }
                    break;

                case "help":
                    if (sender.hasPermission("recycler.cmd.help")) {
                        List<String> help = new ArrayList<>();

                        help.add(" ");
                        help.add(" &e&lRecyclerPlus &8| &fby &bMaazapan &8(&7 " + plugin.getDescription().getVersion() + "&8)");
                        help.add(" ");
                        help.add(" &8- &a/recycler menu &8: &fOpen recycler menu.");
                        help.add(" &8- &a/recycler reload &8: &fReload all plugin config.");
                        help.add(" &8- &a/recycler give <player> &8: &fGive recycler item at player.");
                        help.add(" ");

                        help.forEach(s -> sender.sendMessage(ChatColor.translateAlternateColorCodes('&', s)));

                    } else {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + config.getString("config.messages.nopermission")));
                    }
                    break;

                case "menu":
                    if (sender.hasPermission("recycler.cmd.menu")) {
                        if (args.length < 2) {
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + "&fPlease complete command &a/recycler menu <player>"));
                            return true;
                        }

                        if (Bukkit.getPlayer(args[1]) == null) {
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + "&fThe player &c" + args[1] + " &fis not online."));
                            return true;
                        }

                        Player player = Bukkit.getPlayer(args[1]);
                        new RecyclerGUI(plugin).open(player);

                    } else {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + config.getString("config.messages.nopermission")));
                    }
                    break;

                case "give":
                    if (sender.hasPermission("recycler.cmd.give")) {
                        if (args.length < 2) {
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + "&fPlease complete command &a/recycler give <player>"));
                            return true;
                        }

                        if (Bukkit.getPlayer(args[1]) == null) {
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + "&fThe player &c" + args[1] + " &fis not online."));
                            return true;
                        }

                        Player target = Bukkit.getPlayer(args[1]);
                        target.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + config.getString("config.messages.receive-recycler")));
                        target.getInventory().addItem(utils.getRecyclerItem());
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + config.getString("config.messages.send-recycler").replaceAll("%name%", target.getName())));

                    } else {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + config.getString("config.messages.nopermission")));
                    }
                    break;

                default:
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cPlease use /recycler help for more information"));
                    break;
            }
        } else {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cPlease use /recycler help for more information"));
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            List<String> complete = Arrays.asList("help", "reload", "menu", "give");

            List<String> players = Bukkit.getOnlinePlayers().stream()
                    .map(player -> (String) player.getName())
                    .collect(Collectors.toList());

            if (args.length > 0) {
                if (args[0].equalsIgnoreCase("give") ||
                        args[0].equalsIgnoreCase("menu")) {
                    return players;
                }
                return complete;
            }
        }
        return null;
    }
}
