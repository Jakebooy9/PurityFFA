
package me.wolfmage1.puritymcffa.commands;

import me.wolfmage1.puritymcffa.util.Message;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FixCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("fix")) {
            if (!(sender instanceof Player)) {
                Message.get("no_console_access").sendTo(sender);
                return true;
            }
            if (args.length < 1) {
                Player target = (Player) sender; //To lazy to change variable names

                Location location = target.getLocation();

                location.setX(location.getBlockX());
                location.setY(location.getBlockY());
                location.setZ(location.getBlockZ());

                target.teleport(location);

                Message.get("player_fixed").replace("%player%", target.getName()).sendTo(sender);
                return true;
            }
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                Message.get("player_not_found").replace("%player%", args[0]);
                return true;
            }

            Location location = target.getLocation();

            location.setX(location.getBlockX());
            location.setY(location.getBlockY());
            location.setZ(location.getBlockZ());

            target.teleport(location);

            Message.get("player_fixed").replace("%player%", target.getName()).sendTo(sender);

        }
        return false;
    }
}
