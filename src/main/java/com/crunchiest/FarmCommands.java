package com.crunchiest;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.Bukkit;
import java.util.logging.Logger;


import org.bukkit.entity.Player;

public class FarmCommands implements CommandExecutor{
    private static final Logger LOGGER=Logger.getLogger("farming_trial");

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
    {
        /** 
        /  onCommand, handles event that Farm commands are called
        /  Locked to moderator only for security purposes.
        /  permission: farm_trial.moderator.commands
        */

        Player player = (Player) sender;
        Player target = Bukkit.getPlayer(args[0]);
        if (cmd.getName().equalsIgnoreCase("set_trampling"))
        {
            if (player.hasPermission("farm_trial.moderator.commands"))
            {
                if ((args.length >=2) && (target != null) && ("true".equals(args[1]) || "false".equals(args[1])))
                {
                    String commandOut = ("lp user "+args[0]+" permission set farm_trial.trample.toggle "+args[1]);
                    LOGGER.info(commandOut);
                    Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), commandOut);
                }
                else
                {
                    player.sendMessage("/set_trampling <player> <boolean>");
                }
            }
        }
        return false;

    }

}
