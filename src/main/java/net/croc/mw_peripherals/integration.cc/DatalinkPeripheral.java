package net.croc.mw_peripherals.integration.cc;

import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IPeripheral;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.LecternBlockEntity;

public class DatalinkPeripheral implements IPeripheral {
    private final Level level;
    private final BlockPos pos;

    public DatalinkPeripheral(Level level, BlockPos blockPos) {
        //this.flap = be;
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
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();

        LecternBlockEntity lectern = (LecternBlockEntity) level.getBlockEntity(pos);

        CompoundTag tag = lectern.getBook().getTag();

        if (tag == null || !tag.contains("author")) { return null; }
        String username = tag.getString("author");
        return username;
    };

    private Player getPlayerByUsername(Level level, String username) {
        return level.getServer().getPlayerList().getPlayerByName(username);
    }

    @LuaFunction
    public final String status(String message) {
        String username = getLinkedPlayerName(this.level, this.pos);
        if (username == null) { return "No player linked"; }

        Player player = getPlayerByUsername(this.level, username);
        if (player == null) { return "Player not found"; }

        player.displayClientMessage(Component.literal(message), true);
        return null;
    }
}
