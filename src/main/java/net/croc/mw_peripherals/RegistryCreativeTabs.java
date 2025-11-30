package net.croc.mw_peripherals;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class RegistryCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Main.MOD_ID);
    
    public static final RegistryObject<CreativeModeTab> CREATE_APS_TAB = CREATIVE_TABS.register("mw_peripherals_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.mw_peripherals"))
                    .icon(() -> new ItemStack(RegistryBlocks.APS_VARIANT_BLOCKS.get("aps_charcoal").get()))
                    .displayItems((parameters, output) -> {
                        RegistryBlocks.BLOCKS.forEach((String str, RegistryObject<Block> block) -> {
                            output.accept(block.get());
                        });

                        RegistryBlocks.APS_VARIANT_BLOCKS.forEach((String str, RegistryObject<Block> block) -> {
                            output.accept(block.get());
                        });

                        output.accept(RegistryItems.APS_CHARGE.get());
                    })
                    .build());

    public static void register(IEventBus eventBus) {
        CREATIVE_TABS.register(eventBus);
    }
}