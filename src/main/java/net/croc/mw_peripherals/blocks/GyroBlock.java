package net.croc.mw_peripherals.blocks;

import net.croc.mw_peripherals.RegistryBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public class GyroBlock extends Block implements EntityBlock {
    public GyroBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new GyroBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        if (blockEntityType != RegistryBlockEntities.BLOCK_ENTITIES.get("gyro_module").get()) return null;
        if (level.isClientSide()) return null;

        return (tickLevel, tickPos, tickState, tickBlockEntity) -> {
            if (tickBlockEntity instanceof GyroBlockEntity gyro) {
                GyroBlockEntity.tick(tickLevel, tickPos, tickState, gyro);
            };
        };
    }
}
