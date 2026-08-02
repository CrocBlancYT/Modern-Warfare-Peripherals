package net.croc.mw_peripherals.blocks;

import net.croc.mw_peripherals.RegistryBlockEntities;
import net.croc.mw_peripherals.content.aps.APSBlockEntry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;

import javax.annotation.Nullable;

import net.croc.mw_peripherals.content.aps.APSIntercept;

public class APSFixedBlockEntity extends BlockEntity {

    public int MAX_CHARGES = 0;
    public int MAX_RANGE = 0;
    public int COOLDOWN_DURATION = 0;

    public int charges = 0;
    public int cooldown = 0;

    private final int DEFAULT_COLOR = 0xFFFFFF;

    private int color = -1;
    public final APSFixedBlockEntity.CachedVoxelShape shape = new APSFixedBlockEntity.CachedVoxelShape();

    public APSBlockEntry entry;

    private void loadEntry(APSBlockEntry entry) {
        this.entry = entry;

        this.COOLDOWN_DURATION = entry.cooldown_duration;
        this.MAX_RANGE = entry.max_range;
        this.MAX_CHARGES = entry.max_charges;
        this.charges = entry.max_charges;
    }

    public APSFixedBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);

        if (state.getBlock() instanceof net.croc.mw_peripherals.blocks.APSBlock apsBlock) {
            APSBlockEntry entry = apsBlock.getAPSEntry();
            loadEntry(entry);
        }
    }

    public APSFixedBlockEntity(BlockPos pos, BlockState state) {
        super(RegistryBlockEntities.FIXED_APS_BLOCK_ENTITY.get(), pos, state);

        if (state.getBlock() instanceof net.croc.mw_peripherals.blocks.APSBlock apsBlock) {
            APSBlockEntry entry = apsBlock.getAPSEntry();
            loadEntry(entry);
        }
    }

    public boolean canIntercept(Entity incoming) {
        return true;
    }

    public Entity intercept(Level level, BlockPos pos) {
        for (Entity entity : APSIntercept.detectProjectiles(this, 20)) {
            if (this.charges > 0 && canIntercept(entity)) {
                boolean success = APSIntercept.tryKillProjectile(this, entity);
                if (success) {
                    this.charges--;
                    this.cooldown = this.COOLDOWN_DURATION;
                    this.setChanged();

                    return entity;
                }
            }
        }

        return null;
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        if (this.cooldown > 0) {
            this.cooldown--;
        }
        if (level.getBestNeighborSignal(pos) > 0 && this.cooldown <= 0) {
            Entity hit = this.intercept(level, pos);

            if (hit != null) {
                this.setChanged();
            }
        }
    }

    public boolean trySetColor(int newColor) {
        if (newColor > 0xFFFFFF) return false;
        if (newColor < 0) return false;

        this.color = newColor;
        this.setChanged();
        setUpdated();

        return true;
    }

    public int getColor() {
        return this.color;
    }

    public class CachedVoxelShape {
        private int heightOffset;
        private VoxelShape cachedShapeVoxel;
        private int cachedShapeHeight;

        public CachedVoxelShape() {
            this.heightOffset = 0;
            this.cachedShapeHeight = 0;
            this.cachedShapeVoxel = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 13.0D, 15.0D);
        }

        public VoxelShape get() {
            if (this.cachedShapeHeight != this.heightOffset) {
                this.cachedShapeHeight = this.heightOffset;
                int height = this.getHeightOffset();
                this.cachedShapeVoxel = Block.box(1.0D, 0.0D, 1.0D, 15.0D, (13 + height), 15.0D);
            }

            if (this.cachedShapeVoxel == null) {
                return net.croc.mw_peripherals.blocks.APSBlock.defaultShape;
            }

            return this.cachedShapeVoxel;
        }

        public void updateFromClick(BlockHitResult hitResult, BlockPos pos) {
            this.heightOffset = (int) (((hitResult.getLocation()).y - pos.getY()) * 16.0D);
            setUpdated();
        }

        public int getHeightOffset() {
            int height = this.heightOffset;
            if (height > 16)
                height = 16;
            if (height < 0)
                height = 0;
            return height;
        }

        public void setHeightOffset(int height) {
            if (height > 16)
                height = 16;
            if (height < 0)
                height = 0;
            this.heightOffset = height;
        }
    }

    public boolean addCharge() {
        if (this.charges < this.MAX_CHARGES) {
            this.charges++;
            this.setChanged();
            return true;
        }
        return false;
    }

    public boolean useCharge() {
        if (this.charges > 0) {
            this.charges--;
            this.setChanged();
            return true;
        }
        return false;
    }

    public void pointAt(Vector3d to) { }

    public Direction getFacing() {
        return this.getBlockState().getValue(net.croc.mw_peripherals.blocks.APSBlock.FACING);
    }

    public void setUpdated() {
        if (this.level == null) return;

        this.level.sendBlockUpdated(
                this.getBlockPos(),
                this.getBlockState(),
                this.getBlockState(), 2);
    }

    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("charges", this.charges);
        tag.putInt("cooldown", this.cooldown);
        tag.putInt("offset", this.shape.getHeightOffset());

        int savedColor = this.getColor();
        if (savedColor != -1) tag.putInt("color", savedColor);
    }

    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("charges"))
            this.charges = tag.getInt("charges");
        if (tag.contains("cooldown"))
            this.cooldown = tag.getInt("cooldown");
        if (tag.contains("offset"))
            this.shape.setHeightOffset(tag.getInt("offset"));
        if (tag.contains("color")) {
            this.color = tag.getInt("color");
        } else {
            this.color = DEFAULT_COLOR;
        }
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

    public @NotNull CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        this.saveAdditional(tag);
        return tag;
    }

    public void handleUpdateTag(CompoundTag tag) {
        super.handleUpdateTag(tag);
        this.load(tag);
    }
}