
package com.puritymc.purityffa.player;

import com.puritymc.purityffa.PurityFFA;
import com.puritymc.purityffa.kit.Kit;
import com.puritymc.purityffa.util.Util;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import static com.puritymc.purityffa.util.InventoryToBase64.fromBase64;
import static com.puritymc.purityffa.util.InventoryToBase64.toBase64;

/*******************************************************************************
 * Copyright MartinItsLinda (c) 2016. All Rights Reserved.
 * Any code contained within this document, and any associated API's with similar branding
 * are the sole property of MartinItsLinda. Distribution, reproduction, taking snippets or
 * claiming any contents as your own will break the terms of the liscense and void any
 * agreements with you, the third party.
 ******************************************************************************/

public class FFAPlayer {

    private UUID killed;
    private UUID tagger;

    private int id;
    private int points = 100; //default points
    private int kills;
    private int deaths;
    private int killStreak;
    private int highestKillStreak;

    private Kit kit = null;
    private boolean debugging = false;
    private PurityFFA plugin;
    private Player player;
    private ItemStack[] contents;

    public FFAPlayer(Player player) {
        this.player = player;
        this.plugin = PurityFFA.getInstance();
    }

    public Player getBukkitPlayer() {
        return player;
    }

    public int getId(){
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int getKills() {
        return kills;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }

    public int getDeaths() {
        return deaths;
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }

    public boolean isStreaking() {
        return getKillStreak() % 5 == 0;
    }

    public int getKillStreak() {
        return killStreak;
    }

    public void setKillStreak(int killStreak) {
        this.killStreak = killStreak;
    }

    public int getHighestKillStreak() {
        return highestKillStreak;
    }

    public void setHighestKillStreak(int highestKillStreak) {
        this.highestKillStreak = highestKillStreak;
    }

    public UUID getLastKilled() {
        return killed;
    }

    public void setLastKilled(UUID killed) {
        this.killed = killed;
    }

    public Kit getKit() {
        return kit;
    }

    public void setKit(Kit kit) {
        this.kit = kit;
    }

    public boolean isTagged() {
        return tagger != null;
    }

    public UUID getTagger() {
        return tagger;
    }

    public void setTagger(UUID tagger) {
        this.tagger = tagger;
    }

    public boolean isDebugging() {
        return debugging;
    }

    public void setDebugging(boolean debugging) {
        this.debugging = debugging;
    }

    public ItemStack[] getInventoryContents() {
        return contents;
    }

    public void setInventoryContents(ItemStack[] contents) {
        this.contents = contents;
    }

    public boolean update() {

        boolean success = true;

        Connection connection = null;
        PreparedStatement statement = null;

        StringBuilder query = new StringBuilder();

        query.append("UPDATE ffa_stats SET points = ?, kills = ?, deaths = ?, highestKillStreak = ?");

        if (contents != null && contents.length > 0) {
            try {
                query.append(", inventoryContents = '").append(fromBase64(toBase64(kit, contents))).append("' ");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        query.append("WHERE unique_id = ?");

        try {
            connection = plugin.getConnection();
            statement = connection.prepareStatement(query.toString());

            statement.setInt(1, points);
            statement.setInt(2, kills);
            statement.setInt(3, deaths);
            statement.setInt(4, highestKillStreak);
            statement.setString(5, player.getUniqueId().toString());

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            success = false;
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException ignored) {

                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException ignored) {

                }
            }
        }
        return success;
    }

    public double getKillDeath() {
        return getDeaths() <= 1 ? getKills() : Util.format(getKills() / getDeaths(), 2);
    }

    public Integer getRanking() {
        int rank = -1;

        try (Connection connection = plugin.getConnection();
             PreparedStatement stmt = connection.prepareStatement("SELECT *, FIND_IN_SET( `kills`, " +
                     "(SELECT GROUP_CONCAT( `kills` ORDER BY `kills` DESC ) " +
                     "FROM `ffa_stats` )) AS rank FROM ffa_stats " +
                     "WHERE `unique_id`='" + player + "';");
             ResultSet set = stmt.executeQuery()) {

            if(set.next()) id = set.getInt("rank");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rank;
    }
}
