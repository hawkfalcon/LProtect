package com.hawkfalcon.lprotect;

import com.hawkfalcon.lprotect.util.Lang;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PotionSplashEvent;

public class PotionListener implements Listener {

    private LProtect plugin;

    public PotionListener(LProtect plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void PotionSplashEvent(PotionSplashEvent event) {
        String name = event.getPotion().getItem().getItemMeta().getLore().get(0);
        if (name == null) return;
        if (!plugin.getProtectManager().isRegion(name)) return;
        if (event.getPotion().getShooter() instanceof Player) {
            Player player = (Player) event.getPotion().getShooter();
            if (plugin.getConfigManager().isBlacklisted(player.getWorld().getName())) {
                player.sendMessage(Lang.INVALID_WORLD.toString());
                event.setCancelled(true);
                return;
            }
            if (player.hasPermission("protect.region." + name)) {
                for (LivingEntity ent : event.getAffectedEntities()) {
                    if (ent instanceof Player) {
                        event.setIntensity(ent, 0);
                    }
                }
                event.getPotion().getEffects().clear();
                plugin.getProtectManager().protectArea(name, player);
            } else {
                event.setCancelled(true);
                player.sendMessage(Lang.NO_PERMISSION.toString());
            }
        }
    }
}
