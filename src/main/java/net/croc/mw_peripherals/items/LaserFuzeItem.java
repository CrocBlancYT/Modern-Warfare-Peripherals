package net.croc.mw_peripherals.items;

import java.util.List;

import com.ibm.icu.impl.Pair;
import com.simibubi.create.CreateClient;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;
import rbasamoyai.createbigcannons.munitions.AbstractCannonProjectile;
import rbasamoyai.createbigcannons.munitions.fuzes.FuzeItem;

public class LaserFuzeItem extends FuzeItem {
    public static final float MIN_DISTANCE = 5.0F;

    public static final float MAX_DISTANCE = 50.0F;

    public LaserFuzeItem(Item.Properties properties) {
        super(properties);
    }

    private static void drawLine(Vec3 from, Vec3 to, int color) {
        CreateClient.OUTLINER.showLine("laserFuze", from, to)
                .colored(color)
                .lineWidth(0.0625F);
    }

    private BlockHitResult raycast(Level level, Vector3d start, Vector3d end) {
        ClipContext ctx = new ClipContext(new Vec3(start.x, start.y, start.z), new Vec3(end.x, end.y, end.z), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, null);
        return level.clip(ctx);
    }

    private static float getLaserMaxDistance(CompoundTag tag) {
        if (tag.contains("distance"))
            return tag.getFloat("distance");
        return 5.0F;
    }

    private static float clamp(float value, float min, float max) {
        if (value > max) return max;
        if (value < min) return min;
        return value;
    }

    @Override
    public boolean onProjectileTick(ItemStack stack, AbstractCannonProjectile projectile) {
        float distance = getLaserMaxDistance(stack.getOrCreateTag());
        Vec3 look = projectile.getLookAngle();
        Vec3 pos = projectile.getEyePosition();
        Vector3d start = new Vector3d(pos.x + look.x * 2.0D, pos.y + look.y * 2.0D, pos.z + look.z * 2.0D);
        Vector3d end = new Vector3d(pos.x + look.x * distance, pos.y + look.y * distance, pos.z + look.z * distance);
        BlockHitResult res = raycast(projectile.level(), start, end);
        if (res.getType().toString().equals("BLOCK")) return true;
        return super.onProjectileTick(stack, projectile);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        Vec3 from = player.getEyePosition();
        Vec3 to = player.pick(50.0D, 1.0F, false).getLocation();

        if (level.isClientSide()) {
            drawLine(from.subtract(new Vec3(0,0.1f,0)), to, ChatFormatting.RED.getColor().intValue());
        } else {
            ItemStack stack = player.getItemInHand(hand);
            CompoundTag tag = stack.getOrCreateTag();

            float distance = (float) to.subtract(from).length();
            distance = clamp(distance, 5.0F, 50.0F);
            distance = Math.round(distance * 8.0F) * 0.125F;

            player.displayClientMessage(Component.translatable("%s blocks", distance), true);
            tag.putFloat("distance", distance);
        }

        return InteractionResultHolder.pass(player.getItemInHand(hand));
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
        pTooltipComponents.add(Component.translatable("Laser trigger distance: %s blocks", getLaserMaxDistance(pStack.getOrCreateTag())).withStyle(ChatFormatting.GRAY));

        pTooltipComponents.add(Component.translatable("Minimum Distance: %s", MIN_DISTANCE).withStyle(ChatFormatting.GRAY));
        pTooltipComponents.add(Component.translatable("Maximum Distance: %s", MAX_DISTANCE).withStyle(ChatFormatting.GRAY));
    }
}
