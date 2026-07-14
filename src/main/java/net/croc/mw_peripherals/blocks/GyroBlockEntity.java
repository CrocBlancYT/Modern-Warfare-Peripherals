package net.croc.mw_peripherals.blocks;

import javax.annotation.Nullable;

import net.croc.mw_peripherals.RegistryBlockEntities;
import net.croc.mw_peripherals.stuff.GyroActor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

public class GyroBlockEntity extends BlockEntity {
    private boolean powered = false;

    public GyroBlockEntity(BlockPos pos, BlockState state) {
        super(RegistryBlockEntities.GYRO_BLOCK_ENTITY.get(), pos, state);
    }

    @Nullable
    private static Vec3 getAnalogTargetOmega(Level level, BlockPos pos, GyroBlockEntity gyro) {
        int px = level.getSignal(pos.east(), Direction.WEST);
        int py = level.getSignal(pos.above(), Direction.DOWN);
        int pz = level.getSignal(pos.south(), Direction.NORTH);
        int nx = level.getSignal(pos.west(), Direction.EAST);
        int ny = level.getSignal(pos.below(), Direction.UP);
        int nz = level.getSignal(pos.north(), Direction.SOUTH);

        boolean anySignal = (px != 0 || py != 0 || pz != 0 || nx != 0 || ny != 0 || nz != 0);

        if (anySignal) {
            gyro.powered = true;
            return new Vec3((px - nx), (py - ny), (pz - nz)).scale(REDSTONE_SPEED_MULT);
        } else if (gyro.powered) {
            gyro.powered = false;
        }

        return null;
    }

    public static final double REDSTONE_SPEED_MULT = 1;
    public static final double IMPULSE_DURATION = 0.4D;
    public static final double PROP_MULT = 4D;

    public static void tick(Level level, BlockPos pos, BlockState state, GyroBlockEntity gyro) {
        if (level == null || level.isClientSide()) return;

        ServerShip ship = (ServerShip)VSGameUtilsKt.getShipObjectManagingPos(level, pos);
        if (ship == null) return;

        Vec3 targetOmega = getAnalogTargetOmega(level, pos, gyro);
        if (targetOmega == null) return;

        gyro.getActor().getOrCreateController(pos).withTargetOmega(targetOmega, PROP_MULT);
    }

    public GyroActor getActor() {
        ServerShip ship = (ServerShip)VSGameUtilsKt.getShipObjectManagingPos(level, this.getBlockPos());
        if (ship == null) return null;

        return GyroActor.getOrCreate(ship);
    }
}
