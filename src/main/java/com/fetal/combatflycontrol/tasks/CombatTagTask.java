package com.fetal.combatflycontrol.tasks;

import com.fetal.combatflycontrol.CombatFlyControl;
import com.fetal.combatflycontrol.managers.CombatManager;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class CombatTagTask extends BukkitRunnable {
    
    private final CombatFlyControl plugin;
    private final CombatManager combatManager;
    private final Player player;
    
    public CombatTagTask(CombatFlyControl plugin, Player player) {
        this.plugin = plugin;
        this.combatManager = plugin.getCombatManager();
        this.player = player;
    }
    
    @Override
    public void run() {
        if (!player.isOnline()) {
            this.cancel();
            return;
        }
        
        combatManager.decrementCombatTime(player);
    }
}