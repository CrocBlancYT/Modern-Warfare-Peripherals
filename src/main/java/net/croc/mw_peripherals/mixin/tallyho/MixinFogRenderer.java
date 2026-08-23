package net.croc.mw_peripherals.mixin.tallyho;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.FogRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FogRenderer.class)
public abstract class MixinFogRenderer {
    @Inject(
        method = "setupFog",
        at = @At("TAIL")
    )
    private static void extendFogDistance(Camera camera, FogRenderer.FogMode fogMode, float viewDistance, boolean thickFog, float partialTick, CallbackInfo ci) {
        if (fogMode == FogRenderer.FogMode.FOG_TERRAIN) {
            RenderSystem.setShaderFogStart(9000.0F);
            RenderSystem.setShaderFogEnd(10000.0F);
        }
    }
}