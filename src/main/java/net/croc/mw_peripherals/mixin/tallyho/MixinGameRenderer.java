package net.croc.mw_peripherals.mixin.tallyho;

import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = GameRenderer.class, priority = 1500)
public class MixinGameRenderer {
    @Inject(method = {"getDepthFar"}, at = {@At("HEAD")}, cancellable = true)
    private void moreFar(CallbackInfoReturnable<Float> cir) {
        cir.setReturnValue(3000f);
    }
}