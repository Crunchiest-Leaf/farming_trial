package com.crunchiest;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.logging.Logger;



import org.bukkit.entity.Player;

public class FarmCommands implements CommandExecutor{
    private static final Logger LOGGER=Logger.getLogger("farming_trial");

    /** 
    *  commandFeedback: 
    *  custom formatting for farm command feedback.
    *  sends to specified recipient, with message string.
    *  
    *  @param sender - recipient of message.
    *  @param feedback - string message to be sent.
    *  @return void       
    */

    private void commandFeedback(CommandSender sender, String feedback)
    {
        sender.sendMessage(ChatColor.RED + feedback);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
    {
        /** 
        *  onCommand, handles event that Farm commands are called
        *  Locked to moderator only for security purposes.
        *  permission: farm_trial.moderator.commands
        */

        Player player = (Player) sender;
        Player target = Bukkit.getPlayer(args[0]);
        if (cmd.getName().equalsIgnoreCase("set_trampling"))
        {
            if (player.hasPermission("farm_trial.moderator.commands"))
            {
                if ((args.length >=2) && (target != null) && ("true".equals(args[1]) || "false".equals(args[1])))
                {
                    //piggyback off of lp commands.
                    String commandOut = ("lp user "+args[0]+" permission set farm_trial.trample.toggle "+args[1]);
                    LOGGER.info(commandOut);
                    Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), commandOut);
                    commandFeedback(sender, "Trampling set to " +args[1]+ " for " + args[0]);
                }
                else
                {
                    commandFeedback(sender, "/set_trampling <player> <boolean>");
                }
            }
            else
            {
                commandFeedback(sender, ChatColor.RED + "You Don't Have permission to do that.");
            }
        }
        return false;

    }

}
