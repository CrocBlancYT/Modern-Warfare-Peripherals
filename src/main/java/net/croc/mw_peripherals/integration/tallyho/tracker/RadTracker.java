package net.croc.mw_peripherals.integration.tallyho.tracker;

import edn.stratodonut.tallyho.entity.MountedMissileEntity;
import edn.stratodonut.tallyho.missile.Target;
import net.croc.mw_peripherals.integration.tallyho.RadarSource;
import net.croc.mw_peripherals.integration.tallyho.TargetSolutions;
import net.croc.mw_peripherals.utils.VSUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.UnknownNullability;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

import javax.annotation.Nullable;

public class RadTracker {
    public static class Angle {
        private boolean isMax;
        private Double radians;
        private Double degrees;

        private Angle(Double radians, Double degrees) {
            this.radians = radians;
            this.degrees = degrees;
            this.isMax = radians == null && degrees == null;
        }

        public boolean isMaxAngle() {
            return isMax;
        }

        public double degrees() {
            if (isMaxAngle()) return 360;
            if (degrees == null) degrees = Math.toDegrees(radians);
            return degrees;
        }

        public double radians() {
            if (isMaxAngle()) return Math.PI*2;
            if (radians == null) radians = Math.toRadians(degrees);
            return radians;
        }

        public static Angle degrees(double degrees) {
            return new Angle(null, degrees);
        }

        public static Angle radians(double radians) {
            return new Angle(radians, null);
        }

        public static Angle max() {
            return new Angle(null, null);
        }
    }


    private static TargetSolutions getMissileTargets(Level level, Vec3 from, Vec3 dir, Angle fov, MountedMissileEntity ignoredSource) {
        Ship ship = VSGameUtilsKt.getShipObjectManagingPos(level, new BlockPos((int) from.x, (int) from.y, (int) from.z));
        Target<?> source = null;

        if (ship != null) {
            from = VSUtils.toWorldPosition(level, from);
            dir = VSUtils.toWorldDirection(level, dir, from);
            source = new Target.ShipTarget(ship);
        }

        TargetSolutions ships = TargetSolutions.getShipInCone(level, from, dir, fov, source);
        TargetSolutions missiles = TargetSolutions.getEntitiesInCone(level, from, dir, fov, source,
                (entity -> { return entity instanceof MountedMissileEntity m && m.isDeployed() && !m.equals(ignoredSource); }));

        return ships.merge(missiles);
    }

    public static TargetSolutions pulse_doppler(RadarSource.Transmitter transmitter, RadarSource.Receiver receiver,
                                        BlockPos from, Vec3 dir, Angle fov, double max_range) {
        return getMissileTargets(transmitter.level(), from.getCenter(), dir.scale(max_range), fov, null)
                .illuminatedBy(transmitter)
                .receivedBy(receiver)
                .doppler(true);
    }

    public static TargetSolutions pulse(RadarSource.Transmitter transmitter, RadarSource.Receiver receiver,
                                        BlockPos from, Vec3 dir, Angle fov, double max_range) {
        return getMissileTargets(transmitter.level(), from.getCenter(), dir.scale(max_range), fov, null)
                .illuminatedBy(transmitter)
                .receivedBy(receiver)
                .pulse();
    }

    public static TargetSolutions SARH(RadarSource.Receiver receiver,MountedMissileEntity missile,
                                       Angle fov, double max_range) {
        return getMissileTargets(receiver.level(), missile.position(), missile.getLookAngle().scale(max_range), fov, missile)
                .receivedBy(receiver)
                .pulse();
    }

    public static TargetSolutions ARH(RadarSource.Transmitter transmitter, RadarSource.Receiver receiver,
                                          MountedMissileEntity missile, Angle fov, double max_range) {
        return getMissileTargets(receiver.level(), missile.position(), missile.getLookAngle().scale(max_range), fov, missile)
                .illuminatedBy(transmitter)
                .receivedBy(receiver)
                .doppler(true);
    }

    public static TargetSolutions ARM(RadarSource.Receiver receiver, MountedMissileEntity missile,
                                      Angle fov, double max_range) {
        return TargetSolutions.getTransmittersInCone(missile.level(), missile.position(),
                        missile.getLookAngle().scale(max_range), fov, null)
                .receivedBy(receiver);
    }

    public static TargetSolutions passive(RadarSource.Receiver receiver,
                                        BlockPos from, Vec3 dir, Angle fov) {
        return getMissileTargets(receiver.level(), from.getCenter(), dir, fov, null)
                .receivedBy(receiver);
    }

}