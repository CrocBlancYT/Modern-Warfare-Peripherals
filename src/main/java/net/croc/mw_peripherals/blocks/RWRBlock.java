package net.croc.mw_peripherals.blocks;

import javax.annotation.Nullable;

import net.croc.mw_peripherals.RegistryBlockEntities;
import net.croc.mw_peripherals.integration.computercraft.peripherals.RWRPeripheral;
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
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class RWRBlock extends Block implements EntityBlock {
    public RWRBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter level,
                                List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        tooltip.add(Component.literal("Radar Receiver")
                .withStyle(Style.EMPTY.withColor(ChatFormatting.AQUA)));

        tooltip.add(Component.literal("Receiver FOV: 360°")
                .withStyle(Style.EMPTY.withColor(ChatFormatting.GRAY)));

        tooltip.add(Component.literal("Receiver Range: "+ RWRBlockEntity.maxRange +"m")
                .withStyle(Style.EMPTY.withColor(ChatFormatting.GRAY)));
    }

    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new RWRBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        if (blockEntityType != RegistryBlockEntities.RWR_BLOCK_ENTITY.get())
            return null;
        if (level.isClientSide())
            return null;
        return (tickLevel, tickPos, tickState, tickBlockEntity) -> {
            if (tickBlockEntity instanceof RWRBlockEntity rwr) {
                rwr.tick();
            }
        };
    }
}
