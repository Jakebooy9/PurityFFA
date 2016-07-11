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
package me.wolfmage1.puritymcffa.player;

import me.wolfmage1.puritymcffa.FFA;
import me.wolfmage1.puritymcffa.kit.Kit;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import static me.wolfmage1.puritymcffa.util.InventoryToBase64.fromBase64;
import static me.wolfmage1.puritymcffa.util.InventoryToBase64.toBase64;

public class FFAPlayer {

    private UUID killed;
    private UUID tagger;

    private int id;
    private int points;
    private int kills;
    private int deaths;
    private int killStreak;
    private int highestKillStreak;

    private Kit kit = null;
    private boolean debugging = false;
    private FFA plugin;
    private Player player;
    private ItemStack[] contents;

    public FFAPlayer(Player player) {
        this.player = player;
        this.plugin = FFA.getInstance();
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
        return getDeaths() <= 1 ? getKills() : Double.parseDouble(String.format("%.2f", (double) getKills() / getDeaths()));
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
