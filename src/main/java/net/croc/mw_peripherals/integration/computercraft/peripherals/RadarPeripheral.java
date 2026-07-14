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
import net.croc.mw_peripherals.integration.tallyho.tracker.RadTracker;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class RadarPeripheral implements IPeripheral {
    private final Level level;

    private final BlockPos pos;
    private final RadarBlockEntity blockEntity;

    public final RadarSource.Transmitter transmitter;
    public final RadarSource.Receiver receiver;

    public String getType() {
        return "radar";
    }

    public boolean equals(@Nullable IPeripheral iPeripheral) {
        return (this.level != null && this.level.getBlockEntity(this.pos) instanceof RadarBlockEntity);
    }

    public RadarPeripheral(Level level, BlockPos blockPos) {
        this.level = level;
        this.pos = blockPos;
        this.blockEntity = (RadarBlockEntity) level.getBlockEntity(blockPos);

        this.transmitter = this.blockEntity.getTransmitter();
        this.receiver = this.blockEntity.getReceiver();
    }

    public static final double DISH_FOV = 22;
    public static final double maxRange = 600;

    @LuaFunction
    public final ArrayList<?> scan() {
        ArrayList<Map<String, Double>> output = new ArrayList<>();

        TargetSolutions solutions = this.blockEntity.pulse();
        if (solutions == null) return null;

        for (Target<?> target : solutions.resolve()) {
            output.add(LuaUtils.toLua(target.position()));
        }

        return output;
    }

    @LuaFunction
    public final double getMaxRange() { return maxRange; }

    @LuaFunction
    public final void setYawSpeed(double yawing) {
        this.blockEntity.setYawSpeed((float)yawing);
    }

    @LuaFunction
    public final double getYaw() {
        return this.blockEntity.getYaw();
    }

    @LuaFunction
    public final void setPitchSpeed(double pitching) {
        this.blockEntity.setPitchSpeed((float)pitching);
    }

    @LuaFunction
    public final double getPitch() {
        return this.blockEntity.getPitch();
    }

    @LuaFunction
    public final double getPitchLimit() {
        return this.blockEntity.PITCH_LIMIT;
    }

    @LuaFunction
    public final double getDishFOV() { return DISH_FOV; }

    @LuaFunction
    public final double getMaxPitchSpeed() {
        return this.blockEntity.PITCH_SPEED_LIMIT;
    }

    @LuaFunction
    public final double getMaxYawSpeed() {
        return this.blockEntity.YAW_SPEED_LIMIT;
    }
}
