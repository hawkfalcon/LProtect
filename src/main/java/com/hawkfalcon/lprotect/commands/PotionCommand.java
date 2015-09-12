package com.hawkfalcon.lprotect.commands;

import com.hawkfalcon.lprotect.LProtect;
import com.hawkfalcon.lprotect.data.RegionType;
import com.hawkfalcon.lprotect.util.Lang;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PotionCommand implements LCommand {

    private LProtect plugin;

    public PotionCommand(LProtect plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(Lang.MISSING_ARGUMENTS.toString());
            return false;
        }
        Player player = Bukkit.getPlayer(args[1]);
        if (player == null) {
            sender.sendMessage(Lang.INVALID_PLAYER.toString());
            return false;
        }
        if (!player.hasPermission("protect.potion")) {
            player.sendMessage(Lang.NO_PERMISSION.toString());
            return false;
        }
        String region;
        switch (args.length) {
            case 2:
                region = plugin.getProtectManager().getAllowedRegion(player);
                break;
            case 3:
                region = args[2];
                break;
            default:
                sender.sendMessage(Lang.MISSING_ARGUMENTS.toString());
                return false;
        }
        givePotion(sender, player, region);
        return false;
    }

    private void givePotion(CommandSender sender, Player player, String region) {
        if (region != null) {
            if (!player.hasPermission("protect.region." + region)) {
                sender.sendMessage(Lang.NO_PERMISSION.toString());
                player.sendMessage(Lang.NO_PERMISSION.toString());
                return;
            }
            RegionType type = plugin.getProtectManager().getRegion(region);
            if (type == null) {
                sender.sendMessage(Lang.INVALID_REGION.toString());
                return;
            }
            ItemStack item = type.getPotion();
            player.getInventory().addItem(item);
            sender.sendMessage(Lang.POTION_GIVEN.toString("name", player.getName()));
            if (!sender.getName().equalsIgnoreCase(player.getName())) {
                player.sendMessage(Lang.POTION_RECEIVED.toString());
            }
        } else {
            sender.sendMessage(Lang.INVALID_REGION.toString());
        }
    }
}
