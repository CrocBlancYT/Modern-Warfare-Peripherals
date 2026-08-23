package net.croc.mw_peripherals.blocks;

import net.croc.mw_peripherals.RegistryBlockEntities;
import net.croc.mw_peripherals.integration.tallyho.tracker.RadTracker;
import net.croc.mw_peripherals.utils.IRadarBlockEntity;
import net.croc.mw_peripherals.utils.VSShipComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class RWRBlockEntity extends IRadarBlockEntity {
    public RWRBlockEntity(BlockPos pos, BlockState state) {
        super(RegistryBlockEntities.RWR_BLOCK_ENTITY.get(), pos, state);
    }

    public static final double maxRange = 500;

    @Override
    public double maxRange() {
        return 500;
    }

    @Override
    public RadTracker.Angle scanFoV() {
        return RadTracker.Angle.max();
    }

    @Override
    public RadarType type() {
        return RadarType.PESA;
    }

    public void tick() {}

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        VSShipComponents.subscribe(this);
    }

}