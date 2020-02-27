package com.github.evillootlye.caves.mobs;

import com.destroystokyo.paper.event.entity.PreCreatureSpawnEvent;
import com.github.evillootlye.caves.Dynamics;
import com.github.evillootlye.caves.configuration.Configurable;
import com.github.evillootlye.caves.configuration.Configuration;
import com.github.evillootlye.caves.utils.Rnd;
import com.github.evillootlye.caves.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Configurable.Path("mobs")
public class MobsManager implements Listener, Dynamics.Tickable, Configurable {
    private final Plugin plugin;
    private final Configuration cfg;
    private final Map<String, CustomMob> mobs;
    private final List<CustomMob> mobsList;
    private final Set<String> mobsTicked;
    private final Set<String> worlds;
    private int yMin, yMax;
    private double chance;

    public MobsManager(Plugin plugin, Configuration cfg) {
        try {
            Class.forName("com.destroystokyo.paper.PaperConfig");
            Bukkit.getPluginManager().registerEvents(new PaperListener(), plugin);
        } catch (ClassNotFoundException e) {
            Bukkit.getPluginManager().registerEvents(new SpigotListener(), plugin);
        }
        this.plugin = plugin;
        this.cfg = cfg;
        cfg.register(this);
        mobs = new HashMap<>();
        mobsList = new ArrayList<>();
        mobsTicked = new HashSet<>();
        worlds = new HashSet<>();
    }

    @Override
    public void reload(ConfigurationSection cfg) {
        chance = cfg.getDouble("try-chance", 50)/100;
        yMin = cfg.getInt("y-min", 0);
        yMax = cfg.getInt("y-max", 64);
        worlds.clear();
        Utils.fillWorlds(cfg.getStringList("worlds"), worlds);
    }

    public void register(CustomMob mob) {
        if(!mobs.containsKey(mob.getId())) {
            mobs.put(mob.getId(), mob);
            mobsList.add(mob);
            if(mob instanceof CustomMob.Tickable)
                mobsTicked.add(mob.getId());
            if(mob instanceof Listener)
                Bukkit.getPluginManager().registerEvents((Listener)mob, plugin);
            if(mob instanceof Configurable)
                cfg.register((Configurable)mob);
        }
    }

    @Override
    public void tick() {
        for(String worldStr : worlds) {
            World world = Bukkit.getWorld(worldStr);
            if(world == null) continue;
            for(Entity entity : world.getEntities()) {
                String type = CustomMob.getCustomType(entity);
                if(type == null) continue;
                if(mobsTicked.contains(type))
                    ((CustomMob.Tickable)mobs.get(type)).tick(entity);
            }
        }
    }

    @Override
    public Dynamics.TickLevel getTickLevel() {
        return Dynamics.TickLevel.ENTITY;
    }

    private boolean onSpawn(EntityType type, Location loc, CreatureSpawnEvent.SpawnReason reason) {
        if(chance <= 0 || reason != CreatureSpawnEvent.SpawnReason.NATURAL ||
                loc.getBlockY() > yMax || loc.getBlockY() < yMin ||
                !worlds.contains(loc.getWorld().getName()) ||
                Rnd.nextDouble() < chance)
            return false;
        CustomMob mob = mobsList.get(Rnd.nextInt(mobsList.size()));
        if(mob.canSpawn(type, loc)) {
            mob.spawn(loc);
            return true;
        }
        return false;
    }

    private class PaperListener implements Listener {
        @EventHandler
        public void onSpawn(PreCreatureSpawnEvent event) {
            if(MobsManager.this.onSpawn(event.getType(), event.getSpawnLocation(), event.getReason()))
                event.setCancelled(true);
        }
    }

    private class SpigotListener implements Listener {
        @EventHandler
        public void onSpawn(CreatureSpawnEvent event) {
            if(MobsManager.this.onSpawn(event.getEntityType(), event.getLocation(), event.getSpawnReason()))
                event.setCancelled(true);
        }
    }
}