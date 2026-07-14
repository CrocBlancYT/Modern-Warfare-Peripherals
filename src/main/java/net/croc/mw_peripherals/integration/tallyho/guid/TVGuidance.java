package net.croc.mw_peripherals.integration.tallyho.guid;

import edn.stratodonut.tallyho.entity.MountedMissileEntity;
import edn.stratodonut.tallyho.missile.GuidanceComponent;
import edn.stratodonut.tallyho.missile.IGuidanceFactory;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.croc.mw_peripherals.entity.MountedMissileCameraEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class TVGuidance extends GuidanceComponent {
    public final int ARMING_TICKS = 5;

    public final SeekerProperties properties;

    @Nullable
    private Player steerPlayer;

    public void setSteerPlayer(@Nullable Player steerPlayer) { this.steerPlayer = steerPlayer; }

    @Nullable public Player getSteerPlayer() { return steerPlayer; }

    public record SeekerProperties(float max_G, int range) {
        public void appendHoverText(List<Component> components) {
            components.add(Component.literal("Guidance: TV").setStyle(Style.EMPTY
                    .withColor(ChatFormatting.AQUA)));
            components.add(Component.literal(String.format("Range: %sm", this.range)).setStyle(Style.EMPTY
                    .withColor(ChatFormatting.WHITE)));
        }
    }

    public static class Factory implements IGuidanceFactory<TVGuidance> {
        protected final SeekerProperties properties;

        public Factory(float max_G, int range) {
            this.properties = new TVGuidance.SeekerProperties(max_G, range);
        }

        public TVGuidance create() {
            return new TVGuidance(this.properties);
        }

        public void appendHoverText(List<Component> components) {
            this.properties.appendHoverText(components);
        }
    }

    private TVGuidance(SeekerProperties properties) {
        this.properties = properties;
    }

    public void tick(MountedMissileEntity missile) {
        if (missile.level().isClientSide)
            return;
        if (!isSeeking() || missile.getTicksSinceLaunch() < 5)
            return;
        if (this.steerPlayer == null)
            return;

        Vec3 origin = this.steerPlayer.position();

        if (missile.position().subtract(origin).lengthSqr() > (this.properties.range * this.properties.range))
            return;

        MountedMissileCameraEntity steerCamera = MountedMissileCameraEntity.getOrCreateCamera(missile);
        Vec3 targetPoint = missile.position().add(steerCamera.getLookAngle().scale(4));

        missile.setDeltaMovement(ProportionalGuidance(targetPoint, Vec3.ZERO, missile.position(), missile.getDeltaMovement(), 3.0F, this.properties.max_G));
    }

    public boolean launch(MountedMissileEntity missile) {
        activateSeeker();
        return true;
    }

    @Nonnull
    public CompoundTag serialiseNBT() {
        return super.serialiseNBT();
    }

    public void deserialiseNBT(@Nonnull CompoundTag nbt) {
        super.deserialiseNBT(nbt);
    }
}