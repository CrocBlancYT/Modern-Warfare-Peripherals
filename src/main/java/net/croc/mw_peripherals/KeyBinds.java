package net.croc.mw_peripherals;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(value = {Dist.CLIENT}, bus = Mod.EventBusSubscriber.Bus.MOD, modid = Main.MOD_ID)
public class KeyBinds {
    public static final KeyMapping MCLOS_UP = new KeyMapping("key.mw_peripherals.mclos_up", InputConstants.KEY_UP, "key.categories.mw_peripherals");

    public static final KeyMapping MCLOS_DOWN = new KeyMapping("key.mw_peripherals.mclos_down", InputConstants.KEY_DOWN, "key.categories.mw_peripherals");

    public static final KeyMapping MCLOS_LEFT = new KeyMapping("key.mw_peripherals.mclos_left", InputConstants.KEY_LEFT, "key.categories.mw_peripherals");

    public static final KeyMapping MCLOS_RIGHT = new KeyMapping("key.mw_peripherals.mclos_right", InputConstants.KEY_RIGHT, "key.categories.mw_peripherals");

    public static final KeyMapping HMD_FLARE = new KeyMapping("key.mw_peripherals.hmd_flare", InputConstants.UNKNOWN.getValue(), "key.categories.mw_peripherals");

    public static final KeyMapping HMD_TOGGLE_SCOPE = new KeyMapping("key.mw_peripherals.hmd_toggle_scope", InputConstants.UNKNOWN.getValue(), "key.categories.mw_peripherals");

    public static final KeyMapping HMD_CYCLE_WEAPONS = new KeyMapping("key.mw_peripherals.hmd_cycle_weapons", InputConstants.UNKNOWN.getValue(), "key.categories.mw_peripherals");

    public static final KeyMapping HMD_FIRE_SELECTED_WEAPON = new KeyMapping("key.mw_peripherals.hmd_fire_selected_weapon", InputConstants.UNKNOWN.getValue(), "key.categories.mw_peripherals");

    public static void register() {}

    @SubscribeEvent
    public static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(MCLOS_UP);
        event.register(MCLOS_DOWN);
        event.register(MCLOS_RIGHT);
        event.register(MCLOS_LEFT);

        event.register(HMD_FLARE);
        event.register(HMD_TOGGLE_SCOPE);
        event.register(HMD_CYCLE_WEAPONS);
        event.register(HMD_FIRE_SELECTED_WEAPON);
    }
}
