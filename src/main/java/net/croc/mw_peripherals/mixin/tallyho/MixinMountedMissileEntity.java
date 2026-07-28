package net.croc.mw_peripherals.mixin.tallyho;

import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import edn.stratodonut.tallyho.entity.MountedMissileEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

@Mixin(MountedMissileEntity.class)
public abstract class MixinMountedMissileEntity {

    @Invoker(value = "detonate", remap = false)
    public abstract void Detonate(Vec3 hit);

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

    private boolean collide(MountedMissileEntity missile) {
        Vec3 from = missile.position();
        Vec3 to = from.add(missile.getDeltaMovement());

        for (AbstractContraptionEntity contraption : missile.level().getEntitiesOfClass(
                AbstractContraptionEntity.class,
                missile.getBoundingBox().inflate(missile.getDeltaMovement().length()))) {

            if (isIncoming(contraption.position(), missile) && contraption.getBoundingBox().clip(from, to).isPresent()) {
                return true;
            }
        }

        return false;
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void impactOnContraptions(CallbackInfo ci) {
        MountedMissileEntity missile = (MountedMissileEntity) (Object) this;

        if (!missile.level().isClientSide() && !missile.isPassenger() && missile.getTicksSinceLaunch() > 50 ) {

            boolean noContraption = missile.level().getEntitiesOfClass(
                    AbstractContraptionEntity.class,
                    missile.getBoundingBox().inflate(1D)
            ).isEmpty();

            // TO TEST
            /*if (collide(missile)) {
                this.Detonate(missile.getEyePosition().add(missile.getLookAngle()));
            }*/


            if (!noContraption) {
                this.Detonate(missile.getEyePosition().add(missile.getLookAngle()));
            }
        }
    }
}