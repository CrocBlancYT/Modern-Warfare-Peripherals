package net.croc.mw_peripherals.blocks;

import net.croc.mw_peripherals.RegistryBlockEntities;
import net.croc.mw_peripherals.RegistryConfigs;
import net.croc.mw_peripherals.stuff.GyroActor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public class GyroBlockEntity extends BlockEntity {
    public Vector3dc targetOmega = new Vector3d();

    public GyroBlockEntity(BlockPos pos, BlockState state) {
        super(RegistryBlockEntities.BLOCK_ENTITIES.get("gyro_module").get(), pos, state);
    }

    private double getTorqueMultiplier() { return RegistryConfigs.Config.GYRO_MULTIPLIER.get(); }
    private double getMaxTorque() { return RegistryConfigs.Config.GYRO_MAX_TORQUE.get(); }
    private double getMaxOmega() { return RegistryConfigs.Config.GYRO_MAX_OMEGA.get(); }

    private Vec3 getVec3(Vector3d v) { return new Vec3(v.x, v.y, v.z); }
    private Vector3d getVector3d(Vec3 v) { return new Vector3d(v.x, v.y, v.z); }

    private Vector3d clamp(Vector3d v, double limit) {
        return new Vector3d(
                Math.max(Math.min(v.x, limit), -limit),
                Math.max(Math.min(v.y, limit), -limit),
                Math.max(Math.min(v.z, limit), -limit)
        );
    }

    private Vec3 clamp(Vec3 v, double limit) {
        return getVec3(clamp(getVector3d(v), limit));
    }

    public void setTargetOmega(Vector3dc targetOmega) {
        double maxOmega = getMaxOmega();
        this.targetOmega = clamp((Vector3d) targetOmega, maxOmega);
        setChanged();
    }

    private Vector3d calculateTorque(ServerShip ship) {
        Vector3dc targetOmega = this.targetOmega;
        if (targetOmega == null) { return new Vector3d(); }


        Vector3dc localOmega = ship.getWorldToShip().transformDirection((Vector3d) ship.getOmega());
        double mass = ship.getInertiaData().getMass();

        double maxTorque = getMaxTorque();
        double torqueMultiplier = getTorqueMultiplier();
        
        Vec3 target = getVec3((Vector3d) targetOmega);
        Vec3 current = getVec3((Vector3d) localOmega);

        Vec3 diff = target.subtract(current);
        Vec3 rawTorque = clamp(diff.scale(mass), maxTorque);

        return ship.getShipToWorld().transformDirection(getVector3d(rawTorque.scale(torqueMultiplier)));
    }

    public static void tick(Level level, BlockPos pos, BlockState state, GyroBlockEntity gyro) {
        if (level == null || level.isClientSide()) return;

        ServerShip ship = (ServerShip) VSGameUtilsKt.getShipObjectManagingPos(level, pos);
        if (ship == null) return;

        Vector3dc torque = gyro.calculateTorque(ship);
        GyroActor.getOrCreate(ship).applyInvariantTorque(torque);
    }
}