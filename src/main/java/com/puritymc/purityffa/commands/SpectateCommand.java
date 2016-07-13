package com.puritymc.purityffa.commands;

import com.puritymc.purityffa.PurityFFA;
import com.puritymc.purityffa.player.FFAPlayer;
import com.puritymc.purityffa.player.PlayerManager;
import com.puritymc.purityffa.util.Message;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;

public class SpectateCommand implements CommandExecutor {

    private PurityFFA plugin;

    public SpectateCommand(PurityFFA plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (command.getName().equalsIgnoreCase("spectate")) {

            if (!(sender instanceof Player)) {
                Message.get("no_console_access").sendTo(sender);
                return true;
            }

            if (!(sender.hasPermission("ffa.spectate"))) {
                Message.get("insufficient_permissions").sendTo(sender);
                return true;
            }

            FFAPlayer player = PlayerManager.getPlayer(((Player)sender));
            Player pl = player.getBukkitPlayer();

            if (player.isSpectating()) {

                Bukkit.getOnlinePlayers().stream().forEach(online -> online.showPlayer(pl));

                Block block = pl.getLocation().getWorld().getHighestBlockAt(pl.getLocation());
                pl.teleport(new Location(pl.getLocation().getWorld(),
                        block.getX(), block.getY(), block.getZ()));

                pl.setAllowFlight(false);
                pl.setFlying(false);

                player.setSpectating(false);

                plugin.giveKit(plugin.getKit("default"), player);

                Message.get("spectator_mode_disabled").sendTo(pl);
            } else {

                if (player.getTagger() != null) {
                    Message.get("no_spectator_currently_tagged").sendTo(pl);
                    return true;
                }

                Bukkit.getOnlinePlayers().stream().forEach(online -> online.hidePlayer(pl));

                PlayerInventory inventory = pl.getInventory();

                inventory.setArmorContents(null);
                inventory.clear();
                pl.updateInventory();

                pl.getActivePotionEffects().clear();

                pl.setAllowFlight(true);
                pl.setFlying(true);

                player.setSpectating(true);

                if (args.length > 0) {
                    Player target = Bukkit.getPlayer(args[0]);
                    if (target != null) pl.teleport(target);
                }
                Message.get("spectator_mode_enabled").sendTo(pl);
            }

        }

        return true;
    }
}
