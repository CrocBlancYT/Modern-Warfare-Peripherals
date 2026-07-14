package net.croc.mw_peripherals.mixin;

import com.vicmatskiv.pointblank.client.GunClientState;
import com.vicmatskiv.pointblank.item.GunItem;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(value = GunItem.class, priority = 1100)
public class MixinGunItem {
    @Inject(method = "getMaxServerShootingDistance", at = @At("RETURN"), remap = false, cancellable = true)
    private void constantMaxServerShootingDistance(ItemStack itemStack, boolean isAiming, ServerLevel level,
                                              CallbackInfoReturnable<Double> cir) {
        cir.setReturnValue(500D);
    }

    @Inject(method = "getMaxClientShootingDistance", at = @At("RETURN"), remap = false, cancellable = true)
    private void constantMaxClientShootingDistance(ItemStack itemStack, GunClientState gunClientState,
                                                   CallbackInfoReturnable<Double> cir) {
        cir.setReturnValue(500D);
    }

    @Inject(method = "adjustInaccuracy", at = @At("RETURN"), remap = false, cancellable = true)
    private void constantAdjustedInaccuracy(Player player, ItemStack itemStack, boolean isAiming,
                                  CallbackInfoReturnable<Double> cir) {
        cir.setReturnValue(0.015D);
    }
}