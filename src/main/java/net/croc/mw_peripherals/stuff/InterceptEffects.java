package net.croc.mw_peripherals.stuff;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class InterceptEffects {
    private static double offsetAngle = 0.349; // +-20°

    private static void fragmentParticles(ServerLevel level, ServerPlayer player, Vec3 pos, Vec3 vel) {
        BlockPos blockPos = new BlockPos((int) pos.x(), (int) pos.y(), (int) pos.z());
        level.playSound(player, blockPos, SoundEvents.GENERIC_EXPLODE, SoundSource.HOSTILE, 1.2f, 0.7f);

        Vec3 norm = vel.normalize();
        double pitch = Math.asin(norm.y());
        double yaw = Math.atan2(norm.z(), norm.x());

        double particleSpeed = vel.length() * 3;
        for (int i = 0; i < 10; i++) {
            Vec3 pVel = new Vec3(
                    Math.cos(yaw   + (Math.random()-0.5)*offsetAngle),
                    Math.sin(pitch + (Math.random()-0.5)*offsetAngle),
                    Math.sin(yaw   + (Math.random()-0.5)*offsetAngle)
            ).scale(particleSpeed);

            level.sendParticles(player, ParticleTypes.SMOKE, true,
                    pos.x(), pos.y(), pos.z(),
                    1,
                    pVel.x(), pVel.y(), pVel.z(),
                    0.2
            );
        }
    }

    private static void explosionParticles(ServerLevel level, ServerPlayer player, Vec3 pos) {
        BlockPos blockPos = new BlockPos((int) pos.x(), (int) pos.y(), (int) pos.z());
        level.playSound(player, blockPos, SoundEvents.GENERIC_EXPLODE, SoundSource.HOSTILE, 1.2f, 0.8f);

        double x = pos.x();
        double y = pos.y();
        double z = pos.z();

        double speed = 0.3;

        for (int i = 0; i < 50; i++) {
            level.sendParticles(player, ParticleTypes.EXPLOSION, true,
                    x + (Math.random() - 0.5) * 2.0,
                    y + (Math.random() - 0.5) * 2.0,
                    z + (Math.random() - 0.5) * 2.0,
                    1,
                    (Math.random() - 0.5) * 0.5,
                    (Math.random() - 0.5) * 0.5,
                    (Math.random() - 0.5) * 0.5,
                    speed);

            level.sendParticles(player, ParticleTypes.EXPLOSION, true,
                    x + (Math.random() - 0.5) * 1.5,
                    y + (Math.random() - 0.5) * 1.5,
                    z + (Math.random() - 0.5) * 1.5,
                    1,
                    (Math.random() - 0.5) * 0.8,
                    Math.random() * 0.8,
                    (Math.random() - 0.5) * 0.8,
                    speed);

            level.sendParticles(player, ParticleTypes.SMOKE, true,
                    x + (Math.random() - 0.5) * 1.2,
                    y + (Math.random() - 0.5) * 1.2,
                    z + (Math.random() - 0.5) * 1.2,
                    1,
                    (Math.random() - 0.5) * 0.3,
                    Math.random() * 0.4,
                    (Math.random() - 0.5) * 0.3,
                    speed);
        }

        for (int i = 0; i < 20; i++) {
            level.sendParticles(player, ParticleTypes.POOF, true,
                    x, y, z,
                    1,
                    (Math.random() - 0.5) * 1.1,
                    (Math.random() - 0.5) * 1.1,
                    (Math.random() - 0.5) * 1.1,
                    speed);
        }
    }

    private static final int effectsDistance = 100;

    private static List<ServerPlayer> getServerPlayersInRange(ServerLevel level, BlockPos center, double radius) {
        AABB area = new AABB(center).inflate(radius);
        return level.getPlayers(player -> area.contains(player.position()));
    }


    public static void KineticExplosion(BlockEntity APS, Entity projectile) {
        Level level = APS.getLevel();
        if (level == null) return;
        if (level.isClientSide()) return;

        List<ServerPlayer> players = getServerPlayersInRange((ServerLevel) level, APS.getBlockPos(), effectsDistance);

        players.forEach((player) -> {
            explosionParticles((ServerLevel) level, player, projectile.getEyePosition());
        });
    }

    public static void ChemicalExplosion(BlockEntity APS, Entity projectile) {
        Level level = APS.getLevel();
        if (level == null) return;
        if (level.isClientSide()) return;

        List<ServerPlayer> players = getServerPlayersInRange((ServerLevel) level, APS.getBlockPos(), effectsDistance);

        players.forEach((player) -> {
            fragmentParticles((ServerLevel) level, player, projectile.getEyePosition(), projectile.getDeltaMovement());
            explosionParticles((ServerLevel) level, player, projectile.getEyePosition());
        });
    }
}