
package com.puritymc.purityffa.commands;

import com.puritymc.purityffa.PurityFFA;
import com.puritymc.purityffa.player.PlayerManager;
import com.puritymc.purityffa.player.FFAPlayer;
import com.puritymc.purityffa.util.Message;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

/*******************************************************************************
 * Copyright MartinItsLinda (c) 2016. All Rights Reserved.
 * Any code contained within this document, and any associated API's with similar branding
 * are the sole property of MartinItsLinda. Distribution, reproduction, taking snippets or
 * claiming any contents as your own will break the terms of the liscense and void any
 * agreements with you, the third party.
 ******************************************************************************/

public class StatsCommand implements CommandExecutor {

    private PurityFFA plugin;

    public StatsCommand(PurityFFA plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(final CommandSender sender, Command command, String label, final String[] args) {
        if (command.getName().equalsIgnoreCase("stats")) {
            if (args.length < 1) {
                if (!(sender instanceof Player)) {
                    Message.get("no_console_access").sendTo(sender);
                    return true;
                }

                Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                    FFAPlayer player = PlayerManager.getPlayer(((Player) sender).getUniqueId());
                    String prefix = plugin.getConfig().getString("messages.prefix");
                    for (String s : plugin.getConfig().getStringList("messages.stats")) {
                        s = s.replace("%player%", sender.getName())
                                .replace("%ranking%", Integer.toString(player.getRanking()))
                                .replace("%points%", Integer.toString(player.getPoints()))
                                .replace("%kills%", Integer.toString(player.getKills()))
                                .replace("%deaths%", Integer.toString(player.getDeaths()))
                                .replace("%kd%", Double.toString(player.getKillDeath()))
                                .replace("%highestKillStreak%", Integer.toString(player.getHighestKillStreak()));

                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + " " + s));
                    }
                });
            } else {

                Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                    FFAPlayer player = PlayerManager.getPlayer(args[0]);

                    if (player == null) {
                        Message.get("player_not_found").replace("%player%", args[0]).sendTo(sender);
                        return;
                    }

                    String prefix = Message.getString("prefix");
                    for (String s : plugin.getConfig().getStringList("messages.stats")) {
                        s = s.replace("%player%", args[0])
                                .replace("%ranking%", Integer.toString(player.getRanking()))
                                .replace("%points%", Integer.toString(player.getPoints()))
                                .replace("%kills%", Integer.toString(player.getKills()))
                                .replace("%deaths%", Integer.toString(player.getDeaths()))
                                .replace("%kd%", Double.toString(player.getKillDeath()))
                                .replace("%highestKillStreak%", Integer.toString(player.getHighestKillStreak()));

                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + " " + s));
                    }
                });
            }
        }
        return true;
    }

}
