package net.croc.mw_peripherals.integration.rha;

import net.croc.mw_peripherals.Main;
import net.mcreator.rha.init.RhaModTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegistryObject;

import java.util.HashMap;

@Mod.EventBusSubscriber(modid = Main.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class RHACreativeTabIntegration {
    public static String[] colors = {"gurkha", "kelp", "lapis", "rust", "sinaigrey"};

    public static String[] steels = {"hard", "layered", "riveted", "slashed", "tiled"};

    public static final HashMap<String, RegistryObject<Item>> items = new HashMap<>();

    public static final HashMap<String, RegistryObject<Block>> blocks = new HashMap<>();

    @SubscribeEvent
    public static void addItemsToTabs(BuildCreativeModeTabContentsEvent event) {
        if (event.getTab() == RhaModTabs.ROLLEDHOMOGENOUS.get()) {
            for (String color : colors) {
                for (String steel : steels) {
                    String id = steel+"steel"+color;
                    Item item = items.get(id).get();
                    event.accept(item);
                }
            }
        }
    }
}