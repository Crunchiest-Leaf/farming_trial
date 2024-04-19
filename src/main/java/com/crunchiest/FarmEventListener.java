package com.crunchiest;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Player;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import java.util.Map;
import java.util.Dictionary;
import java.util.Hashtable;
import org.bukkit.Sound;
import java.util.logging.Logger;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.EventPriority;

public class FarmEventListener implements Listener {

    private static final Logger LOGGER=Logger.getLogger("farming_trial"); // logger for debugging

    /**
     *  DropCount:
     *  Simple Dropcount Calculator Method
     *  Takes in tool tier and enchantment
     *  Outputs number of items to drop.
     */
    private int[] DropCount(int tool_tier, int enchant_tier){
        
        int[] counts = new int[2];
        int seed_count = 1 + enchant_tier;
        int crop_count = tool_tier + enchant_tier;

        counts[0] = crop_count;
        counts[1] = seed_count;

        return counts; 
    }

    /**
     *  FarmDrops:
     *  Method to Carry out Item Drops
     *  Takes in Item Values and Player Loc
     *  Drops Items at player Loc Naturally.
     */
    private void FarmDrops(Material crop, int crop_count, Material seed, int seed_count, Player player){

        LOGGER.info(crop.name());
        LOGGER.info((String.valueOf(crop_count)));
        LOGGER.info(seed.name());
        LOGGER.info((String.valueOf(seed_count)));
        ItemStack cropDrop = new ItemStack(crop, crop_count);
        ItemStack seedDrop = new ItemStack(seed, seed_count);

        player.getWorld().dropItemNaturally(player.getLocation(), cropDrop);
        player.getWorld().dropItemNaturally(player.getLocation(), seedDrop);
    }

    /**
     *  FortuneTier_toInteger:
     *  Method to give Integer Value to
     *  'fortune' enchant levels.
     *  +1 for every level of fortune.
     */
    private int FortuneTier_toInteger(ItemStack hoe)
    {
        Map<Enchantment, Integer> enchantments = hoe.getEnchantments();
        if (hoe.containsEnchantment(Enchantment.LOOT_BONUS_BLOCKS))
        {
            int level = enchantments.get(Enchantment.LOOT_BONUS_BLOCKS);
            return level;
        }
        else return 0;
    }

    /**
     *  DamageHoe:
     *  Handles Durability Changes
     *  of hoe used to farm.
     *  Hoe breaks on max durability use.
     */
    private void DamageHoe(ItemStack hoe, Player player)
    {
        int damage_modifier = 1;
        org.bukkit.inventory.meta.Damageable dMeta = (org.bukkit.inventory.meta.Damageable) hoe.getItemMeta();
        int currentDura = dMeta.getDamage();
        int newDura = currentDura + damage_modifier;
        int maxDamage = hoe.getType().getMaxDurability();

        if ((newDura) <= maxDamage)
        {
            dMeta.setDamage(newDura);
            hoe.setItemMeta(dMeta);
        }
        else
        {
            player.playSound(player.getLocation(),Sound.ENTITY_ITEM_BREAK,1,1);
            hoe.setAmount(0);
        }
    }

    @EventHandler
    public void onPlayerTrample(PlayerInteractEvent event)
    {   
        /** 
        /  Checks if player has trampled Farmland block w/ a physical interaction;
        /  Then Cancels event, resetting block data to its pre-trampled state.
        /  permission: farm_trial.trample.toggle
        */

        if(event.getAction() == Action.PHYSICAL) //physical interaction catches trampling
        {
            Block block = event.getClickedBlock();
            if ((block == null)) //|| (event.getPlayer().hasPermission("farm_trial.trample.toggle")))
                return;

            Material blockType = block.getType();

            if(blockType == Material.FARMLAND)
            {
                // cancels trample event...
                event.setUseInteractedBlock(org.bukkit.event.Event.Result.DENY);
                event.setCancelled(true);

                // ... and resets block data.    
                block.setType(blockType);
                block.setBlockData(block.getBlockData());
            }
        }
    }

