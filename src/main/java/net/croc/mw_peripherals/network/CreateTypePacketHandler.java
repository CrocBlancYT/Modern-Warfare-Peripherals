package net.croc.mw_peripherals.network;

import com.simibubi.create.foundation.networking.SimplePacketBase;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

import net.croc.mw_peripherals.Main;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public enum CreateTypePacketHandler {
    MCLOS_INPUT(MCLOSPacket.class, MCLOSPacket::new, NetworkDirection.PLAY_TO_SERVER),
    USE_ITEM_ON(UseItemOnPacket.class, UseItemOnPacket::new, NetworkDirection.PLAY_TO_SERVER),
    APS_EFFECTS(APSEffectsPacketClient.class, APSEffectsPacketClient::new, NetworkDirection.PLAY_TO_CLIENT);

    public static final ResourceLocation CHANNEL_NAME = new ResourceLocation(Main.MOD_ID, "main");
    public static final int NETWORK_VERSION = 3;
    public static final String NETWORK_VERSION_STR = String.valueOf(3);
    private static SimpleChannel channel;
    private final PacketType<?> packetType;

    <T extends SimplePacketBase> CreateTypePacketHandler(Class<T> type, Function<FriendlyByteBuf, T> factory, NetworkDirection direction) {
        this.packetType = new PacketType(type, factory, direction);
    }

    public static void registerPackets() {
        channel = NetworkRegistry.ChannelBuilder.named(CHANNEL_NAME)
            .serverAcceptedVersions(NETWORK_VERSION_STR::equals)
            .clientAcceptedVersions(NETWORK_VERSION_STR::equals)
            .networkProtocolVersion(() -> NETWORK_VERSION_STR)
            .simpleChannel();
        for (CreateTypePacketHandler packet : values())
            packet.packetType.register();
    }

    public static SimpleChannel getChannel() {
        return channel;
    }

    private static class PacketType<T extends SimplePacketBase> {
        private static int index = 0;
        private BiConsumer<T, FriendlyByteBuf> encoder;
        private Function<FriendlyByteBuf, T> decoder;
        private BiConsumer<T, Supplier<NetworkEvent.Context>> handler;
        private Class<T> type;
        private NetworkDirection direction;

        private PacketType(Class<T> type, Function<FriendlyByteBuf, T> factory, NetworkDirection direction) {
            this.encoder = SimplePacketBase::write;
            this.decoder = factory;
            this.handler = (packet, contextSupplier) -> {
                NetworkEvent.Context context = contextSupplier.get();
                if (packet.handle(context))
                    context.setPacketHandled(true);
            };
            this.type = type;
            this.direction = direction;
        }

        private void register() {
            CreateTypePacketHandler.getChannel().messageBuilder(this.type, index++, this.direction)
                .encoder(this.encoder)
                .decoder(this.decoder)
                .consumerNetworkThread(this.handler)
                .add();
        }
    }
}