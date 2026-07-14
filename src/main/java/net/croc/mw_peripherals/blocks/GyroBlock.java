package net.croc.mw_peripherals.blocks;

import javax.annotation.Nullable;
import net.croc.mw_peripherals.RegistryBlockEntities;
import net.croc.mw_peripherals.stuff.GyroActor;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class GyroBlock extends Block implements EntityBlock {
    public GyroBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    public static double getRadiansToDegrees() {
        return RADIANS_TO_DEGREES;
    }

    public static void setRadiansToDegrees(double radiansToDegrees) {
        RADIANS_TO_DEGREES = radiansToDegrees;
    }

    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new GyroBlockEntity(pos, state);
    }

    private static double RADIANS_TO_DEGREES = 1 / Math.PI * 180;

    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter level,
                                List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        tooltip.add(Component.literal("Gyro Module")
                .withStyle(Style.EMPTY.withColor(ChatFormatting.AQUA)));

        tooltip.add(Component.literal("Proportional: "+GyroBlockEntity.PROP_MULT+"x")
                .withStyle(Style.EMPTY.withColor(ChatFormatting.GRAY)));

        tooltip.add(Component.literal("Max Force: "+ GyroActor.MAX_FORCE+"N")
                .withStyle(Style.EMPTY.withColor(ChatFormatting.GRAY)));

        tooltip.add(Component.literal("Max Redstone Target Omega: "+(int)(GyroBlockEntity.REDSTONE_SPEED_MULT*16*RADIANS_TO_DEGREES)+"°/s")
                .withStyle(Style.EMPTY.withColor(ChatFormatting.GRAY)));
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        if (blockEntityType != RegistryBlockEntities.GYRO_BLOCK_ENTITY.get())
            return null;
        if (level.isClientSide())
            return null;
        return (tickLevel, tickPos, tickState, tickBlockEntity) -> {
            if (tickBlockEntity instanceof GyroBlockEntity) {
                GyroBlockEntity gyro = (GyroBlockEntity)tickBlockEntity;
                GyroBlockEntity.tick(tickLevel, tickPos, tickState, gyro);
            }
        };
    }
}
