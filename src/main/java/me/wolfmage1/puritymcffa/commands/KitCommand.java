
package me.wolfmage1.puritymcffa.commands;

import me.wolfmage1.puritymcffa.FFA;
import me.wolfmage1.puritymcffa.kit.Kit;
import me.wolfmage1.puritymcffa.player.PlayerManager;
import me.wolfmage1.puritymcffa.util.Message;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public class KitCommand implements CommandExecutor, TabExecutor {

    private FFA plugin;

    public KitCommand(FFA plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("kit")) {
            if (!(sender instanceof Player)) {
                Message.get("no_console_access")
                        .sendTo(sender);
                return true;
            }

            if (args.length < 1) {
                Message.get("correct_usage")
                        .replace("%command%", command.getName())
                        .replace("%usage%", "<kit>")
                        .sendTo(sender);
                return false;
            }

            if (plugin.getKits().size() == 0) {
                Message.get("no_available_kits")
                        .sendTo(sender);
                return false;
            }

            Kit kit = plugin.getKit(args[0]);

            if (kit == null) {
                Message.get("unknown_kit")
                        .replace("%kit%", args[0])
                        .sendTo(sender);
                return true;
            }

            plugin.giveKit(kit, PlayerManager.getPlayer(((Player) sender)));

            Message.get("kit_received")
                    .replace("%kit%", kit.getName())
                    .sendTo(sender);
            return true;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return plugin.getKits().stream().map(Kit::getName).collect(Collectors.toList());
    }
}
