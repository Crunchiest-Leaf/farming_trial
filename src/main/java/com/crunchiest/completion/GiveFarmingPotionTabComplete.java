package com.crunchiest.completion;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import com.crunchiest.FarmingTrial;
import com.crunchiest.data.FarmingPotionManager;

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

public class GiveFarmingPotionTabComplete implements TabCompleter {

  FarmingTrial plugin;
  FarmingPotionManager potions;

  public GiveFarmingPotionTabComplete(FarmingTrial plugin) {
    this.plugin = plugin;
    this.potions = plugin.getFarmingPotionManager();
  }

  @Override
  public List<String> onTabComplete(CommandSender sender, Command cmd, 
      String alias, String[] args) {
   /** 
    *  Tab Complete Object, handles autofill of Farm commands
    *  Locked to moderator only for security purposes.
    *  permission: farm_trial.toggletrampling
    */
    
    Player player = (Player) sender;
    if (player.hasPermission("farmtrial.givepotion")) {
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
      if (args.length == 2) {
        List<String> availablePotions = potions.getAllCustomPotionIds();
        return availablePotions;
      }
    }
    return null;
    
  }
}
