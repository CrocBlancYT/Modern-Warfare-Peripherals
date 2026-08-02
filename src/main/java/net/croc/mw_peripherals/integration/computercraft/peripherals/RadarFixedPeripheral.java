package net.croc.mw_peripherals.integration.computercraft.peripherals;

import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IPeripheral;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;

import edn.stratodonut.tallyho.missile.Target;
import net.croc.mw_peripherals.blocks.RadarPanelBlockEntity;
import net.croc.mw_peripherals.integration.computercraft.LuaUtils;
import net.croc.mw_peripherals.integration.tallyho.BlockTarget;
import net.croc.mw_peripherals.integration.tallyho.RadarSource;
import net.croc.mw_peripherals.integration.tallyho.TargetSolutions;
import net.croc.mw_peripherals.integration.tallyho.tracker.AerialTracker;
import net.croc.mw_peripherals.integration.tallyho.tracker.RadTracker;
import net.croc.mw_peripherals.utils.IRadarBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public class RadarFixedPeripheral implements IPeripheral {

    public static final double DISH_FOV = 81;

    public static final double MAX_RANGE = 300;

    private final Level level;
    private final BlockPos pos;
    private final IRadarBlockEntity be;

    private Target<?> target;
    public final RadarSource.Transmitter transmitter;
    public final RadarSource.Receiver receiver;

    public String getType() {
        return "fixed_radar";
    }

    public boolean equals(@Nullable IPeripheral iPeripheral) {
        return (this.level != null && iPeripheral instanceof RadarFixedPeripheral radar && radar.pos.equals(pos));
    }

    public RadarFixedPeripheral(Level level, BlockPos blockPos) {
        this.level = level;
        this.pos = blockPos;
        this.be = (IRadarBlockEntity) level.getBlockEntity(blockPos);

        this.target = new BlockTarget.BlockEntityTarget(blockPos, be);
        this.transmitter = new RadarSource.Transmitter(level, target, (float) getMaxRange());
        this.receiver = new RadarSource.Receiver(level, target, (float) getMaxRange());
    }

    @LuaFunction
    public final ArrayList<?> scan() {
        ArrayList<HashMap<?, ?>> output = new ArrayList<>();

        for (Target<?> target : AerialTracker.onlyAerial(this.be.scan().resolve())) {
            HashMap<String, Object> result = new HashMap<>();
            result.put("position", LuaUtils.toLua(target.position()));
            result.put("velocity", LuaUtils.toLua(target.velocity()));
            output.add(result);
        }

        return output;
    }

    @LuaFunction
    public final double getMaxRange() { return this.be.maxRange(); }

    @LuaFunction
    public final double getDishFOV() { return this.be.scanFoV().degrees(); }
}
