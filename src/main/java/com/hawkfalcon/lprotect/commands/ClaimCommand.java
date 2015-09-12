package com.hawkfalcon.lprotect.commands;

import com.hawkfalcon.lprotect.LProtect;
import com.hawkfalcon.lprotect.util.Lang;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ClaimCommand implements LCommand {

    private LProtect plugin;

    public ClaimCommand(LProtect plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Lang.INVALID_PLAYER.toString());
            return false;
        }
        Player player = (Player) sender;

        if (args.length > 1) {
            player.sendMessage(Lang.MISSING_ARGUMENTS.toString());
            return false;
        }

        if (plugin.getConfigManager().isBlacklisted(player.getWorld().getName())) {
            sender.sendMessage(Lang.INVALID_WORLD.toString());
            return false;
        }

        if (player.hasPermission("protect.claim")) {
            plugin.getProtectManager().protectArea(player);
        } else {
            player.sendMessage(Lang.NO_PERMISSION.toString());
        }
        return false;
    }
}