package net.croc.mw_peripherals.integration.computercraft.peripherals;

import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IPeripheral;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import edn.stratodonut.tallyho.missile.Target;
import net.croc.mw_peripherals.integration.computercraft.LuaUtils;
import net.croc.mw_peripherals.integration.tallyho.BlockTarget;
import net.croc.mw_peripherals.integration.tallyho.RadarSource;
import net.croc.mw_peripherals.integration.tallyho.tracker.RadTracker;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

import java.util.ArrayList;
import java.util.HashMap;

public class RWRPeripheral implements IPeripheral {
    private final Level level;
    private final BlockPos pos;

    public final RadarSource.Receiver receiver;

    public RWRPeripheral(Level level, BlockPos blockPos) {
        this.level = level;
        this.pos = blockPos;

        Target<?> target = BlockTarget.getTarget(this.level,this.pos);
        this.receiver = new RadarSource.Receiver(this.level, target, (float) maxRange);
    }

    @Nonnull
    public String getType() {
        return "radar_warning_receiver";
    }

    public boolean equals(@Nullable IPeripheral iPeripheral) { return false; }

    public static final double maxRange = 500;

    @LuaFunction
    public ArrayList<HashMap<String, Object>> receive() {
        ArrayList<HashMap<String, Object>> output = new ArrayList<>();

        Vec3 from = receiver.origin().position();

        RadTracker.passive(receiver, this.pos, new Vec3(0,1,0).scale(maxRange), RadTracker.Angle.max()).resolve().forEach((target) -> {
            HashMap<String, Object> luaResult = new HashMap<>();

            Vec3 to = target.position();
            Vec3 diff = to.subtract(from);

            double distance = diff.length();

            luaResult.put("power", Math.floor(Math.sqrt(distance / 50)));
            luaResult.put("heading", LuaUtils.toLua(diff.scale(1 / distance)));

            output.add(luaResult);
        });

        return output;
    }

    @LuaFunction
    public double getMaxRange() {
        return maxRange;
    }
}
