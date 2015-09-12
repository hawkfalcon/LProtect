package com.hawkfalcon.lprotect;

import com.hawkfalcon.lprotect.data.RegionType;
import com.hawkfalcon.lprotect.util.Lang;
import com.sk89q.worldguard.bukkit.RegionContainer;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;

public class ProtectManager {

    private Map<String, RegionType> regionTypes;
    private Map<UUID, List<String>> claimed;

    private LProtect plugin;

    public ProtectManager(LProtect plugin) {
        this.plugin = plugin;
        regionTypes = plugin.getConfigManager().getRegionTypes();
        claimed = plugin.getConfigManager().populateClaimed();
    }

    public void protectArea(Player player) {
        String name = getAllowedRegion(player);
        if (name != null) {
            protectArea(name, player);
        }
    }

    public void protectArea(String name, Player player) {
        if (getAmount(player) >= plugin.getConfigManager().getMax(player)) {
            player.sendMessage(Lang.MAX_REGIONS_REACHED.toString());
            return;
        }
        Location location = player.getLocation();
        RegionContainer container = plugin.getWorldGuard().getRegionContainer();
        RegionManager regions = container.get(location.getWorld());
        RegionType type = regionTypes.get(name);
        String part = player.getName().toLowerCase() + "_" + type.getName() + "_";
        String id = part + getNextId(part, player, location.getWorld().getName());
        ProtectedRegion region = type.createRegion(id, location);
        region.setFlag(DefaultFlag.GREET_MESSAGE, Lang.GREET_MESSAGE.toString("name", player.getName()));
        region.setFlag(DefaultFlag.FAREWELL_MESSAGE, Lang.FAREWELL_MESSAGE.toString("name", player.getName()));
        DefaultDomain members = region.getMembers();
        members.addPlayer(player.getName());
        if (regions != null) {
            Collection<ProtectedRegion> protectedRegions = new ArrayList<>();
            for (ProtectedRegion protectedRegion : regions.getRegions().values()) {
                if (!protectedRegion.getId().equalsIgnoreCase("__global__")) {
                    protectedRegions.add(protectedRegion);
                }
            }
            if (region.getIntersectingRegions(protectedRegions).size() == 0) {
                plugin.getUtils().visualize(type.getSize().getMinLoc(location), type.getSize().getMaxLoc(location));
                player.sendMessage(Lang.CLAIMED_REGION.toString());
                claimRegion(player, region.getId() + ":" + location.getWorld().getName());
                regions.addRegion(region);
            } else {
                player.sendMessage(Lang.OVERLAPPING_CLAIM.toString());
            }
        } else {
            player.sendMessage(Lang.NOT_CLAIMABLE.toString());
        }
    }

    public void removeArea(Player player) {
        Location location = player.getLocation();
        RegionContainer container = plugin.getWorldGuard().getRegionContainer();
        RegionManager regions = container.get(location.getWorld());
        assert regions != null;
        for (ProtectedRegion region : regions.getApplicableRegions(location)) {
            String id = region.getId();
            String identifier = id + ":" + location.getWorld().getName();
            if (claimed.get(player.getUniqueId()).contains(identifier)) {
                regions.removeRegion(id);
                removeRegion(player.getUniqueId(), identifier);
                player.sendMessage(Lang.REMOVED_REGION.toString());
                return;
            } else {
                if (player.hasPermission("protect.mod")) {
                    for (UUID uuid : claimed.keySet()) {
                        if (claimed.get(uuid).contains(identifier)) {
                            regions.removeRegion(id);
                            removeRegion(uuid, identifier);
                            player.sendMessage(Lang.REMOVED_REGION.toString());
                            return;
                        }
                    }
                    player.sendMessage(Lang.INVALID_REGION.toString());
                } else {
                    player.sendMessage(Lang.NO_PERMISSION.toString());
                    return;
                }
            }
        }
    }

