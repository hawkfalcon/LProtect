package com.hawkfalcon.lprotect.util;

import com.hawkfalcon.lprotect.LProtect;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class Utilities {
    private LProtect plugin;

    public Utilities(LProtect plugin) {
        this.plugin = plugin;
    }

    public void visualize(Location min, Location max) {
        List<Location> square = new ArrayList<>();
        for (int x = min.getBlockX(); x < max.getBlockX(); x++) {
            square.add(new Location(min.getWorld(), x, 0, min.getBlockZ()));
        }
        for (int z = min.getBlockZ(); z < max.getBlockZ(); z++) {
            square.add(new Location(min.getWorld(), min.getBlockX(), 0, z));
        }
        for (int x = min.getBlockX(); x <= max.getBlockX(); x++) {
            square.add(new Location(min.getWorld(), x, 0, max.getBlockZ()));
        }
        for (int z = min.getBlockZ(); z <= max.getBlockZ(); z++) {
            square.add(new Location(min.getWorld(), max.getBlockX(), 0, z));
        }
        animate(square);
    }

    private void animate(final List<Location> square) {
        new BukkitRunnable() {
            int counter = 0;

            @Override
            public void run() {
                World world = square.get(0).getWorld();
                for (Location location : square) {
                    location.setY(world.getHighestBlockYAt(location) + 1);
                    world.playEffect(location, Effect.SMOKE, 4);
                }
                counter++;
                if (counter >= 20) {
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 0L, 3L);
    }
}