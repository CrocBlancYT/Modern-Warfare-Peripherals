package net.croc.mw_peripherals.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.croc.mw_peripherals.blocks.JetEngineBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;

public class JetEngineBlockEntityRenderer implements BlockEntityRenderer<JetEngineBlockEntity> {
    public JetEngineBlockEntityRenderer(BlockEntityRendererProvider.Context context) {}

    public boolean shouldRender(JetEngineBlockEntity blockEntity, Vec3 cameraPos) {
        return true;
    }

    public void render(JetEngineBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        Direction facing = blockEntity.getFacing();


    }
}
