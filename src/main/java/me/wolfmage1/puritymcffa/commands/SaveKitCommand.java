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

/*******************************************************************************
 * Copyright MartinItsLinda (c) 2016. All Rights Reserved.
 * Any code contained within this document, and any associated API's with similar branding
 * are the sole property of MartinItsLinda. Distribution, reproduction, taking snippets or
 * claiming any contents as your own will break the terms of the liscense and void any
 * agreements with you, the third party.
 ******************************************************************************/

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
