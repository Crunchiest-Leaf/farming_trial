package com.crunchiest.commands;

import com.crunchiest.FarmingTrial;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * FARMING TRIAL PLUGIN
 * ______                   _____    _       _ 
 * |  ___|                 |_   _|  (_)     | |
 * | |_ __ _ _ __ _ __ ___   | |_ __ _  __ _| |
 * |  _/ _` | '__| '_ ` _ \  | | '__| |/ _` | |
 * | || (_| | |  | | | | | | | | |  | | (_| | |
 * \_| \__,_|_|  |_| |_| |_| \_/_|  |_|\__,_|_|
 *
 * Author: Crunchiest_Leaf
 *
 * desc: Trial Plugin for LOTC java team
 *       see link for outline.
 * 
 * link: https://docs.google.com/document/d/1zpQpmroUDSb7b6XRdxoifJIs6ig295lM0LOI0gdOvGk/edit#heading=h.h6zgogey5tcq
 * 
 */

public class ReloadFarmTrialCommand implements CommandExecutor {

  FarmingTrial plugin;

  public ReloadFarmTrialCommand(FarmingTrial plugin) {
    this.plugin = plugin;
  }

   /** 
   *  commandFeedback: 
   *  custom formatting for farm command feedback.
   *  sends to specified recipient, with message string.
   *
   *  @param sender - recipient of message.
   *  @param feedback - string message to be sent.
   *  @return void       
   */
  private void commandFeedback(CommandSender sender, String feedback) {
    sender.sendMessage(ChatColor.RED + feedback);
  }
  
  @Override
  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

    /** 
     *  onCommand, handles event that Farm commands are called
     *  Locked to moderator only for security purposes.
     *  permission: farmtrial.toggletrampling
     */
   
    if (!sender.hasPermission("farmtrial.reload")) {
      return true;
    }

    if (cmd.getName().equalsIgnoreCase("farmtrial")) {
      if (args[0] == "reload") {
        commandFeedback(sender, "- - Reloading Plugin - -");
        plugin.reloadPlugin();
      }
    } else {
      commandFeedback(sender, "usage /farmtrial reload");
      return true;
    }
    commandFeedback(sender, "- - Reload Complete - -");
    return true;
  }

}
