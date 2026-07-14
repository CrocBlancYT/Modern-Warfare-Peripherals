package net.croc.mw_peripherals.integration.computercraft.peripherals;

import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IPeripheral;

import java.util.*;
import javax.annotation.Nullable;

import edn.stratodonut.tallyho.missile.Target;
import net.croc.mw_peripherals.integration.computercraft.LuaUtils;
import net.croc.mw_peripherals.integration.tallyho.BlockTarget;
import net.croc.mw_peripherals.integration.tallyho.RadarSource;
import net.croc.mw_peripherals.integration.tallyho.TargetSolutions;
import net.croc.mw_peripherals.integration.tallyho.tracker.RadTracker;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;
import org.joml.Vector3f;

import static net.croc.mw_peripherals.Main.MOD_ID;

public class RadarPanelPeripheral implements IPeripheral {

    private int cached_connected_panels; // todo: set dirty when a new panel is placed
    private final Level level;
    private final BlockPos pos;

    private Target<?> target;
    public final RadarSource.Transmitter transmitter;
    public final RadarSource.Receiver receiver;

    public String getType() {
        return "radar_panel";
    }

    public boolean equals(@Nullable IPeripheral iPeripheral) {
        return (this.level != null && iPeripheral instanceof RadarPanelPeripheral radar && radar.pos.equals(pos));
    }

    private int getConnectedPanels(Level level, BlockPos pos, Direction normal, Set<BlockPos> ignore, int connected_panels) {
        connected_panels++;
        ignore.add(pos);

        for (Direction cardinal : Direction.stream().toList()) {
            if (!cardinal.equals(normal) && !cardinal.equals(normal.getOpposite())) {

                BlockPos newPos = pos.relative(cardinal);
                Block block = level.getBlockState(newPos).getBlock();
                String ID = ForgeRegistries.BLOCKS.getKey(block).toString();


                if (ID.equals(MOD_ID + ":aesa_panel") && !ignore.contains(newPos)) {
                    connected_panels = connected_panels + getConnectedPanels(level, newPos, normal, ignore, connected_panels);
                }
            }
        }

        return connected_panels;
    }

    public RadarPanelPeripheral(Level level, BlockPos blockPos) {
        this.level = level;
        this.pos = blockPos;

        this.target = BlockTarget.getTarget(level, blockPos);
        this.transmitter = new RadarSource.Transmitter(level, target, (float) getMaxRange());
        this.receiver = new RadarSource.Receiver(level, target, (float) getMaxRange());

        refresh();
    }

    public Vec3 getRadarDirection() {
        BlockState state = this.level.getBlockState(this.pos);

        if (state.hasProperty(BlockStateProperties.FACING)) {
            Vector3f dir = state.getValue(BlockStateProperties.FACING).getOpposite().step();
            return new Vec3(dir.x, dir.y, dir.z);
        }

        return Vec3.ZERO;
    }

    public TargetSolutions pulse() {
        return RadTracker.pulse(transmitter, receiver,
                pos, getRadarDirection(),
                (float) Math.toRadians(getDishFOV()), (float) getMaxRange());
    }

    @LuaFunction
    public final ArrayList<?> scan() {
        ArrayList<HashMap<?, ?>> output = new ArrayList<>();

        TargetSolutions solutions = RadTracker.pulse_doppler(transmitter, receiver,
                pos, getRadarDirection().scale(getMaxRange()),
                (float) Math.toRadians(getDishFOV()));
        if (solutions == null) return null;

        for (Target<?> target : solutions.resolve()) {
            HashMap<String, Object> result = new HashMap<>();
            result.put("position", LuaUtils.toLua(target.position()));
            result.put("velocity", LuaUtils.toLua(target.velocity()));
            output.add(result);
        }

        return output;
    }

    @LuaFunction
    public final double getMaxRange() { return 300 + cached_connected_panels * 50; }

    @LuaFunction
    public final double getDishFOV() { return 45; }

    @LuaFunction
    public final double getConnectedPanels() {
        return this.cached_connected_panels;
    }

    @LuaFunction
    public final void refresh() {
        BlockState state = this.level.getBlockState(this.pos);
        if (state.hasProperty(BlockStateProperties.FACING)) {
            this.cached_connected_panels = getConnectedPanels(level, pos, state.getValue(BlockStateProperties.FACING), new HashSet<>(),0);
        } else {
            this.cached_connected_panels = 0;
        }
    }
}
