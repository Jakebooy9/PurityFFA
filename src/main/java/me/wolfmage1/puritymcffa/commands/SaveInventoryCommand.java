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

import me.wolfmage1.puritymcffa.kit.Kit;
import me.wolfmage1.puritymcffa.player.FFAPlayer;
import me.wolfmage1.puritymcffa.player.PlayerManager;
import me.wolfmage1.puritymcffa.util.Message;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Map;

import static me.wolfmage1.puritymcffa.util.InventoryToBase64.toBase64;

public class SaveInventoryCommand implements CommandExecutor {

    private final FFA plugin;

    public SaveInventoryCommand(FFA plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (command.getName().equalsIgnoreCase("saveinventory")) {
            if (!(sender instanceof Player)) {
                Message.get("no_console_access").sendTo(sender);
                return true;
            }
            if (!(sender.hasPermission("ffa.saveinventory"))) {
                Message.get("insufficient_permissions").sendTo(sender);
                return true;
            }

            Player pl = (Player) sender;
            PlayerInventory inventory = pl.getInventory();

            FFAPlayer player = PlayerManager.getPlayer(pl);
            Kit kit = player.getKit();

            if (kit == null) {
                Message.get("no_kit_equipped").sendTo(pl);
                return true;
            }

            for (int i = 0; i < inventory.getSize(); i++) {
                ItemStack item = inventory.getItem(i);
                if (item == null) {
                    continue;
                }

                if (item.getAmount() < kit.getSpecialItems().get(item.getType().toString())) {
                    Message.get("kit_not_complete").sendTo(sender);
                    return true;
                }
            }

            for (Map.Entry<String, Integer> e : kit.getSpecialItems().entrySet()) {
                if (!inventory.contains(Material.getMaterial(e.getKey()), e.getValue())) {
                    Message.get("kit_not_complete").sendTo(sender);
                    return true;
                }
            }

            player.setInventoryContents(inventory.getContents());
            Message.get("inventory_saved").sendTo(sender);

            Bukkit.getScheduler().runTaskAsynchronously(plugin, player::update);

        }
        return false;
    }
}
