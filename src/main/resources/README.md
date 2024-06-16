# Farming Trial Plugin

---

## Overview

The Farming Trial Plugin is designed for the LOTC team to manage and 
enhance farming mechanics within the game.

---

## Author

Crunchiest_Leaf

---

## Description

The plugin provides commands for moderators to manage custom farming potions, 
reload the plugin configuration, and toggle crop trampling for players.

---

## Permissions

- farmtrial.givepotion: Required to execute the /give_potion command.
- farmtrial.reload: Required to execute the /farmtrial reload command.
- farmtrial.toggletrampling: Required to execute the /toggle_trampling command.

---

## Commands

### 1. /give_potion

#### Usage

/give_potion <user_name> <registered_potion_ID>

- user_name: The username of the player who will receive the potion.
- registered_potion_ID: The ID of the registered custom potion to be given.

#### Example

/give_potion Steve POTION_OF_HARVESTING

---

### 2. /farmtrial reload

#### Usage

/farmtrial reload

This command reloads the plugin configuration.

---

### 3. /toggle_trampling

#### Usage

/toggle_trampling <user_name>

This command toggles crop trampling for the specified player.

#### Example

/toggle_trampling Steve

---

## Adding New Potions

To add new potions, update the loadCustomPotions() method in the main plugin class. 
Use the following lines of code to register new potions:

```java
potionManager.registerCustomPotion("<potion display name>", "<UniqueId>", <isSplashPotion?>, <effect block radius>);

//Potion of Growth: Bonemeal potion, makes REGISTERED crops in radius grow to full.
potionManager.registerCustomPotion("Potion of Growth", "POTION_OF_GROWTH", true, 2); 

//Potion of Harvesting: Harvest potion, harvests fully grown REGISTERED crops in radius.
potionManager.registerCustomPotion("Potion of Harvesting", "POTION_OF_HARVESTING", true, 2);
```

These lines register the "Potion of Growth" and the "Potion of Harvesting" with their respective functionalities. 
Adjust the parameters as needed to create and customize new potions.

---

---

## Adding Potion Event Listeners

To add additional event listeners for the custom potions, use the onParticleEvent listeners and access the 
persistent data container keys associated with each custom potion. 
Here is an example for the "Potion of Growth":

#### Example

```java
@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
public void onPotionOfGrowth(ProjectileHitEvent event) {
    CustomPotion customPotion = processCustomPotion(event, "POTION_OF_GROWTH");
    if (customPotion != null) {
        // do stuff
    }
}
```

---

## Adding Hoe Items and Crops

### Adding Hoe Items

Hoe items are listed under the `tools` section in the format `<tool>: <tool level>`. 
Here's how you can add new hoe items:

```yaml
tools:
  wooden_hoe: 1
  stone_hoe: 1
  iron_hoe: 2
  golden_hoe: 2
  diamond_hoe: 3
  netherite_hoe: 3
  your_custom_hoe: 2  # Example of adding a custom hoe
```

Replace your_custom_hoe with the name of your custom hoe item and adjust the tool level as necessary.

### Adding Crops
Crops are defined under the crops section with each crop having its own sub-section containing:

seed: The item name used as a seed for planting the crop.
drop: The item name that is dropped when the crop is harvested.
multiplier: A float value indicating the yield multiplier for the crop.
Here’s how to add new crops:

```yaml
crops:
  wheat:
    seed: wheat_seeds
    drop: wheat
    multiplier: 1
  carrots:
    seed: carrot
    drop: carrot
    multiplier: 0.5
  potatoes:
    seed: potato
    drop: potato
    multiplier: 0.5
  beetroots:
    seed: beetroot_seeds
    drop: beetroot
    multiplier: 1
  your_custom_crop:
    seed: custom_seed_item
    drop: custom_crop_item
    multiplier: 0.75  # Example of adding a custom crop.
```
Replace your_custom_crop, custom_seed_item, custom_crop_item, and adjust the multiplier value accordingly for your custom crop.

## Notes
Ensure that all item names (e.g., wheat_seeds, wheat, etc.) correspond to the correct material names used in your plugin.
After making changes to the farmingData.yml file, ensure to reload or restart your plugin for the changes to take effect.