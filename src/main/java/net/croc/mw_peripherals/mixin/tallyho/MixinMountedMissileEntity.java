package net.croc.mw_peripherals.mixin.tallyho;

import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import edn.stratodonut.tallyho.entity.MountedMissileEntity;
import net.croc.mw_peripherals.RegistryParticles;
import net.croc.mw_peripherals.utils.VSUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

@Mixin(MountedMissileEntity.class)
public abstract class MixinMountedMissileEntity {

    @Invoker(value = "detonate", remap = false)
    public abstract void Detonate(Vec3 hit);

    @Unique
    private static double scalar(Vec3 a, Vec3 b) {
        double dot = a.dot(b);
        double len = b.length();
        return len == 0 ? 0 : dot / len;
    }

    @Unique
    private static boolean isIncoming(Vec3 to, Entity projectile) {
        Vec3 from = projectile.getEyePosition();
        Vec3 dir = to.subtract(from);
        Vec3 vel = projectile.getDeltaMovement();

        return scalar(vel, dir) > 0;
    }

    @Unique
    private boolean collide(MountedMissileEntity missile) {
        Vec3 from = missile.position();
        Vec3 to = from.add(missile.getDeltaMovement());

        for (AbstractContraptionEntity contraption : missile.level().getEntitiesOfClass(
                AbstractContraptionEntity.class,
                missile.getBoundingBox().inflate(missile.getDeltaMovement().length()+1))) {

            Vec3 pos = contraption.position();
            Ship s = VSGameUtilsKt.getShipMountedTo(contraption);

            if (s != null) {
                pos = VSUtils.toWorldPosition(s, pos);
            }

            if (isIncoming(pos, missile) && contraption.getBoundingBox().clip(from, to).isPresent()) {
                return true;
            }
        }

        return false;
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void impactOnContraptionsAndParticles(CallbackInfo ci) {
        MountedMissileEntity missile = (MountedMissileEntity) (Object) this;

        if (!missile.level().isClientSide && !missile.isPassenger() && missile.getTicksSinceLaunch() > 10 ) {
            if (collide(missile)) {
                this.Detonate(missile.getEyePosition().add(missile.getLookAngle()));
            }
        }

        if (missile.level().isClientSide && missile.level() instanceof ServerLevel level &&
                missile.isDeployed() && missile.getMotor() != null && missile.getTicksSinceLaunch() < 60) {
            Vec3 pos = missile.position();
            Vec3 vel = missile.getDeltaMovement();
            /*level.sendParticles(RegistryParticles.MISSILE_CORE_PARTICLE.get(), true,
                    pos.x, pos.y, pos.z,
                    vel.x, vel.y, vel.z
            );*/
        }
    }
}