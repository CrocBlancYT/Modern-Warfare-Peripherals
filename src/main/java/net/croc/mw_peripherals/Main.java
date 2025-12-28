package net.croc.mw_peripherals;

import com.mojang.logging.LogUtils;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;

import net.minecraftforge.fml.common.Mod;

import net.minecraftforge.eventbus.api.IEventBus;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import net.croc.mw_peripherals.integration.cc.PeripheralProviders;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.croc.mw_peripherals.render.MSA_Radar_Renderer;

import static net.croc.mw_peripherals.Main.MOD_ID;

@Mod(MOD_ID)
@Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Main {
    public static final String MOD_ID = "mw_peripherals";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MOD_ID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, MOD_ID);
    
    public Main() {
        TestClass kt = new TestClass();

        ModLoadingContext modLoadingContext = ModLoadingContext.get();
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        MinecraftForge.EVENT_BUS.register(this);

        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        BLOCK_ENTITIES.register(modEventBus);
        RegistryCreativeTabs.CREATIVE_TABS.register(modEventBus);

        RegistryConfigs.register(modLoadingContext);
        RegistryItems.register();
        RegistryBlocks.register();
        RegistryBlockEntities.register();
        RegistryCreativeTabs.register(modEventBus);
        RegistryTags.register();

        PeripheralProviders.register();
    }

    public static ResourceLocation resource(String path) {
        return new ResourceLocation(MOD_ID, path);
    }

    public static final ModelLayerLocation MSA_RADAR_MODEL =
            new ModelLayerLocation(resource("dynamic_model"), "main");

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        // Register block entity renderer
        BlockEntityRenderers.register(RegistryBlockEntities.BLOCK_ENTITIES.get("gyro_module").get(),
                MSA_Radar_Renderer::new);
    }

    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(MSA_RADAR_MODEL,
                MSA_RADAR_MODEL::createBodyLayer);
    }
}
