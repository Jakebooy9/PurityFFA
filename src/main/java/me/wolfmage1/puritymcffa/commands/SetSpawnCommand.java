
package me.wolfmage1.puritymcffa.commands;

import me.wolfmage1.puritymcffa.FFA;
import me.wolfmage1.puritymcffa.util.Message;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetSpawnCommand implements CommandExecutor {

    private FFA plugin;

    public SetSpawnCommand(FFA plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("setspawn")) {
            if (!(sender.hasPermission("ffa.setspawn"))) {
                Message.get("insufficient_permissions").sendTo(sender);
                return true;
            }
            if (!(sender instanceof Player)) {
                Message.get("no_console_access").sendTo(sender);
                return true;
            }
            Player player = (Player) sender;
            int i = plugin.getSpawns().size();
            plugin.getConfig().set("spawns." + i + ".world", player.getWorld().getName());
            plugin.getConfig().set("spawns." + i + ".X", player.getLocation().getBlockX());
            plugin.getConfig().set("spawns." + i + ".Y", player.getLocation().getBlockY());
            plugin.getConfig().set("spawns." + i + ".Z", player.getLocation().getBlockZ());
            plugin.getConfig().set("spawns." + i + ".yaw", player.getLocation().getYaw());
            plugin.getConfig().set("spawns." + i + ".pitch", player.getLocation().getPitch());

            plugin.saveConfig();
            plugin.reloadConfig();

            Message.get("spawn_location_set").sendTo(sender);
        }
        return true;
    }
}
