package com.puritymc.purityffa;

import com.puritymc.purityffa.commands.*;
import com.puritymc.purityffa.kit.Kit;
import com.puritymc.purityffa.listener.DeathEvent;
import com.puritymc.purityffa.listener.GeneralListeners;
import com.puritymc.purityffa.player.FFAPlayer;
import com.puritymc.purityffa.player.PlayerManager;
import com.puritymc.purityffa.sql.ConnectionPool;
import com.puritymc.purityffa.util.Config;
import com.puritymc.purityffa.listener.ChatEvent;

import net.milkbowl.vault.chat.Chat;

import com.puritymc.purityffa.util.Message;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;

import static com.puritymc.purityffa.util.InventoryToBase64.fromBase64;
import static com.puritymc.purityffa.util.InventoryToBase64.toBase64;
import static com.puritymc.purityffa.util.Util.toItemStack;

/*******************************************************************************
 * Copyright MartinItsLinda (c) 2016. All Rights Reserved.
 * Any code contained within this document, and any associated API's with similar branding
 * are the sole property of MartinItsLinda. Distribution, reproduction, taking snippets or
 * claiming any contents as your own will break the terms of the liscense and void any
 * agreements with you, the third party.
 ******************************************************************************/

public class PurityFFA extends JavaPlugin {

    private static PurityFFA instance;
    private boolean hasConnected;
    private ConnectionPool pool;
    private Scoreboard scoreboard;
    private Chat chat;
    private Set<Kit> kits = new HashSet<>();

