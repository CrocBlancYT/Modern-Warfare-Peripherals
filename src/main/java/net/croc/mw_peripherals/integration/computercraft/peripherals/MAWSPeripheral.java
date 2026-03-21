package net.croc.mw_peripherals.integration.computercraft.peripherals;

import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IPeripheral;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import edn.stratodonut.tallyho.entity.MountedMissileEntity;
import edn.stratodonut.tallyho.missile.MotorComponent;
import edn.stratodonut.tallyho.missile.guid.IRSeeker;
import edn.stratodonut.tallyho.missile.motor.RocketMotor;
import edn.stratodonut.tallyho.missile.motor.RocketMotorNoLift;
import net.croc.mw_peripherals.RegistryTags;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

public class MAWSPeripheral implements IPeripheral {
    private static final int maxRange = 100;

    private final Level level;

    private final BlockPos pos;

    public MAWSPeripheral(Level level, BlockPos blockPos) {
        this.level = level;
        this.pos = blockPos;
    }

    @Nonnull
    public String getType() {
        return "maws";
    }

    public boolean equals(@Nullable IPeripheral iPeripheral) {
        return false;
    }

    private static final double MAX_RANGE = 100D;

    public List<MountedMissileEntity> detectActiveMissiles(Level level, BlockPos pos, double range) {
        double clampedRange = range;

        if (clampedRange < 0.0D)
            clampedRange = 0.0D;

        if (clampedRange > MAX_RANGE)
            clampedRange = MAX_RANGE;

        AABB box = (new AABB(pos)).inflate(clampedRange);
        List<Entity> entities = level.getEntities(null, box);
        List<MountedMissileEntity> missiles = new ArrayList<>();

        entities.forEach(entity -> {
            if (entity instanceof MountedMissileEntity missile) {
                if (missile.isDeployed()) {
                    missiles.add(missile);
                }
            }
        });

        return missiles;
    }

    public static final int HOT_LAUNCH_DURATION = 30;

    public static boolean isDetectable(MountedMissileEntity missile) {
        if (missile.getMotor() instanceof RocketMotor && missile.getTicksSinceLaunch() < HOT_LAUNCH_DURATION) return true;
        if (missile.getMotor() instanceof RocketMotorNoLift && missile.getTicksSinceLaunch() < HOT_LAUNCH_DURATION) return true;
        if (missile.getGuidance() instanceof IRSeeker) return true;
        return false;
    }

    @LuaFunction
    public final ArrayList<String> detect(double range) {
        ArrayList<String> output = new ArrayList<>();

        detectActiveMissiles(this.level, this.pos, range).forEach(missile -> {
            if (isDetectable(missile)) {
                output.add(missile.getUUID().toString());
            }
        });

        return output;
    }
}
