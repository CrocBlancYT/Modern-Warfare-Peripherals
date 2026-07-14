package net.croc.mw_peripherals.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

public class VSUtils {
    public static Vec3 toWorldPosition(Level level, Vec3 pos) {
        return VSGameUtilsKt.toWorldCoordinates(level, pos);
    }

    public static Vec3 toWorldPosition(Ship ship, Vec3 pos) {
        Vector3d position = new Vector3d(pos.x, pos.y, pos.z);
        ship.getShipToWorld().transformPosition(position);
        return new Vec3(position.x, position.y, position.z);
    }

    public static Vec3 toWorldPosition(Level level, BlockPos pos) {
        return VSGameUtilsKt.toWorldCoordinates(level, pos.getCenter());
    }

    public static Vec3 toWorldPosition(Ship ship, BlockPos pos) {
        return toWorldPosition(ship, pos.getCenter());
    }

    public static Vec3 toShipPosition(Ship ship, Vec3 pos) {
        Vector3d position = new Vector3d(pos.x, pos.y, pos.z);
        ship.getWorldToShip().transformPosition(position);
        return new Vec3(position.x, position.y, position.z);
    }

    public static Vec3 toWorldDirection(Ship ship, Vec3 dir) {
        Vector3d dirInShip = new Vector3d(dir.x, dir.y, dir.z);
        Vector3d dirInWorld = ship.getShipToWorld().transformDirection(dirInShip);
        return new Vec3(dirInWorld.x, dirInWorld.y, dirInWorld.z);
    }

    public static Vec3 toWorldDirection(Level level, Vec3 dir, Vec3 at) {
        Ship ship = VSGameUtilsKt.getShipManagingPos(level, new BlockPos((int) at.x, (int) at.y, (int) at.z));
        if (ship == null) return dir;
        return toWorldDirection(ship, dir);
    }

    public static Vec3 toWorldDirection(Level level, Vec3 dir, BlockPos at) {
        return toWorldDirection(level, dir, at.getCenter());
    }
}