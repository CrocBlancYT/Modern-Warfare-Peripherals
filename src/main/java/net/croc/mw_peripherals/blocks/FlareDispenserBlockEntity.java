package net.croc.mw_peripherals.blocks;

import net.croc.mw_peripherals.RegistryBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class FlareDispenserBlockEntity extends BlockEntity {
    private int flares = 50;

    public FlareDispenserBlockEntity(BlockPos pos, BlockState state) {
        super(RegistryBlockEntities.FLARE_DISPENSER_BLOCK_ENTITY.get(), pos, state);
    }

    public boolean addFlare() {
        if (this.flares < 50) {
            this.flares++;
            return true;
        }
        return false;
    }

    public boolean removeFlare() {
        if (this.flares > 0) {
            this.flares--;
            return true;
        }
        return false;
    }

    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("flares", this.flares);
    }

    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("flares")) {
            this.flares = tag.getInt("flares");
        }
    }

    public int getFlares() {
        return this.flares;
    }
}