    @Override
    public void onLoad() {
        instance = this;

        Config.load(getInstance());

        String url = "jdbc:mysql://" + getConfig().getString("ffa.mysql.host") + ":" + getConfig().getInt("ffa.mysql.port") + "/" + getConfig().getString("ffa.mysql.database");

        pool = new ConnectionPool(url, getConfig().getString("ffa.mysql.user"), getConfig().getString("ffa.mysql.password"));

        Connection connection = null;

        try {
            connection = getConnection();

            hasConnected = true;

            if (!connection.getMetaData().getTables(null, null, "ffa_stats", null).next()) {
                String values = "`player_id` int(11) not null auto_increment primary key, " +
                        "`unique_id` varchar(36) not null," +
                        "`username` varchar(16) not null,  " +
                        "`points` int(11) default '100', " +
                        "`kills` int(11) default '0', " +
                        "`deaths` int(11) default '0', " +
                        "`inventoryContents` text, " +
                        "`highestKillStreak` int(11) default '0'";

                connection.prepareStatement("create table `ffa_stats` (" + values + ");").executeUpdate();
            }


        } catch (SQLException e) {
            getLogger().info("Failed to connect: " + e.getMessage());
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onEnable() {
        if (!hasConnected) {
            getLogger().info("Cannot function without a valid SQL connection, Disabling...");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        setupScoreboard();

        setupChat();

        setupKits();

        Message.load(getConfig());

        getCommand("stats").setExecutor(new StatsCommand(this));
        getCommand("resetstats").setExecutor(new ResetStatsCommand(this));
        getCommand("setspawn").setExecutor(new SetSpawnCommand(this));
        getCommand("savekit").setExecutor(new SaveKitCommand(this));
        getCommand("kit").setExecutor(new KitCommand(this));
        getCommand("fix").setExecutor(new FixCommand());
        getCommand("debug").setExecutor(new DebugCommand(this));
        getCommand("saveinventory").setExecutor(new SaveInventoryCommand(this));
        getCommand("leaderboard").setExecutor(new LeaderboardCommand(this));
        getCommand("spectate").setExecutor(new SpectateCommand(this));

        getServer().getPluginManager().registerEvents(new DeathEvent(), this);
        getServer().getPluginManager().registerEvents(new GeneralListeners(this), this);
    }

    @Override
    public void onDisable() {
        if (pool != null) {
            pool.getSource().close();
        }

        PlayerManager.getPlayers().clear();

        setInstance(null);
    }

    public void setupScoreboard() {
        scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();

        Objective objective = scoreboard.registerNewObjective("killstreak", "dummy");
        objective.setDisplayName(ChatColor.AQUA + "Killstreak");
        objective.setDisplaySlot(DisplaySlot.BELOW_NAME);
    }

    public void setupChat() {

        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            getLogger().log(Level.INFO, "Failed to find Vault,  Chat support will be disabled");
            return;
        }

        RegisteredServiceProvider<Chat> chatProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.chat.Chat.class);
        if (chatProvider == null) {
            getLogger().log(Level.INFO, "Failed to find PermissionsManager,  Chat support will be disabled");
            return;
        }
        chat = chatProvider.getProvider();
        getServer().getPluginManager().registerEvents(new ChatEvent(this), this);
    }

    public void setupKits() {
        if (getConfig().getString("kits") != null) {
            for (String name : getConfig().getConfigurationSection("kits").getKeys(false)) {
                Kit kit = new Kit(name);

                if (getConfig().getString("kits." + name + ".helmet") != null)
                    kit.setHelmet(Material.getMaterial(getConfig().getString("kits." + name + ".helmet")));


                if (getConfig().getString("kits." + name + ".chestplate") != null)
                    kit.setChestplate(Material.getMaterial(getConfig().getString("kits." + name + ".chestplate")));


                if (getConfig().getString("kits." + name + ".leggings") != null)
                    kit.setLeggings(Material.getMaterial(getConfig().getString("kits." + name + ".leggings")));


                if (getConfig().getString("kits." + name + ".boots") != null)
                    kit.setBoots(Material.getMaterial(getConfig().getString("kits." + name + ".boots")));

                Map<String, Integer> specialItems = new HashMap<>();

                for (String key : getConfig().getConfigurationSection("kits." + name + ".specialItems").getKeys(true)) {
                    specialItems.put(key, getConfig().getInt("kits." + name + ".specialItems." + key));
                }

                kit.setSpecialItems(specialItems);

                getKits().add(kit);
            }
        }
    }

    public Chat getChat() {
        return chat;
    }

    public Scoreboard getScoreboard() {
        return scoreboard;
    }

    public Set<Kit> getKits() {
        return kits;
    }

    public Kit getKit(String name) {
        for (Kit kit : getKits()) {
            if (kit.getName().equalsIgnoreCase(name)) {
                return kit;
            }
        }
        return null;
    }

    public Set<String> getSpawns() {
        return getConfig().getConfigurationSection("spawns") == null ?
                new HashSet<>() :
                getConfig().getConfigurationSection("spawns").getKeys(false);
    }

    public Connection getConnection() throws SQLException {
        return pool.getConnection();
    }

    public void giveKit(Kit kit, FFAPlayer player) {

        if (kit == null) return;
        if (player == null) return;

        Player pl = player.getBukkitPlayer();
        PlayerInventory pi = pl.getInventory();

        pi.clear();
        pi.setArmorContents(null);
        pl.getActivePotionEffects().clear();

        if (kit.getHelmet() != null) pi.setHelmet(toItemStack(kit.getHelmet(), 1));
        if (kit.getChestplate() != null) pi.setChestplate(toItemStack(kit.getChestplate(), 1));
        if (kit.getLeggings() != null) pi.setLeggings(toItemStack(kit.getLeggings(), 1));
        if (kit.getBoots() != null) pi.setBoots(toItemStack(kit.getBoots(), 1));

        ItemStack[] contents = toItemStack(kit.getSpecialItems()).toArray(new ItemStack[0]);

        if (player.getInventoryContents() != null) {
            contents = player.getInventoryContents();
        }

        pi.setContents(contents);
        pl.updateInventory();
        player.setKit(kit);
    }

    public static PurityFFA getInstance() {
        return instance;
    }

    public static void setInstance(PurityFFA instance) {
        PurityFFA.instance = instance;
    }
}
