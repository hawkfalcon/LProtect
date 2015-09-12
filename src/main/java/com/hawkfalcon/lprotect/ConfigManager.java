package com.hawkfalcon.lprotect;

import com.hawkfalcon.lprotect.data.RegionType;
import com.hawkfalcon.lprotect.data.Size;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.potion.PotionType;

import java.util.*;

public class ConfigManager {

    private FileConfiguration config;

    private Map<String, Integer> maxRegions = new HashMap<>();

    private List<String> blacklist;

    public ConfigManager(LProtect plugin) {
        config = plugin.getConfig();
        populateData();
    }

    private void populateData() {
        populateAmount();
        blacklist = config.getStringList("blacklisted_worlds");
        if (blacklist == null) {
            blacklist = new ArrayList<>();
        }
    }

    private void populateAmount() {
        for (String name : config.getConfigurationSection("amount").getKeys(false)) {
            addPermission("protect.amount." + name);
            int num = config.getInt("amount." + name);
            maxRegions.put(name, num);
        }
    }

    public Map<UUID, List<String>> populateClaimed() {
        Map<UUID, List<String>> claimed = new HashMap<>();
        if (config.get("claimed") == null) {
            return claimed;
        }
        for (String name : config.getConfigurationSection("claimed").getKeys(false)) {
            UUID uuid = UUID.fromString(name);
            List<String> regions = config.getStringList("claimed." + name);
            claimed.put(uuid, regions);
        }
        return claimed;
    }

    public Map<String, RegionType> getRegionTypes() {
        Map<String, RegionType> regionTypes = new HashMap<>();
        for (String name : config.getConfigurationSection("regions").getKeys(false)) {
            addPermission("protect.region." + name);
            String path = "regions." + name + ".";
            PotionType potionType = PotionType.valueOf(config.getString(path + "potion.type"));
            String potionName = config.getString(path + "potion.name");
            Size size = new Size(config.getInt(path + "x"), 0, config.getInt(path + "z"));
            regionTypes.put(name, new RegionType(name, potionType, potionName, size));
        }
        return regionTypes;
    }

    private void addPermission(String name) {
        Bukkit.getPluginManager().addPermission(new Permission(name, PermissionDefault.OP));
    }


    public int getMax(Player player) {
        int largest = 0;
        for (String name : maxRegions.keySet()) {
            int num = maxRegions.get(name);
            if (num > largest && player.hasPermission("protect.amount." + name)) {
                largest = num;
            }
        }
        return largest;
    }

    public boolean isBlacklisted(String world) {
        return blacklist.contains(world);
    }
}