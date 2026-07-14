package net.croc.mw_peripherals;

import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(value = {Dist.CLIENT}, bus = Mod.EventBusSubscriber.Bus.MOD, modid = Main.MOD_ID)
public class KeyBinds {
    public static final KeyMapping MCLOS_UP = new KeyMapping("key.mw_peripherals.mclos_up", 265, "key.categories.mw_peripherals");

    public static final KeyMapping MCLOS_DOWN = new KeyMapping("key.mw_peripherals.mclos_down", 264, "key.categories.mw_peripherals");

    public static final KeyMapping MCLOS_LEFT = new KeyMapping("key.mw_peripherals.mclos_left", 263, "key.categories.mw_peripherals");

    public static final KeyMapping MCLOS_RIGHT = new KeyMapping("key.mw_peripherals.mclos_right", 262, "key.categories.mw_peripherals");

    public static void register() {}

    @SubscribeEvent
    public static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(MCLOS_UP);
        event.register(MCLOS_DOWN);
        event.register(MCLOS_RIGHT);
        event.register(MCLOS_LEFT);
    }
}
