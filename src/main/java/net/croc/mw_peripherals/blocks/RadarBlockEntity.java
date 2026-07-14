package net.croc.mw_peripherals.blocks;

import com.ibm.icu.impl.Pair;
import com.mojang.math.Axis;
import javax.annotation.Nullable;

import edn.stratodonut.tallyho.missile.Target;
import net.croc.mw_peripherals.Main;
import net.croc.mw_peripherals.RegistryBlockEntities;
import net.croc.mw_peripherals.integration.computercraft.peripherals.RadarPeripheral;
import net.croc.mw_peripherals.integration.tallyho.BlockTarget;
import net.croc.mw_peripherals.integration.tallyho.RadarSource;
import net.croc.mw_peripherals.integration.tallyho.TargetSolutions;
import net.croc.mw_peripherals.integration.tallyho.tracker.RadTracker;
import net.croc.mw_peripherals.utils.VSUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaterniond;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

import java.util.Optional;

import static net.croc.mw_peripherals.integration.computercraft.peripherals.RadarPeripheral.DISH_FOV;
import static net.croc.mw_peripherals.integration.computercraft.peripherals.RadarPeripheral.maxRange;

public class RadarBlockEntity extends BlockEntity {

    public static final float PITCH_LIMIT = 33f;
    public static final float PITCH_SPEED_LIMIT = 180f;
    public static final float YAW_SPEED_LIMIT = 180f;

    private float yaw = 0.0F;
    private float pitch = 0.0F;

    private float yawSpeed = 0.0F;
    private float pitchSpeed = 0.0F;

    private float client_yaw = 0.0F;
    private float client_pitch = 0.0F;

    private RadarSource.Transmitter transmitter;
    private RadarSource.Receiver receiver;
    private Target<?> lockedTarget;
    private boolean auto_pitch_right = false;

    public RadarBlockEntity(BlockPos pos, BlockState state) {
        super(RegistryBlockEntities.RADAR_BLOCK_ENTITY.get(), pos, state);
    }

    private float clamp(float min, float max, float value) {
        if (value > max) return max;
        if (value < min) return min;
        return value;
    }

    private float lerp(float a, float b, float alpha) {
        return a + (b - a) * alpha;
    }

    private float short_angle(float angle) {
        return ((angle + 180f) % 360f) - 180f;
    }

    private float circular_lerp(float a, float b, float alpha) {
        return a + short_angle(b - a) * alpha;
    }

    public void onRender() {
        this.client_yaw = short_angle(circular_lerp(this.client_yaw, this.yaw, 0.5F)) % 360f;
        this.client_pitch = lerp(this.client_pitch, this.pitch, 0.5F);
    }

    public float getClientYaw() {
        return this.client_yaw;
    }

    public float getClientPitch() {
        return this.client_pitch;
    }

    public float getYaw() {
        return this.yaw;
    }

    public float getPitch() {
        return clamp(-PITCH_LIMIT, PITCH_LIMIT, this.pitch);
    }

    public void setYaw(float yaw) {
        this.yaw = yaw % 360f;
        setChanged();
        setUpdated();
    }

    public void setPitch(float pitch) {
        this.pitch = clamp(-PITCH_LIMIT, PITCH_LIMIT, pitch);
        setChanged();
        setUpdated();
    }

    public void setYawSpeed(float yawing) {
        this.yawSpeed = clamp(-YAW_SPEED_LIMIT, YAW_SPEED_LIMIT, yawing);
        setChanged();
        setUpdated();
    }

    public void setPitchSpeed(float pitching) {
        this.pitchSpeed = clamp(-PITCH_SPEED_LIMIT, PITCH_SPEED_LIMIT, pitching);
        setChanged();
        setUpdated();
    }

