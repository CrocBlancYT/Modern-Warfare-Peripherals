package net.croc.mw_peripherals.integration.computercraft;

import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IPeripheral;
import edn.stratodonut.tallyho.camera.entity.RemoteStationEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.Map;

import static net.croc.mw_peripherals.integration.computercraft.LuaUtils.*;

public class LuaCROWS {
    public static final float MAX_RANGE_FIND = 500;

    private final RemoteStationEntity crows;

    public LuaCROWS(RemoteStationEntity crows, Level level, BlockPos pos) {
        this.crows = crows;
    }

    public boolean equals(@Nullable IPeripheral iPeripheral) {
        return false;
    }

    @LuaFunction
    public boolean isCROWS() { return true; }

    @LuaFunction
    public String getUUID() {
        return this.crows.getUUID().toString();
    }

    @LuaFunction
    public void fire() {
        this.crows.handleShoot(null);
    }

    @LuaFunction
    public void turnByX(double x) {
        this.crows.turnView(0, x);
    }

    @LuaFunction
    public void turnByY(double y) {
        this.crows.turnView(y, 0);
    }

    @LuaFunction(mainThread = true)
    public void setXRot(double x) { this.crows.setXRot((float) x); }

    @LuaFunction(mainThread = true)
    public void setYRot(double y) { this.crows.setYRot((float) y); }

    @LuaFunction
    public Map<String, ?> getPosition() {
        return toLua(this.crows.getEyePosition());
    }

    @LuaFunction(mainThread = true)
    public double rangeFind() {
        return this.crows.pick(MAX_RANGE_FIND, 1, false).distanceTo(this.crows);
    }

    @LuaFunction
    public double maxRangeFind() { return MAX_RANGE_FIND; }

    @LuaFunction(mainThread = true)
    public int getBelt() {
        CompoundTag nbt = crows.saveWithoutId(new CompoundTag());
        if (nbt.contains("belt")) {
            return nbt.getInt("belt");
        }
        return 400;
    }
}