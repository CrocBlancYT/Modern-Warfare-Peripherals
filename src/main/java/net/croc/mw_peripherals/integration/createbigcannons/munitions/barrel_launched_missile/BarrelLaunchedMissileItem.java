package net.croc.mw_peripherals.integration.createbigcannons.munitions.barrel_launched_missile;

import java.util.List;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

public class BarrelLaunchedMissileItem extends BlockItem {
    private String missileId = "unknown";

    public BarrelLaunchedMissileItem(Block block, Properties properties) {
        super(block, properties);
    }

    public BarrelLaunchedMissileItem withMissileId(String id) {
        this.missileId = id;
        return this;
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);

        pTooltipComponents.add(Component.literal("Missile: ").append(Component.translatable(this.missileId))
                .withStyle(ChatFormatting.GRAY));
    }
}