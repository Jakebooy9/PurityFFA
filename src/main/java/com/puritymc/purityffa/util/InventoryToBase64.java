
package com.puritymc.purityffa.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.puritymc.purityffa.kit.Kit;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;


/*******************************************************************************
 * Copyright MartinItsLinda (c) 2016. All Rights Reserved.
 * Any code contained within this document, and any associated API's with similar branding
 * are the sole property of MartinItsLinda. Distribution, reproduction, taking snippets or
 * claiming any contents as your own will break the terms of the liscense and void any
 * agreements with you, the third party.
 ******************************************************************************/

public class InventoryToBase64 {

    public static String toBase64(Kit kit, Inventory inventory) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

            // Write the size of the inventory
            dataOutput.writeInt(inventory.getSize());
            for(ItemStack item : inventory.getContents()) {
                if (item == null) {
                    dataOutput.writeObject(null);
                    continue;
                }
                if (kit.getSpecialItems().get(item.getType().toString()) == null) {
                    dataOutput.writeObject(null);
                    continue;
                }
                dataOutput.writeObject(new ItemStack(item.getType(), kit.getSpecialItems().get(item.getType().toString())));
            }

            // Serialize that array
            dataOutput.close();
            return Base64Coder.encodeLines(outputStream.toByteArray());
        } catch (Exception e) {
            throw new IllegalStateException("Unable to save item stacks.", e);
        }
    }

    public static String toBase64(Kit kit, ItemStack[] inventory) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

            // Write the size of the inventory
            dataOutput.writeInt(inventory.length);
            for (ItemStack item : inventory) {
                if (item == null) {
                    dataOutput.writeObject(null);
                    continue;
                }
                if (kit.getSpecialItems().get(item.getType().toString()) == null) {
                    dataOutput.writeObject(null);
                    continue;
                }
                dataOutput.writeObject(new ItemStack(item.getType(), kit.getSpecialItems().get(item.getType().toString())));
            }

            // Serialize that array
            dataOutput.close();
            return Base64Coder.encodeLines(outputStream.toByteArray());
        } catch (Exception e) {
            throw new IllegalStateException("Unable to save item stacks.", e);
        }
    }

    public static Inventory fromBase64(String data) throws IOException {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            Inventory inventory = Bukkit.getServer().createInventory(null, dataInput.readInt());

            // Read the serialized inventory
            for (int i = 0; i < inventory.getSize(); i++) {
                inventory.setItem(i, (ItemStack) dataInput.readObject());
            }

            dataInput.close();
            return inventory;
        } catch (ClassNotFoundException e) {
            throw new IOException("Unable to decode class type.", e);
        }
    }
}
