package net.croc.mw_peripherals.integration.tallyho;

import com.ibm.icu.impl.CollectionSet;
import edn.stratodonut.tallyho.missile.Target;
import net.croc.mw_peripherals.integration.tallyho.tracker.AerialTracker;
import net.croc.mw_peripherals.integration.tallyho.tracker.RadTracker.Angle;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Math;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;


public class TargetSolutions {

    public static final float MULTIPATH_ALTITUDE = 5f;
    public static final float MULTIPATH_DISTANCE = 0;

    public static final float DOPPLER_ALTITUDE = 10f;
    public static final float DOPPLER_RELATIVE_SPEED = 1f;


    private final CollectionSet<Target<?>> targets;
    private final Level level;
    private Target<?> source;
    private final Cone solutionCone;

    private record Cone(Vec3 origin, Vec3 dir, Angle fov) {
        public boolean isInCone(Target<?> target) {
            if (fov.isMaxAngle()) return true;
            return target.position().subtract(origin).toVector3f().angle(dir.toVector3f()) <= fov.radians();
        }
    }

    private TargetSolutions(List<Target<?>> targets, Cone cone, Level level, @Nullable Target<?> origin) {
        this.targets = new CollectionSet<>(targets);
        this.level = level;
        this.solutionCone = cone;

        if (origin == null) {
            this.source = new BlockTarget.BlockEntityTarget(BlockPos.containing(cone.origin), null);
        } else {
            this.source = origin;
        }
    }

    public static TargetSolutions getTransmittersInCone(Level level, Vec3 from, Vec3 dir, Angle fov, @Nullable Target<?> origin) {
        ArrayList<Target<?>> results = new ArrayList<>();
        Cone cone = new Cone(from, dir, fov);

        for (RadarSource.Transmitter transmitter : RadarSource.getAllActiveTransmitters()) {
            Target<?> target = transmitter.origin();
            if (cone.isInCone(target)) {
                results.add(target);
            }
        }

        return new TargetSolutions(results, cone, level, origin);
    }

    public static TargetSolutions getShipInCone(Level level, Vec3 from, Vec3 dir, Angle fov, @Nullable Target<?> origin) {
        ArrayList<Target<?>> results = new ArrayList<>();
        Cone cone = new Cone(from, dir, fov);
        AABB area = new AABB(new BlockPos((int) from.x, (int) from.y, (int) from.z)).inflate(dir.length());

        for (Ship ship : VSGameUtilsKt.getShipsIntersecting(level, area)) {
            Target.ShipTarget target = new Target.ShipTarget(ship);

            if (cone.isInCone(target)) {
                results.add(target);
            }
        }

        return new TargetSolutions(results, cone, level, origin);
    }

    public static TargetSolutions getEntitiesInCone(Level level, Vec3 from, Vec3 dir, Angle fov, @Nullable Target<?> origin, @Nullable Predicate<Entity> isValid) {
        ArrayList<Target<?>> results = new ArrayList<>();
        Cone cone = new Cone(from, dir, fov);
        AABB area = new AABB(from, from.add(dir));

        for (Entity e : level.getEntitiesOfClass(Entity.class, area)) {
            if (isValid == null || isValid.test(e)) {
                Target.EntityTarget target = new Target.EntityTarget(e);

                if (cone.isInCone(target)) {
                    results.add(target);
                }
            }
        }

        return new TargetSolutions(results, cone, level, origin);
    }

    public static TargetSolutions getBlocksInCone(Level level, Vec3 from, Vec3 dir, Angle fov, @Nullable Target<?> origin) {
        ArrayList<Target<?>> results = new ArrayList<>();
        Cone cone = new Cone(from, dir, fov);

        for (RadarSource.Transmitter transmitter : RadarSource.getAllActiveTransmitters()) {
            if (cone.isInCone(transmitter.origin())) {
                results.add(transmitter.origin());
            }
        }

        return new TargetSolutions(results, cone, level, origin);
    }

    public TargetSolutions illuminatedBy(RadarSource.Transmitter transmitter) {
        RadarSource.transmit(transmitter);

        for (Target<?> target: this.targets) {
            if (transmitter.canIlluminate(target)) {
                RadarSource.illuminate(transmitter.level(), target);
            } else {
                this.discard(target);
            }
        }

        return this.next();
    }

    public TargetSolutions receivedBy(RadarSource.Receiver receiver) {
        for (Target<?> target: this.targets) {
            if (!receiver.canReceive(target) || !(RadarSource.isIlluminated(receiver.level(), target) || RadarSource.isTransmitting(receiver.level(), target))) {
                this.discard(target);
            }
        }

        return this.next();
    }

