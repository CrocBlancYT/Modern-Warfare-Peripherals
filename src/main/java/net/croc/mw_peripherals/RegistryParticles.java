package net.croc.mw_peripherals;

import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class RegistryParticles {
    public static final RegistryObject<SimpleParticleType> MISSILE_CORE_PARTICLE =
            Main.PARTICLES.register("missile_core_particle",
                    () -> new SimpleParticleType(false)
            );

    public static void register() {}
}