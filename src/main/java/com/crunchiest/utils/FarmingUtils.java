package com.crunchiest.utils;

import com.crunchiest.FarmingTrial;
import java.util.concurrent.ThreadLocalRandom;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;

public class FarmingUtils {

  /**
  * dropCount:
  * Simple dropCount Calculator Method
  * Takes in tool tier and enchantment
  * Outputs number of items to drop.
  *
  * @param toolTier    - custom integer value of hoe level
  * @param enchantTier - custom integer value of hoe level
  * @return counts - array of crop and seed counts.
  */
  
  public static int[] dropCount(int toolTier, int enchantTier) {
    return new int[]{toolTier + enchantTier, 1 + enchantTier};
  }
  
  /**
  * FarmDrops:
  * Method to Carry out Item Drops.
  * Takes in Item Values and Player Loc
  * Drops Items at player Loc Naturally.
  *
  * @param crop      - crop type to drop.
  * @param cropCount - Amount of crop.
  * @param seed      - seed type to drop.
  * @param seedCount - Amount of seed.
  * @param mult      - seed Multiplier.
  * @param player    - player object.
  * @return void
  */
  public static void farmDrops(Material crop, int cropCount, Material seed, int seedCount, float mult, Block block) {
    World world = block.getWorld();
    Location loc = block.getLocation();
    if (seedCount > 0) {
      world.dropItemNaturally(loc, new ItemStack(seed, 
          (int) Math.ceil((double) (seedCount * mult))));
    }
    if (cropCount > 0) {
      world.dropItemNaturally(loc, new ItemStack(crop, 
            (int) Math.ceil((double) (seedCount * mult))));
    }
  }
  
  /**
  * Gets the level of a specific enchantment on an item.
  *
  * @param item the item stack
  * @param enchantment the enchantment
  * @return the level of the enchantment
  */
  public static int getEnchantmentLevel(ItemStack item, Enchantment enchantment) {
    return item.getEnchantmentLevel(enchantment);
  }
  
  /**
  * DamageHoe:
  * Handles Durability Changes
  * of hoe used to farm.
  * Hoe breaks on max durability use.
  *
  * @param hoe    - hoe itemstack object.
  * @param player - player object.
  * @return void
  */
  public static void damageHoe(ItemStack hoe, Player player) {
    int unbreakingLevel = getEnchantmentLevel(hoe, Enchantment.DURABILITY);
    if (ThreadLocalRandom.current().nextInt(unbreakingLevel + 1) == 0) {
      Damageable meta = (Damageable) hoe.getItemMeta();
      meta.setDamage(meta.getDamage() + 1);
      if (meta.getDamage() < hoe.getType().getMaxDurability()) {
        hoe.setItemMeta(meta);
      } else {
        player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1, 1);
        hoe.setAmount(0);
      }
    }
  }
  
  /**
  * fixHoe:
  * Handles Durability Changes.
  * restores durability to max.
  * (used for random event).
  *
  * @param hoe    - hoe itemstack object.
  * @param player - player object.
  * @return void
  */
  
  public static void fixHoe(ItemStack hoe, Player player) {
    Damageable meta = (Damageable) hoe.getItemMeta();
    meta.setDamage(0);
    hoe.setItemMeta(meta);
    player.playSound(player.getLocation(), Sound.BLOCK_BELL_RESONATE, 1, 1);
    player.playSound(player.getLocation(), Sound.BLOCK_GRINDSTONE_USE, 1, 1);
  }
  
  /**
  * potatoRain:
  * Method to make potatoes rain from sky
  * on farming event roll.
  *
  * @param player - block at given farm location.
  * @return void
  */
  public static void potatoRain(Player player, FarmingTrial plugin) {
    World world = player.getWorld();
    Location loc = player.getLocation();
    Bukkit.getScheduler().runTaskLater(plugin, () -> {
      for (int i = 0; i < 40; i++) {
        Location dropLoc = loc.clone().add(ThreadLocalRandom.current().nextInt(-5, 6), i * 10, 
            ThreadLocalRandom.current().nextInt(-5, 6));
        Item potato = world.dropItem(dropLoc, new ItemStack(Material.BAKED_POTATO, 1));
        potato.setPickupDelay(Integer.MAX_VALUE);
        potato.setTicksLived(5600);
        potato.setGlowing(true);
      }
    }, 10L);
    world.playSound(loc, Sound.ENTITY_EVOKER_CAST_SPELL, 10, 1);
  }
  
  /**
  * SpawnNamedEntity:
  * Method to spawn random entity
  * on farming event roll.
  *
  * @param locBlock - block at given farm location.
  * @param entity   - entity type to spawn.
  * @param name     - name to give entity.
  * @return void
  */
  public static void spawnNamedEntity(Block locBlock, EntityType entity, String name) {
    World world = locBlock.getWorld();
    Entity villager = world.spawnEntity(locBlock.getLocation(), entity);
    villager.setCustomName(name);
  }
  
  /**
  * rangeCheck:
  * Method for random event
  * control flow.
  * returns true if range satisfied.
  *
  * @param min - minimum value in range.
  * @param max - maximum value in range.
  * @param val - value to test against.
  * @return boolean
  */
  public static boolean rangeCheck(int min, int max, int val) {
    return (val > min && val < max);
  }
  
  /**
  * randomFarmEvent:
  * Method to random roll table
  * a set of possible events.
  * MobDrops
  * todo: ItemDrops.
  *
  * @param block  - block at given farm location.
  * @param player - player object.
  * @return void
  */
  public static void randomFarmEvent(Block block, Player player, FarmingTrial plugin) {
    int randomNum = ThreadLocalRandom.current().nextInt(0, 1000 + 1);
    if (rangeCheck(0, 2, randomNum)) {
      //spawn undead farmer mob
      player.getWorld().playSound(block.getLocation(), Sound.ENTITY_WITCH_AMBIENT, 10, 1);
      spawnNamedEntity(block, EntityType.ZOMBIE_VILLAGER, ChatColor.DARK_PURPLE + "Undead Farmer");
      player.sendMessage(ChatColor.LIGHT_PURPLE 
          + "Unded Farmer: \"ARGGGHHHHH\"");
      
    } else if (rangeCheck(5, 20, randomNum)) {
      //completely repair hoe
      fixHoe(player.getInventory().getItemInMainHand(), player);
      player.sendMessage(ChatColor.GOLD 
          + "The Farm spirits have Blessed your Hoe with new Life!");
      
    } else if (rangeCheck(21, 25, randomNum)) { 
      //potato rain. rains cooked potatoes.
      potatoRain(player, plugin);
      player.sendMessage(ChatColor.GOLD 
          + "The Potato Gods have noticed your Efforts. Potato Rain!");
      
    } else if (rangeCheck(26, 30, randomNum)) { 
      //fish?!
      player.getWorld().playSound(block.getLocation(), Sound.ENTITY_DOLPHIN_PLAY, 10, 1);
      spawnNamedEntity(block, EntityType.COD, ChatColor.AQUA + "FISH?!");
      player.sendMessage(ChatColor.AQUA + "FISH?!");
      
    } //else if (rangeCheck(min, max, randomNum))
  }
}