    @EventHandler
    public void onPlayerHoe(PlayerInteractEvent event)
    {
        /** 
        /  onPlayerHoe:
        /  Event Listener to handle the interaction of the player with farm-blocks;
        /  Harness for the Overall Farm interaction side of things.
        /  permission: farm_trial.trample.toggle
        */

        Player player = event.getPlayer();
        
        // Dictionary to lookup custom hoe value enums
        Dictionary<Material, Integer> hoe_tiers = new Hashtable<>();
        hoe_tiers.put(Material.WOODEN_HOE, 1);
        hoe_tiers.put(Material.STONE_HOE, 1);
        hoe_tiers.put(Material.IRON_HOE, 2);
        hoe_tiers.put(Material.GOLDEN_HOE, 2);
        hoe_tiers.put(Material.DIAMOND_HOE, 3);
        hoe_tiers.put(Material.NETHERITE_HOE, 3);

        // Dictionary to lookup crop to seed drop enums
        Dictionary<Material, Material> crop_to_seed = new Hashtable<>();
        crop_to_seed.put(Material.WHEAT, Material.WHEAT_SEEDS);
        crop_to_seed.put(Material.CARROTS, Material.CARROT);
        crop_to_seed.put(Material.POTATOES, Material.POTATO);
        crop_to_seed.put(Material.BEETROOTS, Material.BEETROOT_SEEDS);

        // Dictionary to lookup crop to crop drop enums
        Dictionary<Material, Material> crop_to_drop = new Hashtable<>();
        crop_to_drop.put(Material.WHEAT, Material.WHEAT);
        crop_to_drop.put(Material.CARROTS, Material.CARROT);
        crop_to_drop.put(Material.POTATOES, Material.POTATO);
        crop_to_drop.put(Material.BEETROOTS, Material.BEETROOT);

        ItemStack heldItem = player.getInventory().getItemInMainHand();
            
        if ((event.getAction() == Action.LEFT_CLICK_BLOCK))
        {
            Block block = event.getClickedBlock();
            if(block == null)
                return;

            Material blockType = block.getType();

            //filters out other non-farm blocks
            if (crop_to_seed.get(blockType) != null) 
            {
                Ageable ageable = (Ageable) block.getBlockData();
                event.setUseInteractedBlock(org.bukkit.event.Event.Result.DENY);
                event.setCancelled(true);
                    
                if (ageable.getAge() < ageable.getMaximumAge() || (hoe_tiers.get(heldItem.getType()) == null))
                {
                    // if crop not grown, or not using hoe
                    // reset block data to pre-event.
                    block.setType(blockType);
                    ageable.setAge(ageable.getAge());
                    block.setBlockData(ageable);
                }
                else 
                {
                    // if crop grown, and using hoe
                    // Harvest crop, and reset to baby crop.
                    block.setType(blockType);
                    ageable.setAge(0);
                    block.setBlockData(ageable);

                    // handle drops & hoe durability change.
                    int dropCounts[] = new int[2];
                    dropCounts = DropCount(hoe_tiers.get(heldItem.getType()), FortuneTier_toInteger(heldItem));
                    FarmDrops(crop_to_drop.get(blockType), dropCounts[0], crop_to_seed.get(blockType), dropCounts[1], player);
                    DamageHoe(heldItem, player);
                }
            }
        }
    }


    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockFlowToCrop(BlockFromToEvent event)
    {
        /** 
        /  onBlockFlowToCrop:
        /  Event Listener to handle flow of block onto crops;
        /  Prevents water from giving player crop drops.
        /  by cancelling block flow event onto crops.
        */

        Block block = event.getToBlock();
        Material blockType = block.getType();
        Material crops[] = new Material[]{Material.WHEAT, Material.POTATOES, Material.BEETROOTS, Material.CARROTS};
        if (Arrays.asList(crops).contains(blockType))
        {
            event.setCancelled(true);
            block.setType(Material.AIR, true);
            block.setBlockData(null);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockUnderBreak(BlockBreakEvent event) 
    {   
        /** 
        /  onBlockUnderBreak:
        /  Event Listener to handle break of block below crops;
        /  Prevents block under break from giving player crop drops.
        /  by cancelling block break event.
        */

        Block block = event.getBlock();
        Material blockAboveType = block.getRelative(BlockFace.UP).getType();
        Material crops[] = new Material[]{Material.WHEAT, Material.POTATOES, Material.BEETROOTS, Material.CARROTS};
        if (Arrays.asList(crops).contains(blockAboveType))
        {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPistonPushCrop(BlockPistonExtendEvent event) 
    {
        /** 
        /  onPistonPushCrop:
        /  Event Listener to handle piston breaking crops;
        /  Prevents piston from pushing itself, or other blocks into crops.
        /  by cancelling piston extent event.
        */

        List<Block> effectedBlocks = new ArrayList<Block>(event.getBlocks());
        Material crops[] = new Material[]{Material.WHEAT, Material.POTATOES, Material.BEETROOTS, Material.CARROTS};
        for (int i = 0; i < effectedBlocks.size(); i++)
        {
            Block checked = effectedBlocks.get(i);
            if (Arrays.asList(crops).contains(checked.getType()))
            {
                event.setCancelled(true);
            }
        }
    }
}




