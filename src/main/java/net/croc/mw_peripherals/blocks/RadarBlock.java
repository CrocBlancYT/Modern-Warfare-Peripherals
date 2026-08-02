package net.croc.mw_peripherals.blocks;

import net.croc.mw_peripherals.RegistryBlockEntities;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
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
import net.minecraft.world.level.block.state.properties.DirectionProperty;

import javax.annotation.Nullable;
import java.util.List;

import static net.croc.mw_peripherals.integration.computercraft.peripherals.RadarPeripheral.DISH_FOV;
import static net.croc.mw_peripherals.integration.computercraft.peripherals.RadarPeripheral.MAX_RANGE;

public class RadarBlock extends Block implements EntityBlock {
    public static final DirectionProperty FACING = BlockStateProperties.FACING;

    public RadarBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, net.minecraft.core.Direction.NORTH));
    }

    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getNearestLookingDirection().getOpposite());
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter level,
                                List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        tooltip.add(Component.literal("Gimballed Radar")
                .withStyle(Style.EMPTY.withColor(ChatFormatting.AQUA)));

        tooltip.add(Component.literal("Radar Type: MSA")
                .withStyle(Style.EMPTY.withColor(ChatFormatting.GRAY)));

        tooltip.add(Component.literal("Scan FOV: "+45+"°")
                .withStyle(Style.EMPTY.withColor(ChatFormatting.GRAY)));

        tooltip.add(Component.literal("Scan Range: "+750+"m")
                .withStyle(Style.EMPTY.withColor(ChatFormatting.GRAY)));

        tooltip.add(Component.literal("Gimbal Field: 360.0°x"+(double)RadarBlockEntity.PITCH_LIMIT+"°")
                .withStyle(Style.EMPTY.withColor(ChatFormatting.GRAY)));

        tooltip.add(Component.literal("Gimbal Speed: "
                        +(double)RadarBlockEntity.YAW_SPEED_LIMIT+"°x"
                        +(double)RadarBlockEntity.PITCH_SPEED_LIMIT+"° per second")
                .withStyle(Style.EMPTY.withColor(ChatFormatting.GRAY)));
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new RadarBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        if (blockEntityType != RegistryBlockEntities.RADAR_BLOCK_ENTITY.get()) return null;
        if (level.isClientSide()) return null;

        return (tickLevel, tickPos, tickState, tickBlockEntity) -> {
            if (tickBlockEntity instanceof RadarBlockEntity radar) {
                RadarBlockEntity.tick(tickLevel, tickPos, tickState, radar);
            };
        };
    }
}
