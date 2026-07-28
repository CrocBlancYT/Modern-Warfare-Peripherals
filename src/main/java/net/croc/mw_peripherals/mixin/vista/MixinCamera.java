package net.croc.mw_peripherals.mixin.vista;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.croc.mw_peripherals.utils.IVSCamera;
import net.minecraft.client.Camera;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.valkyrienskies.core.api.ships.ClientShip;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

/* EXTRACTED FROM VS2 */
@Mixin(value = Camera.class, priority = 1100)
public abstract class MixinCamera implements IVSCamera {
    // region Shadow
    @Shadow
    private boolean initialized;
    @Shadow
    private BlockGetter level;
    @Shadow
    private Entity entity;
    @Shadow
    @Final
    private Vector3f forwards;
    @Shadow
    @Final
    private Vector3f up;
    @Shadow
    @Final
    private Vector3f left;
    @Shadow
    private float xRot;
    @Shadow
    private float yRot;

    @Unique
    private float vs$zRot;

    @Shadow
    @Final
    private Quaternionf rotation;
    @Shadow
    private boolean detached;
    @Shadow
    private float eyeHeight;
    @Shadow
    private float eyeHeightOld;
    @Shadow
    private Vec3 position;
    @Shadow
    private BlockPos.MutableBlockPos blockPosition;

    // endregion

    /**
     * @author Bunting_chj
     * @reason This Injection will modify the Camera position to transform the eye offset with the ship player is mounted on.
     *  Funny thing that original code doesn't utilize getEyePosition().
     */
    @WrapOperation(
        method = "setup",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Camera;setPosition(DDD)V")
    )
    private void setPosition(Camera camera, double d, double e, double f, Operation<Void> original){
        if(VSGameUtilsKt.getShipMountedTo(this.entity) instanceof ClientShip ship) {
            double eyeHeight = Mth.lerp(f, this.eyeHeightOld, this.eyeHeight);

            Vector3d eyeOffset = ship.getRenderTransform().getShipToWorldRotation().transform(new Vector3d(0, eyeHeight, 0));
            original.call(camera, d + eyeOffset.x, e - eyeHeight + eyeOffset.y, f + eyeOffset.z);
        }
        else original.call(camera, d, e, f);
    }

    @Override
    public void setRotationVS(float yaw, float pitch, float roll) {
        this.xRot = pitch;
        this.yRot = yaw;
        this.vs$zRot = roll;
        this.rotation.rotationYXZ(-yaw * ((float)Math.PI / 180), pitch * ((float)Math.PI / 180), roll * ((float)Math.PI / 180));
        this.forwards.set(0.0f, 0.0f, 1.0f).rotate(this.rotation);
        this.up.set(0.0f, 1.0f, 0.0f).rotate(this.rotation);
        this.left.set(1.0f, 0.0f, 0.0f).rotate(this.rotation);
    }

    @Override
    public void setPositionVS(Vec3 position) {
        this.position = position;
        this.blockPosition.set(position.x, position.y, position.z);
    }

    @Override
    public float getZrot() {
        return vs$zRot;
    }
}
