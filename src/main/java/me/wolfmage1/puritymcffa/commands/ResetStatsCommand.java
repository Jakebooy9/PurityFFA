/*
* This file is part of PurityMCFFA
*
* PurityMCFFA is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* PurityMCFFA is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with PurityMCFFA. If not, see <http://www.gnu.org/licenses/>
*/
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
