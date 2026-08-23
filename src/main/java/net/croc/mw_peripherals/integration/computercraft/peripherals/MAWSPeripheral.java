package net.croc.mw_peripherals.integration.computercraft.peripherals;

import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IPeripheral;
import edn.stratodonut.tallyho.entity.MountedMissileEntity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import edn.stratodonut.tallyho.missile.motor.RocketMotor;
import edn.stratodonut.tallyho.missile.motor.RocketMotorNoLift;
import net.croc.mw_peripherals.blocks.MAWSBlockEntity;
import net.croc.mw_peripherals.entity.MountedPodEntity;
import net.croc.mw_peripherals.integration.computercraft.LuaUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

public class MAWSPeripheral implements IPeripheral {
    private static final int maxRange = 100;

    private final Level level;

    private final BlockPos pos;

    private final MAWSBlockEntity maws;

    public static final double MAX_RANGE = 100.0D;

    public static final int HOT_LAUNCH_DURATION = 30;

    public MAWSPeripheral(Level level, BlockPos blockPos) {
      this.level = level;
      this.pos = blockPos;
      this.maws = (MAWSBlockEntity) level.getBlockEntity(pos);
    }

    @Nonnull
    public String getType() {
      return "maws";
    }

    public boolean equals(@Nullable IPeripheral iPeripheral) {
      return false;
    }

    public static boolean isDetectable(MountedMissileEntity missile) {
        if (missile.getMotor() instanceof RocketMotor && missile.getTicksSinceLaunch() < HOT_LAUNCH_DURATION)
            return true;
        if (missile.getMotor() instanceof RocketMotorNoLift && missile.getTicksSinceLaunch() < HOT_LAUNCH_DURATION)
            return true;
        return false;
    }

    @LuaFunction
    public ArrayList<HashMap<?, ?>> detect() {
        ArrayList<HashMap<?, ?>> output = new ArrayList<>();

        maws.getIncoming().forEach(missile -> {
            if (missile.isDeployed() && isDetectable(missile)
                    && !(missile instanceof MountedPodEntity)) {
                HashMap<Object, Object> lua_missile = new HashMap<>();

                lua_missile.put("x", missile.getX());
                lua_missile.put("y", missile.getY());
                lua_missile.put("z", missile.getZ());
                lua_missile.put("uuid", missile.getUUID().toString());

                output.add(lua_missile);
            }
        });

        return output;
    }
}
