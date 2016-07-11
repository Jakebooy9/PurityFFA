
package me.wolfmage1.puritymcffa.commands;

import me.wolfmage1.puritymcffa.FFA;
import me.wolfmage1.puritymcffa.player.FFAPlayer;
import me.wolfmage1.puritymcffa.player.PlayerManager;
import me.wolfmage1.puritymcffa.util.Message;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.UUID;

public class ResetStatsCommand implements CommandExecutor {

    private FFA plugin;

    public ResetStatsCommand(FFA plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(final CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("resetstats")) {
            if (!(sender.hasPermission("ffa.resetstats"))) {
                Message.get("insufficient_permissions").sendTo(sender);
                return true;
            }
            if (args.length < 1) {
                Message.get("correct_usage").replace("%command%", command.getName()).replace("%usage%", "<player>").sendTo(sender);
                return false;
            }
            UUID uuid = Bukkit.getOfflinePlayer(args[0]).getUniqueId();

            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                FFAPlayer player = PlayerManager.getPlayer(uuid);

                player.setPoints(plugin.getConfig().getInt("ffa.starting_points"));
                player.setInventoryContents(null);
                player.setKills(0);
                player.setDeaths(0);
                player.setKillStreak(0);
                player.setHighestKillStreak(0);
                player.setLastKilled(null);
                player.setTagger(null);

                if (!(player.update())) {
                    Message.get("failed_to_update_stats")
                            .sendTo(sender);
                    return;
                }

                Message.get("stats_reset")
                        .replace("%player%", args[0])
                        .sendTo(sender);
            });

            return true;
        }
        return false;
    }
}
