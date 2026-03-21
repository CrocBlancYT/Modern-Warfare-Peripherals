package net.croc.mw_peripherals.stuff;

import net.croc.mw_peripherals.RegistryConfigs;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.valkyrienskies.core.api.ships.PhysShip;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.api.ships.ShipForcesInducer;
import org.valkyrienskies.core.impl.game.ships.PhysShipImpl;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

import java.util.ArrayList;
import java.util.HashMap;

public class GyroActor implements ShipForcesInducer {
    private static double clamp(double min, double number, double max) {
        if (number > max) return max;
        if (number < min) return min;
        return number;
    }

    static class Gyro {
        private Vector3d torque;
        private int usesLeft;

        public void consume() {
            this.usesLeft--;
        }

        public boolean isEmpty() {
            return (this.usesLeft <= 0);
        }

        public Gyro(int usesLeft, Vector3d newTorque) {
            double max_torque = RegistryConfigs.Config.GYRO_MAX_TORQUE.get();

            this.usesLeft = usesLeft;
            this.torque = new Vector3d(
                    clamp(-max_torque, newTorque.x, max_torque),
                    clamp(-max_torque, newTorque.y, max_torque),
                    clamp(-max_torque, newTorque.z, max_torque)
            );
        }
    }

    private final HashMap<String, Gyro> gyros = new HashMap<>();

    public void setGyro(BlockPos pos, Vector3d torque) {
        String key = pos.getX() + "_" + pos.getY() + "_" + pos.getZ();
        this.gyros.put(key, new Gyro(5, torque));
    }

    public void applyForces(PhysShip physShip) {
        ArrayList<String> removeQueue = new ArrayList<>();

        this.gyros.forEach((key_pos, gyro) -> {
            if (!physShip.isStatic()) {
                physShip.applyRotDependentTorque(gyro.torque);
            }

            gyro.consume();

            if (gyro.isEmpty()) {
                removeQueue.add(key_pos);
            }
        });

        removeQueue.forEach(this.gyros::remove);
    }

    public static GyroActor getOrCreate(ServerShip ship) {
        GyroActor control = ship.getAttachment(GyroActor.class);

        if (control == null) {
            control = new GyroActor();
            ship.saveAttachment(GyroActor.class, control);
        }

        return control;
    }

    public static GyroActor getOrCreate(Level level, BlockPos pos) {
        ServerShip ship = (ServerShip)VSGameUtilsKt.getShipManagingPos(level, pos);
        if (ship == null)
            return null;
        return getOrCreate(ship);
    }
}
