package net.croc.mw_peripherals.integration.computercraft.peripherals;

import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IPeripheral;

import java.util.*;
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
    private final Level level;
    private final BlockPos pos;
    private final RadarPanelBlockEntity be;

    private Target<?> target;
    public final RadarSource.Transmitter transmitter;
    public final RadarSource.Receiver receiver;

    public String getType() {
        return "radar_panel";
    }

    public boolean equals(@Nullable IPeripheral iPeripheral) {
        return (this.level != null && iPeripheral instanceof RadarPanelPeripheral radar && radar.pos.equals(pos));
    }

    public RadarPanelPeripheral(Level level, BlockPos blockPos) {
        this.level = level;
        this.pos = blockPos;
        this.be = (RadarPanelBlockEntity) level.getBlockEntity(blockPos);

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

    @LuaFunction
    public final double getConnectedPanels() {
        return this.be.getConnectedPanels();
    }
}
