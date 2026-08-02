package net.croc.mw_peripherals.integration.computercraft.peripherals;

import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IPeripheral;
import java.util.ArrayList;
import java.util.Map;
import javax.annotation.Nullable;

import edn.stratodonut.tallyho.missile.Target;
import net.croc.mw_peripherals.blocks.RadarBlockEntity;
import net.croc.mw_peripherals.integration.computercraft.LuaUtils;
import net.croc.mw_peripherals.integration.tallyho.RadarSource;
import net.croc.mw_peripherals.integration.tallyho.TargetSolutions;
import net.croc.mw_peripherals.integration.tallyho.tracker.AerialTracker;
import net.croc.mw_peripherals.integration.tallyho.tracker.RadTracker;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class RadarPeripheral implements IPeripheral {

    public static final double DISH_FOV = 45;

    public static final double MAX_RANGE = 750;

    private final Level level;

    private final BlockPos pos;
    private final RadarBlockEntity be;

    public String getType() {
        return "radar";
    }

    public boolean equals(@Nullable IPeripheral iPeripheral) {
        return (this.level != null && this.level.getBlockEntity(this.pos) instanceof RadarBlockEntity);
    }

    public RadarPeripheral(Level level, BlockPos blockPos) {
        this.level = level;
        this.pos = blockPos;
        this.be = (RadarBlockEntity) level.getBlockEntity(blockPos);
    }

    @LuaFunction
    public final ArrayList<?> scan() {
        ArrayList<Map<String, Double>> output = new ArrayList<>();

        TargetSolutions solutions = this.be.scan();
        if (solutions == null) return null;

        for (Target<?> target : AerialTracker.onlyAerial(solutions.resolve())) {
            output.add(LuaUtils.toLua(target.position()));
        }

        return output;
    }

    @LuaFunction
    public final double getPitch() {
        return this.be.getPitch();
    }

    @LuaFunction
    public final double getYaw() {
        return this.be.getYaw();
    }

    @LuaFunction
    public final void setYawSpeed(double yawing) {
        this.be.setYawSpeed((float)yawing);
    }

    @LuaFunction
    public final void setPitchSpeed(double pitching) {
        this.be.setPitchSpeed((float)pitching);
    }

    @LuaFunction
    public final double getPitchLimit() {
        return RadarBlockEntity.PITCH_LIMIT;
    }

    @LuaFunction
    public final double getMaxPitchSpeed() {
        return RadarBlockEntity.PITCH_SPEED_LIMIT;
    }

    @LuaFunction
    public final double getMaxYawSpeed() {
        return RadarBlockEntity.YAW_SPEED_LIMIT;
    }

    @LuaFunction
    public final double getMaxRange() { return this.be.maxRange(); }

    @LuaFunction
    public final double getDishFOV() { return this.be.scanFoV().degrees(); }
}
