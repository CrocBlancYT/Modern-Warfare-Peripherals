package net.croc.mw_peripherals;

import com.mojang.logging.LogUtils;
import com.simibubi.create.foundation.data.CreateRegistrate;
import net.croc.mw_peripherals.client.ClientEventsRegistrar;
import net.croc.mw_peripherals.integration.computercraft.PeripheralProviders;
import net.croc.mw_peripherals.integration.tallyho.ForeignMissileRegistry;
import net.croc.mw_peripherals.network.CreateTypePacketHandler;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;

import java.util.Objects;

import static net.croc.mw_peripherals.Main.MOD_ID;

@Mod(MOD_ID)
@EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Main {
    public static final String MOD_ID = "mw_peripherals";

    public static final Logger LOGGER = LogUtils.getLogger();

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MOD_ID);

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);

    public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, MOD_ID);

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, MOD_ID);

    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MOD_ID);

    public static final CreateRegistrate REGISTRATE = CreateRegistrate.create(MOD_ID)
            .defaultCreativeTab(RegistryCreativeTab.MWP_TAB.getKey());

    private static final String[] depList = new String[] {
            "create", "createbigcannons", "vs_clockwork"
    };

    public Main() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        MinecraftForge.EVENT_BUS.register(this);
        modEventBus.addListener(this::onClientSetup);
        modEventBus.addListener(this::onCommonSetup);

        BLOCKS.register(modEventBus);
        PARTICLES.register(modEventBus);
        ITEMS.register(modEventBus);
        BLOCK_ENTITIES.register(modEventBus);
        CREATIVE_TABS.register(modEventBus);
        REGISTRATE.registerEventListeners(modEventBus);

        RegistrySounds.prepare();
        RegistryItems.register();
        RegistryBlocks.register();
        RegistryBlockEntities.register();
        RegistryTags.register();
        RegistryParticles.register();
        ForeignMissileRegistry.init();
        RegistryEntities.register();
        RegistryCreativeTab.register(modEventBus);
        PartialModels.init();
        PeripheralProviders.register();
        RegistryBlockStateInfo.INSTANCE.register();
        CreateTypePacketHandler.registerPackets();

        String zblock = ModList.get().getModFileById("zblock").getFile().getFileName();
        if (!Objects.equals(zblock, "zblock-2.3.0.jar")) {
            throw new ImcompatibleModListException(zblock);
        }
    }

    public static ResourceLocation resource(String path) {
        return new ResourceLocation(MOD_ID, path);
    }

    private void onCommonSetup(FMLCommonSetupEvent event) {
        ModList list = ModList.get();
        for (String dep : depList) {
            if (list.getModFileById(dep) == null)
                throw new BadModListException(dep);
        }
    }

    private void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ClientEventsRegistrar.setup();
            KeyBinds.register();
        });
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        RegistryCommands.register(event);
    }
}
