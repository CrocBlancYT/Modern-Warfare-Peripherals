package net.croc.mw_peripherals.mixin.vista;

import net.mehvahdjukaar.vista.client.renderer.LevelRendererCameraState;
import net.minecraft.client.renderer.LevelRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.croc.mw_peripherals.mixinducks.LevelRendererVanillaDuck;

/* EXTRACTED FROM VS2 */
@Mixin(LevelRendererCameraState.class)
public class LevelRendererCameraStateMixin {
    LevelRendererVanillaDuck.VisibleChunkData capturedData;

    @Inject(method = "copyFrom", at = @At("HEAD"), remap = false)
    private void vs$copyFrom(LevelRenderer lr, CallbackInfo ci){
        capturedData = ((LevelRendererVanillaDuck)lr).vs$captureShipVisibleChunks();
    }

    @Inject(method = "apply", at = @At("HEAD"), remap = false)
    private void vs$apply(LevelRenderer lr, CallbackInfo ci){
        if(capturedData != null) {
            ((LevelRendererVanillaDuck)lr).vs$reloadShipVisibleChunks(capturedData);
        }
    }
}
