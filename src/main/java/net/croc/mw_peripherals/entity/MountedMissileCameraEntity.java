package net.croc.mw_peripherals.entity;

import edn.stratodonut.tallyho.camera.AngleLimits;
import edn.stratodonut.tallyho.camera.entity.TargetingPodEntity;
import edn.stratodonut.tallyho.entity.MountedMissileEntity;
import net.croc.mw_peripherals.RegistryEntities;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class MountedMissileCameraEntity extends TargetingPodEntity {
    public static int FOV = 25;

    public MountedMissileCameraEntity(EntityType<? extends Entity> type, Level level) {
        super(type, level);
        this.setInvulnerable(true);
        this.setInvisible(true);
        this.setNoGravity(true);
    }

    public MountedMissileCameraEntity(MountedMissileEntity missile) {
        this(RegistryEntities.MOUNTED_TV_CAMERA.get(), missile.level());
        this.startRiding(missile);
        this.setParams(missile.getYRot(), PostFXType.THERMALS_WHITE, new AngleLimits(44.0F, 44.0F, 44.0F), FOV);
        this.setYRot(missile.getYRot());
        this.setXRot(missile.getXRot());
        missile.level().addFreshEntity(this);
    }

    public static MountedMissileCameraEntity getOrCreateCamera(MountedMissileEntity missile) {
        if (missile.getFirstPassenger() instanceof MountedMissileCameraEntity camera && camera.isAlive()) {
            return camera;
        }

        return new MountedMissileCameraEntity(missile);
    }

    @Override
    public float getBaseYaw() {
        if (this.getVehicle() == null) return super.getBaseYaw();
        return this.getVehicle().getYRot();
    }

    @Override
    public void rideTick() {
        this.setDeltaMovement(Vec3.ZERO);
        if (this.canUpdate()) {
            this.tick();
        }

        Entity vehicle = this.getVehicle();
        if (this.isPassenger() && vehicle != null) {
            Vec3 offset = vehicle.getLookAngle().scale(2.5);

            this.setPos(
                    vehicle.getX() + offset.x,
                    vehicle.getY() + offset.y + vehicle.getPassengersRidingOffset() + this.getMyRidingOffset(),
                    vehicle.getZ() + offset.z
            );

        }
    }

    @Override
    public void tick() {
        super.tick();

        if (getVehicle() instanceof MountedMissileEntity missile && missile.isAlive()) {
            float xRot = missile.getXRot();
            float yRot = missile.getYRot();
            this.setParams(yRot, PostFXType.THERMALS_WHITE, new AngleLimits(Math.min(44.0F + xRot, 90.0F), Math.min(44.0F - xRot, 90.0F), 44.0F), FOV);
        }

        if (getVehicle() == null || getVehicle().isRemoved()) {
            ServerPlayer viewer = this.currentlyViewing.get();
            if (viewer != null) this.stopViewing(viewer);
            this.discard();
        }
    }
}