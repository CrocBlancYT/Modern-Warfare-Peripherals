package net.croc.mw_peripherals.items;

import net.croc.mw_peripherals.blocks.FlareDispenserBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public class FlareItem extends Item {
    public FlareItem(Item.Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        BlockPos pos = pContext.getClickedPos();
        Level level = pContext.getLevel();

        if (level.getBlockEntity(pos) instanceof FlareDispenserBlockEntity flareDispenser) {
            ItemStack item = pContext.getItemInHand();
            if (flareDispenser.addFlare()) {
                item.shrink(1);
                return InteractionResult.SUCCESS;
            }
        }

        return InteractionResult.PASS;
    }
}
