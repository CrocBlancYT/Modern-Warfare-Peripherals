package net.croc.mw_peripherals.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.foundation.render.CachedBufferer;
import net.croc.mw_peripherals.PartialModels;
import net.croc.mw_peripherals.blocks.RadarBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class RadarBlockEntityRenderer implements BlockEntityRenderer<RadarBlockEntity> {
    public RadarBlockEntityRenderer(BlockEntityRendererProvider.Context context) {}

    public boolean shouldRender(RadarBlockEntity blockEntity, Vec3 cameraPos) {
        return true;
    }

    public void render(RadarBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {

        blockEntity.onRender();
        float yaw = blockEntity.getClientYaw();
        float pitch = blockEntity.getClientPitch();
        Direction facing = blockEntity.getFacing();

        BlockState blockState = blockEntity.getBlockState();
        VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.cutout());

        CachedBufferer.partial(PartialModels.RADAR_BASE, blockState)
                .centre()
                .rotateToFace(facing)
                .rotateX(-90)
                .unCentre()
                .light(packedLight)
                .renderInto(poseStack, vertexConsumer);

        CachedBufferer.partial(PartialModels.RADAR_CRADLE, blockState)
                .centre()
                .rotateToFace(facing)
                .rotateX(-90)
                .rotateY(yaw)
                .unCentre()
                .light(packedLight)
                .renderInto(poseStack, vertexConsumer);

        CachedBufferer.partial(PartialModels.RADAR_DISH, blockState)
                .centre()
                .rotateToFace(facing)
                .rotateX(-90)
                .rotateY(yaw)
                .rotateX(pitch)
                .unCentre()
                .light(packedLight)
                .renderInto(poseStack, vertexConsumer);

    }
}
