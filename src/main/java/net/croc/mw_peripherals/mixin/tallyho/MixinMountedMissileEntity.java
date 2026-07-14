package net.croc.mw_peripherals.mixin.tallyho;

import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import edn.stratodonut.tallyho.entity.MountedMissileEntity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MountedMissileEntity.class)
public abstract class MixinMountedMissileEntity {

    @Invoker(value = "detonate", remap = false)
    public abstract void Detonate(Vec3 hit);

    @Inject(method = "tick", at = @At("HEAD"))
    private void impactOnContraptions(CallbackInfo ci) {
        MountedMissileEntity missile = (MountedMissileEntity) (Object) this;

        if (!missile.level().isClientSide() && !missile.isPassenger() && missile.getTicksSinceLaunch() > 50 ) {

            boolean noContraption = missile.level().getEntitiesOfClass(
                    AbstractContraptionEntity.class,
                    missile.getBoundingBox().inflate(1D)
            ).isEmpty();

            if (!noContraption) {
                this.Detonate(missile.getEyePosition().add(missile.getLookAngle()));
            }
        }
    }
}