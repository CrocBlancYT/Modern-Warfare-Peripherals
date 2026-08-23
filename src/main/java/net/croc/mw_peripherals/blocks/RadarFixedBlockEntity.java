package net.croc.mw_peripherals.blocks;

import net.croc.mw_peripherals.RegistryBlockEntities;
import net.croc.mw_peripherals.integration.tallyho.tracker.RadTracker.Angle;
import net.croc.mw_peripherals.utils.IRadarBlockEntity;
import net.croc.mw_peripherals.utils.VSShipComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class RadarFixedBlockEntity extends IRadarBlockEntity {
    public RadarFixedBlockEntity(BlockPos pos, BlockState state) {
        super(RegistryBlockEntities.RADAR_FIXED_BLOCK_ENTITY.get(), pos, state);
    }

    @Override
    public double maxRange() {
        return 300;
    }

    @Override
    public Angle scanFoV() {
        return Angle.degrees(81);
    }

    @Override
    public RadarType type() {
        return RadarType.AESA;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, RadarFixedBlockEntity radar) {
        if (level.getBestNeighborSignal(pos) > 0) {
            radar.scan();
        }
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        VSShipComponents.subscribe(this);
    }
}