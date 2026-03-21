package net.croc.mw_peripherals;

import net.croc.mw_peripherals.render.APSBlockEntityRenderer;
import net.croc.mw_peripherals.render.HatchBlockEntityRenderer;
import net.croc.mw_peripherals.render.JetEngineBlockEntityRenderer;
import net.croc.mw_peripherals.render.RadarBlockEntityRenderer;
import net.mcreator.rha.block.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.registries.RegistryObject;

import static net.mcreator.rha.init.RhaModBlockEntities.*;

@EventBusSubscriber(modid = Main.MOD_ID, value = {Dist.CLIENT}, bus = Mod.EventBusSubscriber.Bus.MOD)
public class RegistryRender {
    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(RegistryBlockEntities.APS_BLOCK_ENTITY.get(), APSBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(RegistryBlockEntities.RADAR_BLOCK_ENTITY.get(), RadarBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(RegistryBlockEntities.JET_ENGINE_BLOCK_ENTITY.get(), JetEngineBlockEntityRenderer::new);

        RegistryObject<BlockEntityType<?>>[] types = new RegistryObject[] {
                HATCHALGAE, HATCHOLIVE, HATCH_4BO, HATCHARDENNE, HATCHAZURE, HATCHHORIZON, HATCHPATTON, HATCHCACTUS, HATCHCAMEL,
                HATCHCHARCOAL, HATCHDESERT, HATCHDUST, HATCHGELB, HATCHGINK, HATCHGORGE, HATCHGRAVEL, HATCHGRIZZLY, HATCHHIDE,
                HATCHJET, HATCHKAMPFGRAU, HATCHKAT, HATCHPANZERGRAU, HATCHPARADE, HATCHPINE, HATCHSLATE, HATCHSNOW, HATCHSCALE,
                HATCHLEY, HATCHROTA, HATCHTYPE, HATCHCORAL, HATCHCHERENKOV
        };
        
        for (RegistryObject<BlockEntityType<?>> type : types) {
            event.registerBlockEntityRenderer(type.get(), HatchBlockEntityRenderer::new);
        }

    }
}
