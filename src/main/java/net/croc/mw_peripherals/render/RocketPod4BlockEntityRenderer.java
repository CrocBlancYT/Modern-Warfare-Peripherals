package net.croc.mw_peripherals.render;

import com.jozufozu.flywheel.core.PartialModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.foundation.render.CachedBufferer;
import net.croc.mw_peripherals.PartialModels;
import net.croc.mw_peripherals.Shapes;
import net.croc.mw_peripherals.blocks.launchers.RocketPod4BlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class RocketPod4BlockEntityRenderer implements BlockEntityRenderer<RocketPod4BlockEntity> {
    public RocketPod4BlockEntityRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    public boolean shouldRender(RocketPod4BlockEntity aps, Vec3 cameraPos) {
        return true;
    }

    @Override
    public void render(RocketPod4BlockEntity pod, float v, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        BlockState blockState = pod.getBlockState();
        Direction facing = pod.getFacing();
        int color = pod.getColor();

        VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.cutout());

        CachedBufferer.partial(PartialModels.ROCKET_POD_4_COLORED, blockState)
                .translate((float) pod.render_x_offset * 0.0625F, (float) pod.render_y_offset  * 0.0625F, (float) pod.render_z_offset * 0.0625F)
                .centre()
                .rotateToFace(facing)
                .unCentre()
                .light(packedLight)
                .color(color)
                .renderInto(poseStack, vertexConsumer);


        CachedBufferer.partial(pod.getRocketState().model, blockState)
                .translate((float) pod.render_x_offset * 0.0625F, (float) pod.render_y_offset  * 0.0625F, (float) pod.render_z_offset * 0.0625F)
                .centre()
                .rotateToFace(facing)
                .unCentre()
                .light(packedLight)
                .renderInto(poseStack, vertexConsumer);
    }
}