package net.croc.mw_peripherals.network;

import com.simibubi.create.foundation.networking.SimplePacketBase;
import net.croc.mw_peripherals.items.MCLOSJoystick;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

public class MCLOSPacket extends SimplePacketBase {
    byte map;

    public MCLOSPacket() {
        this.map = 0;
    }

    public MCLOSPacket(FriendlyByteBuf buf) {
        this.map = buf.readByte();
    }

    public MCLOSPacket(byte map) {
        this.map = map;
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeByte(this.map);
    }

    public record MCLOS_Binds(byte map) {
        public boolean down() { return (map & 0x1) == 0x1; }
        public boolean up() { return (map & 0x2) == 0x2; }
        public boolean left() { return (map & 0x4) == 0x4; }
        public boolean right() { return (map & 0x8) == 0x8; }
    }

    @Override
    public boolean handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null && player.getMainHandItem().getItem() instanceof MCLOSJoystick joystick) {
                joystick.handleInput(player, new MCLOS_Binds(this.map));
            }
        });

        return true;
    }
}