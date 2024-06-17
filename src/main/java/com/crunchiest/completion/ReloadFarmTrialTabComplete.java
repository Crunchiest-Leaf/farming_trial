package com.crunchiest.completion;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
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
*  ReloadFarmTrialTabComplete: 
*  handles autofill of reload plugin command.
*  Locked to moderator only for security purposes.
*  permission: farmtrial.reload
*/
public class ReloadFarmTrialTabComplete implements TabCompleter {

  @Override
  public List<String> onTabComplete(CommandSender sender, Command cmd, 
      String alias, String[] args) {
    /* 
    *  Tab Complete Object, handles autofill of reload command
    *  Locked to moderator only for security purposes.
    *  permission: farmtrial.reload
    */
    List<String> returnVals = new ArrayList<>();
    Player player = (Player) sender;
    if (player.hasPermission("farmtrial.reload")) {
      if (args.length == 1) {
        returnVals.add("reload");
        return returnVals;
      }
    }
    return null;
    
  }
  
}
