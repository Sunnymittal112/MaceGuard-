package com.fetal.combatflycontrol.listeners;

import com.fetal.combatflycontrol.CombatFlyControl;
import com.fetal.combatflycontrol.managers.CombatManager;
import com.fetal.combatflycontrol.managers.ConfigManager;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

public class CombatListener implements Listener {
    
    private final CombatFlyControl plugin;
    private final CombatManager combatManager;
    private final ConfigManager configManager;
    private final boolean isMaceAvailable;
    private Material maceMaterial;
    
    public CombatListener(CombatFlyControl plugin) {
        this.plugin = plugin;
        this.combatManager = plugin.getCombatManager();
        this.configManager = plugin.getConfigManager();
        this.isMaceAvailable = plugin.isMaceAvailable();
        
        if (isMaceAvailable) {
            try {
                this.maceMaterial = Material.valueOf("MACE");
            } catch (IllegalArgumentException e) {
                // Should not happen if isMaceAvailable is true
            }
        }
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        Entity victim = event.getEntity();
        
        // Only handle player vs player
        if (!(damager instanceof Player) || !(victim instanceof Player)) {
            return;
        }
        
        Player attacker = (Player) damager;
        Player defender = (Player) victim;
        
        // Check if attacker is using Mace (only in 1.21+)
        if (isMaceAvailable && isHoldingMace(attacker)) {
            // Check if attacker is flying or has wings enchantment
            if (attacker.isFlying() || hasWingsEnchantment(attacker)) {
                // Tag both players for combat
                combatManager.tagBothPlayers(attacker, defender);
                
                // Disable flight for attacker immediately
                if (attacker.isFlying()) {
                    combatManager.disableFlight(attacker);
                }
                
                configManager.debug("Mace hit detected from flying player: " + attacker.getName());
            } else {
                // Normal combat tag for mace users
                combatManager.tagPlayer(attacker);
                combatManager.tagPlayer(defender);
            }
        } else {
            // Normal combat - tag both players
            combatManager.tagPlayer(attacker);
            combatManager.tagPlayer(defender);
        }
    }
    
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        if (combatManager.isInCombat(player)) {
            combatManager.removeCombatTag(player);
        }
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (combatManager.isInCombat(player)) {
            // Could add combat logging punishment here
            combatManager.removeCombatTag(player);
        }
    }
    
    private boolean isHoldingMace(Player player) {
        if (!isMaceAvailable) return false;
        
        ItemStack mainHand = player.getInventory().getItemInMainHand();
        ItemStack offHand = player.getInventory().getItemInOffHand();
        
        return mainHand.getType() == maceMaterial || offHand.getType() == maceMaterial;
    }
    
    private boolean hasWingsEnchantment(Player player) {
        if (!configManager.isCheckWingsEnchantment()) return false;
        
        // Check for AdvancedEnchantments Wings enchantment
        ItemStack chestplate = player.getInventory().getChestplate();
        if (chestplate == null || chestplate.getType() == Material.AIR) {
            return false;
        }
        
        // Check lore for wings enchantment (AdvancedEnchantments stores enchants in lore)
        if (chestplate.hasItemMeta() && chestplate.getItemMeta().hasLore()) {
            for (String line : chestplate.getItemMeta().getLore()) {
                String stripped = line.toLowerCase().replaceAll("[§&][0-9a-fk-or]", "");
                if (stripped.contains(configManager.getWingsEnchantmentName().toLowerCase())) {
                    return true;
                }
            }
        }
        
        return false;
    }
}