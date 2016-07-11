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
