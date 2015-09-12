package com.hawkfalcon.lprotect.data;

import com.sk89q.worldedit.BlockVector;
import org.bukkit.Location;

public class Size {
    private int x;
    private int y;
    private int z;


    public Size(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public BlockVector getMin(Location location) {
        return new BlockVector(location.getX() - x / 2, 0, location.getZ() - z / 2);
    }

    public BlockVector getMax(Location location) {
        return new BlockVector(location.getX() + x / 2, 255, location.getZ() + z / 2);
    }

    public Location getMinLoc(Location location) {
        return new Location(location.getWorld(), location.getX() - x / 2, 0, location.getZ() - z / 2);
    }

    public Location getMaxLoc(Location location) {
        return new Location(location.getWorld(), location.getX() + x / 2, 255, location.getZ() + z / 2);
    }
}