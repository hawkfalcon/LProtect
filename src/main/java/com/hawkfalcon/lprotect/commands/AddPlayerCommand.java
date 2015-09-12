package com.hawkfalcon.lprotect.commands;

import com.hawkfalcon.lprotect.LProtect;
import com.hawkfalcon.lprotect.util.Lang;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AddPlayerCommand implements LCommand {

    private LProtect plugin;

    public AddPlayerCommand(LProtect plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Lang.INVALID_PLAYER.toString());
            return false;
        }
        Player owner = (Player) sender;

        if (args.length != 2) {
            sender.sendMessage(Lang.MISSING_ARGUMENTS.toString());
            return false;
        }

        Player player = Bukkit.getPlayer(args[1]);
        if (player == null) {
            sender.sendMessage(Lang.INVALID_PLAYER.toString());
            return false;
        }
        if (player.hasPermission("protect.use.addplayer")) {
            plugin.getProtectManager().addMember(owner, player);
        } else {
            player.sendMessage(Lang.NO_PERMISSION.toString());
        }
        return false;
    }
}
