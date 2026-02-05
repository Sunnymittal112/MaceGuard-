package com.fetal.combatflycontrol.listeners;

import com.fetal.combatflycontrol.CombatFlyControl;
import com.fetal.combatflycontrol.managers.CombatManager;
import com.fetal.combatflycontrol.managers.ConfigManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;

public class FlyListener implements Listener {
    
    private final CombatFlyControl plugin;
    private final CombatManager combatManager;
    private final ConfigManager configManager;
    
    public FlyListener(CombatFlyControl plugin) {
        this.plugin = plugin;
        this.combatManager = plugin.getCombatManager();
        this.configManager = plugin.getConfigManager();
    }
    
    /**
     * HIGHEST priority to override WorldGuard and EssentialsX
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onPlayerToggleFlight(PlayerToggleFlightEvent event) {
        Player player = event.getPlayer();
        
        // Check if player is in combat
        if (combatManager.isInCombat(player)) {
            if (!player.hasPermission("combatflycontrol.bypass")) {
                // Cancel the flight attempt
                event.setCancelled(true);
                
                // Disable flight immediately
                player.setFlying(false);
                player.setAllowFlight(false);
                
                // Send message
                String message = configManager.getMessage("cannot-fly-combat");
                player.sendMessage(message);
                
                configManager.debug("Blocked flight attempt for " + player.getName() + " (in combat)");
            }
        }
    }
    
    /**
     * Monitor flight attempts during movement (for Wings enchantment)
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        
        // Only check if player is flying and in combat
        if (player.isFlying() && combatManager.isInCombat(player)) {
            if (!player.hasPermission("combatflycontrol.bypass")) {
                // Force disable flight
                combatManager.disableFlight(player);
                
                String message = configManager.getMessage("cannot-fly-combat");
                player.sendMessage(message);
                
                configManager.debug("Force disabled flight for " + player.getName() + " during move (in combat)");
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onGameModeChange(PlayerGameModeChangeEvent event) {
        Player player = event.getPlayer();
        
        // If player enters combat and changes gamemode, ensure flight is disabled
        if (combatManager.isInCombat(player)) {
            // Schedule flight disable for next tick (after gamemode change)
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                if (player.isFlying()) {
                    combatManager.disableFlight(player);
                }
            }, 1L);
        }
    }
}