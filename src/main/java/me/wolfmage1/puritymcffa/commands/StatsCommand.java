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
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class StatsCommand implements CommandExecutor {

    private FFA plugin;

    public StatsCommand(FFA plugin) {
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
                    FFAPlayer player = PlayerManager.getPlayer(((Player) sender));
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

                UUID uuid = Bukkit.getOfflinePlayer(args[0]).getUniqueId();

                if (uuid == null) {
                    Message.get("player_not_found")
                            .sendTo(sender);
                    return true;
                }

                Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                    FFAPlayer player = PlayerManager.getPlayer(uuid);
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
