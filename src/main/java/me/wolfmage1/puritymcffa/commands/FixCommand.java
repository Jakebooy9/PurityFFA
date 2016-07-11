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
