package com.crunchiest.commands;

import com.crunchiest.FarmingTrial;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/*
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

/** 
 * ReloadFarmTrialCommand:
 * command executor for reloading plugin.
 * via command.
*/
public class ReloadFarmTrialCommand implements CommandExecutor {

  private FarmingTrial plugin;

  /**
   * ReloadFarmTrialCommand constructor.
   *
   * @param plugin main plugin instance
   */
  public ReloadFarmTrialCommand(FarmingTrial plugin) {
    this.plugin = plugin;
  }

  /**
   * Sends a formatted message to the command sender.
   *
   * @param sender  Command sender
   * @param message Message to send
   */
  private void commandFeedback(CommandSender sender, String message) {
    sender.sendMessage(ChatColor.GREEN + message); // Use GREEN for success feedback
  }
  
  /**
   * Executes the /farmtrial reload command.
   *
   * @param sender Command sender
   * @param cmd    Command instance
   * @param label  Command label
   * @param args   Command arguments
   * @return true if the command executed successfully, otherwise false
   */
  @Override
  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    if (!sender.hasPermission("farmtrial.reload")) {
      commandFeedback(sender, "You do not have permission to use this command.");
      return true;
    }

    if (args.length != 1 || !args[0].equalsIgnoreCase("reload")) {
      commandFeedback(sender, "Usage: /farmtrial reload");
      return true;
    }

    commandFeedback(sender, "Reloading plugin...");
    plugin.loadPlugin();
    commandFeedback(sender, "Plugin reloaded successfully.");
    return true;
  }
}
