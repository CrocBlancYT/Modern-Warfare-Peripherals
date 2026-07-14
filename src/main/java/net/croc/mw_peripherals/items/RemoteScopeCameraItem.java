package net.croc.mw_peripherals.items;

import com.simibubi.create.CreateClient;
import edn.stratodonut.tallyho.camera.block.ScopeBlock;
import edn.stratodonut.tallyho.camera.block.ThermalsBlock;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RemoteScopeCameraItem extends Item {
    public RemoteScopeCameraItem(Item.Properties properties) {
        super(properties);
    }

    private BlockHitResult dummyHitResult = new BlockHitResult(Vec3.ZERO, Direction.DOWN, BlockPos.ZERO, false);

    private static final AABB UNIT_CUBE = AABB.unitCubeFromLowerCorner(Vec3.ZERO);
    private static void drawOutline(Level level, BlockPos pos, int color) {
        BlockState state = level.getBlockState(pos);
        AABB box = state.getShape(level, pos).isEmpty() ?
                UNIT_CUBE :
                state.getShape(level, pos).bounds();

        CreateClient.OUTLINER.showAABB("remoteScopeCamera", box.move(pos))
                .colored(color)
                .lineWidth(0.0625F);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {

        ItemStack pStack = player.getItemInHand(hand);

        if (pStack.getOrCreateTag().contains("optics")) {
            BlockPos pos = NbtUtils.readBlockPos(pStack.getOrCreateTag().getCompound("optics"));

            BlockState state = level.getBlockState(pos);
            Block block = state.getBlock();

            if (block instanceof ScopeBlock scope) {
                scope.use(state, level, pos, player, hand, dummyHitResult);
            } else if (block instanceof ThermalsBlock thermals) {
                thermals.use(state, level, pos, player, hand, dummyHitResult);
            }
        }

        return super.use(level, player, hand);
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        BlockPos clickedPos = pContext.getClickedPos();

        Level level = pContext.getLevel();
        Block block = level.getBlockState(clickedPos).getBlock();

        if (block instanceof ScopeBlock || block instanceof ThermalsBlock) {
            pContext.getItemInHand().getOrCreateTag().put("optics", NbtUtils.writeBlockPos(clickedPos));
            if (level.isClientSide()) drawOutline(level, clickedPos, ChatFormatting.GREEN.getColor().intValue());
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);

        if (pStack.getOrCreateTag().contains("optics")) {
            BlockPos pos = NbtUtils.readBlockPos(pStack.getOrCreateTag().getCompound("optics"));
            pTooltipComponents.add(Component.translatable("Linked to: {x=%s y=%s z=%s}", pos.getX(), pos.getY(), pos.getZ()).withStyle(ChatFormatting.GRAY));
        } else {
            pTooltipComponents.add(Component.literal("Not linked").withStyle(ChatFormatting.GRAY));
        }
    }
}