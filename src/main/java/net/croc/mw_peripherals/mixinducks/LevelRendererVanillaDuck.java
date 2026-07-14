package net.croc.mw_peripherals.mixinducks;

import it.unimi.dsi.fastutil.objects.ObjectList;
import java.util.WeakHashMap;
import net.minecraft.client.renderer.LevelRenderer.RenderChunkInfo;
import net.minecraft.client.renderer.culling.Frustum;
import org.valkyrienskies.core.api.ships.ClientShip;
import org.valkyrienskies.core.util.datastructures.BlockPos2ByteOpenHashMap;

public interface LevelRendererVanillaDuck {

    void vs$addShipVisibleChunks(final Frustum frustum);

    VisibleChunkData vs$captureShipVisibleChunks();

    void vs$reloadShipVisibleChunks(VisibleChunkData data);

    public record VisibleChunkData(WeakHashMap<ClientShip, ObjectList<RenderChunkInfo>> shipRenderChunks, BlockPos2ByteOpenHashMap visibleShipChunks) {

    }
    boolean vs$isShipChunkVisible(int chunkX, int sectionY, int chunkZ);

}
