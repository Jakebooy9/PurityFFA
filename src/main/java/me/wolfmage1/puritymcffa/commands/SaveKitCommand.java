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
import me.wolfmage1.puritymcffa.util.Message;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SaveKitCommand implements CommandExecutor {

    private FFA plugin;

    public SaveKitCommand(FFA plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("savekit")) {
            if (!(sender instanceof Player)) {
                Message.get("no_console_access").sendTo(sender);
                return true;
            }
            if (!(sender.hasPermission("ffa.savekit"))) {
                Message.get("no_console_access").sendTo(sender);
                return true;
            }
            if (args.length < 1) {
                Message.get("correct_usage").replace("%command%", command.getName()).replace("%usage%", "<kitName>").sendTo(sender);
                return false;
            }
            Player player = (Player) sender;

            Kit kit = new Kit(args[0]);

            kit.setHelmet(player.getInventory().getHelmet().getType());
            kit.setChestplate(player.getInventory().getChestplate().getType());
            kit.setLeggings(player.getInventory().getLeggings().getType());
            kit.setBoots(player.getInventory().getBoots().getType());

            Map<String, Integer> specialItems = new HashMap<>();

            for (int i = 0; i < player.getInventory().getSize(); i++) {
                ItemStack item = player.getInventory().getItem(i);
                if (item == null) {
                    continue;
                }
                specialItems.put(item.getType().toString(), item.getAmount());
            }

            kit.setSpecialItems(specialItems);

            FileConfiguration config = plugin.getConfig();

            if (kit.getHelmet() != null) {
                config.set("kits." + kit.getName() + ".helmet", kit.getHelmet().toString());
            }
            if (kit.getChestplate() != null) {
                config.set("kits." + kit.getName() + ".chestplate", kit.getChestplate().toString());
            }
            if (kit.getLeggings() != null) {
                config.set("kits." + kit.getName() + ".leggings", kit.getLeggings().toString());
            }
            if (kit.getBoots() != null) {
                config.set("kits." + kit.getName() + ".boots", kit.getBoots().toString());
            }

            for (Map.Entry<String, Integer> e : specialItems.entrySet()) {
                plugin.getConfig().set("kits." + kit.getName() + ".specialItems." + e.getKey(), e.getValue());
            }

            plugin.getKits().add(kit);
            plugin.saveConfig();

            Message.get("kit_added").replace("%kit%", kit.getName()).sendTo(player);
        }
        return true;
    }
}
