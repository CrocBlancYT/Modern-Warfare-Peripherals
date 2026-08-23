package net.croc.mw_peripherals.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.croc.mw_peripherals.Main;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class Modelflighthelmet<T extends Entity> extends EntityModel<T> {
  public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(Main.MOD_ID, "modelflighthelmet"), "main");
  
  public final ModelPart helmet2;
  
  public Modelflighthelmet(ModelPart root) {
    this.helmet2 = root.getChild("helmet2");
  }
  
  public static LayerDefinition createBodyLayer() {
    MeshDefinition meshdefinition = new MeshDefinition();
    PartDefinition partdefinition = meshdefinition.getRoot();
    PartDefinition helmet2 = partdefinition.addOrReplaceChild("helmet2",
        CubeListBuilder.create().texOffs(24, 24).addBox(-4.603F, -6.003F, -4.853F, 9.006F, 3.006F, 4.006F, new CubeDeformation(0.0F)).texOffs(40, 41).addBox(-1.6F, -2.75F, -4.7F, 3.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)).texOffs(46, 6)
        .addBox(-0.85F, -0.5F, -5.75F, 1.5F, 1.0F, 1.5F, new CubeDeformation(0.0F)).texOffs(0, 0).addBox(-4.35F, -8.25F, -2.85F, 8.5F, 5.5F, 7.5F, new CubeDeformation(0.0F)).texOffs(0, 55)
        .addBox(-4.35F, -2.75F, -1.85F, 8.5F, 2.5F, 6.5F, new CubeDeformation(0.0F)).texOffs(0, 3).addBox(1.551F, -2.849F, -3.699F, 2.698F, 2.698F, 1.698F, new CubeDeformation(0.0F)).texOffs(0, 0)
        .addBox(-4.349F, -2.749F, -3.599F, 2.498F, 2.498F, 1.498F, new CubeDeformation(0.0F)).texOffs(25, 7).addBox(-4.1F, -0.5F, -0.85F, 8.0F, 1.0F, 5.0F, new CubeDeformation(0.0F)).texOffs(38, 34)
        .addBox(-4.599F, -3.749F, -2.849F, 8.998F, 3.998F, 0.998F, new CubeDeformation(0.0F)).texOffs(14, 37).addBox(-2.1F, -0.25F, -2.85F, 4.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(24, 41)
        .addBox(3.55F, -1.019F, -4.1889F, 1.0F, 1.0F, 6.0F, new CubeDeformation(0.0F)).texOffs(25, 13).addBox(-0.1F, -0.5F, -5.6F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(44, 22)
        .addBox(-4.605F, -1.505F, -1.605F, 1.01F, 1.01F, 1.01F, new CubeDeformation(0.0F)).texOffs(9, 42).addBox(3.395F, -1.505F, -1.605F, 1.01F, 1.01F, 1.01F, new CubeDeformation(0.0F)).texOffs(32, 37)
        .addBox(4.399F, -3.001F, -2.601F, 0.002F, 3.002F, 4.002F, new CubeDeformation(0.0F)).texOffs(14, 30).addBox(-4.6F, -3.0F, -2.6F, 0.0F, 3.0F, 4.0F, new CubeDeformation(0.0F)).texOffs(22, 20)
        .addBox(-4.1F, -8.75F, -1.6F, 8.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)).texOffs(0, 12).addBox(-4.604F, -5.104F, -2.104F, 9.008F, 1.008F, 7.008F, new CubeDeformation(0.0F)).texOffs(30, 39)
        .addBox(-1.85F, -0.5F, -5.6F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(0, 23).addBox(-2.1F, 0.5F, -5.35F, 3.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)),
        PartPose.offset(0.0F, 24.0F, 1.0F));
    PartDefinition cube_r1 = helmet2.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(38, 31).addBox(-4.5F, -0.5F, -1.0F, 9.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)),
        PartPose.offsetAndRotation(-0.1F, -7.0344F, -0.0434F, 0.3927F, 0.0F, 0.0F));
    PartDefinition cube_r2 = helmet2.addOrReplaceChild("cube_r2",
        CubeListBuilder.create().texOffs(0, 42).addBox(-0.501F, -1.501F, -1.501F, 1.002F, 3.002F, 3.002F, new CubeDeformation(0.0F)).texOffs(8, 44).addBox(8.498F, -1.501F, -1.501F, 1.002F, 3.002F, 3.002F, new CubeDeformation(0.0F)),
        PartPose.offsetAndRotation(-4.6F, -2.5201F, 2.3059F, -0.3927F, 0.0F, 0.0F));
    PartDefinition cube_r3 = helmet2.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(5, 42).addBox(4.25F, 1.25F, 0.25F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)),
        PartPose.offsetAndRotation(-0.1F, -3.5F, 1.9F, -0.3927F, 0.0F, 0.0F));
    PartDefinition cube_r4 = helmet2.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(32, 44).addBox(-1.0F, -1.5F, 0.0F, 2.0F, 3.0F, 0.0F, new CubeDeformation(0.0F)),
        PartPose.offsetAndRotation(2.3239F, -1.25F, -5.0827F, 0.0F, 0.3927F, 0.0F));
    PartDefinition cube_r5 = helmet2.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(0, 12).addBox(-1.5F, -1.5F, 0.0F, 3.0F, 3.0F, 0.0F, new CubeDeformation(0.0F)),
        PartPose.offsetAndRotation(2.7858F, -1.25F, -4.126F, 0.0F, -0.3927F, 0.0F));
    PartDefinition cube_r6 = helmet2.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(36, 44).addBox(-1.0F, -1.5F, 0.0F, 2.0F, 3.0F, 0.0F, new CubeDeformation(0.0F)),
        PartPose.offsetAndRotation(-2.5239F, -1.25F, -5.0827F, 0.0F, -0.3927F, 0.0F));
    PartDefinition cube_r7 = helmet2.addOrReplaceChild("cube_r7", CubeListBuilder.create().texOffs(0, 15).addBox(-1.5F, -1.5F, 0.0F, 3.0F, 3.0F, 0.0F, new CubeDeformation(0.0F)),
        PartPose.offsetAndRotation(-2.9858F, -1.25F, -4.126F, 0.0F, 0.3927F, 0.0F));
    PartDefinition cube_r8 = helmet2.addOrReplaceChild("cube_r8", CubeListBuilder.create().texOffs(22, 24).addBox(-1.0F, -0.75F, -0.75F, 2.0F, 1.5F, 1.5F, new CubeDeformation(0.0F)),
        PartPose.offsetAndRotation(0.0F, -0.4955F, -4.6872F, 0.7854F, 0.0F, 0.0F));
    PartDefinition cube_r9 = helmet2.addOrReplaceChild("cube_r9", CubeListBuilder.create().texOffs(0, 20).addBox(-1.0F, -1.25F, -0.75F, 2.0F, 2.5F, 1.5F, new CubeDeformation(0.0F)),
        PartPose.offsetAndRotation(-0.1F, -1.3633F, -4.5766F, -0.3927F, 0.0F, 0.0F));
    PartDefinition cube_r10 = helmet2.addOrReplaceChild("cube_r10", CubeListBuilder.create().texOffs(41, 19).addBox(-1.55F, -1.0F, -0.75F, 3.0F, 2.0F, 1.5F, new CubeDeformation(0.0F)),
        PartPose.offsetAndRotation(-0.1F, -1.0367F, -4.4413F, -0.3927F, 0.0F, 0.0F));
    PartDefinition cube_r11 = helmet2.addOrReplaceChild("cube_r11", CubeListBuilder.create().texOffs(0, 28).addBox(-4.501F, -1.501F, -1.501F, 9.002F, 3.002F, 3.002F, new CubeDeformation(0.0F)),
        PartPose.offsetAndRotation(-0.1F, -7.036F, -2.1248F, 0.3927F, 0.0F, 0.0F));
    PartDefinition cube_r12 = helmet2.addOrReplaceChild("cube_r12", CubeListBuilder.create().texOffs(25, 13).addBox(-4.502F, -1.002F, -2.002F, 9.004F, 2.004F, 4.004F, new CubeDeformation(0.0F)),
        PartPose.offsetAndRotation(-0.1F, -6.1585F, -2.6196F, -0.3927F, 0.0F, 0.0F));
    PartDefinition cube_r13 = helmet2.addOrReplaceChild("cube_r13", CubeListBuilder.create().texOffs(36, 37).addBox(-4.5F, -1.0F, -1.0F, 9.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)),
        PartPose.offsetAndRotation(-0.1F, -3.0F, -3.4358F, 0.7854F, 0.0F, 0.0F));
    PartDefinition box2 = helmet2.addOrReplaceChild("box2",
        CubeListBuilder.create().texOffs(39, 0).addBox(-12.0F, -8.0F, 4.0F, 8.0F, 5.0F, 0.0F, new CubeDeformation(0.0F)).texOffs(0, 27).addBox(-12.0F, -8.0F, 4.0F, 0.0F, 8.0F, 7.0F, new CubeDeformation(0.0F)).texOffs(24, 24)
        .addBox(-4.0F, -8.0F, 4.0F, 0.0F, 8.0F, 7.0F, new CubeDeformation(0.0F)).texOffs(16, 0).addBox(-12.0F, -8.0F, 4.0F, 8.0F, 0.0F, 7.0F, new CubeDeformation(0.0F)).texOffs(14, 39)
        .addBox(-12.0F, -8.0F, 11.0F, 8.0F, 8.0F, 0.0F, new CubeDeformation(0.0F)),
        PartPose.offset(7.9F, 0.0F, -7.6F));
    return LayerDefinition.create(meshdefinition, 64, 64);
  }
  
  public void setupAnim(@NotNull T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {}
  
  public void renderToBuffer(@NotNull PoseStack poseStack, @NotNull VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
    this.helmet2.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
  }
}
