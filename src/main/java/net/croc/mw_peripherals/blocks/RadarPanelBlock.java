package net.croc.mw_peripherals.blocks;

import com.simibubi.create.content.contraptions.bearing.SailBlock;
import com.simibubi.create.foundation.placement.IPlacementHelper;
import com.simibubi.create.foundation.placement.PlacementHelpers;
import com.simibubi.create.foundation.placement.PlacementOffset;
import net.croc.mw_peripherals.RegistryBlockEntities;
import net.croc.mw_peripherals.RegistryBlocks;
import net.croc.mw_peripherals.Shapes;
import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
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
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Predicate;

public class RadarPanelBlock extends Block implements EntityBlock {
    public static final DirectionProperty FACING = BlockStateProperties.FACING;

    private static final int placementHelperId = PlacementHelpers.register(new PlacementHelper());

    public RadarPanelBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, net.minecraft.core.Direction.NORTH));
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new RadarPanelBlockEntity(pos, state);
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult ray) {
        ItemStack heldItem = player.getItemInHand(hand);
        IPlacementHelper placementHelper = PlacementHelpers.get(placementHelperId);
        if (!player.isShiftKeyDown() && player.mayBuild() && placementHelper.matchesItem(heldItem)) {
            placementHelper.getOffset(player, world, state, pos, ray).placeInWorld(world, (BlockItem)heldItem.getItem(), player, hand, ray);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
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
    public void appendHoverText(@NotNull ItemStack stack, @Nullable BlockGetter level,
                                @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
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
        if (blockEntityType != RegistryBlockEntities.RADAR_PANEL_BLOCK_ENTITY.get()) return null;
        if (level.isClientSide()) return null;

        return (tickLevel, tickPos, tickState, tickBlockEntity) -> {
            if (tickBlockEntity instanceof RadarPanelBlockEntity radar) {
                RadarPanelBlockEntity.tick(tickLevel, tickPos, tickState, radar);
            };
        };
    }

    @MethodsReturnNonnullByDefault
    private static class PlacementHelper implements IPlacementHelper {
        @Override
        public Predicate<ItemStack> getItemPredicate() {
            return i -> RegistryBlocks.RADAR_PANEL.getItem().get() == i.getItem();
        }

        @Override
        public Predicate<BlockState> getStatePredicate() {
            return s -> s.getBlock() instanceof RadarPanelBlock;
        }

        @Override
        public PlacementOffset getOffset(Player player, Level world, BlockState state, BlockPos pos,
                                         BlockHitResult ray) {
            List<Direction> directions = IPlacementHelper.orderedByDistanceExceptAxis(pos, ray.getLocation(),
                    state.getValue(RadarPanelBlock.FACING)
                            .getAxis(),
                    dir -> world.getBlockState(pos.relative(dir))
                            .canBeReplaced());

            if (directions.isEmpty())
                return PlacementOffset.fail();
            else {
                return PlacementOffset.success(pos.relative(directions.get(0)),
                        s -> s.setValue(FACING, state.getValue(FACING)));
            }
        }
    }
}
