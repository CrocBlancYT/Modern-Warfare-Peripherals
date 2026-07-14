package net.croc.mw_peripherals.items;

import java.util.List;

import com.ibm.icu.impl.Pair;
import com.simibubi.create.CreateClient;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;
import rbasamoyai.createbigcannons.munitions.AbstractCannonProjectile;
import rbasamoyai.createbigcannons.munitions.fuzes.FuzeItem;

public class RemoteFuzeItem extends FuzeItem {
    public RemoteFuzeItem(Item.Properties properties) {
        super(properties);
    }
    
    private static final AABB UNIT_CUBE = AABB.unitCubeFromLowerCorner(Vec3.ZERO);
    private static void drawOutline(Level level, BlockPos pos, int color) {
        BlockState state = level.getBlockState(pos);
        AABB box = state.getShape(level, pos).isEmpty() ?
                UNIT_CUBE :
                state.getShape(level, pos).bounds();

        CreateClient.OUTLINER.showAABB("remoteFuze", box.move(pos))
                .colored(color)
                .lineWidth(0.0625F);
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        BlockPos clickedPos = pContext.getClickedPos();
        pContext.getItemInHand().getOrCreateTag().put("controller", NbtUtils.writeBlockPos(clickedPos));

        Level level = pContext.getLevel();
        if (level.isClientSide()) drawOutline(level, clickedPos, ChatFormatting.GREEN.getColor().intValue());

        return InteractionResult.SUCCESS;
    }

    @Override
    public boolean onProjectileTick(ItemStack stack, AbstractCannonProjectile projectile) {
        CompoundTag tag = stack.getOrCreateTag();
        if (tag.contains("controller")) {
            BlockPos pos = NbtUtils.readBlockPos(tag.getCompound("controller"));
            int signal = projectile.level().getBestNeighborSignal(pos);
            if (signal > 0) return true;
        }
        return super.onProjectileTick(stack, projectile);
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
        if (pStack.getOrCreateTag().contains("controller")) {
            BlockPos pos = NbtUtils.readBlockPos(pStack.getOrCreateTag().getCompound("controller"));
            pTooltipComponents.add(Component.translatable("Linked to: {x=%s y=%s z=%s}", pos.getX(), pos.getY(), pos.getZ()).withStyle(ChatFormatting.GRAY));
        } else {
            pTooltipComponents.add(Component.translatable("Not linked").withStyle(ChatFormatting.GRAY));
        }
    }
}
