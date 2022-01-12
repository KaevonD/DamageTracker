package me.drysu.damagetracker;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;


public class MyListener implements Listener {

    Map<String, Double> bossDamagers = new HashMap<>();

    @EventHandler
    public void onDamageDone(EntityDamageByEntityEvent event) {

        //if entity damaged is the Ender Dragon
        if(event.getEntity() instanceof EnderDragon) {

            //if damage is done by a projectile, AKA an arrow
            if(event.getCause() == EntityDamageEvent.DamageCause.PROJECTILE) {
                Projectile projectile = (Projectile)event.getDamager();

                //check if the entity who shot the arrow is a player
                if(((projectile.getShooter() instanceof Player))) {

                    //if the boss damage tracker does not contain the player yet
                    if(!bossDamagers.containsKey(((Player)projectile.getShooter()).getName())) {

                        //insert the new player in with their damage
                        bossDamagers.put(((Player)projectile.getShooter()).getName(), event.getDamage());
                    }

                    //if the boss damage tracker already has an instance of the player recorded in it
                    else {

                        //update the players total damage to boss by adding the damage they did with the arrow to their previous damage
                        bossDamagers.put(((Player)projectile.getShooter()).getName(), bossDamagers.get(((Player)projectile.getShooter()).getName()) + event.getDamage());
                    }
                }
            }

            //if the damage is done by a non-projectile, AKA melee
            else {

                //if the boss damage tracker does not contain the player yet
                if(!bossDamagers.containsKey(event.getDamager().getName())) {

                    //insert the new player in with their damage
                    bossDamagers.put(event.getDamager().getName(), event.getDamage());
                }

                //if the boss damage tracker already has an instance of the player recorded in it
                else {

                    //update the players total damage to boss by adding the damage they did with the arrow to their previous damage
                    bossDamagers.put(event.getDamager().getName(), bossDamagers.get(event.getDamager().getName()) + event.getDamage());
                }
            }
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {

        //if the entity that died is the Ender Dragon
        if(event.getEntity() instanceof EnderDragon) {

            //sort the damages stored from greatest to least
            LinkedHashMap<String, Double> sortedBossDamagers = new LinkedHashMap<>();
            bossDamagers.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).forEachOrdered(x -> sortedBossDamagers.put(x.getKey(), x.getValue()));
            //iterate through the map containing the players and their damage dealt to the Ender Dragon and display the values
            Bukkit.broadcastMessage(ChatColor.RED + "Damage dealt to Ender Dragon");
            Bukkit.broadcastMessage(ChatColor.GRAY + "------------------------------------");
            for(Map.Entry<String, Double> entry : sortedBossDamagers.entrySet()) {
                Bukkit.broadcastMessage(entry.getKey() + ": " + ChatColor.GOLD + entry.getValue().intValue() + " damage");
            }

            //clear the map of players and their damages
            bossDamagers.clear();
        }

    }
}
