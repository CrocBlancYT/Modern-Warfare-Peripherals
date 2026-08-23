package net.croc.mw_peripherals.blocks;

import net.croc.mw_peripherals.RegistryBlockEntities;
import net.croc.mw_peripherals.Shapes;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.List;

import static net.croc.mw_peripherals.integration.computercraft.peripherals.RadarFixedPeripheral.DISH_FOV;
import static net.croc.mw_peripherals.integration.computercraft.peripherals.RadarFixedPeripheral.MAX_RANGE;

public class RadarFixedBlock extends Block implements EntityBlock {
    public static final DirectionProperty FACING = BlockStateProperties.FACING;

    public RadarFixedBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, net.minecraft.core.Direction.NORTH));
    }

    @Override
    public @org.jetbrains.annotations.Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new RadarFixedBlockEntity(blockPos, blockState);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter level,
                                List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        tooltip.add(Component.literal("Fixed Radar")
                .withStyle(Style.EMPTY.withColor(ChatFormatting.AQUA)));

        tooltip.add(Component.literal("Radar Type: AESA")
                .withStyle(Style.EMPTY.withColor(ChatFormatting.GRAY)));

        tooltip.add(Component.literal("Scan FOV: "+81+"°")
                .withStyle(Style.EMPTY.withColor(ChatFormatting.GRAY)));

        tooltip.add(Component.literal("Scan Range: "+300+"m")
                .withStyle(Style.EMPTY.withColor(ChatFormatting.GRAY)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getNearestLookingDirection());
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        Direction facing = state.getValue(FACING);
        return Shapes.RADAR.get(facing);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return getShape(state, level, pos, context);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        if (blockEntityType != RegistryBlockEntities.RADAR_FIXED_BLOCK_ENTITY.get()) return null;
        if (level.isClientSide()) return null;

        return (tickLevel, tickPos, tickState, tickBlockEntity) -> {
            if (tickBlockEntity instanceof RadarFixedBlockEntity radar) {
                RadarFixedBlockEntity.tick(tickLevel, tickPos, tickState, radar);
            };
        };
    }
}
