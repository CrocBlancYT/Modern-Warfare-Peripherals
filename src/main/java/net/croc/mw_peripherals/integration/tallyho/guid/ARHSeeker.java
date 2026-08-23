package net.croc.mw_peripherals.integration.tallyho.guid;

import edn.stratodonut.tallyho.AllSounds;
import edn.stratodonut.tallyho.entity.MountedMissileEntity;
import edn.stratodonut.tallyho.missile.GuidanceComponent;
import edn.stratodonut.tallyho.missile.IGuidanceFactory;
import edn.stratodonut.tallyho.missile.Target;
import edn.stratodonut.tallyho.network.CreateTypePacketHandler;
import edn.stratodonut.tallyho.network.MissileTonePacket;

import java.util.*;
import javax.annotation.Nullable;

import net.croc.mw_peripherals.integration.tallyho.RadarSource;
import net.croc.mw_peripherals.integration.tallyho.tracker.RadTracker;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

public class ARHSeeker extends GuidanceComponent {
    private static final int ARMING_TICKS = 10;
    public final SeekerProperties properties;
    @Nullable
    protected Target<?> target;
    public @Nullable Target<?> getTarget() {
        return this.target;
    }

    public SeekerProperties getProperties() {
        return this.properties;
    }

    public record SeekerProperties(int seeker_fov, int seeker_range, int lead_coefficient, float max_G) { // todo: add gimbal fov
        public void appendHoverText(List<Component> components) {
            components.add(Component.literal("Guidance: ARH")
                    .setStyle(Style.EMPTY.withColor(ChatFormatting.AQUA)));

            components.add(Component.literal(String.format("Fov: %s°", this.seeker_fov))
                    .setStyle(Style.EMPTY.withColor(ChatFormatting.WHITE)));

            components.add(Component.literal(String.format("Transmitter/Seeker Range: %sm", this.seeker_range))
                    .setStyle(Style.EMPTY.withColor(ChatFormatting.WHITE)));
        }
    }


    public static class Factory implements IGuidanceFactory<ARHSeeker> {
        private final ARHSeeker.SeekerProperties props;

        public Factory(int seeker_fov, int seeker_range, int lead_coefficient, float max_G) {
            this.props = new ARHSeeker.SeekerProperties(seeker_fov, seeker_range, lead_coefficient, max_G);
        }

        public ARHSeeker create() {
            return new ARHSeeker(this.props);
        }

        public void appendHoverText(List<Component> components) {
            this.props.appendHoverText(components);
        }
    }

    private ARHSeeker(SeekerProperties props) {
        this.properties = props;
    }

    public void tick(MountedMissileEntity missile) {
        if (missile.level().isClientSide) return;
        if (!isSeeking()) return;

        Vec3 v_m = missile.getDeltaMovement();
        Target<?> t = this.target;

        // detects a target OR searches for a better one after 0.5s
        if (this.target == null || missile.tickCount % 10 == 0) {
            Target<?> m = new Target.EntityTarget(missile);
            RadarSource.Receiver receiver = new RadarSource.Receiver(missile.level(), m, this.properties.seeker_range);
            RadarSource.Transmitter transmitter = new RadarSource.Transmitter(missile.level(), m, this.properties.seeker_range);

            Optional<Target<?>> target = RadTracker
                    .ARH(transmitter, receiver, missile,
                            RadTracker.Angle.degrees(this.properties.seeker_fov),
                            this.properties.seeker_range)
                    .tryLockAerial();

            target.ifPresent(value -> this.target = value);
        }

        // guides to target after 0.5s from launch
        if (this.target != null && missile.getTicksSinceLaunch() > ARMING_TICKS) {
            missile.setDeltaMovement(GuidanceComponent.ProportionalGuidance(this.target.position(), this.target.velocity(), missile
                    .position(), v_m, this.properties.lead_coefficient, this.properties.max_G));
        }

        if (missile.getVehicle() != null) {
            boolean interrupt = (t != this.target);
            if (missile.tickCount % 20 != 0 && !interrupt) return;

            missile.level().players().stream().filter(player ->
                            (VSGameUtilsKt.getShipMountedTo(player) != null && VSGameUtilsKt.getShipMountedTo(missile) != null && Objects.equals(VSGameUtilsKt.getShipMountedTo(player).getSlug(), VSGameUtilsKt.getShipMountedTo(missile).getSlug())))
                    .forEach(player -> CreateTypePacketHandler.getChannel().send(PacketDistributor.PLAYER.with(() -> (ServerPlayer)player), new MissileTonePacket((this.target != null) ? AllSounds.AIM9_LOCK.get().getLocation() : AllSounds.AIM9_SEEK.get().getLocation(), missile.getId(), interrupt)));
        }
    }

    public boolean launch(MountedMissileEntity e) {
        if (!isSeeking())
            activateSeeker();
        return true;
    }
}