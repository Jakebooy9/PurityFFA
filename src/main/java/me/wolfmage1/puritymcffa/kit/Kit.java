
package me.wolfmage1.puritymcffa.kit;

import org.bukkit.Material;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Kit {

    private Material helmet, chestplate, leggings, boots;
    private String name;
    private Map<String, Integer> specialItems = new HashMap<>();


    public Kit(String name) {
        this.name = name;
    }

    public Material getHelmet() {
        return helmet;
    }

    public void setHelmet(Material helmet) {
        this.helmet = helmet;
    }

    public Material getChestplate() {
        return chestplate;
    }

    public void setChestplate(Material chestplate) {
        this.chestplate = chestplate;
    }

    public Material getLeggings() {
        return leggings;
    }

    public void setLeggings(Material leggings) {
        this.leggings = leggings;
    }

    public Material getBoots() {
        return boots;
    }

    public void setBoots(Material boots) {
        this.boots = boots;
    }

    public Map<String, Integer> getSpecialItems() {
        return specialItems;
    }

    public void setSpecialItems(Map<String, Integer> specialItems) {
        this.specialItems = specialItems;
    }

    public String getName() {
        return name;
    }

}
