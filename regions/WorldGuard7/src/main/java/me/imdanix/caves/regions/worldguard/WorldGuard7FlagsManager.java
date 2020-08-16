/*
 * Dangerous Caves 2 | Make your caves scary
 * Copyright (C) 2020  imDaniX
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package me.imdanix.caves.regions.worldguard;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import me.imdanix.caves.regions.CheckType;
import me.imdanix.caves.regions.RegionManager;
import org.bukkit.Location;

public class WorldGuard7FlagsManager implements RegionManager {
    private static final StateFlag ENTITY_FLAG = new StateFlag("dc-entity-grief", true);
    private static final StateFlag BLOCK_FLAG = new StateFlag("dc-block-change", true);
    private static final StateFlag EFFECT_FLAG = new StateFlag("dc-player-effect", true);

    public WorldGuard7FlagsManager() {
        try {
            FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
            registry.register(ENTITY_FLAG);
            registry.register(BLOCK_FLAG);
            registry.register(EFFECT_FLAG);
        } catch (FlagConflictException ignored) {}
    }

    @Override
    public boolean test(CheckType type, Location location) {
        ApplicableRegionSet set = getContainer().createQuery().getApplicableRegions(BukkitAdapter.adapt(location));
        switch (type) {
            case ENTITY:
                return !set.testState(null, ENTITY_FLAG);
            case BLOCK:
                return !set.testState(null, BLOCK_FLAG);
            case EFFECT:
                return !set.testState(null, EFFECT_FLAG);
        }
        return true;
    }

    private RegionContainer getContainer() {
        return WorldGuard.getInstance().getPlatform().getRegionContainer();
    }
}
