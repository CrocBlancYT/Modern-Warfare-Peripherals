package net.croc.mw_peripherals.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;

public class MissileCoreParticle extends TextureSheetParticle {
    public MissileCoreParticle(ClientLevel level, double x, double y, double z) {
        super(level, x, y, z);
        this.lifetime = 1;
        this.hasPhysics = false;
        this.scale(8);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    public static ParticleProvider<SimpleParticleType> createNewProvider() {
        return new ParticleProvider<>() {
            @Override
            public Particle createParticle(SimpleParticleType type, ClientLevel level,
                                           double x, double y, double z,
                                           double xSpeed, double ySpeed, double zSpeed) {
                return new MissileCoreParticle(level, x, y, z);
            }
        };
    }
}