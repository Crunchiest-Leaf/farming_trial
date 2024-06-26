package com.crunchiest.commands;

import com.crunchiest.FarmingTrial;
import com.crunchiest.data.FarmingPotionManager;
import com.crunchiest.data.FarmingPotionManager.CustomPotion;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
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
 * GiveFarmingPotionCommand:
 * command executor for giving custom potions
 * via command.
*/
public class GiveFarmingPotionCommand implements CommandExecutor {
  
  private FarmingTrial plugin;
  private FarmingPotionManager potions;
  
  public GiveFarmingPotionCommand(FarmingTrial plugin) {
    this.plugin = plugin;
    this.potions = this.plugin.getFarmingPotionManager();
  }

  
  /** 
   * commandFeedback:
   * Command feedback to user.
   *
   * @param sender sender of command.
   * @param feedback feedback message string.
   */
  private void commandFeedback(CommandSender sender, String feedback) {
    sender.sendMessage(ChatColor.RED + feedback);
  }
  
  /** 
   * onCommand: 
   * Command executor for giving potions.
   *
   * @param sender command sender.
   * @param cmd command.
   * @param label command label.
   * @param args command args.
   * @return boolean success.
   */
  @Override
  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    
    /* 
    *  onCommand, handles give potion command. 
    *  Locked to moderator only for security purposes.
    *  permission: farmtrial.givepotion
    */
    if (args.length < 1) {
      commandFeedback(sender, ChatColor.GREEN 
          + "Command usage: /give_potion <user_name> <registered potion ID>");
      return true;
    }
    
    if (!sender.hasPermission("farmtrial.givepotion")) {
      return true;
    }
    
    Player target = Bukkit.getPlayer(args[0]);
    String potionId = args[1];
    if (cmd.getName().equalsIgnoreCase("give_potion")) {
      if (target == null) {
        commandFeedback(sender, ChatColor.GREEN + "Player '" + args[0] + "' not found.");
        return true;
      }
      CustomPotion potion = potions.getCustomPotion(args[1]);
      if (potion == null) {
        commandFeedback(sender, ChatColor.GREEN + "Potion: " + args[1] + " not found.");
        return true;
      }
      potions.giveCustomPotion(target, potionId);
    }
    commandFeedback(sender, "Player: " + args[0] + " given '" + args[1] + "' potion");
    return true;
  }
}