    private void setUpdated() {
        this.level.sendBlockUpdated(
                this.getBlockPos(),
                this.getBlockState(),
                this.getBlockState(), 2);
    }

    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putFloat("yaw", this.yaw);
        tag.putFloat("pitch", this.pitch);
    }

    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("yaw"))
            this.yaw = tag.getFloat("yaw");
        if (tag.contains("pitch"))
            this.pitch = tag.getFloat("pitch");
    }

    @Nullable
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        CompoundTag tag = pkt.getTag();
        if (tag != null)
            this.load(tag);
    }

    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        this.saveAdditional(tag);
        return tag;
    }

    public void handleUpdateTag(CompoundTag tag) {
        super.handleUpdateTag(tag);
        this.load(tag);
    }

    public Direction getFacing() {
        return this.getBlockState().getValue(RadarBlock.FACING);
    }

    public Vec3 getRadarDirection() {
        float yaw = getYaw();
        float pitch = getPitch();

        Direction facing = getFacing();
        Vector3d dir = new Vector3d(0.0D, 1.0D, 0.0D);
        dir = Axis.XP.rotationDegrees(pitch).transform(dir);
        dir = Axis.YP.rotationDegrees(yaw).transform(dir);
        dir = facing.getRotation().transform(dir);

        return new Vec3(dir.x, dir.y, dir.z);
    }

    private Target<?> target;
    public Target<?> getTarget() {
        if (target != null) return target;
        if (level == null) return null;
        target = BlockTarget.getTarget(this.level, this.getBlockPos());
        return target;
    }

    public RadarSource.Transmitter getTransmitter() {
        if (transmitter != null) return transmitter;
        if (level == null) return null;

        Target<?> target = getTarget();
        if (target == null) return null;

        transmitter = new RadarSource.Transmitter(this.level, target, (float) maxRange);
        return transmitter;
    }

    public RadarSource.Receiver getReceiver() {
        if (receiver != null) return receiver;
        if (level == null) return null;

        Target<?> target = getTarget();
        if (target == null) return null;

        receiver = new RadarSource.Receiver(this.level, target, (float) maxRange);
        return receiver;
    }

    public float getAnimatedYawSpeed(int signal) {
        return (90f - this.getYaw()) * 4;
    }

    public float getAnimatedPitchSpeed(int signal) {
        if (this.getPitch() == PITCH_LIMIT) {
            this.auto_pitch_right = false;
        } else if (this.getPitch() == -PITCH_LIMIT) {
            this.auto_pitch_right = true;
        }

        if (this.auto_pitch_right) {
            return this.lerp(0, PITCH_SPEED_LIMIT, 0.0625f * signal);
        } else {
            return this.lerp(0, -PITCH_SPEED_LIMIT, 0.0625f * signal);
        }
    }

    public Vec3 relative(Vec3 dir1, Vec3 dir2) {
        Vector3d relative = new Vector3d(dir1.x, dir1.y, dir1.z)
                .rotationTo(
                        new Vector3d(dir2.x, dir2.y, dir2.z),
                        new Quaterniond())
                .transform(new Vector3d());

        return new Vec3(relative.x, relative.y, relative.z);
    }

    public float getLockingPitchSpeed(int signal) {
        Vec3 worldPos = VSUtils.toWorldPosition(this.level, this.getBlockPos());
        Vec3 worldDir = VSUtils.toWorldDirection(this.level, this.getRadarDirection(), this.getBlockPos());

        Vec3 targetPos = getLockedTarget().position();

        Vec3 targetDir = targetPos.subtract(worldPos);

        Vec3 relative = relative(worldDir, targetDir);

        return (float) Math.atan2(relative.z, relative.x) * 4;
    }

    public Target<?> getLockedTarget() {
        return lockedTarget;
    }

    public boolean hasLock() {
        return lockedTarget != null;
    }

    @Nullable
    public TargetSolutions pulse() {

        RadarSource.Receiver receiver = this.getReceiver();
        RadarSource.Transmitter transmitter = this.getTransmitter();
        if (receiver== null || transmitter == null) return null;

        return RadTracker.pulse(transmitter, receiver,
                this.getBlockPos(), this.getRadarDirection(),
                (float) Math.toRadians(DISH_FOV), (float) maxRange);
    }

    public void findLock() {
        Vec3 dir = this.getRadarDirection();

        TargetSolutions solutions = pulse();
        if (solutions == null) return;

        lockedTarget = solutions
                .tryLockAerial()
                .orElse(null);
    }

    public void loseLock() {
        lockedTarget = null;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, RadarBlockEntity radar) {
        float yawSpeed = radar.yawSpeed;
        float pitchSpeed = radar.pitchSpeed;

        int signal = level.getBestNeighborSignal(pos);
        if (signal > 0) {
            //radar.loseLock();
            //radar.findLock();
            radar.pulse();

            yawSpeed = radar.getAnimatedYawSpeed(signal);
            pitchSpeed = radar.getAnimatedPitchSpeed(signal);

            /*if (radar.hasLock()) {
                pitchSpeed = radar.getLockingPitchSpeed(signal);
            } else {
                pitchSpeed = radar.getAnimatedPitchSpeed(signal);
            }*/
        }

        radar.setYaw(radar.getYaw() + yawSpeed * 0.05f);
        radar.setPitch(radar.getPitch() + pitchSpeed * 0.05f);
    }
}
