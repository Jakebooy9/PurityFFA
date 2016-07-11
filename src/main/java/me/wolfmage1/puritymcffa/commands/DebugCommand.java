
package me.wolfmage1.puritymcffa.commands;

import me.wolfmage1.puritymcffa.FFA;
import me.wolfmage1.puritymcffa.player.FFAPlayer;
import me.wolfmage1.puritymcffa.player.PlayerManager;
import me.wolfmage1.puritymcffa.util.Message;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DebugCommand implements CommandExecutor {

    private FFA plugin;

    public DebugCommand(FFA plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] strings) {
        if (command.getName().equalsIgnoreCase("debug")) {
            if (!(sender.hasPermission("ffa.debug"))) {
                Message.get("insufficient_permissions").sendTo(sender);
                return true;
            }
            if (!(sender instanceof Player)) {
                Message.get("no_console_access").sendTo(sender);
                return true;
            }
            FFAPlayer player = PlayerManager.getPlayer(((Player) sender));
            player.setDebugging(!player.isDebugging());
            Message.get(player.isDebugging() ? "now_debugging" : "no_longer_debugging").sendTo(sender);
        }
        return false;
    }
}
