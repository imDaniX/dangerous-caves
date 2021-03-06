package me.imdanix.caves.util;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

import java.util.function.Consumer;

public final class Locations {
    public static final BlockFace[] HORIZONTAL_FACES = {BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST};
    public static final BlockFace[] FULL_FACES = {BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST, BlockFace.UP, BlockFace.DOWN};

    /**
     * Creates new Location instance and adds coordinates
     * @param loc Initial location
     * @param x X to add
     * @param y Y to add
     * @param z Z to add
     * @return Edited Location instance
     */
    public static Location add(Location loc, double x, double y, double z) {
        return new Location(loc.getWorld(), loc.getX() + x, loc.getY() + y, loc.getZ() + z);
    }

    /**
     * Creates new Location instance and subtracts coordinates
     * @param loc Initial location
     * @param x X to subtract
     * @param y Y to subtract
     * @param z Z to subreact
     * @return Edited Location instance
     */
    public static Location subtract(Location loc, double x, double y, double z) {
        return new Location(loc.getWorld(), loc.getX() - x, loc.getY() - y, loc.getZ() - z);
    }

    public static void loop(int radius, Location start, Consumer<Location> loop) {
        World world = start.getWorld();
        for (int x = start.getBlockX() - radius; x <= start.getBlockX() + radius; x++)
        for (int y = start.getBlockY() - radius; y <= start.getBlockY() + radius; y++)
        for (int z = start.getBlockZ() - radius; z <= start.getBlockZ() + radius; z++)
            loop.accept(new Location(world, x, y, z));
    }

    public static void loop(int radius, Location start, LocationConsumer loop) {
        World world = start.getWorld();
        for (int x = start.getBlockX() - radius; x <= start.getBlockX() + radius; x++)
        for (int y = start.getBlockY() - radius; y <= start.getBlockY() + radius; y++)
        for (int z = start.getBlockZ() - radius; z <= start.getBlockZ() + radius; z++)
            loop.accept(world, x, y, z);
    }

    public static boolean isCave(Location loc) {
        Block block = loc.getBlock();
        Material lowerType;
        return block.getLightFromSky() <= 1 && (
                Materials.isCave(lowerType = block.getRelative(BlockFace.DOWN).getType()) ||
                Materials.isAir(lowerType));

    }

    public static boolean isLookingAt(LivingEntity viewer, LivingEntity target) {
        Location eye = viewer.getEyeLocation();
        Vector toEntity = target.getEyeLocation().toVector().subtract(eye.toVector());
        return toEntity.normalize().dot(eye.getDirection()) > 0.70D;
    }

    public static void playSound(Location loc, Sound sound, SoundCategory category, float volume, float pitch) {
        loc.getWorld().playSound(loc, sound, category, volume, pitch);
    }

    public static void playSound(Location loc, Sound sound, float volume, float pitch) {
        loc.getWorld().playSound(loc, sound, volume, pitch);
    }

    @FunctionalInterface
    public interface LocationConsumer {
        void accept(World w, int x, int y, int z);
    }
}
