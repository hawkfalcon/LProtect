package com.hawkfalcon.lprotect.data;

import com.hawkfalcon.lprotect.util.Lang;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

import java.util.Arrays;

public class RegionType {
    private String name;
    private PotionType type;
    private String potionName;
    private Size size;

    public RegionType(String name, PotionType type, String potionName, Size size) {
        this.name = name;
        this.type = type;
        this.potionName = potionName;
        this.size = size;
    }

    public String getName() {
        return name;
    }

    public Size getSize() {
        return size;
    }

    public ProtectedCuboidRegion createRegion(String id, Location location) {
        return new ProtectedCuboidRegion(id, size.getMin(location), size.getMax(location));
    }

    public ItemStack getPotion() {
        Potion splash = new Potion(type, 1);
        splash.setSplash(true);
        ItemStack item = splash.toItemStack(1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', potionName));
        meta.setLore(Arrays.asList(name, Lang.POTION_INSTRUCTIONS.toString()));
        item.setItemMeta(meta);
        return item;
    }
}