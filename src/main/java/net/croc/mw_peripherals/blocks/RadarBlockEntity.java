package net.croc.mw_peripherals.blocks;

import com.mojang.math.Axis;
import javax.annotation.Nullable;

import net.croc.mw_peripherals.RegistryBlockEntities;
import net.croc.mw_peripherals.integration.tallyho.tracker.RadTracker.Angle;
import net.croc.mw_peripherals.utils.IRadarBlockEntity;
import net.croc.mw_peripherals.utils.VSShipComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;

public class RadarBlockEntity extends IRadarBlockEntity {

    public static final float PITCH_LIMIT = 33f;
    public static final float PITCH_SPEED_LIMIT = 180f;
    public static final float YAW_SPEED_LIMIT = 180f;

    private float yaw = 0.0F;
    private float pitch = 0.0F;

    private float yawSpeed = 0.0F;
    private float pitchSpeed = 0.0F;

    private float client_yaw = 0.0F;
    private float client_pitch = 0.0F;

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

    @Override
    public double maxRange() {
        return 750;
    }

    @Override
    public Angle scanFoV() {
        return Angle.degrees(45);
    }

    @Override
    public RadarType type() {
        return RadarType.MSA;
    }

    @Override
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

    private void setUpdated() {
        if (this.level == null) return;
        this.level.sendBlockUpdated(
                this.getBlockPos(),
                this.getBlockState(),
                this.getBlockState(), 2);
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putFloat("yaw", this.yaw);
        tag.putFloat("pitch", this.pitch);
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        if (tag.contains("yaw"))
            this.yaw = tag.getFloat("yaw");
        if (tag.contains("pitch"))
            this.pitch = tag.getFloat("pitch");

        VSShipComponents.subscribe(this);
    }

    @Nullable
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        CompoundTag tag = pkt.getTag();
        if (tag != null)
            this.load(tag);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        this.saveAdditional(tag);
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        super.handleUpdateTag(tag);
        this.load(tag);
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

    public static void tick(Level level, BlockPos pos, BlockState state, RadarBlockEntity radar) {
        float yawSpeed = radar.yawSpeed;
        float pitchSpeed = radar.pitchSpeed;

        int signal = level.getBestNeighborSignal(pos);
        if (signal > 0) {
            radar.scan();

            yawSpeed = radar.getAnimatedYawSpeed(signal);
            pitchSpeed = radar.getAnimatedPitchSpeed(signal);
        }

        radar.setYaw(radar.getYaw() + yawSpeed * 0.05f);
        radar.setPitch(radar.getPitch() + pitchSpeed * 0.05f);
    }
}
