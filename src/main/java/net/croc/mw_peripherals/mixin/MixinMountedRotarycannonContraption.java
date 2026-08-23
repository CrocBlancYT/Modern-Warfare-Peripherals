package net.croc.mw_peripherals.mixin;

import net.croc.mw_peripherals.RegistrySounds;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import rbasamoyai.createbigcannons.utils.CBCUtils;
import riftyboi.cbcmodernwarfare.cannon_control.contraption.MountedRotarycannonContraption;

@Mixin({MountedRotarycannonContraption.class})
public class MixinMountedRotarycannonContraption {
    @Redirect(
            method = "fireShot",
            at = @At(
                    value = "INVOKE",
                    target = "Lrbasamoyai/createbigcannons/utils/CBCUtils;" +
                            "playBlastLikeSoundOnServer(" +
                            "Lnet/minecraft/server/level/ServerLevel;" +
                            "DDD" +
                            "Lnet/minecraft/sounds/SoundEvent;" +
                            "Lnet/minecraft/sounds/SoundSource;" +
                            "FFF)" +
                            "V"
            ),
            remap = false
    )
    private void redirectPlayBlastSound(
            ServerLevel level,
            double x,
            double y,
            double z,
            SoundEvent sound,
            SoundSource source,
            float volume,
            float pitch,
            float range
    ) {
        CBCUtils.playBlastLikeSoundOnServer(
                level,
                x,
                y,
                z,
                RegistrySounds.FIRE_ROTARY.getMainEvent(),
                source,
                volume,
                pitch,
                range
        );
    }
}