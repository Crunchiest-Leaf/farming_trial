package com.crunchiest.completion;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public class ReloadFarmTrialTabComplete implements TabCompleter{

  @Override
  public List<String> onTabComplete(CommandSender sender, Command cmd, 
      String alias, String[] args) {
   /** 
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
