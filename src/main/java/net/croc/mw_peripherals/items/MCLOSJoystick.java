package net.croc.mw_peripherals.items;

import edn.stratodonut.tallyho.entity.MountedMissileEntity;
import net.croc.mw_peripherals.integration.tallyho.guid.MCLOS;
import net.croc.mw_peripherals.network.MCLOSPacket;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class MCLOSJoystick extends Item {
    public MCLOSJoystick(Properties prop) {
        super(prop);
    }

    public @NotNull InteractionResult useOn(UseOnContext ctx) {
        Vec3 clickedPos = ctx.getClickLocation();

        for (MountedMissileEntity missile : ctx.getLevel().getEntitiesOfClass(MountedMissileEntity.class, AABB
                .ofSize(clickedPos, 0.1D, 0.1D, 0.1D).inflate(0.2))) {

            if (missile.getGuidance() instanceof MCLOS guid) {
                guid.linkJoystick(ctx.getPlayer());
                
                ctx.getItemInHand().getOrCreateTag().put("SelectedRemote", NbtUtils.createUUID(missile.getUUID()));
                return InteractionResult.CONSUME;
            }
        }

        return InteractionResult.FAIL;
    }

    private MCLOS getGuidance(MountedMissileEntity missile) {
        if (missile == null) return null;
        if (missile.getGuidance() instanceof MCLOS mclos) {
            return mclos;
        }
        return null;
    }

    public void handleInput(ServerPlayer player, MCLOSPacket.MCLOS_Binds map) {
        ItemStack pStack = player.getMainHandItem();

        if (pStack.getItem().equals(this) && pStack.getOrCreateTag().contains("SelectedRemote")) {
            UUID uuid = pStack.getOrCreateTag().getUUID("SelectedRemote");

            if (player.serverLevel().getEntity(uuid) instanceof MountedMissileEntity missile) {
                MCLOS guid = getGuidance(missile);
                if (guid == null) return;

                int y = 0;
                if (map.up()) y++;
                if (map.down()) y--;

                int z = 0;
                if (map.left()) z--;
                if (map.right()) z++;

                guid.updateSteering(new Vec3(0, y, z));
            }
        }
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
        if (pStack.getOrCreateTag().contains("SelectedRemote")) {
            String uuid = pStack.getOrCreateTag().getUUID("SelectedRemote").toString();
            pTooltipComponents.add(Component.translatable("Linked to: %s", uuid).withStyle(ChatFormatting.GRAY));
        } else {
            pTooltipComponents.add(Component.translatable("Not linked").withStyle(ChatFormatting.GRAY));
        }
    }
}