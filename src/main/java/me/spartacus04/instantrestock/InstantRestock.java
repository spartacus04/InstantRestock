package me.spartacus04.instantrestock;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.entity.VillagerAcquireTradeEvent;
import org.bukkit.event.entity.VillagerCareerChangeEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public final class InstantRestock extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onVillagerCareerChange(VillagerCareerChangeEvent event){
        if(event.getProfession() != Villager.Profession.NITWIT || event.getProfession() != Villager.Profession.NONE){
            List<MerchantRecipe> new_recipes = new ArrayList<>();
            for (MerchantRecipe recipe:
                 event.getEntity().getRecipes()) {
                recipe.setMaxUses(Integer.MAX_VALUE);
                recipe.setUses(0);
                new_recipes.add(recipe);
            }

            event.getEntity().setRecipes(new_recipes);
        }
    }

    @EventHandler
    public void onVillagerAcquireTrade(VillagerAcquireTradeEvent event){
        List<MerchantRecipe> new_recipes = new ArrayList<>();
        for (MerchantRecipe recipe:
                event.getEntity().getRecipes()) {
            recipe.setMaxUses(Integer.MAX_VALUE);
            recipe.setUses(0);
            new_recipes.add(recipe);
        }

        event.getEntity().setRecipes(new_recipes);
    }

    @EventHandler
    public void onVillagerClick(PlayerInteractAtEntityEvent event){
        if(event.getRightClicked().getType() == EntityType.VILLAGER){
            Villager villager = (Villager) event.getRightClicked();

            if(villager.getProfession() != Villager.Profession.NITWIT || villager.getProfession() != Villager.Profession.NONE){
                List<MerchantRecipe> new_recipes = new ArrayList<>();
                for (MerchantRecipe recipe:
                        villager.getRecipes()) {
                    recipe.setMaxUses(Integer.MAX_VALUE);
                    recipe.setUses(0);
                    new_recipes.add(recipe);
                }

                villager.setRecipes(new_recipes);
            }
        }
    }
}
