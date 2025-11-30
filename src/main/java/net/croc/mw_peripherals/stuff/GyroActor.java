package net.croc.mw_peripherals.stuff;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.joml.Vector3dc;
import org.valkyrienskies.core.api.ships.PhysShip;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.api.ships.ShipForcesInducer;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.valkyrienskies.mod.common.VSGameUtilsKt;

public class GyroActor implements ShipForcesInducer {
    private final ConcurrentLinkedQueue<Vector3dc> torques = new ConcurrentLinkedQueue<>();

    @Override
    public void applyForces(PhysShip physShip) {
        while (!this.torques.isEmpty()) {
            Vector3dc torque = this.torques.poll();
            physShip.applyInvariantTorque(torque);
        }
    }

    public void applyInvariantTorque(Vector3dc torque) {
        this.torques.add(torque);
    }

    public static GyroActor getOrCreate(ServerShip ship) {
        GyroActor control = ship.getAttachment(GyroActor.class);

        if (control == null) {
            control = new GyroActor();
            ship.saveAttachment(GyroActor.class, control);
        }

        return control;
    }

    public static GyroActor get(Level level, BlockPos pos) {
        ServerShip ship = (ServerShip) VSGameUtilsKt.getShipManagingPos(level, pos);
        if (ship == null) { return null; }
        return getOrCreate(ship);
    }
}