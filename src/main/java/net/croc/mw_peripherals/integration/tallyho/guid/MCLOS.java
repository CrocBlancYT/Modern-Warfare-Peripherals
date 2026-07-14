package net.croc.mw_peripherals.integration.tallyho.guid;

import edn.stratodonut.tallyho.entity.MountedMissileEntity;
import edn.stratodonut.tallyho.missile.GuidanceComponent;
import edn.stratodonut.tallyho.missile.IGuidanceFactory;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.croc.mw_peripherals.Main;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class MCLOS extends GuidanceComponent {
    public final int ARMING_TICKS = 5;

    public final SeekerProperties properties;

    @Nullable
    private Vec3 steerAngVel;

    @Nullable
    private Player steerPlayer;

    public void linkJoystick(Player player) {
        this.steerPlayer = player;
    }

    public void updateSteering(Vec3 input) {
        this.steerAngVel = input;
    }

    public record SeekerProperties(float max_G, int range) {
        public void appendHoverText(List<Component> components) {
                components.add(Component.literal("Guidance: MCLOS").setStyle(Style.EMPTY
                        .withColor(ChatFormatting.AQUA)));
                components.add(Component.literal(String.format("Range: %sm", this.range)).setStyle(Style.EMPTY
                        .withColor(ChatFormatting.WHITE)));
            }
        }

    public static class Factory implements IGuidanceFactory<MCLOS> {
        protected final MCLOS.SeekerProperties properties;

        public Factory(float max_G, int range) {
            this.properties = new MCLOS.SeekerProperties(max_G, range);
        }

        public MCLOS create() {
            return new MCLOS(this.properties);
        }

        public void appendHoverText(List<Component> components) {
            this.properties.appendHoverText(components);
        }
    }

    private MCLOS(SeekerProperties properties) {
        this.properties = properties;
    }

    public void tick(MountedMissileEntity missile) {
        if (missile.level().isClientSide)
            return;
        if (!isSeeking() || missile.getTicksSinceLaunch() < 5)
            return;
        if (this.steerAngVel == null || this.steerPlayer == null)
            return;

        Vec3 origin = this.steerPlayer.position();

        Vec3 forward = missile.getLookAngle();
        Vec3 up = missile.getUpVector(1);
        Vec3 right = forward.cross(up);

        Vec3 steerDirection = forward
                .add(right.scale(this.steerAngVel.z).scale(this.properties.max_G))
                .add(up.scale(this.steerAngVel.y).scale(this.properties.max_G));

        Vec3 steerTarget = missile.position().add(steerDirection.scale(5));

        if (steerTarget.subtract(origin).lengthSqr() > (this.properties.range * this.properties.range))
            return;

        missile.setDeltaMovement(ProportionalGuidance(steerTarget, Vec3.ZERO, missile.position(), missile.getDeltaMovement(), 3.0F, this.properties.max_G));
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