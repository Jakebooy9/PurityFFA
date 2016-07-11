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
