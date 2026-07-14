package net.croc.mw_peripherals.stuff;

import net.croc.mw_peripherals.Main;
import net.croc.mw_peripherals.RegistryConfigs;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Math;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.valkyrienskies.core.api.ships.PhysShip;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.api.ships.ShipForcesInducer;
import org.valkyrienskies.core.impl.game.ships.PhysShipImpl;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.BiConsumer;

public class GyroActor implements ShipForcesInducer {
    private final HashMap<BlockPos, Controller> controllers;

    public GyroActor() {
        this.controllers = new HashMap<>();
    }

    public static final int MAX_FORCE = 12500;

    public Vector3d clamp(Vector3d v) {
        return new Vector3d(
                Math.clamp(-MAX_FORCE, MAX_FORCE, v.x),
                Math.clamp(-MAX_FORCE, MAX_FORCE, v.y),
                Math.clamp(-MAX_FORCE, MAX_FORCE, v.z)
        );
    }

    public class Controller {
        private BlockPos from;
        private BiConsumer<PhysShip, Integer> routine;
        private int clock;

        public Controller(BlockPos from) {
            this.from = from;
            this.routine = (s, c) -> {};
            this.clock = 0;
            controllers.put(from, this);
        }

        public void discard() {
            controllers.remove(this.from);
        }

        final static double dT = 1 / 60D;

        public void withImpulse(Vec3 torque, double duration) {
            final int physTicks = (int) (duration * 60D);
            final Vector3d dImpulse = torque.scale(dT / duration).toVector3f().get(new Vector3d());

            this.routine = (physShip, clock) -> {
                if (clock > physTicks) {
                    this.discard();
                } else {
                    physShip.applyRotDependentForce(clamp(dImpulse));
                }
            };
        }

        public void withTorque(Vec3 torque) {
            final Vector3d dImpulse = torque.scale(dT).toVector3f().get(new Vector3d());

            this.routine = (physShip, clock) -> {
                physShip.applyRotDependentForce(clamp(dImpulse));
            };
        }

        public void withTargetOmega(Vec3 targetOmega, double strength) {
            final Vector3d to_omega = targetOmega.toVector3f().get(new Vector3d());

            this.routine = (physShip, clock) -> {
                Vector3d from_omega = (Vector3d) ((PhysShipImpl) physShip).getPoseVel().getOmega();

                physShip.applyInvariantTorque(clamp(to_omega.sub(from_omega).mul(strength)));
            };
        }

        public void run(PhysShip physShip) {
            this.routine.accept(physShip, this.clock);
            this.clock++;
        }
    }

    public Controller getOrCreateController(BlockPos from) {
        Controller controller = controllers.get(from);

        if (controller == null) {
            controller = new Controller(from);
        }

        return controller;
    }

    @Override
    public void applyForces(PhysShip physShip) {
        for (Controller controller : (Controller[]) controllers.values().toArray()) {
            controller.run(physShip);

            if (controller.clock >= 120D) { // 2s in physics ticks (2 * 60)
                controller.discard();
            }
        }
    }

    public static GyroActor getOrCreate(ServerShip ship) {
        GyroActor control = ship.getAttachment(GyroActor.class);

        if (control == null) {
            control = new GyroActor();
            ship.saveAttachment(GyroActor.class, control);
        }

        return control;
    }

    @Nullable
    public static GyroActor getOrCreate(Level level, BlockPos pos) {
        ServerShip ship = (ServerShip)VSGameUtilsKt.getShipManagingPos(level, pos);
        if (ship == null)
            return null;
        return getOrCreate(ship);
    }
}
