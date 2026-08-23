package net.croc.mw_peripherals.utils;

import edn.stratodonut.tallyho.missile.Target;
import net.croc.mw_peripherals.blocks.RadarBlock;
import net.croc.mw_peripherals.integration.tallyho.BlockTarget;
import net.croc.mw_peripherals.integration.tallyho.RadarSource;
import net.croc.mw_peripherals.integration.tallyho.TargetSolutions;
import net.croc.mw_peripherals.integration.tallyho.tracker.RadTracker;
import net.croc.mw_peripherals.integration.tallyho.tracker.RadTracker.Angle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.function.Function;

public abstract class IRadarBlockEntity extends BlockEntity {
    public static final DirectionProperty FACING = BlockStateProperties.FACING;

    private Target<?> target;
    private RadarSource.Transmitter transmitter;
    private RadarSource.Receiver receiver;

    public Target<?> asTarget() {
        if (this.target == null) {
            this.target = BlockTarget.getTarget(level, this.worldPosition);
        }
        return target;
    }

    public RadarSource.Transmitter transmitter() {
        if (this.transmitter == null) {
            this.transmitter = new RadarSource.Transmitter(level, asTarget(), maxRange());
        }
        return this.transmitter;
    }

    public RadarSource.Receiver receiver() {
        if (this.receiver == null) {
            this.receiver = new RadarSource.Receiver(level, asTarget(), maxRange());
        }
        return receiver;
    }

    public abstract double maxRange();

    public abstract Angle scanFoV();

    public abstract RadarType type();

    public IRadarBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public enum RadarType {
        MSA((radar) -> RadTracker.pulse(
                radar.transmitter(), radar.receiver(),
                radar.getBlockPos(), radar.getRadarDirection(),
                radar.scanFoV(), radar.maxRange())
        ),

        AESA((radar) -> RadTracker.pulse_doppler(
                radar.transmitter(), radar.receiver(),
                radar.getBlockPos(), radar.getRadarDirection(),
                radar.scanFoV(), radar.maxRange())
        ),

        PESA((radar) -> RadTracker.passive(
                radar.receiver(),
                radar.getBlockPos(), radar.getRadarDirection(),
                radar.scanFoV())
        );

        private final Function<IRadarBlockEntity, TargetSolutions> scan;

        RadarType(Function<IRadarBlockEntity, TargetSolutions> scan) {
            this.scan = scan;
        }

        public TargetSolutions scan(IRadarBlockEntity radar) {
            return this.scan.apply(radar);
        }
    }

    public Direction getFacing() {
        BlockState state = this.getBlockState();
        if (state.hasProperty(FACING)) {
            return state.getValue(FACING);
        }
        return Direction.UP;
    }

    public Vec3 getRadarDirection() {
        Vector3f dir = getFacing().getOpposite().step();
        return new Vec3(dir.x, dir.y, dir.z);
    }

    public TargetSolutions scan() {
        return this.type().scan(this);
    }
}