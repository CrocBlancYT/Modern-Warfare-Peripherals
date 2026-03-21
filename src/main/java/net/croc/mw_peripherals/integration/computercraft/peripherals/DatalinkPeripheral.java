package net.croc.mw_peripherals.integration.computercraft.peripherals;

import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IPeripheral;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.LecternBlockEntity;

public class DatalinkPeripheral implements IPeripheral {
    private final Level level;
    private final BlockPos pos;

    public DatalinkPeripheral(Level level, BlockPos blockPos) {
        this.level = level;
        this.pos = blockPos;
    }

    @Nonnull
    public String getType() {
        return "data_link";
    }

    public boolean equals(@Nullable IPeripheral iPeripheral) {
        return false;
    }

    private String getLinkedPlayerName(Level level, BlockPos pos) {
        if (level.getBlockEntity(pos) instanceof LecternBlockEntity lectern) {
            CompoundTag tag = lectern.getBook().getTag();

            if (tag != null && tag.contains("author")) {
                return tag.getString("author");
            }
        }

        return null;
    };

    private Player getPlayerByUsername(Level level, String username) {
        MinecraftServer server = level.getServer();
        if (server == null) return null;
        return server.getPlayerList().getPlayerByName(username);
    }

    @LuaFunction(mainThread = true)
    public final String status(String message) {
        String username = getLinkedPlayerName(this.level, this.pos);
        if (username == null) { return "No player linked"; }

        Player player = getPlayerByUsername(this.level, username);
        if (player == null) { return "Player not found"; }

        player.displayClientMessage(Component.literal(message), true);
        return null;
    }

    @LuaFunction(mainThread = true)
    public final String system(String message) {
        String username = getLinkedPlayerName(this.level, this.pos);
        if (username == null) { return "No player linked"; }

        Player player = getPlayerByUsername(this.level, username);
        if (player == null) { return "Player not found"; }

        player.displayClientMessage(Component.literal(message), false);
        return null;
    }
}