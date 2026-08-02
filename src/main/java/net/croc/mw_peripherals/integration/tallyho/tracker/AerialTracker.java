package net.croc.mw_peripherals.integration.tallyho.tracker;

import edn.stratodonut.tallyho.entity.MountedMissileEntity;
import edn.stratodonut.tallyho.missile.Target;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.Vector3f;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.common.util.VectorConversionsMCKt;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static edn.stratodonut.tallyho.missile.GuidanceComponent.tryLock;

public class AerialTracker {
    private static final HashMap<Long, Boolean> cache = new HashMap<>();

    public static boolean isAirborne(Ship ship) {
        if (cache.get(ship.getId()) != null) { return true; }
        return false;
    }

    public static boolean isAirborne(Target<?> target) {
        if (target.get() instanceof Ship ship) {
            return isAirborne(ship);
        }

        if (target.get() instanceof MountedMissileEntity missile && missile.isDeployed()) {
            return true;
        }

        return false;
    }

    public static void subscribe(Ship ship) {
        cache.put(ship.getId(), true);
    }

    public static boolean isAligned(MountedMissileEntity missile, Ship target, float rear_angle) {
        double maxRadAngle = Math.toRadians(rear_angle);

        Vector3f dir1 = missile.getDeltaMovement().toVector3f();
        Vector3f dir2 = target.getVelocity().get(new Vector3f());

        if (dir1.lengthSquared() < 2) return true;
        if (dir2.lengthSquared() < 2) return true;

        return dir1.angle(dir2) <= maxRadAngle;
    }

    @Nonnull
    public static Optional<Ship> tryLockAerial(Level level, Vec3 forward, Vec3 position, @Nullable AABB aabb, int fov, int range) {
        return tryLock(level, forward, position, aabb, fov, range, (Ship a, Ship b) -> {
            Vector3dc pos = VectorConversionsMCKt.toJOML(position);
            double a_dist = a.getTransform().getPositionInWorld().distance(pos);
            double a_size = a.getShipAABB() == null ? (double)1.0F : (double)1.0F / a.getShipAABB().extent(new Vector3d()).length();
            double b_dist = b.getTransform().getPositionInWorld().distance(pos);
            double b_size = b.getShipAABB() == null ? (double)1.0F : (double)1.0F / b.getShipAABB().extent(new Vector3d()).length();
            return (int)(a_dist * a_size - b_dist * b_size);
        }, AerialTracker::isAirborne);
    }

    @Nonnull
    public static Optional<Ship> tryLockAerialAligned(Level level, Vec3 forward, Vec3 position, @Nullable AABB aabb, int fov, int range, MountedMissileEntity missile, float rear_angle) {
        return tryLock(level, forward, position, aabb, fov, range, (Ship a, Ship b) -> {
            Vector3dc pos = VectorConversionsMCKt.toJOML(position);
            double a_dist = a.getTransform().getPositionInWorld().distance(pos);
            double a_size = a.getShipAABB() == null ? (double)1.0F : (double)1.0F / a.getShipAABB().extent(new Vector3d()).length();
            double b_dist = b.getTransform().getPositionInWorld().distance(pos);
            double b_size = b.getShipAABB() == null ? (double)1.0F : (double)1.0F / b.getShipAABB().extent(new Vector3d()).length();
            return (int)(a_dist * a_size - b_dist * b_size);
        }, (s) -> {
            return isAirborne(s) && isAligned(missile, s, rear_angle);
        } );
    }

    public static List<Target<?>> onlyAerial(List<Target<?>> targets) {
        return targets.stream().filter(AerialTracker::isAirborne).toList();
    }
}