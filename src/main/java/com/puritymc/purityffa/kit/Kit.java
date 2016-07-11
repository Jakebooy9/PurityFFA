
package com.puritymc.purityffa.kit;

import org.bukkit.Material;

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