    private void claimRegion(Player player, String id) {
        UUID uuid = player.getUniqueId();
        List<String> claims;
        if (!claimed.containsKey(uuid)) {
            claims = new ArrayList<>();
        } else {
            claims = claimed.get(uuid);
        }
        claims.add(id);
        claimed.put(uuid, claims);
        plugin.getConfig().set("claimed." + uuid.toString(), claims);
        plugin.saveConfig();
    }

    private void removeRegion(UUID uuid, String id) {
        List<String> claims = claimed.get(uuid);
        if (claimed.containsKey(uuid)) {
            claims.remove(id);
        }
        claimed.put(uuid, claims);
        plugin.getConfig().set("claimed." + uuid.toString(), claims);
        plugin.saveConfig();
    }

    public String getAllowedRegion(Player player) {
        String name = null;
        for (String region : regionTypes.keySet()) {
            if (player.hasPermission("protect.region." + region)) {
                name = region;
            }
        }
        if (name == null) {
            player.sendMessage(Lang.NO_PERMISSION.toString());
        }
        return name;
    }

    public RegionType getRegion(String region) {
        return regionTypes.get(region);
    }

    public int getAmount(Player player) {
        if (!claimed.containsKey(player.getUniqueId())) {
            return 0;
        }
        return claimed.get(player.getUniqueId()).size();
    }

    private int getNextId(String part, Player player, String world) {
        List<String> claimed = getClaimed(player);
        int amount = getAmount(player);
        if (claimed != null) {
            while (claimed.contains(part + amount + ":" + world)) {
                amount++;
            }
        }
        return amount;
    }

    public List<String> getClaimed(Player player) {
        return claimed.get(player.getUniqueId());
    }

    public boolean isRegion(String name) {
        return regionTypes != null && regionTypes.keySet().contains(name);
    }

    public void addMember(Player owner, Player player) {
        ProtectedRegion region = getCurrentRegion(owner);
        if (region == null) {
            owner.sendMessage(Lang.INVALID_REGION.toString());
            return;
        }
        DefaultDomain members = region.getMembers();
        members.addPlayer(player.getName());
        owner.sendMessage(Lang.PLAYER_ADD.toString("name", player.getName()));
        player.sendMessage(Lang.PLAYER_ADDED.toString("name", owner.getName()));
    }

    public void removeMember(Player owner, Player player) {
        ProtectedRegion region = getCurrentRegion(owner);
        if (region == null) {
            owner.sendMessage(Lang.INVALID_REGION.toString());
            return;
        }
        DefaultDomain members = region.getMembers();
        if (members.contains(player.getName())) {
            members.removePlayer(player.getName());
            owner.sendMessage(Lang.PLAYER_REMOVE.toString("name", player.getName()));
            player.sendMessage(Lang.PLAYER_REMOVED.toString("name", owner.getName()));
        } else {
            owner.sendMessage(Lang.INVALID_PLAYER.toString());
        }
    }

    public void toggleEnter(Player player) {
        ProtectedRegion region = getCurrentRegion(player);
        if (region == null) {
            player.sendMessage(Lang.INVALID_REGION.toString());
            return;
        }
        StateFlag.State entry = StateFlag.State.ALLOW;
        if (region.getFlag(DefaultFlag.ENTRY) == entry) {
            entry = StateFlag.State.DENY;
        }
        region.setFlag(DefaultFlag.ENTRY, entry);
        player.sendMessage(Lang.TOGGLED_ENTRY.toString());
    }

    private ProtectedRegion getCurrentRegion(Player player) {
        Location location = player.getLocation();
        RegionContainer container = plugin.getWorldGuard().getRegionContainer();
        RegionManager regions = container.get(location.getWorld());
        assert regions != null;
        for (ProtectedRegion region : regions.getApplicableRegions(location)) {
            String id = region.getId();
            String identifier = id + ":" + location.getWorld().getName();
            if (claimed.get(player.getUniqueId()).contains(identifier)) {
                return region;
            }
        }
        return null;
    }
}