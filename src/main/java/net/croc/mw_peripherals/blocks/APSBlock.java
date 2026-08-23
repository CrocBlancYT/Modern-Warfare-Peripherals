package net.croc.mw_peripherals.blocks;

import com.simibubi.create.AllItems;
import net.croc.mw_peripherals.RegistryBlockEntities;
import net.croc.mw_peripherals.RegistryItems;
import net.croc.mw_peripherals.content.aps.APSBlockEntry;
import net.croc.mw_peripherals.utils.RHAColors;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.List;

public class APSBlock extends Block implements EntityBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty LIT = BlockStateProperties.LIT;

    private final APSBlockEntry entry;

    public APSBlock(BlockBehaviour.Properties properties, APSBlockEntry entry) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, net.minecraft.core.Direction.NORTH)
                .setValue(LIT, false));
        this.entry = entry;
    }

    public APSBlockEntry getAPSEntry() {
        return this.entry;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
        builder.add(LIT); // todo : test
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return this.entry.getBlockEntity(pos, state);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return getOcclusionShape(state, level, pos);
    }

    public static final VoxelShape defaultShape = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 13.0D, 15.0D);

    @Override
    public VoxelShape getOcclusionShape(BlockState state, BlockGetter level, BlockPos pos) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof APSFixedBlockEntity aps) {
            VoxelShape shape = aps.shape.get();
            if (shape != null) {
                return shape;
            }
        }
        return defaultShape;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter level,
                                List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        if (this.entry.isNoRot()) {
            tooltip.add(Component.literal("Fixed APS")
                    .withStyle(Style.EMPTY.withColor(ChatFormatting.AQUA)));
        } else {
            tooltip.add(Component.literal("Turreted APS")
                    .withStyle(Style.EMPTY.withColor(ChatFormatting.AQUA)));
        }

        tooltip.add(Component.literal("Radar FOV: "+this.entry.fov+"°")
                .withStyle(Style.EMPTY.withColor(ChatFormatting.GRAY)));

        tooltip.add(Component.literal("Radar Range: "+this.entry.max_range+"m")
                .withStyle(Style.EMPTY.withColor(ChatFormatting.GRAY)));

        tooltip.add(Component.literal("Max Charges: "+this.entry.max_charges)
                .withStyle(Style.EMPTY.withColor(ChatFormatting.GRAY)));

        tooltip.add(Component.literal("Cooldown: "+this.entry.cooldown_duration * 0.05D)
                .withStyle(Style.EMPTY.withColor(ChatFormatting.GRAY)));

        if (this.entry.isYRot() || this.entry.isZYRot()) {
            tooltip.add(Component.literal("Yaw Limit: "+this.entry.min_yRot+"° / "+this.entry.max_yRot+"°")
                    .withStyle(Style.EMPTY.withColor(ChatFormatting.GRAY)));
        }
        if (this.entry.isZYRot()) {
            tooltip.add(Component.literal("Pitch Limit: "+this.entry.min_zRot+"° / "+this.entry.max_zRot+"°")
                    .withStyle(Style.EMPTY.withColor(ChatFormatting.GRAY)));
        }
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (hand != InteractionHand.MAIN_HAND) return InteractionResult.PASS;

        BlockEntity be = level.getBlockEntity(pos);

        if (be instanceof APSFixedBlockEntity aps) {
            ItemStack item = player.getItemInHand(hand);

            if (item.getItem() == RegistryItems.APS_CHARGE.get()) {
                if (level.isClientSide()) return InteractionResult.PASS;

                if (aps.addCharge()) {
                    if (!player.isCreative()) {
                        item.shrink(1);
                    }
                    level.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 1.0F, 1.0F);
                    return InteractionResult.SUCCESS;
                }
            } else if (item.getItem() == AllItems.WRENCH.get()) {
                if (level.isClientSide()) return InteractionResult.PASS;
                aps.shape.updateFromClick(hit, pos);
                return InteractionResult.SUCCESS;
            } else {
                int color = RHAColors.getColor(item.getItem());

                if (color != -1 && aps.trySetColor(color)) {
                    return InteractionResult.SUCCESS;
                }
            }
        }

        return InteractionResult.PASS;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        if (level.isClientSide()) return null;

        if (blockEntityType == RegistryBlockEntities.FIXED_APS_BLOCK_ENTITY.get()) {
            return (tickLevel, tickPos, tickState, tickBlockEntity) -> {
                if (tickBlockEntity instanceof APSFixedBlockEntity aps) {
                    aps.tick(tickLevel, tickPos, tickState);
                };
            };
        };

        if (blockEntityType == RegistryBlockEntities.Y_APS_BLOCK_ENTITY.get()) {
            return (tickLevel, tickPos, tickState, tickBlockEntity) -> {
                if (tickBlockEntity instanceof APSOneAxisBlockEntity aps) {
                    aps.tick(tickLevel, tickPos, tickState);
                };
            };
        };

        if (blockEntityType == RegistryBlockEntities.ZY_APS_BLOCK_ENTITY.get()) {
            return (tickLevel, tickPos, tickState, tickBlockEntity) -> {
                if (tickBlockEntity instanceof APSTwoAxisBlockEntity aps) {
                    aps.tick(tickLevel, tickPos, tickState);
                };
            };
        };

        return null;
    }
}
