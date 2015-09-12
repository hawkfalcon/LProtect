package com.hawkfalcon.lprotect.util;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

public enum Lang {
    HELP("help", "&4Protect with /protect claim\nSee your protections with /protect list"),
    LIST_NAME("list-name", "&4%name%'s Regions"),
    LIST_REGION("list-region", "&2%region%"),
    NO_PERMISSION("no-permission", "&4You do not have permission for this"),
    MISSING_ARGUMENTS("missing-arguments", "&aWrong number of arguments"),
    NOT_CLAIMABLE("not-claimable", "&4This region is not claimable!"),
    OVERLAPPING_CLAIM("overlapping-claim", "&4This region is too close to another region!"),
    NO_CLAIMS("no-claims", "&cYou don't have any protected regions"),
    MAX_REGIONS_REACHED("max-regions-reached", "&bYou are not allowed any more regions."),
    POTION_INSTRUCTIONS("potion-instructions", "&1Throw to claim"),
    INVALID_PLAYER("invalid-player", "&4That is not a player"),
    INVALID_REGION("invalid-region", "&4That is not a region"),
    INVALID_WORLD("invalid-world", "&4This world is blacklisted!"),
    POTION_RECEIVED("potion-received", "&2Received a potion"),
    POTION_GIVEN("potion-given", "%name% received potion."),
    CLAIMED_REGION("claimed-region", "&aYou have claimed this region!"),
    REMOVED_REGION("removed-region", "&aYou removed the region!"),
    GREET_MESSAGE("greet-message", "&1Entering %name%'s region"),
    FAREWELL_MESSAGE("farewell-message", "&2Leaving %name%'s region"),
    PLAYER_ADD("player-add", "&aAdded %name% to your region!"),
    PLAYER_REMOVE("player-remove", "&aRemoved %name% from your region!"),
    PLAYER_ADDED("player-added", "&7You were added to %name%'s region"),
    PLAYER_REMOVED("player-removed", "&7You were removed from %name%'s region"),
    TOGGLED_ENTRY("toggle-entry", "&6Toggled entry to region");


    private String path;
    private String def;
    private static YamlConfiguration LANG;

    /**
     * Lang enum constructor.
     *
     * @param path  The string path.
     * @param start The default string.
     */
    Lang(String path, String start) {
        this.path = path;
        this.def = start;
    }

    /**
     * Set the {@code YamlConfiguration} to use.
     *
     * @param config The config to set.
     */
    public static void setFile(YamlConfiguration config) {
        LANG = config;
    }

    @Override
    public String toString() {
        return ChatColor.translateAlternateColorCodes('&', LANG.getString(this.path, def));
    }

    public String toString(String replace, String with) {
        return toString().replace("%" + replace + "%", with);
    }

    /**
     * Get the default value of the path.
     *
     * @return The default value of the path.
     */
    public String getDefault() {
        return this.def;
    }

    /**
     * Get the path to the string.
     *
     * @return The path to the string.
     */
    public String getPath() {
        return this.path;
    }
}
