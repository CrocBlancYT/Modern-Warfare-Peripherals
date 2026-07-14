package net.croc.mw_peripherals.integration.tallyho.tracker;

import edn.stratodonut.tallyho.entity.MountedMissileEntity;
import edn.stratodonut.tallyho.missile.Target;
import net.croc.mw_peripherals.integration.tallyho.RadarSource;
import net.croc.mw_peripherals.integration.tallyho.TargetSolutions;
import net.croc.mw_peripherals.utils.VSUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

import javax.swing.text.html.parser.Entity;
import java.util.List;
import java.util.Optional;

public class RadTracker {
    public static TargetSolutions getMissileTargets(Level level, Vec3 from, Vec3 dir, float fovRadians, MountedMissileEntity ignoredSource) {
        Ship ship = VSGameUtilsKt.getShipObjectManagingPos(level, new BlockPos((int) from.x, (int) from.y, (int) from.z));
        Target<?> source = null;

        if (ship != null) {
            from = VSUtils.toWorldPosition(level, from);
            dir = VSUtils.toWorldDirection(level, dir, from);
            source = new Target.ShipTarget(ship);
        }

        TargetSolutions ships = TargetSolutions.getShipInCone(level, from, dir, fovRadians, source);
        TargetSolutions missiles = TargetSolutions.getEntitiesInCone(level, from, dir, fovRadians, source,
                (entity -> { return entity instanceof MountedMissileEntity m && m.isDeployed() && !m.equals(ignoredSource); }));

        return ships.merge(missiles);
    }

    public static TargetSolutions pulse_doppler(RadarSource.Transmitter transmitter, RadarSource.Receiver receiver,
                                        BlockPos from, Vec3 dir, float fovRadians) {
        return getMissileTargets(transmitter.level(), from.getCenter(), dir, fovRadians, null)
                .illuminatedBy(transmitter)
                .receivedBy(receiver)
                .doppler(true);
    }

    public static TargetSolutions pulse(RadarSource.Transmitter transmitter, RadarSource.Receiver receiver,
                                        BlockPos from, Vec3 dir, float fovRadians, float max_range) {
        return getMissileTargets(transmitter.level(), from.getCenter(), dir.scale(max_range), fovRadians, null)
                .illuminatedBy(transmitter)
                .receivedBy(receiver)
                .pulse();
    }

    public static TargetSolutions SARH(RadarSource.Receiver receiver,MountedMissileEntity missile,
                                           float fovRadians, float max_range) {
        return getMissileTargets(receiver.level(), missile.position(), missile.getLookAngle().scale(max_range), fovRadians, missile)
                .receivedBy(receiver)
                .pulse();
    }

    public static TargetSolutions ARH(RadarSource.Transmitter transmitter, RadarSource.Receiver receiver,
                                          MountedMissileEntity missile, float fovRadians, float max_range) {
        return getMissileTargets(receiver.level(), missile.position(), missile.getLookAngle().scale(max_range), fovRadians, missile)
                .illuminatedBy(transmitter)
                .receivedBy(receiver)
                .doppler(true);
    }


    public static TargetSolutions ARM(RadarSource.Receiver receiver, MountedMissileEntity missile,
                                          float fovRadians, float max_range) {
        return TargetSolutions.getTransmittersInCone(missile.level(), missile.position(),
                        missile.getLookAngle().scale(max_range), fovRadians, null)
                .receivedBy(receiver);
    }

    public static TargetSolutions passive(RadarSource.Receiver receiver,
                                        BlockPos from, Vec3 dir, float fovRadians) {
        return getMissileTargets(receiver.level(), from.getCenter(), dir, fovRadians, null)
                .receivedBy(receiver)
                .pulse();
    }

}