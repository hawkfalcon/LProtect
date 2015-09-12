package com.hawkfalcon.lprotect.commands;

import com.hawkfalcon.lprotect.util.Lang;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class ProtectCommand implements LCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        sender.sendMessage(Lang.HELP.toString());
        return false;
    }
}