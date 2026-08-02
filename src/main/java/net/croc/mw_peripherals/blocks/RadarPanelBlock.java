package net.croc.mw_peripherals.blocks;

import net.croc.mw_peripherals.RegistryBlockEntities;
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
import net.minecraft.world.level.LevelReader;
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

public class RadarPanelBlock extends Block implements EntityBlock {
    public static final DirectionProperty FACING = BlockStateProperties.FACING;

    public RadarPanelBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, net.minecraft.core.Direction.NORTH));
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new RadarPanelBlockEntity(pos, state);
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState state2, boolean bool) {
        super.onPlace(state, level, pos, state2, bool);
        if (level.getBlockEntity(pos) instanceof RadarPanelBlockEntity panel) {
            panel.refreshPanels();
        }
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState state2, boolean bool) {
        super.onRemove(state, level, pos, state2, bool);

        Direction.stream().forEach(direction -> {
            BlockPos neighbor = pos.relative(direction);

            if (level.getBlockEntity(neighbor) instanceof RadarPanelBlockEntity panel) {
                panel.refreshPanels();
            }
        });
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter level,
                                List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        tooltip.add(Component.literal("Radar Panel")
                .withStyle(Style.EMPTY.withColor(ChatFormatting.AQUA)));

        tooltip.add(Component.literal("Radar Type: AESA")
                .withStyle(Style.EMPTY.withColor(ChatFormatting.GRAY)));

        tooltip.add(Component.literal("Scan FOV: "+60+"°")
                .withStyle(Style.EMPTY.withColor(ChatFormatting.GRAY)));

        tooltip.add(Component.literal("Scan Range: "+300+"m + "+50+"m per panel")
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

    private static final VoxelShape SHAPE_NORTH = Block.box(0, 0, 0, 16, 16, 8);
    private static final VoxelShape SHAPE_SOUTH = Block.box(0, 0, 8, 16, 16, 16);
    private static final VoxelShape SHAPE_EAST = Block.box(8, 0, 0, 16, 16, 16);
    private static final VoxelShape SHAPE_WEST = Block.box(0, 0, 0, 8, 16, 16);
    private static final VoxelShape SHAPE_UP = Block.box(0, 8, 0, 16, 16, 16);
    private static final VoxelShape SHAPE_DOWN = Block.box(0, 0, 0, 16, 8, 16);

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        Direction facing = state.getValue(FACING);

        return switch (facing) {
            case NORTH -> SHAPE_NORTH;
            case SOUTH -> SHAPE_SOUTH;
            case EAST -> SHAPE_EAST;
            case WEST -> SHAPE_WEST;
            case UP -> SHAPE_UP;
            case DOWN -> SHAPE_DOWN;
        };
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return getShape(state, level, pos, context);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        if (blockEntityType != RegistryBlockEntities.RADAR_PANEL_BLOCK_ENTITY.get()) return null;
        if (level.isClientSide()) return null;

        return (tickLevel, tickPos, tickState, tickBlockEntity) -> {
            if (tickBlockEntity instanceof RadarPanelBlockEntity radar) {
                RadarPanelBlockEntity.tick(tickLevel, tickPos, tickState, radar);
            };
        };
    }
}
