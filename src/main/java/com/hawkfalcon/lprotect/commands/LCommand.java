package com.hawkfalcon.lprotect.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public interface LCommand {
    boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args);
}
