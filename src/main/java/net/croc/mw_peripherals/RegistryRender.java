package net.croc.mw_peripherals;

import net.croc.mw_peripherals.client.hmd.HMDRenderer;
import net.croc.mw_peripherals.client.model.Modelflighthelmet;
import net.croc.mw_peripherals.items.FighterhelmItem;
import net.croc.mw_peripherals.particles.MissileCoreParticle;
import net.croc.mw_peripherals.render.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.RegistryObject;

import static net.mcreator.rha.init.RhaModBlockEntities.*;

@EventBusSubscriber(modid = Main.MOD_ID, value = {Dist.CLIENT}, bus = Mod.EventBusSubscriber.Bus.MOD)
public class RegistryRender {
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ItemBlockRenderTypes.setRenderLayer(
                    RegistryBlocks.GLASS_DARK_15.getBlock().get(),
                    RenderType.translucent()
            );
            ItemBlockRenderTypes.setRenderLayer(
                    RegistryBlocks.GLASS_DARK_50.getBlock().get(),
                    RenderType.translucent()
            );
            ItemBlockRenderTypes.setRenderLayer(
                    RegistryBlocks.GLASS_ORANGE_50.getBlock().get(),
                    RenderType.translucent()
            );
        });
    }

    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(RegistryBlockEntities.FIXED_APS_BLOCK_ENTITY.get(), APSFixedRenderer::new);
        event.registerBlockEntityRenderer(RegistryBlockEntities.Y_APS_BLOCK_ENTITY.get(), APSYRotRenderer::new);
        event.registerBlockEntityRenderer(RegistryBlockEntities.ZY_APS_BLOCK_ENTITY.get(), APSZYRotRenderer::new);

        event.registerBlockEntityRenderer(RegistryBlockEntities.RADAR_BLOCK_ENTITY.get(), RadarBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(RegistryBlockEntities.JET_ENGINE_BLOCK_ENTITY.get(), JetEngineBlockEntityRenderer::new);

        event.registerBlockEntityRenderer(RegistryBlockEntities.ROCKET_POD_4_BLOCK_ENTITY.get(), RocketPod4BlockEntityRenderer::new);
        event.registerBlockEntityRenderer(RegistryBlockEntities.ROCKET_POD_7_BLOCK_ENTITY.get(), RocketPod7BlockEntityRenderer::new);
        event.registerBlockEntityRenderer(RegistryBlockEntities.ROCKET_POD_19_BLOCK_ENTITY.get(), RocketPod19BlockEntityRenderer::new);

        RegistryObject<BlockEntityType<?>>[] types = new RegistryObject[]{
                HATCHALGAE, HATCHOLIVE, HATCH_4BO, HATCHARDENNE, HATCHAZURE, HATCHHORIZON, HATCHPATTON, HATCHCACTUS, HATCHCAMEL,
                HATCHCHARCOAL, HATCHDESERT, HATCHDUST, HATCHGELB, HATCHGINK, HATCHGORGE, HATCHGRAVEL, HATCHGRIZZLY, HATCHHIDE,
                HATCHJET, HATCHKAMPFGRAU, HATCHKAT, HATCHPANZERGRAU, HATCHPARADE, HATCHPINE, HATCHSLATE, HATCHSNOW, HATCHSCALE,
                HATCHLEY, HATCHROTA, HATCHTYPE, HATCHCORAL, HATCHCHERENKOV
        };
        
        for (RegistryObject<BlockEntityType<?>> type : types) {
            event.registerBlockEntityRenderer(type.get(), HatchBlockEntityRenderer::new);
        }
    }

    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(Modelflighthelmet.LAYER_LOCATION, Modelflighthelmet::createBodyLayer);
    }

    @SubscribeEvent
    public static void registerParticleProviders(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(RegistryParticles.MISSILE_CORE_PARTICLE.get(), (spriteSet) ->
                (type, level,
                 x, y, z,
                 xSpeed, ySpeed, zSpeed) -> {
            MissileCoreParticle particle = new MissileCoreParticle(level, x, y, z);
            particle.pickSprite(spriteSet);
            return particle;
        });
    }

    @Mod.EventBusSubscriber(modid = Main.MOD_ID, value = {Dist.CLIENT}, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class HMDHandler implements IGuiOverlay {
        private static boolean isActive = false;

        @SubscribeEvent
        @OnlyIn(Dist.CLIENT)
        public static void onClientTick(TickEvent.ClientTickEvent event) {
            if (event.phase == TickEvent.Phase.END) {
                Player player = Minecraft.getInstance().player;
                if (player != null) {
                    ItemStack helmet = player.getItemBySlot(EquipmentSlot.HEAD);
                    isActive = helmet.getItem() instanceof FighterhelmItem;
                }
            }
        }

        @Override
        public void render(ForgeGui forgeGui, GuiGraphics guiGraphics, float partialTick, int width, int height) {
            if (HMDHandler.isActive) {
                HMDRenderer.onRender(guiGraphics);
            }
        }
    }

    @SubscribeEvent
    public static void registerOverlays(RegisterGuiOverlaysEvent event) {
        event.registerAboveAll("hmd_overlay", new HMDHandler());
    }
}
