package net.croc.mw_peripherals.integration.tallyho.guid;

import edn.stratodonut.tallyho.entity.MountedMissileEntity;
import edn.stratodonut.tallyho.missile.GuidanceComponent;
import edn.stratodonut.tallyho.missile.IGuidanceFactory;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

import javax.annotation.Nullable;
import java.util.List;

public class DataLink extends GuidanceComponent  {

    public final SeekerProperties properties;

    @Nullable
    protected Vec3 target;
    protected Vec3 velocity;
    protected long gameTime;

    public SeekerProperties getProperties() {
        return this.properties;
    }

    public void refresh(long gameTime) { this.gameTime = gameTime; }

    public Vec3 getTargetPos() {
        return this.target;
    }

    public Vec3 getTargetVelocity() {
        return this.velocity;
    }

    public boolean isActive(long currentGameTime) {
        return this.gameTime + this.properties.guidance_time < currentGameTime && this.target != null && this.velocity != null;
    }

    public DataLink(SeekerProperties properties) {
        this.properties = properties;
        this.gameTime = -properties.guidance_time;
    }

    private Vec3 transformToWorld(Level level, Vec3 pos) {
        Vector3d p = pos.toVector3f().get(new Vector3d());
        Ship ship = VSGameUtilsKt.getShipManagingPos(level, pos);

        if (ship != null) {
            p = ship.getShipToWorld().transformPosition(p);
        }

        return new Vec3(p.x, p.y, p.z);
    }

    public void setTargetPosition(MountedMissileEntity missile, BlockPos origin, Vec3 targetPos) {
        double distanceSqr = transformToWorld(missile.level(), origin.getCenter()).subtract(missile.position()).lengthSqr();
        double link_range = this.properties.link_range;

        if (distanceSqr < link_range * link_range) {
            this.target = targetPos;
        }
    }

    public void setTargetVelocity(MountedMissileEntity missile, BlockPos origin, Vec3 targetVelocity) {
        double distanceSqr = transformToWorld(missile.level(), origin.getCenter()).subtract(missile.position()).lengthSqr();
        double link_range = this.properties.link_range;

        if (distanceSqr < link_range*link_range) {
            this.velocity = targetVelocity;
        }
    }

    public record SeekerProperties(int link_range, int lead_coefficient, float max_G, int guidance_time) {
        public void appendHoverText(List<Component> components) {
            components.add(Component.literal("Guidance: Data-Link").setStyle(Style.EMPTY
                    .withColor(ChatFormatting.AQUA)));

            components.add(Component.literal(String.format("Link Range: %sm", this.link_range)).setStyle(Style.EMPTY
                    .withColor(ChatFormatting.WHITE)));

            components.add(Component.literal(String.format("Guidance Time: %ss", this.guidance_time * 0.05D))
                    .setStyle(Style.EMPTY.withColor(ChatFormatting.WHITE)));
        }
    }

    public static class Factory implements IGuidanceFactory<DataLink> {
        private final DataLink.SeekerProperties props;

        public Factory(int link_range, int lead_coefficient, float max_G, int guidance_time) {
            this.props = new DataLink.SeekerProperties(link_range, lead_coefficient, max_G, guidance_time);
        }

        public DataLink create() {
            return new DataLink(this.props);
        }

        public void appendHoverText(List<Component> components) {
            this.props.appendHoverText(components);
        }
    }


    @Override
    public void tick(MountedMissileEntity missile) {
        if (isActive(missile.level().getGameTime()) && this.target != null) {
            missile.setDeltaMovement(GuidanceComponent.ProportionalGuidance(this.target, this.velocity,
                    missile.position(), missile.getDeltaMovement(), this.properties.lead_coefficient, this.properties.max_G));
        }
    }

    @Override
    public boolean launch(MountedMissileEntity mountedMissileEntity) {
        return true;
    }
}