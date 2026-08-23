package net.croc.mw_peripherals.render;

import com.jozufozu.flywheel.core.PartialModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.foundation.render.CachedBufferer;
import net.croc.mw_peripherals.PartialModels;
import net.croc.mw_peripherals.Shapes;
import net.croc.mw_peripherals.blocks.launchers.RocketPod4BlockEntity;
import net.croc.mw_peripherals.blocks.launchers.RocketPod7BlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class RocketPod7BlockEntityRenderer implements BlockEntityRenderer<RocketPod7BlockEntity> {
    public RocketPod7BlockEntityRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    public boolean shouldRender(RocketPod7BlockEntity aps, Vec3 cameraPos) {
        return true;
    }

    private float[] x_rocket_offsets = {6.3f, 8.8f, 5.15f, 7.55f, 9.95f, 6.3f, 8.8f};
    private float[] y_rocket_offsets = {1.7f, 1.7f, 4.15f, 4.15f, 4.15f, 6.6f, 6.6f};

    @Override
    public void render(RocketPod7BlockEntity pod, float v, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        BlockState blockState = pod.getBlockState();
        Direction facing = pod.getFacing();
        int color = pod.getColor();

        VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.cutout());

        CachedBufferer.partial(PartialModels.ROCKET_POD_7_COLORED, blockState)
                .translate((float) pod.render_x_offset * 0.0625F, (float) pod.render_y_offset  * 0.0625F, (float) pod.render_z_offset * 0.0625F)
                .centre()
                .rotateToFace(facing)
                .unCentre()
                .light(packedLight)
                .color(color)
                .renderInto(poseStack, vertexConsumer);

        CachedBufferer.partial(PartialModels.ROCKET_POD_7_UNCOLORED, blockState)
                .translate((float) pod.render_x_offset * 0.0625F, (float) pod.render_y_offset  * 0.0625F, (float) pod.render_z_offset * 0.0625F)
                .centre()
                .rotateToFace(facing)
                .unCentre()
                .light(packedLight)
                .renderInto(poseStack, vertexConsumer);

        for (int i = 0; i < Math.min(7, pod.getLoadedRockets()); i++) {
            CachedBufferer.partial(PartialModels.ROCKET_POD_7_ROCKET, blockState)
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