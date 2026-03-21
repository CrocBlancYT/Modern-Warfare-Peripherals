package net.croc.mw_peripherals.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.foundation.render.CachedBufferer;
import net.croc.mw_peripherals.PartialModels;
import net.croc.mw_peripherals.blocks.APSBlock;
import net.croc.mw_peripherals.blocks.APSBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class APSBlockEntityRenderer implements BlockEntityRenderer<APSBlockEntity> {
    public APSBlockEntityRenderer(BlockEntityRendererProvider.Context context) {}

    public boolean shouldRender(APSBlockEntity blockEntity, Vec3 cameraPos) {
        return true;
    }

    public void render(APSBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        BlockState blockState = blockEntity.getBlockState();
        String variant = ((APSBlock) blockState.getBlock()).getVariant();

        blockEntity.onRender();
        float yaw = blockEntity.getClientYaw();
        float pitch = blockEntity.getClientPitch();
        Direction facing = blockEntity.getFacing();
        int heightOffset = blockEntity.getHeightOffset();

        VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.cutout());

        CachedBufferer.partial(PartialModels.APS_BASES.get(variant), blockState)
                .translateY(heightOffset * 0.0625F)
                .centre()
                .rotateToFace(facing)
                .unCentre()
                .light(packedLight)
                .renderInto(poseStack, vertexConsumer);

        CachedBufferer.partial(PartialModels.APS_CRADLES.get(variant), blockState)
                .translateY(heightOffset * 0.0625F)
                .centre()
                .rotateToFace(facing)
                .rotateY(yaw)
                .unCentre()
                .light(packedLight)
                .renderInto(poseStack, vertexConsumer);

        CachedBufferer.partial(PartialModels.APS_TURRETS.get(variant), blockState)
                .translateY(heightOffset * 0.0625F)
                .centre()
                .rotateToFace(facing)
                .rotateY(yaw)
                .rotateX(pitch)
                .unCentre()
                .light(packedLight)
                .renderInto(poseStack, vertexConsumer);
    }
}
