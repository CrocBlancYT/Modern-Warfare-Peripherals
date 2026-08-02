package net.croc.mw_peripherals.blocks;

import net.croc.mw_peripherals.RegistryBlockEntities;
import net.croc.mw_peripherals.content.aps.APSBlockEntry;
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

public class APSTwoAxisBlockEntity extends APSOneAxisBlockEntity {
    private float zRot = 0;
    private float min_zRot = 0;
    private float max_zRot = 0;

    public APSBlockEntry entry;

    private void loadEntry(APSBlockEntry entry) {
        this.entry = entry;

        this.min_zRot = entry.min_zRot;
        this.max_zRot = entry.max_zRot;
    }

    public APSTwoAxisBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);

        if (state.getBlock() instanceof APSBlock apsBlock) {
            APSBlockEntry entry = apsBlock.getAPSEntry();
            loadEntry(entry);
        }
    }

    public APSTwoAxisBlockEntity(BlockPos pos, BlockState state) {
        super(RegistryBlockEntities.ZY_APS_BLOCK_ENTITY.get(), pos, state);

        if (state.getBlock() instanceof APSBlock apsBlock) {
            APSBlockEntry entry = apsBlock.getAPSEntry();
            loadEntry(entry);
        }
    }

    public void setZRot(float zRot) {
        if (zRot < min_zRot) zRot = min_zRot;
        if (zRot > max_zRot) zRot = max_zRot;

        this.zRot = zRot;
        setChanged();
        setUpdated();
    }

    @Override
    public boolean canIntercept(Entity incoming) {
        return true;
    }

    public float getZRot() { return this.zRot; }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putFloat("pitch", this.zRot);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("pitch"))
            setZRot(tag.getFloat("pitch"));
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

    @Override
    public void pointAt(Vector3d to) {
        BlockPos pos = this.getBlockPos();
        ServerShip ship = (ServerShip) VSGameUtilsKt.getShipManagingPos(this.getLevel(), pos);

        if (ship != null) to = ship.getWorldToShip().transformPosition(to);

        Vector3d from = new Vector3d(pos.getX() + 0.5D, pos.getY() + 0.5D + this.shape.getHeightOffset() * 0.0625D, pos.getZ() + 0.5D);
        Vector3d dir = to.sub(from);

        Direction facing = this.getFacing();

        setYRot((float) Math.toDegrees(Math.atan2(dir.x, dir.z)) + facing.toYRot());
        setZRot((float)Math.toDegrees(Math.asin(dir.y / dir.length())));
        setUpdated();
    }
}