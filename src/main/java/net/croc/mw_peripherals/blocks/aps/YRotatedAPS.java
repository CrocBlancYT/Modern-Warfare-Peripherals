package net.croc.mw_peripherals.blocks.aps;

import net.croc.mw_peripherals.RegistryBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

public class YRotatedAPS extends APS {
    private float yRot = 0;
    private float min_yRot = 0;
    private float max_yRot = 0;

    private float circular_lerp(float a, float b, float alpha) {
        float diff = (b - a);
        diff = ((diff + 180f) % 360f) - 180f;
        return (a + diff * alpha) % 360f;
    }

    private float lerp(float a, float b, float alpha) {
        return a + (b - a) * alpha;
    }

    private float short_angle(float angle) {
        return ((angle + 180f) % 360f) - 180f;
    }

    public APSEntry entry;

    private void loadEntry(APSEntry entry) {
        this.entry = entry;

        this.min_yRot = entry.min_yRot;
        this.max_yRot = entry.max_yRot;
    }

    public YRotatedAPS(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);

        if (state.getBlock() instanceof APSBlock apsBlock) {
            APSEntry entry = apsBlock.getAPSEntry();
            loadEntry(entry);
        }
    }

    public YRotatedAPS(BlockPos pos, BlockState state) {
        super(RegistryBlockEntities.Y_APS_BLOCK_ENTITY.get(), pos, state);

        if (state.getBlock() instanceof APSBlock apsBlock) {
            APSEntry entry = apsBlock.getAPSEntry();
            loadEntry(entry);
        }
    }

    @Override
    public boolean canIntercept(Entity incoming) {
        return true;
    }

    public void setYRot(float yRot) {
        yRot = short_angle(yRot);

        if (yRot < min_yRot) yRot = min_yRot;
        if (yRot > max_yRot) yRot = max_yRot;

        this.yRot = yRot;
        setChanged();
        setUpdated();
    }

    @Override
    public void tick(Level level, BlockPos pos, BlockState state) {
        if (this.cooldown > 0) {
            this.cooldown--;
        }
        if (level.getBestNeighborSignal(pos) > 0 && this.cooldown <= 0) {
            Entity hit = this.intercept(level, pos);

            if (hit == null) return;

            Vec3 targetPos = hit.getEyePosition();
            this.pointAt(new Vector3d(targetPos.x(), targetPos.y(), targetPos.z()));

            this.setChanged();
        }
    }

    public float getYRot() { return this.yRot; }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putFloat("yaw", this.yRot);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("yaw"))
            setYRot(tag.getFloat("yaw"));
    }

    public void pointAt(Vector3d to) {
        BlockPos pos = this.getBlockPos();
        ServerShip ship = (ServerShip) VSGameUtilsKt.getShipManagingPos(this.getLevel(), pos);

        if (ship != null) to = ship.getWorldToShip().transformPosition(to);

        Vector3d from = new Vector3d(pos.getX() + 0.5D, pos.getY() + 0.65D + this.shape.getHeightOffset() * 0.0625D, pos.getZ() + 0.5D);
        Vector3d dir = to.sub(from);

        Direction facing = this.getFacing();

        setYRot((float) Math.toDegrees(Math.atan2(dir.x, dir.z)) + facing.toYRot());
        setUpdated();
    }
}