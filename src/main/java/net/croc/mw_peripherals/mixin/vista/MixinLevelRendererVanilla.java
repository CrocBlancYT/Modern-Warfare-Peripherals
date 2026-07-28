package net.croc.mw_peripherals.mixin.vista;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import java.util.WeakHashMap;

import net.croc.mw_peripherals.mixinducks.LevelRendererVanillaDuck;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LevelRenderer.RenderChunkInfo;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.valkyrienskies.core.api.ships.ClientShip;
import org.valkyrienskies.core.util.datastructures.BlockPos2ByteOpenHashMap;

/* EXTRACTED FROM VS2 */
@Mixin(value = LevelRenderer.class, priority = 999)
public abstract class MixinLevelRendererVanilla implements LevelRendererVanillaDuck {
    @Unique
    private final WeakHashMap<ClientShip, ObjectList<RenderChunkInfo>> shipRenderChunks = new WeakHashMap<>();
    @Shadow
    private ClientLevel level;

    @Shadow
    @Final
    @Mutable
    private ObjectArrayList<RenderChunkInfo> renderChunksInFrustum;

    @Unique
    private BlockPos2ByteOpenHashMap vs$visibileShipChunks = new BlockPos2ByteOpenHashMap();

    @Override
    public VisibleChunkData vs$captureShipVisibleChunks() {
        WeakHashMap<ClientShip, ObjectList<RenderChunkInfo>> temp = new WeakHashMap<>();
        shipRenderChunks.forEach((ship, chunks) -> {
            ObjectArrayList<RenderChunkInfo> subTemp = new ObjectArrayList<>();
            chunks.forEach(subTemp::add);
            temp.put(ship, subTemp);
        });
        return new VisibleChunkData(temp, vs$visibileShipChunks);
    }

    @Override
    public void vs$reloadShipVisibleChunks(VisibleChunkData data) {
        this.vs$visibileShipChunks = data.visibleShipChunks();
        shipRenderChunks.forEach((ship, chunks) -> chunks.clear());
        shipRenderChunks.clear();
        this.shipRenderChunks.putAll(data.shipRenderChunks());
    }
}
