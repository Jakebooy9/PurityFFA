
package com.puritymc.purityffa.commands;

import com.puritymc.purityffa.PurityFFA;
import com.puritymc.purityffa.player.PlayerManager;
import com.puritymc.purityffa.player.FFAPlayer;
import com.puritymc.purityffa.util.Message;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.UUID;

/*******************************************************************************
 * Copyright MartinItsLinda (c) 2016. All Rights Reserved.
 * Any code contained within this document, and any associated API's with similar branding
 * are the sole property of MartinItsLinda. Distribution, reproduction, taking snippets or
 * claiming any contents as your own will break the terms of the liscense and void any
 * agreements with you, the third party.
 ******************************************************************************/

public class ResetStatsCommand implements CommandExecutor {

    private PurityFFA plugin;

    public ResetStatsCommand(PurityFFA plugin) {
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
