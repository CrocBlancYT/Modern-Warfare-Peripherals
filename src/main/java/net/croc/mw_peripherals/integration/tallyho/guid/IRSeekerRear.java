package net.croc.mw_peripherals.integration.tallyho.guid;

import edn.stratodonut.tallyho.AllSounds;
import edn.stratodonut.tallyho.entity.FlareEntity;
import edn.stratodonut.tallyho.entity.MountedMissileEntity;
import edn.stratodonut.tallyho.missile.GuidanceComponent;
import edn.stratodonut.tallyho.missile.IGuidanceFactory;
import edn.stratodonut.tallyho.missile.Target;
import edn.stratodonut.tallyho.missile.guid.IRSeeker;
import edn.stratodonut.tallyho.missile.tracker.FlareTracker;
import edn.stratodonut.tallyho.network.CreateTypePacketHandler;
import edn.stratodonut.tallyho.network.MissileTonePacket;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.Vector3f;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.util.VectorConversionsMCKt;

import static net.croc.mw_peripherals.integration.tallyho.tracker.AerialTracker.tryLockAerial;
import static net.croc.mw_peripherals.integration.tallyho.tracker.AerialTracker.tryLockAerialAligned;

public class IRSeekerRear extends GuidanceComponent {
    private static final int ARMING_TICKS = 5;
    public final SeekerProperties properties;
    @Nullable
    protected Target<?> target;
    protected long flareCounter = 0L;

    public record SeekerProperties(int seeker_fov, int gimbal_fov, double seeker_fov_rate, int lead_coefficient,
                                   float max_G, int seeker_range, float decoy_resistance, float rear_angle) {
        public void appendHoverText(List<Component> components) {
            components.add(Component.literal("Guidance: IR, Rear-Aspect").setStyle(Style.EMPTY
                    .withColor(ChatFormatting.AQUA)));
            components.add(Component.literal(String.format("Fov: %s°/%s°", this.seeker_fov, this.gimbal_fov)).setStyle(Style.EMPTY
                    .withColor(ChatFormatting.WHITE)));
            components.add(Component.literal(String.format("Seeker Range: %sm", this.seeker_range)).setStyle(Style.EMPTY
                    .withColor(ChatFormatting.WHITE)));
            components.add(Component.literal(String.format("Rear Angle: %s°", (int) this.rear_angle)).setStyle(Style.EMPTY
                    .withColor(ChatFormatting.WHITE)));
            components.add(Component.literal(String.format("Decoy Resistance: %s%%", this.decoy_resistance * 100.0F)).setStyle(Style.EMPTY
                    .withColor(ChatFormatting.WHITE)));
        }
    }

    public static class Factory implements IGuidanceFactory<IRSeekerRear> {
        private final IRSeekerRear.SeekerProperties props;

        public Factory(int seeker_fov, int gimbal_fov, double seeker_fov_rate, int lead_coefficient, float max_G, int seeker_range, float decoy_resistance, float rear_angle) {
            this.props = new IRSeekerRear.SeekerProperties(seeker_fov, gimbal_fov, seeker_fov_rate, lead_coefficient, max_G, seeker_range, decoy_resistance, rear_angle);
        }

        public IRSeekerRear create() {
            return new IRSeekerRear(this.props);
        }

        public void appendHoverText(List<Component> components) {
            this.props.appendHoverText(components);
        }
    }

    private IRSeekerRear(SeekerProperties properties) {
        this.properties = properties;
    }

    private boolean isAligned(MountedMissileEntity missile, Ship target) {
        double maxRadAngle = Math.toRadians(this.properties.rear_angle);

        Vector3f dir1 = missile.getDeltaMovement().toVector3f();
        Vector3f dir2 = target.getVelocity().get(new Vector3f());

        if (dir1.lengthSquared() < 4) return true;
        if (dir2.lengthSquared() < 4) return true;
        
        return dir1.angle(dir2) <= maxRadAngle;
    }

    public void tick(MountedMissileEntity missile) {
        if (missile.level().isClientSide)
            return;
        if (!isSeeking())
            return;
        Target<?> t = this.target;
        Vec3 lookDir = missile.getLookAngle();
        Vec3 v_m = missile.getDeltaMovement();
        if (missile.getTicksSinceLaunch() > 5)
            lookDir = v_m.normalize();
        int fov = this.properties.seeker_fov() + (missile.isPassenger() ? 0 : this.properties.gimbal_fov());
        if (t == null || !t.isAlive()) {
            Optional<Ship> ship = tryLockAerialAligned(missile.level(), lookDir, missile.position(), null, fov, this.properties.seeker_range(), missile, this.properties.rear_angle());
            ship.ifPresent(value -> {
                    this.target = new Target.ShipTarget(value);
                    this.flareCounter = FlareTracker.getFlareCount(value);
                });
        } else {
            if (t.isAlive() && t instanceof Target.ShipTarget) {
                Target.ShipTarget st = (Target.ShipTarget)t;
                Ship s = st.get();
                long shipFlareCount = FlareTracker.getFlareCount(s);
                if (shipFlareCount != this.flareCounter) {
                    this.flareCounter = shipFlareCount;
                    AABB aabb = t.boundingBox();
                    List<FlareEntity> flares = missile.level().getEntitiesOfClass(FlareEntity.class, aabb.inflate(aabb.getSize()));
                    float decoy_strength = 0.0F;
                    for (FlareEntity flare : flares) {
                        decoy_strength += this.random.nextFloat();
                        if (decoy_strength > this.properties.decoy_resistance)
                            this.target = new Target.EntityTarget((Entity)flare);
                    }
                }
            }
            Vec3 rel_pos = t.position().subtract(missile.position());
            Vec3 v_r = t.velocity().scale(0.05D).subtract(v_m);
            Vec3 omega_r = rel_pos.cross(v_r).scale(1.0D / rel_pos.dot(rel_pos));
            if (rel_pos.normalize().dot(lookDir) < Math.cos(Math.toRadians(fov)) || omega_r.length() > this.properties.seeker_fov_rate()) {
                this.target = null;
                return;
            }
            if (missile.getTicksSinceLaunch() > 5)
                missile.setDeltaMovement(GuidanceComponent.ProportionalGuidance(t
                        .position(), t.velocity(), missile
                        .position(), v_m, this.properties.lead_coefficient, this.properties.max_G));
        }
        if (missile.getVehicle() != null) {
            boolean interrupt = !Objects.equals(t, this.target);
            if (missile.tickCount % 20 != 0 && !interrupt)
                return;
            missile.level().players().stream().filter(player ->
                    (VSGameUtilsKt.getShipMountedTo((Entity)player) != null && VSGameUtilsKt.getShipMountedTo((Entity)missile) != null && Objects.equals(VSGameUtilsKt.getShipMountedTo((Entity)player).getSlug(), VSGameUtilsKt.getShipMountedTo((Entity)missile).getSlug())))
                .forEach(player -> CreateTypePacketHandler.getChannel().send(PacketDistributor.PLAYER.with(() -> (ServerPlayer)player), new MissileTonePacket((this.target == null) ? AllSounds.AIM9_LOCK.get().getLocation() : AllSounds.AIM9_SEEK.get().getLocation(), missile.getId(), interrupt)));
        }
    }

    public boolean canLaunch() {
        return this.isSeeking;
    }

    public boolean launch(MountedMissileEntity e) {
        if (!isSeeking())
            activateSeeker();
        return true;
    }

    public void setCode(String c) {}
}