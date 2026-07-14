package net.croc.mw_peripherals.blocks.aps;

import net.croc.mw_peripherals.RegistryConfigs;
import net.croc.mw_peripherals.RegistryTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

import org.joml.Vector3d;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import rbasamoyai.createbigcannons.munitions.big_cannon.AbstractBigCannonProjectile;
import rbasamoyai.createbigcannons.munitions.big_cannon.BigCannonProjectileBlockEntity;
import rbasamoyai.createbigcannons.munitions.big_cannon.FuzedBigCannonProjectile;

public class APSIntercept {
    private static double getCounterForce() { return RegistryConfigs.Config.APS_COUNTER_FORCE.get(); }
    private static double getCounterMultiplier() { return RegistryConfigs.Config.APS_COUNTER_MULTIPLIER.get(); }

    private static String getProjectileType(Entity projectile) {
        if (RegistryTags.isKinetic(projectile)) return "kinetic";
        if (RegistryTags.isChemical(projectile)) return "chemical";
        return "none";
    }

    private static boolean isActiveProjectile(Entity projectile) {
        return !getProjectileType(projectile).equals("none");
    }

    public static List<Entity> detectProjectiles(BlockEntity APS, int range) {
        List<Entity> viableEntities = new java.util.ArrayList<>();

        Level level = APS.getLevel();
        if (level == null) return viableEntities;

        AABB box = new AABB(APS.getBlockPos()).inflate(range);
        List<Entity> entities = APS.getLevel().getEntities(null, box);

        entities.forEach((Entity projectile) -> {
            if (isActiveProjectile(projectile)) {
                viableEntities.add(projectile);
            }
        });

        return viableEntities;
    }

    private static Vec3 getDeviationImpulse(Vec3 from, Vec3 to, Vec3 vel) {
        Vec3 slownBy = vel.scale(getCounterMultiplier());
        Vec3 punch = to.subtract(from).normalize().scale(getCounterForce() / 20);
        return punch.subtract(slownBy);
    }

    public static double scalar(Vec3 a, Vec3 b) {
        double dot = a.dot(b);
        double len = b.length();
        return len == 0 ? 0 : dot / len;
    }

    private static boolean isIncoming(Vec3 to, Entity projectile) {
        Vec3 from = projectile.getEyePosition();
        Vec3 dir = to.subtract(from);
        Vec3 vel = projectile.getDeltaMovement();

        return scalar(vel, dir) > 0;
    }

    private static boolean isDeployed(Entity missile) {
        try { return (Boolean) missile.getClass().getMethod("isDeployed").invoke(missile);
        } catch (Exception e) { return false; }
    }

    public static boolean tryKillProjectile(BlockEntity APS, Entity projectile) {
        Vec3 pos = APS.getBlockPos().getCenter();

        Ship ship = VSGameUtilsKt.getShipManagingPos(APS.getLevel(), APS.getBlockPos());
        if (ship != null) {
            Vector3d p = ship.getShipToWorld().transformPosition(new Vector3d(pos.x(), pos.y(), pos.z()));
            pos = new Vec3(p.x, p.y, p.z);
        };

        if (!isIncoming(pos, projectile)) return false;

        if (projectile.getType().toString().equals("tallyho:missile") && !isDeployed(projectile)) {
            return false;
        }

        String type = getProjectileType(projectile);
        if (type.equals("chemical")) {
            APSEffects.ChemicalExplosion(APS, projectile);
            
            if (projectile instanceof FuzedBigCannonProjectile proj) {
                proj.setExplosionCountdown(0);
            } else {
                projectile.kill();
            }

            return true;
        } else if (type.equals("kinetic")) {
            APSEffects.KineticExplosion(APS, projectile);
            Vec3 impulse = getDeviationImpulse(pos, projectile.getEyePosition(), projectile.getDeltaMovement());
            projectile.setDeltaMovement(projectile.getDeltaMovement().add(impulse));
            return true;
        }

        return false;
    }
}