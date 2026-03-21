package net.croc.mw_peripherals.integration.tallyho;

import net.croc.mw_peripherals.Main;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static edn.stratodonut.tallyho.AllCreativeTabs.BASE_CREATIVE_TAB;
import static edn.stratodonut.tallyho.AllItems.*;
import static edn.stratodonut.tallyho.missile.MissileRegistry.*;
import static net.croc.mw_peripherals.RegistryItems.*;

@Mod.EventBusSubscriber(modid = Main.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CreativeTabIntegration {
    @SubscribeEvent
    public static void addItemsToTabs(BuildCreativeModeTabContentsEvent event) {
        if (event.getTab() == BASE_CREATIVE_TAB.get()) {
            // missing registry
            event.accept(TURRET_CAMERA_ITEM.get());
            event.accept(REMOTE_CAMERA_ITEM.get());
            event.accept(CBU_87.getItemEntry().get());
            event.accept(ROCKEYE.getItemEntry().get());

            // misc
            event.accept(REMOTE_OPTICS.get());
            event.accept(FLARE_CARTRIDGE.get());
        }
    }
}