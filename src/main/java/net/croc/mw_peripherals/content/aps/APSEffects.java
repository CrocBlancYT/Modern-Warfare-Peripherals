package net.croc.mw_peripherals.content.aps;

import net.croc.mw_peripherals.network.APSEffectsPacketClient;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import rbasamoyai.createbigcannons.index.CBCSoundEvents;
import rbasamoyai.createbigcannons.utils.CBCUtils;

import java.util.List;

public class APSEffects {
    private static double offsetAngle = 0.349; // +-20°

    public static void fragmentParticles(ClientLevel level, Vec3 pos, Vec3 vel) {
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

            level.addParticle(ParticleTypes.SMOKE, true,
                    pos.x(), pos.y(), pos.z(),
                    pVel.x(), pVel.y(), pVel.z()
            );
        }
    }

    public static void explosionParticles(ClientLevel level, Vec3 pos) {
        double x = pos.x();
        double y = pos.y();
        double z = pos.z();

        for (int i = 0; i < 50; i++) {
            level.addParticle(ParticleTypes.EXPLOSION, true,
                    x + (Math.random() - 0.5) * 2.0,
                    y + (Math.random() - 0.5) * 2.0,
                    z + (Math.random() - 0.5) * 2.0,
                    (Math.random() - 0.5) * 0.5,
                    (Math.random() - 0.5) * 0.5,
                    (Math.random() - 0.5) * 0.5
                    );

            level.addParticle(ParticleTypes.EXPLOSION, true,
                    x + (Math.random() - 0.5) * 1.5,
                    y + (Math.random() - 0.5) * 1.5,
                    z + (Math.random() - 0.5) * 1.5,
                    (Math.random() - 0.5) * 0.8,
                    Math.random() * 0.8,
                    (Math.random() - 0.5) * 0.8
                    );

            level.addParticle(ParticleTypes.SMOKE, true,
                    x + (Math.random() - 0.5) * 1.2,
                    y + (Math.random() - 0.5) * 1.2,
                    z + (Math.random() - 0.5) * 1.2,
                    (Math.random() - 0.5) * 0.3,
                    Math.random() * 0.4,
                    (Math.random() - 0.5) * 0.3
                    );
        }

        for (int i = 0; i < 20; i++) {
            level.addParticle(ParticleTypes.POOF, true,
                    x, y, z,
                    (Math.random() - 0.5) * 1.1,
                    (Math.random() - 0.5) * 1.1,
                    (Math.random() - 0.5) * 1.1
                    );
        }
    }

    private static final int effectsDistance = 100;

    private static List<ServerPlayer> getServerPlayersInRange(ServerLevel level, BlockPos center) {
        AABB area = new AABB(center).inflate(effectsDistance);
        return level.getPlayers(player -> area.contains(player.position()));
    }

    public static void serverExplosionSound(ServerLevel serverLevel, Vec3 spawnPos) {
        CBCUtils.playBlastLikeSoundOnServer(serverLevel,
                spawnPos.x, spawnPos.y, spawnPos.z,
                CBCSoundEvents.SHELL_EXPLOSION.getMainEvent(),
                SoundSource.BLOCKS, 12.0F, 1.0F, 5.0F);
    }

    public static void KineticExplosion(BlockEntity APS, Entity projectile) {
        Level level = APS.getLevel();
        if (level == null) return;
        if (level.isClientSide()) return;

        serverExplosionSound((ServerLevel) level, projectile.getEyePosition());

        for (ServerPlayer player : getServerPlayersInRange((ServerLevel) level, APS.getBlockPos())) {
            APSEffectsPacketClient.sendExplosion(player, projectile.getEyePosition());
        }
    }

    public static void ChemicalExplosion(BlockEntity APS, Entity projectile) {
        Level level = APS.getLevel();
        if (level == null) return;
        if (level.isClientSide()) return;

        serverExplosionSound((ServerLevel) level, projectile.getEyePosition());

        for (ServerPlayer player : getServerPlayersInRange((ServerLevel) level, APS.getBlockPos())) {
            APSEffectsPacketClient.sendFragments(player, projectile.getEyePosition(), projectile.getDeltaMovement());
            APSEffectsPacketClient.sendExplosion(player, projectile.getEyePosition());
        }
    }
}