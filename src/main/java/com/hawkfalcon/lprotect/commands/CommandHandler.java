package com.hawkfalcon.lprotect.commands;

import com.hawkfalcon.lprotect.util.Lang;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.HashMap;

public class CommandHandler implements CommandExecutor {

    private static HashMap<String, LCommand> commands = new HashMap<>();

    public void register(String name, LCommand cmd) {
        commands.put(name, cmd);
    }

    public boolean exists(String name) {
        return commands.containsKey(name);
    }

    public LCommand getExecutor(String name) {
        return commands.get(name);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (args.length == 0) {
            getExecutor("protect").onCommand(sender, cmd, commandLabel, args);
            return true;
        }

        if (args.length > 0) {
            if (exists(args[0])) {
                getExecutor(args[0]).onCommand(sender, cmd, commandLabel, args);
                return true;
            } else {
                sender.sendMessage(Lang.MISSING_ARGUMENTS.toString());
                return true;
            }
        }
        return false;
    }
}