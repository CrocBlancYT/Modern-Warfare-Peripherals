package net.croc.mw_peripherals.integration.computercraft.peripherals;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.ILuaCallback;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IPeripheral;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        return (this.level != null && this.level.getBlockEntity(this.pos) instanceof LecternBlockEntity);
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

    public final String status(MutableComponent message) {
        String username = getLinkedPlayerName(this.level, this.pos);
        if (username == null) { return "No player linked"; }

        Player player = getPlayerByUsername(this.level, username);
        if (player == null) { return "Player not found"; }

        player.displayClientMessage(message, true);
        return null;
    }

    public final String system(MutableComponent message) {
        String username = getLinkedPlayerName(this.level, this.pos);
        if (username == null) { return "No player linked"; }

        Player player = getPlayerByUsername(this.level, username);
        if (player == null) { return "Player not found"; }

        player.displayClientMessage(message, false);
        return null;
    }

    int MAX_SIZE = 64;
    int size = 1;
    MutableComponent message = Component.literal("");

    @LuaFunction()
    public final void clear() {
        message = Component.literal("");
        size = 1;
    }

    @LuaFunction()
    public final boolean appendPlain(String text) {
        if (size+1 > MAX_SIZE) return false;
        size++;

        message.append(Component.literal(text));

        return true;
    }

    @LuaFunction()
    public final boolean appendColored(String text, String color_name) {
        if (size+1 > MAX_SIZE) return false;
        size++;

        ChatFormatting color = colors.get(color_name);
        if (color == null) return false;

        message.append(Component.literal(text).withStyle(color));
        return true;
    }

    @LuaFunction(mainThread = true)
    public final String status() {
        String result = status(message);
        clear();
        return result;
    }

    @LuaFunction(mainThread = true)
    public final String system() {
        String result = system(message);
        clear();
        return result;
    }

    private static final HashMap<String, ChatFormatting> colors = new HashMap<>();

    static {
        for (String name : ChatFormatting.getNames(true, true)) {
            colors.put(name, ChatFormatting.getByName(name));
        }
    }

    @LuaFunction
    public List<String> getColors() {
        return colors.keySet().stream().toList();
    }
}