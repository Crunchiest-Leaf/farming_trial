package com.crunchiest.completion;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
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
*  ToggleTramplingTabComplete: 
*  handles autofill of toggle trampling command.
*  Locked to moderator only for security purposes.
*  permission: farmtrial.toggletrampling
*/

public class ToggleTramplingTabComplete implements TabCompleter {
  
  @Override
  public List<String> onTabComplete(CommandSender sender, Command cmd, 
      String alias, String[] args) {
    /* 
    *  Tab Complete Object, handles autofill of Farm commands
    *  Locked to moderator only for security purposes.
    *  permission: farmtrial.toggletrampling
    */
    
    Player player = (Player) sender;
    if (player.hasPermission("farmtrial.toggletrampling")) {
      if (args.length == 1) {
        // returns list of online players
        List<String> playerNames = new ArrayList<>();
        Player[] players = new Player[Bukkit.getServer().getOnlinePlayers().size()];
        Bukkit.getServer().getOnlinePlayers().toArray(players);
        for (int playerX = 0; playerX < players.length; playerX++) {
          playerNames.add(players[playerX].getName());
        }
        return playerNames;
      }
    }
    return null;
    
  }
  
}
