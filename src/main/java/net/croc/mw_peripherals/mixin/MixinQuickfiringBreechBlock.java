package net.croc.mw_peripherals.mixin;

import net.croc.mw_peripherals.RegistrySounds;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import rbasamoyai.createbigcannons.cannons.big_cannons.breeches.quickfiring_breech.QuickfiringBreechBlock;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import rbasamoyai.createbigcannons.cannons.big_cannons.breeches.sliding_breech.SlidingBreechBlock;
import rbasamoyai.createbigcannons.cannons.big_cannons.breeches.sliding_breech.SlidingBreechBlockGen;
import rbasamoyai.createbigcannons.cannons.big_cannons.breeches.sliding_breech.SlidingBreechCTBehavior;
import rbasamoyai.createbigcannons.cannons.big_cannons.breeches.sliding_breech.SlidingBreechInstance;
import rbasamoyai.createbigcannons.cannons.big_cannons.breeches.sliding_breech.forge.SlidingBreechBlockGenImpl;
import riftyboi.cbcmodernwarfare.cannons.medium_cannon.breech.MediumcannonBreechBlock;

@Mixin({QuickfiringBreechBlock.class, MediumcannonBreechBlock.class})
public class MixinQuickfiringBreechBlock {
    @Redirect(
            method = "onInteractWhileAssembled",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;" +
                            "playSound(" +
                            "Lnet/minecraft/world/entity/player/Player;" +
                            "Lnet/minecraft/core/BlockPos;" +
                            "Lnet/minecraft/sounds/SoundEvent;" +
                            "Lnet/minecraft/sounds/SoundSource;" +
                            "FF)" +
                            "V"
            )
    )
    private void redirectPlaySound(
            Level level,
            Player player,
            BlockPos pos,
            SoundEvent sound,
            SoundSource category,
            float volume,
            float pitch
    ) {
        if (sound.getLocation().equals(SoundEvents.IRON_TRAPDOOR_OPEN.getLocation())) {
            level.playSound(player, pos, RegistrySounds.BREECH_OPEN.getMainEvent(), category, volume, pitch);
        } else if (sound.getLocation().equals(SoundEvents.IRON_TRAPDOOR_CLOSE.getLocation())) {
            level.playSound(player, pos, RegistrySounds.BREECH_CLOSE.getMainEvent(), category, volume, pitch);
        } else {
            level.playSound(player, pos, sound, category, volume, pitch);
        }
    }
}