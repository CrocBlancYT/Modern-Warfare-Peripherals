package net.croc.mw_peripherals.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.foundation.render.CachedBufferer;
import net.croc.mw_peripherals.utils.MixinHatch;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class HatchBlockEntityRenderer implements BlockEntityRenderer<BlockEntity> {
    public HatchBlockEntityRenderer(BlockEntityRendererProvider.Context context) {}

    public boolean shouldRender(BlockEntity blockEntity, Vec3 cameraPos) {
        return true;
    }

    public void render(BlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        BlockState state = blockEntity.getBlockState();
        VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.cutout());

        if (blockEntity instanceof MixinHatch hatch) {
            CachedBufferer.block(state)
                    .translateY(hatch.getHeight() * 0.0625F)
                    .light(packedLight)
                    .renderInto(poseStack, vertexConsumer);
        }

    }
}
