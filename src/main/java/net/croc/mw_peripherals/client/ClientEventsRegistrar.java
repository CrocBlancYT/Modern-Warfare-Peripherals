package net.croc.mw_peripherals.client;

import net.croc.mw_peripherals.KeypressHandler;
import net.croc.mw_peripherals.Main;
import net.croc.mw_peripherals.MouseclickHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid = Main.MOD_ID, value = {Dist.CLIENT}, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientEventsRegistrar {
    public static void setup() {
        MinecraftForge.EVENT_BUS.register(KeypressHandler.get());
        MinecraftForge.EVENT_BUS.register(MouseclickHandler.get());
    }
}