    public static class OffsetTarget<T> extends Target<T> {
        private final Target<T> t;
        private final Vec3 offset;

        public OffsetTarget(Target<T> target, Vec3 offset) {
            this.t = target;
            this.offset = offset;
        }

        @Override
        public Vec3 position() {
            return this.t.position().add(this.offset);
        }

        @Override
        public Vec3 velocity() {
            return this.t.velocity();
        }

        @Override
        public AABB boundingBox() {
            return this.t.boundingBox();
        }

        @Override
        public boolean isAlive() {
            return this.t.isAlive();
        }

        @Override
        public @Nullable T get() {
            return this.t.get();
        }
    }

    private float relativeSpeedSqr(Target<?> target1, Target<?> target2) {
        Vec3 relativePos = target1.position().subtract(target2.position());
        Vec3 relativeVel = target1.velocity().subtract(target2.velocity());
        Vec3 direction = relativePos.normalize();
        return (float) Math.abs(relativeVel.dot(direction));
    }

    private float altitude(Target<?> target) {
        Vec3 p = target.position();
        float groundHeight = this.level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, new BlockPos((int) p.x, (int) p.y, (int) p.z)).getY();
        return ((float) p.y) - groundHeight;
    }

    public TargetSolutions doppler(boolean isAdvanced) {
        /*float sourceAltitude = altitude(this.source);

        for (Target<?> target: this.targets) {

            float targetAltitude = altitude(target);

            boolean groundClutter = targetAltitude < DOPPLER_ALTITUDE && targetAltitude < sourceAltitude;
            boolean dopplerFiltered = relativeSpeedSqr(this.source, target) < DOPPLER_RELATIVE_SPEED * DOPPLER_RELATIVE_SPEED;
            boolean multipathing = targetAltitude < MULTIPATH_ALTITUDE;

            if (dopplerFiltered) {
                if (groundClutter) { // notching on all radars
                    this.discard(target);
                } else if (!isAdvanced) { // notching on unadvanced radars
                    this.discard(target);
                }
            } else if (multipathing) { // multipathing
                Vec3 offset = new Vec3(0, (MULTIPATH_ALTITUDE+targetAltitude) * 0.5, 0);

                double distSqr = this.source.position().subtract(target.position()).lengthSqr();

                if (distSqr > MULTIPATH_DISTANCE*MULTIPATH_DISTANCE) {
                    this.discard(target);
                    this.accept(new OffsetTarget<>(target, offset));
                }
            }
        }*/

        return this.next();
    }

    public TargetSolutions pulse() {
        /*float sourceAltitude = altitude(this.source);

        for (Target<?> target: this.targets) {
            float targetAltitude = altitude(target);

            boolean groundClutter = targetAltitude < DOPPLER_ALTITUDE && targetAltitude < sourceAltitude;
            if (groundClutter) {
                this.discard(target);
            }
        }*/

        return this.next();
    }

    public TargetSolutions continuous() {
        return this.doppler(false);
    }

    public TargetSolutions merge(TargetSolutions solutions) {
        this.targets.addAll(solutions.targets);
        return this;
    }


    private final ArrayList<Target<?>> toRemove = new ArrayList<>();
    private final ArrayList<Target<?>> toAdd = new ArrayList<>();

    private void discard(Target<?> target) {
        this.toRemove.add(target);
    }

    private void accept(Target<?> target) {
        this.toAdd.add(target);
    }

    private TargetSolutions next() {
        for (Target<?> target : toRemove) {
            this.targets.remove(target);
        }

        for (Target<?> target : toAdd) {
            this.targets.add(target);
        }

        toRemove.clear();
        toAdd.clear();
        return this;
    }

    public Optional<Target<?>> tryLockAerial() {
        Vec3 position = this.solutionCone.origin;

        return resolve().stream().filter(AerialTracker::isAirborne).min((Target<?> a, Target<?> b) -> {
            double a_dist = a.position().distanceTo(position);
            double a_size = a.boundingBox().getSize();
            double b_dist = b.position().distanceTo(position);
            double b_size = b.boundingBox().getSize();
            return (int)(a_dist * a_size - b_dist * b_size);
        });
    }

    public Optional<Target<?>> tryLockAny() {
        Vec3 position = this.solutionCone.origin;

        return resolve().stream().min((Target<?> a, Target<?> b) -> {
            double a_dist = a.position().distanceTo(position);
            double a_size = a.boundingBox().getSize();
            double b_dist = b.position().distanceTo(position);
            double b_size = b.boundingBox().getSize();
            return (int)(a_dist * a_size - b_dist * b_size);
        });
    }

    public List<Target<?>> resolve() {
        return this.targets.stream().toList();
    }
}