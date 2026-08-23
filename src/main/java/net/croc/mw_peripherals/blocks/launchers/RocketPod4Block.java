package net.croc.mw_peripherals.blocks.launchers;

import javax.annotation.Nullable;

import com.simibubi.create.AllItems;
import com.simibubi.create.AllSoundEvents;
import edn.stratodonut.tallyho.block.FlareDispenserBlock;
import edn.stratodonut.tallyho.entity.FlareEntity;
import net.croc.mw_peripherals.RegistryBlockEntities;
import net.croc.mw_peripherals.RegistryItems;
import net.croc.mw_peripherals.RegistrySounds;
import net.croc.mw_peripherals.Shapes;
import net.croc.mw_peripherals.blocks.APSFixedBlockEntity;
import net.croc.mw_peripherals.blocks.GyroBlockEntity;
import net.croc.mw_peripherals.integration.tallyho.ForeignMissileRegistry;
import net.croc.mw_peripherals.stuff.GyroActor;
import net.croc.mw_peripherals.utils.RHAColors;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerLevel;
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
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

import java.util.List;

public class RocketPod4Block extends Block implements EntityBlock {
    public static final DirectionProperty HORIZONTAL_FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty LIT = BlockStateProperties.LIT;

    public RocketPod4Block(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(HORIZONTAL_FACING, Direction.NORTH)
                .setValue(LIT, false)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(HORIZONTAL_FACING);
        builder.add(LIT);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction direction = context.getHorizontalDirection();

        if (context.getPlayer() != null && context.getPlayer().isShiftKeyDown()) {
            direction = direction.getOpposite();
        }

        return this.defaultBlockState().setValue(HORIZONTAL_FACING, direction);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new RocketPod4BlockEntity(pos, state);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter level,
                                List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        tooltip.add(Component.literal("Capacity: 4x Zuni MK32")
                .withStyle(Style.EMPTY.withColor(ChatFormatting.AQUA)));
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (hand != InteractionHand.MAIN_HAND) return InteractionResult.PASS;

        if (level.getBlockEntity(pos) instanceof RocketPod4BlockEntity pod) {
            ItemStack item = player.getItemInHand(hand);

            if (!level.isClientSide && item.getItem() == ForeignMissileRegistry.ZUNI_MK32.getItemEntry().get() &&
                    !player.getCooldowns().isOnCooldown(item.getItem())) {
                if (pod.addRocket()) {
                    if (!player.isCreative()) {
                        item.shrink(1);
                        player.getCooldowns().addCooldown(item.getItem(), 30);
                    }
                    level.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 1.0F, 1.0F);

                    return InteractionResult.SUCCESS;
                }
            } else if (!level.isClientSide && item.getItem() == AllItems.WRENCH.get()) {
                pod.updateFromClick(hit, pos);
                level.playSound(null, pos, AllSoundEvents.WRENCH_ROTATE.getMainEvent(), SoundSource.BLOCKS, 1.0F, 1.0F);
                return InteractionResult.SUCCESS;
            } else {
                int color = RHAColors.getColor(item.getItem());
                if (color != -1 && pod.trySetColor(color)) {
                    return InteractionResult.SUCCESS;
                }
            }
        }

        return InteractionResult.PASS;
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block p_55669_, BlockPos p_55670_, boolean p_55671_) {
        if (!level.isClientSide && level instanceof ServerLevel slevel) {
            boolean flag = level.hasNeighborSignal(pos);
            if (flag != state.getValue(LIT)) {
                if (flag && level.getBlockEntity(pos) instanceof RocketPod4BlockEntity pod) {
                    if (!pod.fire()) {
                        slevel.playSound(null, pos,
                                SoundEvents.DISPENSER_FAIL, SoundSource.BLOCKS,
                                1.0F, 1.5F
                        );
                    }
                }

                level.setBlock(pos, state.setValue(LIT, flag), 3);
            }
        }
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return getOcclusionShape(state, level, pos);
    }

    @Override
    public VoxelShape getOcclusionShape(BlockState state, BlockGetter level, BlockPos pos) {
        if (level.getBlockEntity(pos) instanceof RocketPod4BlockEntity pod) {
            return pod.getCachedShape();
        } else {
            return Shapes.ROCKET_POD_4.get(state.getValue(HORIZONTAL_FACING));
        }
    }
}
