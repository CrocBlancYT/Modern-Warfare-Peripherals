package net.croc.mw_peripherals.integration.computercraft.peripherals;

import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IPeripheral;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.croc.mw_peripherals.blocks.GyroBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;

public class GyroscopePeripheral implements IPeripheral {
    private final GyroBlockEntity gyro;

    private final Level level;

    private final BlockPos pos;

    public GyroscopePeripheral(Level level, BlockPos blockPos, BlockEntity be) {
        this.gyro = (GyroBlockEntity)be;
        this.level = level;
        this.pos = blockPos;
    }

    @Nonnull
    public String getType() {
        return "gyro_module";
    }

    public boolean equals(@Nullable IPeripheral iPeripheral) {
        return (this.level != null && this.level.getBlockEntity(this.pos) instanceof GyroBlockEntity);
    }

    @LuaFunction
    public final void applyImpulse(double x, double y, double z) {
        this.gyro.getActor().getOrCreateController(this.pos).withImpulse(new Vec3(x,y,z), GyroBlockEntity.IMPULSE_DURATION);
    }

    @LuaFunction
    public final void applyTorque(double x, double y, double z) {
        this.gyro.getActor().getOrCreateController(this.pos).withTorque(new Vec3(x,y,z));
    }

    @LuaFunction
    public final void applyTargetOmega(double x, double y, double z) {
        this.gyro.getActor().getOrCreateController(this.pos).withTargetOmega(new Vec3(x,y,z), GyroBlockEntity.PROP_MULT);
    }
}
