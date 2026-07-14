package net.croc.mw_peripherals.items;

import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.foundation.utility.Components;
import edn.stratodonut.tallyho.camera.entity.CameraEntity2;
import edn.stratodonut.tallyho.camera.entity.TargetingPodEntity;
import java.util.List;
import java.util.UUID;

import edn.stratodonut.tallyho.entity.MountedMissileEntity;
import net.croc.mw_peripherals.entity.MountedMissileCameraEntity;
import net.croc.mw_peripherals.integration.tallyho.guid.MCLOS;
import net.croc.mw_peripherals.integration.tallyho.guid.TVGuidance;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.util.VectorConversionsMCKt;

public class TVGuidanceRemote extends Item {

    public TVGuidanceRemote(Item.Properties properties) {
        super(properties);
    }

    public @NotNull InteractionResult useOn(UseOnContext ctx) {
        Vec3 clickedPos = ctx.getClickLocation();


        for (MountedMissileEntity missile : ctx.getLevel().getEntitiesOfClass(MountedMissileEntity.class, AABB
                .ofSize(clickedPos, 0.1D, 0.1D, 0.1D).inflate(0.2))) {

            if (missile.getGuidance() instanceof TVGuidance guid) {
                guid.setSteerPlayer(ctx.getPlayer());

                ctx.getItemInHand().getOrCreateTag().put("SelectedRemote", NbtUtils.createUUID(missile.getUUID()));
                return InteractionResult.CONSUME;
            }
        }

        return InteractionResult.FAIL;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("SelectedRemote")) {
            UUID conductorId = tag.getUUID("SelectedRemote");
            tooltip.add(Components.translatable("railways.whistle.tool.bound").withStyle(ChatFormatting.DARK_GREEN));
            tooltip.add(Components.translatable("railways.whistle.tool.conductor_id", conductorId.toString().substring(0, 5)));
            tooltip.add(Components.translatable("railways.remote_lens.tool.bound_usage"));
        } else {
            tooltip.add(Components.translatable("railways.remote_lens.tool.not_bound").withStyle(ChatFormatting.DARK_RED));
        }
    }

    @NotNull
    @Override
    public InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);

        if (level instanceof ServerLevel) {
            ServerLevel serverLevel = (ServerLevel) level;

            if (player instanceof ServerPlayer) {
                ServerPlayer serverPlayer = (ServerPlayer) player;
                CompoundTag stackTag = stack.getTag();

                if (stackTag != null && stackTag.contains("SelectedRemote")) {
                    UUID cameraId = stackTag.getUUID("SelectedRemote");

                    if (player.isShiftKeyDown()) {
                        stackTag.remove("SelectedRemote");
                        AllSoundEvents.CONTROLLER_CLICK.play(level, null, player.blockPosition(), 0.5F, 1.1F);
                        player.displayClientMessage(Components.translatable("railways.remote_lens.clear"), true);
                        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
                    }

                    Entity entity = serverLevel.getEntity(cameraId);
                    if (entity instanceof MountedMissileEntity missile) {
                        CameraEntity2 camera = MountedMissileCameraEntity.getOrCreateCamera(missile);
                        if (camera.startViewing(serverPlayer)) {
                            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
                        } else {
                            return InteractionResultHolder.fail(stack);
                        }
                    }

                    player.displayClientMessage(Component.literal("No connection to camera!"), true);
                    return InteractionResultHolder.fail(stack);
                }

                return InteractionResultHolder.fail(stack);
            }
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }
}