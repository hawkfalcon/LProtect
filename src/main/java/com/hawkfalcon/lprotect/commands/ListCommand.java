package com.hawkfalcon.lprotect.commands;

import com.hawkfalcon.lprotect.LProtect;
import com.hawkfalcon.lprotect.util.Lang;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldguard.bukkit.RegionContainer;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class ListCommand implements LCommand {

    private LProtect plugin;

    public ListCommand(LProtect plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (!sender.hasPermission("protect.list")) {
            sender.sendMessage(Lang.NO_PERMISSION.toString());
            return false;
        }
        Player player = null;
        if (args.length == 2) {
            if (sender.hasPermission("protect.mod")) {
                player = Bukkit.getPlayer(args[1]);
            } else {
                sender.sendMessage(Lang.NO_PERMISSION.toString());
                return false;
            }
        } else if (args.length == 1 && sender instanceof Player) {
            player = (Player) sender;
        } else {
            sender.sendMessage(Lang.MISSING_ARGUMENTS.toString());
        }
        if (player != null) {
            sendList(player, sender);
        }
        return false;
    }

    private void sendList(Player player, CommandSender sender) {
        try {
            List<String> claimed = plugin.getProtectManager().getClaimed(player);
            if (claimed == null || claimed.isEmpty()) {
                sender.sendMessage(Lang.NO_CLAIMS.toString());
                return;
            }
            sender.sendMessage(Lang.LIST_NAME.toString("name", player.getName()));
            for (String id : plugin.getProtectManager().getClaimed(player)) {
                String[] raw = id.split(":");
                World world = Bukkit.getWorld(raw[1]);
                RegionContainer container = plugin.getWorldGuard().getRegionContainer();
                RegionManager rm = container.get(world);
                assert rm != null;
                ProtectedRegion region = rm.getRegion(raw[0]);
                if (region != null) {
                    BlockVector vector = region.getMinimumPoint();
                    int y = world.getHighestBlockYAt(vector.getBlockX(), vector.getBlockZ());
                    String list = "- " + raw[0] + " at (" +
                            world.getName() + ", " + vector.getBlockX() + ", " + y + ", " + vector.getBlockZ() + ")";
                    sender.sendMessage(Lang.LIST_REGION.toString("region", list));
                }
            }
        } catch (Error e) {
            System.out.println(e.getMessage());
        }
    }

}
