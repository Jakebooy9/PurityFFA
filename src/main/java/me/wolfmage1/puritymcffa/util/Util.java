package me.wolfmage1.puritymcffa.util;

import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

/*******************************************************************************
 * Copyright MartinItsLinda (c) 2016. All Rights Reserved.
 * Any code contained within this document, and any associated API's with similar branding
 * are the sole property of MartinItsLinda. Distribution, reproduction, taking snippets or
 * claiming any contents as your own will break the terms of the liscense and void any
 * agreements with you, the third party.
 ******************************************************************************/

public class Util {

    public static double format(double round, int places) {
        return Double.parseDouble(String.format("%." + places + "f", round));
    }

    public static void spawnFirework(final Location l) {
        Firework fw = (Firework) l.getWorld().spawnEntity(l, EntityType.FIREWORK);
        FireworkMeta fwm = fw.getFireworkMeta();

        //Our random generator
        Random r = new Random();

        //Get the type
        int ft = r.nextInt(4) + 1;

        FireworkEffect.Type type = FireworkEffect.Type.BALL;

        switch (ft) {
            case 2:
                type = FireworkEffect.Type.BALL_LARGE;
                break;
            case 3:
                type = FireworkEffect.Type.BURST;
                break;
            case 4:
                type = FireworkEffect.Type.CREEPER;
                break;
            case 5:
                type = FireworkEffect.Type.STAR;
                break;
        }

        Color colour = getColor(r.nextInt(17) + 1);
        Color fade = getColor(r.nextInt(17) + 1);

        //Create our effect with this
        FireworkEffect effect = FireworkEffect.builder()
                .flicker(r.nextBoolean())
                .withColor(colour)
                .withFade(fade)
                .with(type)
                .trail(r.nextBoolean())
                .build();

        //Then apply the effect to the meta
        fwm.addEffect(effect);

        //Then apply this to our rocket
        fw.setFireworkMeta(fwm);
    }

    private static Color getColor(int i) {
        Color c = null;
        switch (i) {
            case 1:
                c = Color.AQUA;
                break;
            case 2:
                c = Color.BLACK;
                break;
            case 3:
                c = Color.BLUE;
                break;
            case 4:
                c = Color.FUCHSIA;
                break;
            case 5:
                c = Color.GRAY;
                break;
            case 6:
                c = Color.GREEN;
                break;
            case 7:
                c = Color.LIME;
                break;
            case 8:
                c = Color.MAROON;
                break;
            case 9:
                c = Color.NAVY;
                break;
            case 10:
                c = Color.OLIVE;
                break;
            case 11:
                c = Color.ORANGE;
                break;
            case 12:
                c = Color.PURPLE;
                break;
            case 13:
                c = Color.RED;
                break;
            case 14:
                c = Color.SILVER;
                break;
            case 15:
                c = Color.TEAL;
                break;
            case 16:
                c = Color.WHITE;
                break;
            case 17:
                c = Color.YELLOW;
                break;
        }

        return c;
    }

    public static String colorize(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static List<ItemStack> toItemStack(Map<String, Integer> materials) {
        return materials.entrySet().stream().map(e -> new ItemStack(Material.getMaterial(e.getKey()), e.getValue())).collect(Collectors.toList());
    }

    public static ItemStack toItemStack(Material m, int amount) {
        return new ItemStack(m, amount);
    }

}
