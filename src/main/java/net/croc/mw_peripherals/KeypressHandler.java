package net.croc.mw_peripherals;

import net.croc.mw_peripherals.items.MCLOSJoystick;
import net.croc.mw_peripherals.network.CreateTypePacketHandler;
import net.croc.mw_peripherals.network.MCLOSPacket;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.HashMap;

public class KeypressHandler {
    private static KeypressHandler instance;

    public static KeypressHandler get() {
        if (instance == null)
            instance = new KeypressHandler();
        return instance;
    }

    private byte currentMap = 0;
    
    public byte getKeyStates() {
        return currentMap;
    }

    @SubscribeEvent
    public void onKeyPressed(InputEvent.Key event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) {
            if (mc.player.getMainHandItem().getItem() instanceof MCLOSJoystick) {
                byte map = 0;
                if (KeyBinds.MCLOS_DOWN.isDown())
                    map = (byte) (map | 0x1);
                if (KeyBinds.MCLOS_UP.isDown())
                    map = (byte) (map | 0x2);
                if (KeyBinds.MCLOS_LEFT.isDown())
                    map = (byte) (map | 0x4);
                if (KeyBinds.MCLOS_RIGHT.isDown())
                    map = (byte) (map | 0x8);

                if (map != currentMap) {
                    currentMap = map;
                    CreateTypePacketHandler.getChannel().sendToServer(new MCLOSPacket(map));
                }
            }
        }
    }
}