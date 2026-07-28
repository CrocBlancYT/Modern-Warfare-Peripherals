package net.croc.mw_peripherals.mixin.vpb;

import com.vicmatskiv.pointblank.explosion.CustomExplosion;
import edn.stratodonut.tallyho.missile.warhead.HighExplosiveWarhead;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(value = CustomExplosion.class)
public class MixinCustomExplosion {
    @Inject(method = "explode", at = @At("RETURN"))
    private static void explode(Level level, Item item,
                               @Nullable Entity entity, @Nullable DamageSource damageSource,
                               @Nullable ExplosionDamageCalculator calc,
                               double posX, double posY, double posZ,
                               float power, boolean fire, Level.ExplosionInteraction interaction,
                               boolean particlesEnabled,
                                CallbackInfoReturnable<CustomExplosion> cir) {

        HighExplosiveWarhead warhead = new HighExplosiveWarhead(power);
        warhead.detonate(level, new Vec3(posX, posY, posZ), null);
    }
}