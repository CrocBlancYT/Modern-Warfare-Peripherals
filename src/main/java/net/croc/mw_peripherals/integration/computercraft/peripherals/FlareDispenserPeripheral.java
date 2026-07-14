package net.croc.mw_peripherals.integration.computercraft.peripherals;

import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IPeripheral;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.croc.mw_peripherals.blocks.FlareDispenserBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class FlareDispenserPeripheral implements IPeripheral {
    private final FlareDispenserBlockEntity dispenser;

    private final Level level;
    private final BlockPos pos;

    public FlareDispenserPeripheral(Level level, BlockPos blockPos, BlockEntity be) {
        this.dispenser = (FlareDispenserBlockEntity) be;
        this.level = level;
        this.pos = blockPos;
    }

    @Nonnull
    public String getType() {
        return "flare_dispenser";
    }

    public boolean equals(@Nullable IPeripheral iPeripheral) {
        return (this.level != null && this.level.getBlockEntity(this.pos) instanceof FlareDispenserBlockEntity);
    }

    @LuaFunction
    public final int getAmount() {
        return this.dispenser.getFlares();
    }

    @LuaFunction(mainThread = true)
    public final void popFlare() {
        this.dispenser.triggerFlare();
    }
}
