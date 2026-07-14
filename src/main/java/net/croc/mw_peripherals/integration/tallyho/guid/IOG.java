package net.croc.mw_peripherals.integration.tallyho.guid;

import edn.stratodonut.tallyho.entity.MountedMissileEntity;
import edn.stratodonut.tallyho.missile.GuidanceComponent;
import edn.stratodonut.tallyho.missile.IGuidanceFactory;
import edn.stratodonut.tallyho.missile.Target;
import edn.stratodonut.tallyho.missile.guid.IRSeeker;
import net.croc.mw_peripherals.mixin.tallyho.IRSeekerAccessor;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.List;

public class IOG extends GuidanceComponent {

    public final SeekerProperties properties;

    private TargetInertia<?> inertia;

    public SeekerProperties getProperties() {
        return this.properties;
    }

    public TargetInertia<?> getTarget() {
        return this.inertia;
    }

    public boolean isActive(long currentGameTime) {
        return this.inertia != null && this.inertia.gameTime + this.properties.guidance_time < currentGameTime;
    }

    public record TargetInertia<T>(long gameTime, Vec3 position, Vec3 velocity) {
        public Vec3 getPredictedPosition(long newGameTime) {
            double dT = (newGameTime - this.gameTime) * 0.05D;
            return this.position.add(this.velocity.scale(dT));
        }
    }

    private void saveTargetInertia(Level level, @Nullable Vec3 position, @Nullable Vec3 velocity) {
        if (velocity == null) {
            velocity = Vec3.ZERO;
        }

        if (position != null) {
            this.inertia = new TargetInertia<>(level.getGameTime(), position, velocity);
        }
    }

    private void saveTargetInertia(Level level, Target<?> target) {
        this.inertia = new TargetInertia<>(level.getGameTime(), target.position(), target.velocity());
    }

    public void saveTargetInertia(MountedMissileEntity missile, MultiModeSeeker mode) {
        for (GuidanceComponent comp : mode.getGuidances()) {
            if (comp instanceof IRSeeker IR) {
                Target<?> target = ((IRSeekerAccessor) IR).getTarget();

                if (target != null) {
                    saveTargetInertia(missile.level(), target);
                    return;
                }
            } else if (comp instanceof SARHSeeker SARH && SARH.target != null) {
                Target<?> target = SARH.getTarget();

                if (target != null) {
                    saveTargetInertia(missile.level(), target);
                    return;
                }
            } else if (comp instanceof DataLink DL) {
                saveTargetInertia(missile.level(), DL.target, DL.velocity);
            }
        }
    }

    public record SeekerProperties(int lead_coefficient, float max_G, int guidance_time) {
        public void appendHoverText(List<Component> components) {
            components.add(Component.literal("Guidance: IOG")
                    .setStyle(Style.EMPTY.withColor(ChatFormatting.AQUA)));

            components.add(Component.literal(String.format("Guidance Time: %ss", this.guidance_time * 0.05D))
                    .setStyle(Style.EMPTY.withColor(ChatFormatting.WHITE)));
        }
    }

    public static class Factory implements IGuidanceFactory<IOG> {
        private final SeekerProperties props;

        public Factory(int lead_coefficient, float max_G, int guidance_time) {
            this.props = new SeekerProperties(lead_coefficient, max_G, guidance_time);
        }

        public IOG create() {
            return new IOG(this.props);
        }

        public void appendHoverText(List<Component> components) {
            this.props.appendHoverText(components);
        }
    }

    private IOG(SeekerProperties props) {
        this.properties = props;
    }

    @Override
    public void tick(MountedMissileEntity missile) {
        long gametime = missile.level().getGameTime();
        Vec3 v_m = missile.getDeltaMovement();

        if (isActive(gametime)) {
            Vec3 targetPos = this.inertia.getPredictedPosition(gametime);
            Vec3 targetVel = this.inertia.velocity;
            missile.setDeltaMovement(ProportionalGuidance(targetPos, targetVel, missile.position(), v_m,
                    this.properties.lead_coefficient, this.properties.max_G));
        }

    }

    @Override
    public boolean launch(MountedMissileEntity missile) {
        return true;
    }
}