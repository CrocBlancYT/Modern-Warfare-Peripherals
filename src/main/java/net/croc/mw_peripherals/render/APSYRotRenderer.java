package net.croc.mw_peripherals.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.foundation.render.CachedBufferer;
import net.croc.mw_peripherals.PartialModels;
import net.croc.mw_peripherals.blocks.aps.APS;
import net.croc.mw_peripherals.blocks.aps.YRotatedAPS;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class APSYRotRenderer implements BlockEntityRenderer<YRotatedAPS> {
    public APSYRotRenderer(BlockEntityRendererProvider.Context context) {}

    public boolean shouldRender(YRotatedAPS aps, Vec3 cameraPos) {
        return true;
    }

    public void render(YRotatedAPS aps, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        BlockState blockState = aps.getBlockState();
        
        float yaw = aps.getYRot();
        Direction facing = aps.getFacing();
        int heightOffset = aps.shape.getHeightOffset();

        int color = aps.getColor();
        if (color == -1) return;

        VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.cutout());

        CachedBufferer.partial(aps.entry.MODEL_BASE, blockState)
                .translateY(heightOffset * 0.0625F)
                .centre()
                .rotateToFace(facing)
                .unCentre()
                .light(packedLight)
                .color(color)
                .renderInto(poseStack, vertexConsumer);

        CachedBufferer.partial(aps.entry.MODEL_CRADLE, blockState)
                .translateY(heightOffset * 0.0625F)
                .centre()
                .rotateToFace(facing)
                .rotateY(yaw)
                .unCentre()
                .light(packedLight)
                .color(color)
                .renderInto(poseStack, vertexConsumer);

        CachedBufferer.partial(aps.entry.MODEL_CHARGES, blockState)
                .translateY(heightOffset * 0.0625F)
                .centre()
                .rotateToFace(facing)
                .rotateY(yaw)
                .unCentre()
                .light(packedLight)
                .renderInto(poseStack, vertexConsumer);
    }
}
