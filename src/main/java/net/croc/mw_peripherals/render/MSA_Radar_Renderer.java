package net.croc.mw_peripherals.render;

import net.croc.mw_peripherals.Main;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.croc.mw_peripherals.blocks.GyroBlockEntity;
import net.croc.mw_peripherals.models.MSA_Radar_Model;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.model.geom.ModelPart;

public class MSA_Radar_Renderer implements BlockEntityRenderer<GyroBlockEntity> {
    private final MSA_Radar_Model model;

    public MSA_Radar_Renderer(BlockEntityRendererProvider.Context context) {
        ModelPart modelPart = context.bakeLayer(MyModelLayers.DYNAMIC_MODEL);
        this.model = new MSA_Radar_Model(modelPart);
    }

    @Override
    public void render(GyroBlockEntity blockEntity, float partialTick,
                       PoseStack poseStack, MultiBufferSource bufferSource,
                       int packedLight, int packedOverlay) {

        poseStack.pushPose();
        poseStack.translate(0.5, 0, 0.5);

        //poseStack.translate(0.5, 0, 0.5);
        //poseStack.mulPose(Axis.ZP.rotationDegrees(180)); // Flip upright

        ResourceLocation texture = Main.resource("textures/block/dynamic_block.png");
        VertexConsumer vertexConsumer = bufferSource.getBuffer(
                RenderType.entitySolid(texture));

        model.render(poseStack, vertexConsumer, packedLight, packedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);

        poseStack.popPose();
    }
}