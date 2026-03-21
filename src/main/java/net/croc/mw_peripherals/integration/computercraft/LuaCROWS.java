package net.croc.mw_peripherals.integration.computercraft;

import dan200.computercraft.api.lua.LuaFunction;
import edn.stratodonut.tallyho.camera.entity.RemoteStationEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;

import java.util.Map;

import static net.croc.mw_peripherals.integration.computercraft.LuaUtils.*;

public class LuaCROWS {
    private final RemoteStationEntity crows;

    public LuaCROWS(RemoteStationEntity crows, Level level, BlockPos pos) {
        this.crows = crows;
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
    public void turnX(double x) {
        this.crows.turnView(0, x);
    }

    @LuaFunction
    public void turnY(double y) {
        this.crows.turnView(y, 0);
    }

    @LuaFunction
    public Map<String, ?> getPosition() {
        return toLua(this.crows.getEyePosition());
    }

    @LuaFunction(mainThread = true)
    public int getBelt() {
        CompoundTag nbt = crows.saveWithoutId(new CompoundTag());
        if (nbt.contains("belt")) {
            return nbt.getInt("belt");
        }
        return 400;
    }
}