package net.croc.mw_peripherals.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.foundation.render.CachedBufferer;
import net.croc.mw_peripherals.PartialModels;
import net.croc.mw_peripherals.blocks.launchers.RocketPod19BlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class RocketPod19BlockEntityRenderer implements BlockEntityRenderer<RocketPod19BlockEntity> {
    public RocketPod19BlockEntityRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    public boolean shouldRender(RocketPod19BlockEntity aps, Vec3 cameraPos) {
        return true;
    }

    private float[] x_rocket_offsets = new float[]{5.08f, 7.51f, 9.9063f, 3.865f, 6.295f, 8.6913f, 11.1212f, 2.7513f, 5.08f, 7.51f, 9.9063f, 12.3362f, 3.865f, 6.295f, 8.6913f, 11.1212f, 5.08f, 7.51f, 9.9063f};
    private float[] y_rocket_offsets = new float[]{1.1981f, 1.1981f, 1.1981f, 3.5606f, 3.5606f, 3.5606f, 3.5606f, 5.9231f, 5.9231f, 5.9231f, 5.9231f, 5.9231f, 8.3531f, 8.3531f, 8.3531f, 8.3531f, 10.7831f, 10.7831f, 10.7831f};

    @Override
    public void render(RocketPod19BlockEntity pod, float v, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        BlockState blockState = pod.getBlockState();
        Direction facing = pod.getFacing();
        int color = pod.getColor();

        VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.cutout());

        CachedBufferer.partial(PartialModels.ROCKET_POD_19_COLORED, blockState)
                .translate((float) pod.render_x_offset * 0.0625F, (float) pod.render_y_offset  * 0.0625F, (float) pod.render_z_offset * 0.0625F)
                .centre()
                .rotateToFace(facing)
                .unCentre()
                .light(packedLight)
                .color(color)
                .renderInto(poseStack, vertexConsumer);

        CachedBufferer.partial(PartialModels.ROCKET_POD_19_UNCOLORED, blockState)
                .translate((float) pod.render_x_offset * 0.0625F, (float) pod.render_y_offset  * 0.0625F, (float) pod.render_z_offset * 0.0625F)
                .centre()
                .rotateToFace(facing)
                .unCentre()
                .light(packedLight)
                .renderInto(poseStack, vertexConsumer);
        
        for (int i = 0; i < Math.min(19, pod.getLoadedRockets()); i++) {
            CachedBufferer.partial(PartialModels.ROCKET_POD_19_ROCKET, blockState)
                    .translate((float) pod.render_x_offset * 0.0625F, (float) pod.render_y_offset  * 0.0625F, (float) pod.render_z_offset * 0.0625F)
                    .centre()
                    .rotateToFace(facing)
                    .unCentre()
                    .translate(x_rocket_offsets[i] * 0.0625, y_rocket_offsets[i] * 0.0625, 0)
                    .light(packedLight)
                    .renderInto(poseStack, vertexConsumer);
        }
    }
}