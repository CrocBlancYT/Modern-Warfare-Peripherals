package net.croc.mw_peripherals;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class RegistryCreativeTab {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = Main.CREATIVE_TABS;

    public static final RegistryObject<CreativeModeTab> MWP_TAB = CREATIVE_TABS.register("mw_peripherals_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.mw_peripherals"))
                    .icon(() -> new ItemStack(RegistryBlocks.APS_VARIANTS.get("aps_charcoal").getBlock().get()))
                    .displayItems((parameters, output) -> {
                        RegistryBlocks.entries.forEach((entry) -> {
                            if (entry != RegistryBlocks.JET_ENGINE) {
                                output.accept(entry.getBlock().get());
                            }
                        });

                        output.accept(RegistryItems.APS_CHARGE.get());
                        output.accept(RegistryItems.LASER_FUZE.get());
                        output.accept(RegistryItems.REMOTE_FUZE.get());
                    })

                    .build());

    public static void register(IEventBus eventBus) {
        CREATIVE_TABS.register(eventBus);
    }
}