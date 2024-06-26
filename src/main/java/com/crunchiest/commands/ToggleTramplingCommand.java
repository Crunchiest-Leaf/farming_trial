package com.crunchiest.commands;

import com.crunchiest.FarmingTrial;
import com.crunchiest.data.PluginConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

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
 * ToggleTramplingCommand:
 * command executor for toggling trampling
 * via command.
*/
public class ToggleTramplingCommand implements CommandExecutor {
  private FarmingTrial plugin;
  private PluginConfigManager pluginData;

  public ToggleTramplingCommand(FarmingTrial plugin) {
    this.plugin = plugin;
    this.pluginData = this.plugin.getPluginDataManager();
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

    /*
     *  onCommand, handles event that Farm commands are called
     *  Locked to moderator only for security purposes.
     *  permission: farmtrial.toggletrampling
     */
    if (args.length < 1) {
      commandFeedback(sender, ChatColor.GREEN + "Command usage: /toggle_trampling <user_name>");
      return true;
    }

    if (!sender.hasPermission("farmtrial.toggletrampling")) {
      return true;
    }

    Player target = Bukkit.getPlayer(args[0]);
    ConfigurationSection players = pluginData.getPlayerConfig().getConfigurationSection("players");
    boolean state = false;
    if (cmd.getName().equalsIgnoreCase("toggle_trampling")) {
      if (target != null) {
        if (players == null) {
          pluginData.getPlayerConfig().set("players." + target.getUniqueId().toString(), 
              target.getName());
          state = true;
        } else if (players.contains(target.getUniqueId().toString())) {
          pluginData.getPlayerConfig().set("players." 
              + target.getUniqueId().toString(), null);
          state = false;
        } else {
          pluginData.getPlayerConfig().set("players." + target.getUniqueId().toString(), 
              target.getName());
          state = true;
        }
        pluginData.savePlayerConfig();
      } else {
        commandFeedback(sender, "Player '" + args[0] + "' not found.");
        return true;
      }
    }
    commandFeedback(sender, "Player '" + args[0] 
        + "' Crop trampling toggled to: " + String.valueOf(state));
    return true;
  }
  
}
