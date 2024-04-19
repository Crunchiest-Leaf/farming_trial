package com.crunchiest;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class FarmTabComplete implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args)
    {
        /** 
        /  Tab Complete Object, handles autofill of Farm commands
        /  Locked to moderator only for security purposes.
        /  permission: farm_trial.moderator.commands
        */

        Player player = (Player) sender;
        if (player.hasPermission("farm_trial.moderator.commands"))
        {
            if(args.length == 1)
            {
                // returns list of online players
                List<String> playerNames = new ArrayList<>();
                Player[] players = new Player[Bukkit.getServer().getOnlinePlayers().size()];
                Bukkit.getServer().getOnlinePlayers().toArray(players);
                for (int playerX = 0; playerX < players.length; playerX++)
                {
                    playerNames.add(players[playerX].getName());
                }
                
                return playerNames;
            }
            else if(args.length == 2)
            {
                // returns boolean true / false
                List<String> bools = new ArrayList<>();
                bools.add("true");
                bools.add("false");
                return bools;
            }
        }
        return null;
          
    }

}
