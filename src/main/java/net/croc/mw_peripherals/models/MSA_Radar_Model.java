package net.croc.mw_peripherals.models;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.croc.mw_peripherals.blocks.GyroBlock;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;

public class MSA_Radar_Model {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation("modid", "msa_radar"), "main");
	private final ModelPart bone;
	private final ModelPart Dish;

	public MSA_Radar_Model(ModelPart root) {
		this.bone = root.getChild("bone");
		this.Dish = this.bone.getChild("Dish");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition bone = partdefinition.addOrReplaceChild("bone", CubeListBuilder.create().texOffs(0, 0).addBox(-16.0F, -6.2F, 0.0F, 16.0F, 6.2F, 16.0F, new CubeDeformation(0.0F)), PartPose.offset(8.0F, 24.0F, -8.0F));

		PartDefinition DishBase_r1 = bone.addOrReplaceChild("DishBase_r1", CubeListBuilder.create().texOffs(29, 23).addBox(-3.0F, -3.0F, -4.0F, 6.0F, 6.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-8.0F, -5.8858F, 8.0F, -1.5708F, -0.7854F, 1.5708F));

		PartDefinition DishBase_r2 = bone.addOrReplaceChild("DishBase_r2", CubeListBuilder.create().texOffs(0, 23).addBox(-3.0F, -3.0F, -4.0F, 6.0F, 6.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-8.0F, -5.8858F, 8.0F, 0.0F, 0.0F, 0.7854F));

		PartDefinition Dish = bone.addOrReplaceChild("Dish", CubeListBuilder.create().texOffs(18, 63).addBox(-0.7F, -11.6F, -0.4F, 1.4F, 0.8F, 0.8F, new CubeDeformation(0.0F))
		.texOffs(65, 14).addBox(-0.4F, -11.0F, -0.4F, 0.8F, 1.3F, 0.8F, new CubeDeformation(0.0F))
		.texOffs(18, 60).addBox(-0.5F, -11.5F, -0.6F, 1.0F, 0.7F, 1.2F, new CubeDeformation(0.0F))
		.texOffs(0, 38).addBox(-3.5F, -3.6F, -3.5F, 7.0F, 2.4F, 7.0F, new CubeDeformation(0.0F)), PartPose.offset(-8.0F, -4.6F, 8.0F));

		PartDefinition a_r1 = Dish.addOrReplaceChild("a_r1", CubeListBuilder.create().texOffs(36, 59).addBox(0.7927F, 1.0023F, -0.3F, 0.6F, 8.1F, 0.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -12.0F, 0.0F, -2.5703F, -0.5724F, 2.271F));

		PartDefinition a_r2 = Dish.addOrReplaceChild("a_r2", CubeListBuilder.create().texOffs(31, 59).addBox(0.7927F, 1.0023F, -0.3F, 0.6F, 8.1F, 0.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -12.0F, 0.0F, -0.5713F, -0.5724F, 0.8706F));

		PartDefinition a_r3 = Dish.addOrReplaceChild("a_r3", CubeListBuilder.create().texOffs(26, 59).addBox(0.7927F, 1.0023F, -0.3F, 0.6F, 8.1F, 0.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -12.0F, 0.0F, 2.5703F, 0.5724F, 2.271F));

		PartDefinition a_r4 = Dish.addOrReplaceChild("a_r4", CubeListBuilder.create().texOffs(58, 23).addBox(0.7927F, 1.0023F, -0.3F, 0.6F, 8.1F, 0.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -12.0F, 0.0F, 0.5713F, 0.5724F, 0.8706F));

		PartDefinition c_r1 = Dish.addOrReplaceChild("c_r1", CubeListBuilder.create().texOffs(65, 12).addBox(2.4707F, 2.6297F, -0.55F, 3.0F, 0.3F, 1.4F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -5.0F, 0.0F, -0.1526F, 0.3152F, -0.4606F));

		PartDefinition c_r2 = Dish.addOrReplaceChild("c_r2", CubeListBuilder.create().texOffs(13, 57).addBox(3.2629F, 4.8587F, -0.85F, 4.0F, 0.3F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -5.0F, 0.0F, -0.2898F, 0.1975F, -0.9888F));

		PartDefinition c_r3 = Dish.addOrReplaceChild("c_r3", CubeListBuilder.create().texOffs(65, 10).addBox(2.4707F, 2.6297F, -0.55F, 3.0F, 0.3F, 1.4F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -5.0F, 0.0F, -0.263F, 0.5086F, -0.5051F));

		PartDefinition c_r4 = Dish.addOrReplaceChild("c_r4", CubeListBuilder.create().texOffs(0, 57).addBox(3.2629F, 4.8587F, -0.85F, 4.0F, 0.3F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -5.0F, 0.0F, -0.481F, 0.3133F, -1.0374F));

		PartDefinition c_r5 = Dish.addOrReplaceChild("c_r5", CubeListBuilder.create().texOffs(65, 8).addBox(2.4707F, 2.6297F, -0.55F, 3.0F, 0.3F, 1.4F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -5.0F, 0.0F, -0.3999F, 0.6956F, -0.583F));

		PartDefinition c_r6 = Dish.addOrReplaceChild("c_r6", CubeListBuilder.create().texOffs(52, 56).addBox(3.2629F, 4.8587F, -0.85F, 4.0F, 0.3F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -5.0F, 0.0F, -0.6863F, 0.4176F, -1.1111F));

		PartDefinition c_r7 = Dish.addOrReplaceChild("c_r7", CubeListBuilder.create().texOffs(65, 6).addBox(2.4707F, 2.6297F, -0.55F, 3.0F, 0.3F, 1.4F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -5.0F, 0.0F, -0.5857F, 0.8701F, -0.7148F));

		PartDefinition c_r8 = Dish.addOrReplaceChild("c_r8", CubeListBuilder.create().texOffs(39, 56).addBox(3.2629F, 4.8587F, -0.85F, 4.0F, 0.3F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -5.0F, 0.0F, -0.9098F, 0.5049F, -1.211F));

		PartDefinition c_r9 = Dish.addOrReplaceChild("c_r9", CubeListBuilder.create().texOffs(65, 4).addBox(2.4707F, 2.6297F, -0.55F, 3.0F, 0.3F, 1.4F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -5.0F, 0.0F, -0.8598F, 1.0191F, -0.938F));

		PartDefinition c_r10 = Dish.addOrReplaceChild("c_r10", CubeListBuilder.create().texOffs(26, 56).addBox(3.2629F, 4.8587F, -0.85F, 4.0F, 0.3F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -5.0F, 0.0F, -1.1527F, 0.5692F, -1.3357F));

		PartDefinition c_r11 = Dish.addOrReplaceChild("c_r11", CubeListBuilder.create().texOffs(65, 2).addBox(2.4707F, 2.6297F, -0.55F, 3.0F, 0.3F, 1.4F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -5.0F, 0.0F, -1.2688F, 1.1165F, -1.2979F));

		PartDefinition c_r12 = Dish.addOrReplaceChild("c_r12", CubeListBuilder.create().texOffs(55, 47).addBox(3.2629F, 4.8587F, -0.85F, 4.0F, 0.3F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -5.0F, 0.0F, -1.4114F, 0.6049F, -1.4797F));

		PartDefinition c_r13 = Dish.addOrReplaceChild("c_r13", CubeListBuilder.create().texOffs(65, 0).addBox(2.4707F, 2.6297F, -0.55F, 3.0F, 0.3F, 1.4F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -5.0F, 0.0F, -1.7749F, 1.1264F, -1.7556F));

		PartDefinition c_r14 = Dish.addOrReplaceChild("c_r14", CubeListBuilder.create().texOffs(55, 44).addBox(3.2629F, 4.8587F, -0.85F, 4.0F, 0.3F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -5.0F, 0.0F, -1.6772F, 0.6082F, -1.6317F));

		PartDefinition c_r15 = Dish.addOrReplaceChild("c_r15", CubeListBuilder.create().texOffs(9, 64).addBox(2.4707F, 2.6297F, -0.55F, 3.0F, 0.3F, 1.4F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -5.0F, 0.0F, -2.2118F, 1.0439F, -2.1436F));

		PartDefinition c_r16 = Dish.addOrReplaceChild("c_r16", CubeListBuilder.create().texOffs(55, 41).addBox(3.2629F, 4.8587F, -0.85F, 4.0F, 0.3F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -5.0F, 0.0F, -1.9382F, 0.5788F, -1.7783F));

		PartDefinition c_r17 = Dish.addOrReplaceChild("c_r17", CubeListBuilder.create().texOffs(55, 38).addBox(3.2629F, 4.8587F, -0.85F, 4.0F, 0.3F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -5.0F, 0.0F, -2.6208F, 0.3352F, -2.0916F));

		PartDefinition c_r18 = Dish.addOrReplaceChild("c_r18", CubeListBuilder.create().texOffs(0, 64).addBox(2.4707F, 2.6297F, -0.55F, 3.0F, 0.3F, 1.4F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -5.0F, 0.0F, -3.0482F, 0.1974F, -2.696F));

		PartDefinition c_r19 = Dish.addOrReplaceChild("c_r19", CubeListBuilder.create().texOffs(13, 54).addBox(3.2629F, 4.8587F, -0.85F, 4.0F, 0.3F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -5.0F, 0.0F, -2.9619F, 0.1245F, -2.1704F));

		PartDefinition c_r20 = Dish.addOrReplaceChild("c_r20", CubeListBuilder.create().texOffs(0, 54).addBox(3.2629F, 4.8587F, -0.85F, 4.0F, 0.3F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -5.0F, 0.0F, -2.8145F, 0.2213F, -2.145F));

		PartDefinition c_r21 = Dish.addOrReplaceChild("c_r21", CubeListBuilder.create().texOffs(59, 63).addBox(2.4707F, 2.6297F, -0.55F, 3.0F, 0.3F, 1.4F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -5.0F, 0.0F, -2.9683F, 0.3542F, -2.6742F));

		PartDefinition c_r22 = Dish.addOrReplaceChild("c_r22", CubeListBuilder.create().texOffs(50, 63).addBox(2.4707F, 2.6297F, -0.55F, 3.0F, 0.3F, 1.4F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -5.0F, 0.0F, -2.8539F, 0.5467F, -2.6241F));

		PartDefinition c_r23 = Dish.addOrReplaceChild("c_r23", CubeListBuilder.create().texOffs(41, 63).addBox(2.4707F, 2.6297F, -0.55F, 3.0F, 0.3F, 1.4F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -5.0F, 0.0F, -2.5097F, 0.9025F, -2.391F));

		PartDefinition c_r24 = Dish.addOrReplaceChild("c_r24", CubeListBuilder.create().texOffs(52, 53).addBox(3.2629F, 4.8587F, -0.85F, 4.0F, 0.3F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -5.0F, 0.0F, -2.1847F, 0.5198F, -1.9076F));

		PartDefinition c_r25 = Dish.addOrReplaceChild("c_r25", CubeListBuilder.create().texOffs(39, 53).addBox(3.2629F, 4.8587F, -0.85F, 4.0F, 0.3F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -5.0F, 0.0F, -2.4121F, 0.4366F, -2.0127F));

		PartDefinition c_r26 = Dish.addOrReplaceChild("c_r26", CubeListBuilder.create().texOffs(63, 31).addBox(2.4707F, 2.6297F, -0.55F, 3.0F, 0.3F, 1.4F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -5.0F, 0.0F, -2.7095F, 0.7318F, -2.5375F));

		PartDefinition c_r27 = Dish.addOrReplaceChild("c_r27", CubeListBuilder.create().texOffs(63, 29).addBox(2.4707F, 2.6297F, -0.55F, 3.0F, 0.3F, 1.4F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -5.0F, 0.0F, 1.1755F, -1.1029F, -1.2143F));

		PartDefinition c_r28 = Dish.addOrReplaceChild("c_r28", CubeListBuilder.create().texOffs(26, 53).addBox(3.2629F, 4.8587F, -0.85F, 4.0F, 0.3F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -5.0F, 0.0F, 1.3588F, -0.6003F, -1.4498F));

		PartDefinition c_r29 = Dish.addOrReplaceChild("c_r29", CubeListBuilder.create().texOffs(63, 27).addBox(2.4707F, 2.6297F, -0.55F, 3.0F, 0.3F, 1.4F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -5.0F, 0.0F, 0.7954F, -0.9924F, -0.8836F));

		PartDefinition c_r30 = Dish.addOrReplaceChild("c_r30", CubeListBuilder.create().texOffs(52, 50).addBox(3.2629F, 4.8587F, -0.85F, 4.0F, 0.3F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -5.0F, 0.0F, 1.1026F, -0.5585F, -1.309F));

		PartDefinition c_r31 = Dish.addOrReplaceChild("c_r31", CubeListBuilder.create().texOffs(63, 25).addBox(2.4707F, 2.6297F, -0.55F, 3.0F, 0.3F, 1.4F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -5.0F, 0.0F, 0.543F, -0.8367F, -0.6826F));

		PartDefinition c_r32 = Dish.addOrReplaceChild("c_r32", CubeListBuilder.create().texOffs(13, 51).addBox(3.2629F, 4.8587F, -0.85F, 4.0F, 0.3F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -5.0F, 0.0F, 0.8635F, -0.4891F, -1.1889F));

		PartDefinition c_r33 = Dish.addOrReplaceChild("c_r33", CubeListBuilder.create().texOffs(63, 23).addBox(2.4707F, 2.6297F, -0.55F, 3.0F, 0.3F, 1.4F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -5.0F, 0.0F, 0.3695F, -0.659F, -0.564F));

		PartDefinition c_r34 = Dish.addOrReplaceChild("c_r34", CubeListBuilder.create().texOffs(0, 51).addBox(3.2629F, 4.8587F, -0.85F, 4.0F, 0.3F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -5.0F, 0.0F, 0.6439F, -0.3979F, -1.0942F));

		PartDefinition c_r35 = Dish.addOrReplaceChild("c_r35", CubeListBuilder.create().texOffs(9, 62).addBox(2.4707F, 2.6297F, -0.55F, 3.0F, 0.3F, 1.4F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -5.0F, 0.0F, 0.2393F, -0.4703F, -0.4939F));

		PartDefinition c_r36 = Dish.addOrReplaceChild("c_r36", CubeListBuilder.create().texOffs(39, 50).addBox(3.2629F, 4.8587F, -0.85F, 4.0F, 0.3F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -5.0F, 0.0F, 0.4418F, -0.2909F, -1.0257F));

		PartDefinition c_r37 = Dish.addOrReplaceChild("c_r37", CubeListBuilder.create().texOffs(0, 62).addBox(2.4707F, 2.6297F, -0.55F, 3.0F, 0.3F, 1.4F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -5.0F, 0.0F, 0.1325F, -0.276F, -0.4548F));

		PartDefinition c_r38 = Dish.addOrReplaceChild("c_r38", CubeListBuilder.create().texOffs(26, 50).addBox(3.2629F, 4.8587F, -0.85F, 4.0F, 0.3F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -5.0F, 0.0F, 0.2528F, -0.1733F, -0.982F));

		PartDefinition c_r39 = Dish.addOrReplaceChild("c_r39", CubeListBuilder.create().texOffs(13, 48).addBox(3.2629F, 4.8587F, -0.85F, 4.0F, 0.3F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -5.0F, 0.0F, 0.0715F, -0.05F, -0.9617F));

		PartDefinition c_r40 = Dish.addOrReplaceChild("c_r40", CubeListBuilder.create().texOffs(0, 48).addBox(3.2629F, 4.8587F, -0.85F, 4.0F, 0.3F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -5.0F, 0.0F, -0.1074F, 0.0749F, -0.964F));

		PartDefinition c_r41 = Dish.addOrReplaceChild("c_r41", CubeListBuilder.create().texOffs(59, 61).addBox(2.4707F, 2.6297F, -0.55F, 3.0F, 0.3F, 1.4F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -5.0F, 0.0F, -0.0556F, 0.1186F, -0.4396F));

		PartDefinition c_r42 = Dish.addOrReplaceChild("c_r42", CubeListBuilder.create().texOffs(50, 61).addBox(2.4707F, 2.6297F, -0.55F, 3.0F, 0.3F, 1.4F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -5.0F, 0.0F, 0.037F, -0.0791F, -0.4378F));

		PartDefinition c_r43 = Dish.addOrReplaceChild("c_r43", CubeListBuilder.create().texOffs(41, 61).addBox(2.4707F, 2.6297F, -0.55F, 3.0F, 0.3F, 1.4F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -5.0F, 0.0F, 2.675F, -0.7674F, -2.514F));

		PartDefinition c_r44 = Dish.addOrReplaceChild("c_r44", CubeListBuilder.create().texOffs(42, 47).addBox(3.2629F, 4.8587F, -0.85F, 4.0F, 0.3F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -5.0F, 0.0F, 2.3682F, -0.4549F, -1.9937F));

		PartDefinition c_r45 = Dish.addOrReplaceChild("c_r45", CubeListBuilder.create().texOffs(9, 60).addBox(2.4707F, 2.6297F, -0.55F, 3.0F, 0.3F, 1.4F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -5.0F, 0.0F, 2.4597F, -0.9338F, -2.3513F));

		PartDefinition c_r46 = Dish.addOrReplaceChild("c_r46", CubeListBuilder.create().texOffs(29, 47).addBox(3.2629F, 4.8587F, -0.85F, 4.0F, 0.3F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -5.0F, 0.0F, 2.1369F, -0.5338F, -1.8835F));

		PartDefinition c_r47 = Dish.addOrReplaceChild("c_r47", CubeListBuilder.create().texOffs(42, 44).addBox(3.2629F, 4.8587F, -0.85F, 4.0F, 0.3F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -5.0F, 0.0F, 1.8869F, -0.5872F, -1.7501F));

		PartDefinition c_r48 = Dish.addOrReplaceChild("c_r48", CubeListBuilder.create().texOffs(29, 44).addBox(3.2629F, 4.8587F, -0.85F, 4.0F, 0.3F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -5.0F, 0.0F, 1.624F, -0.6102F, -1.6013F));

		PartDefinition c_r49 = Dish.addOrReplaceChild("c_r49", CubeListBuilder.create().texOffs(0, 60).addBox(2.4707F, 2.6297F, -0.55F, 3.0F, 0.3F, 1.4F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -5.0F, 0.0F, 1.6737F, -1.1324F, -1.6641F));

		PartDefinition c_r50 = Dish.addOrReplaceChild("c_r50", CubeListBuilder.create().texOffs(59, 59).addBox(2.4707F, 2.6297F, -0.55F, 3.0F, 0.3F, 1.4F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -5.0F, 0.0F, 2.1359F, -1.0663F, -2.0775F));

		PartDefinition c_r51 = Dish.addOrReplaceChild("c_r51", CubeListBuilder.create().texOffs(42, 41).addBox(3.2629F, 4.8587F, -0.85F, 4.0F, 0.3F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -5.0F, 0.0F, 2.7767F, -0.2448F, -2.1363F));

		PartDefinition c_r52 = Dish.addOrReplaceChild("c_r52", CubeListBuilder.create().texOffs(42, 38).addBox(3.2629F, 4.8587F, -0.85F, 4.0F, 0.3F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -5.0F, 0.0F, 2.5804F, -0.3567F, -2.0779F));

		PartDefinition c_r53 = Dish.addOrReplaceChild("c_r53", CubeListBuilder.create().texOffs(50, 59).addBox(2.4707F, 2.6297F, -0.55F, 3.0F, 0.3F, 1.4F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -5.0F, 0.0F, 2.828F, -0.5844F, -2.6102F));

		PartDefinition c_r54 = Dish.addOrReplaceChild("c_r54", CubeListBuilder.create().texOffs(41, 59).addBox(2.4707F, 2.6297F, -0.55F, 3.0F, 0.3F, 1.4F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -5.0F, 0.0F, 2.947F, -0.3931F, -2.6664F));

		PartDefinition c_r55 = Dish.addOrReplaceChild("c_r55", CubeListBuilder.create().texOffs(29, 41).addBox(3.2629F, 4.8587F, -0.85F, 4.0F, 0.3F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -5.0F, 0.0F, 2.9619F, -0.1245F, -2.1704F));

		PartDefinition c_r56 = Dish.addOrReplaceChild("c_r56", CubeListBuilder.create().texOffs(58, 35).addBox(2.4707F, 2.6297F, -0.55F, 3.0F, 0.3F, 1.4F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -5.0F, 0.0F, 3.0482F, -0.1974F, -2.696F));

		PartDefinition c_r57 = Dish.addOrReplaceChild("c_r57", CubeListBuilder.create().texOffs(29, 38).addBox(3.2629F, 4.8587F, -0.85F, 4.0F, 0.3F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -5.0F, 0.0F, 3.1416F, 0.0F, -2.1817F));

		PartDefinition c_r58 = Dish.addOrReplaceChild("c_r58", CubeListBuilder.create().texOffs(58, 33).addBox(2.4707F, 2.6297F, -0.55F, 3.0F, 0.3F, 1.4F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -5.0F, 0.0F, 3.1416F, 0.0F, -2.7053F));

		return LayerDefinition.create(meshdefinition, 128, 128);
	}
	
	public void render(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		bone.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}