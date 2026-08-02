package net.croc.mw_peripherals.network;

import com.simibubi.create.foundation.networking.SimplePacketBase;
import net.croc.mw_peripherals.content.aps.APSEffects;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

public class APSEffectsPacketClient extends SimplePacketBase {
    private final char type;
    private final Vec3 pos;
    private final Vec3 vel;

    public APSEffectsPacketClient(char type, Vec3 pos, Vec3 vel) {
        this.type = type;
        this.pos = pos;
        this.vel = vel;
    }

    public APSEffectsPacketClient(FriendlyByteBuf buf) {
        this.type = buf.readChar();
        this.pos = new Vec3(buf.readFloat(), buf.readFloat(), buf.readFloat());
        this.vel = new Vec3(buf.readFloat(), buf.readFloat(), buf.readFloat());
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeChar(this.type);
        buffer.writeFloat((float) this.pos.x);
        buffer.writeFloat((float) this.pos.y);
        buffer.writeFloat((float) this.pos.z);
        buffer.writeFloat((float) this.vel.x);
        buffer.writeFloat((float) this.vel.y);
        buffer.writeFloat((float) this.vel.z);
    }

    public final static char EXPLOSION = 'E';
    public final static char FRAGMENT = 'F';

    @Override
    public boolean handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> {
            Minecraft mc = Minecraft.getInstance();
            if (mc.level == null) return;

            if (this.type == EXPLOSION) {
                APSEffects.explosionParticles(mc.level, this.pos);
            } else if (this.type == FRAGMENT) {
                APSEffects.fragmentParticles(mc.level, this.pos, this.vel);
            }

            context.setPacketHandled(true);
        });
        return true;
    }

    public static void sendExplosion(ServerPlayer player, Vec3 pos) {
        CreateTypePacketHandler.getChannel().send(PacketDistributor.PLAYER.with(() -> player), new APSEffectsPacketClient(EXPLOSION, pos, Vec3.ZERO));
    }

    public static void sendFragments(ServerPlayer player, Vec3 pos, Vec3 vel) {
        CreateTypePacketHandler.getChannel().send(PacketDistributor.PLAYER.with(() -> player), new APSEffectsPacketClient(FRAGMENT, pos, vel));
    }
}