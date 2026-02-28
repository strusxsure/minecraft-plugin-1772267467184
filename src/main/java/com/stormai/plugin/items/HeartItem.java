package com.stormai.plugin.items;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class HeartItem {
    private static final String CUSTOM_TAG = "lifesteal_heart";
    private static final String DISPLAY_NAME = "§5Heart";
    private static final String LORE = "§7Right-click to gain 1 heart";

    public static ItemStack createHeartItem() {
        ItemStack item = new ItemStack(Material.NETHER_STAR);
        ItemMeta meta = item.getItemMeta();

        // Set display name
        meta.setDisplayName(DISPLAY_NAME);

        // Set lore
        List<String> lore = new ArrayList<>();
        lore.add(LORE);
        meta.setLore(lore);

        // Set custom NBT tag
        meta.getPersistentDataContainer().set(new NamespacedKey("lifesteal", CUSTOM_TAG),
                PersistentDataType.BYTE, (byte) 1);

        // Hide enchants if any
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        item.setItemMeta(meta);
        return item;
    }

    public static boolean isHeartItem(ItemStack item) {
        if (item == null || item.getType() != Material.NETHER_STAR) return false;
        if (!item.hasItemMeta()) return false;

        ItemMeta meta = item.getItemMeta();
        return meta.getPersistentDataContainer().has(new NamespacedKey("lifesteal", CUSTOM_TAG),
                PersistentDataType.BYTE);
    }

    public static Recipe createHeartRecipe() {
        ItemStack result = createHeartItem();
        NamespacedKey key = new NamespacedKey("lifesteal", "heart_recipe");
        ShapedRecipe recipe = new ShapedRecipe(key, result);

        recipe.shape("GGG", "GNG", "GGG");
        recipe.setIngredient('G', Material.GOLD_BLOCK);
        recipe.setIngredient('N', Material.NETHER_STAR);

        return recipe;
    }
}