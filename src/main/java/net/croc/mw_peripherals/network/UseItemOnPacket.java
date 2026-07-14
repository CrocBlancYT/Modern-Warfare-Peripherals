package net.croc.mw_peripherals.network;

import com.simibubi.create.foundation.networking.SimplePacketBase;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkEvent;

public class UseItemOnPacket extends SimplePacketBase {
    private final BlockHitResult blockHit;
    private final InteractionHand hand;

    public UseItemOnPacket(InteractionHand hand, BlockHitResult hitResult) {
        this.hand = hand;
        this.blockHit = hitResult;
    }

    public UseItemOnPacket(FriendlyByteBuf buffer) {
        this.hand = buffer.readEnum(InteractionHand.class);
        this.blockHit = buffer.readBlockHitResult();
    }

    public void write(FriendlyByteBuf buffer) {
        buffer.writeEnum(this.hand);
        buffer.writeBlockHitResult(this.blockHit);
    }

    @Override
    public boolean handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null) return;

            Level level = player.level();
            InteractionHand hand = this.getHand();
            BlockHitResult hit = this.getHitResult();

            level.getBlockState(hit.getBlockPos()).use(level, player, hand, hit);
            player.getItemInHand(hand).useOn(new UseOnContext(player, hand, hit));
        });

        return true;
    }

    public InteractionHand getHand() {
        return this.hand;
    }

    public BlockHitResult getHitResult() {
        return this.blockHit;
    }

    public static void useItemOn(LocalPlayer player, InteractionHand hand, BlockHitResult blockHit) {
        Level level = player.level();
        level.getBlockState(blockHit.getBlockPos()).use(level, player, hand, blockHit);
        player.getItemInHand(hand).useOn(new UseOnContext(player, hand, blockHit));
        CreateTypePacketHandler.getChannel().sendToServer(new UseItemOnPacket(hand, blockHit));
    }
}