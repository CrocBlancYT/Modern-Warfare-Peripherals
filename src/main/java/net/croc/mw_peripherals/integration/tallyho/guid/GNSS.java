package net.croc.mw_peripherals.integration.tallyho.guid;

import edn.stratodonut.tallyho.entity.MountedMissileEntity;
import edn.stratodonut.tallyho.missile.GuidanceComponent;
import edn.stratodonut.tallyho.missile.IGuidanceFactory;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.List;

import static org.joml.Math.*;

public class GNSS extends GuidanceComponent {

    public final SeekerProperties properties;

    private Vec3 targetPos;

    public SeekerProperties getProperties() {
        return this.properties;
    }

    public Vec3 getTarget() {
        return this.targetPos;
    }

    public void setTargetPos(Vec3 targetPos) {
        this.targetPos = targetPos;
    }

    private static double atan(double x) {
        return asin(x / sqrt(x*x + 1));
    }

    public static Vec3 GPSGuidance(Vec3 targetPos, Vec3 missilePos, Vec3 missileVel,
                                   float coefficient, float maxG, float gps_diving_coefficient) {

        Vec3 relative_pos = targetPos.subtract(missilePos);
        double horizontal_distance = sqrt(relative_pos.x*relative_pos.x + relative_pos.z+relative_pos.z);
        double atan_distance = atan(horizontal_distance / gps_diving_coefficient);

        double sin_k = sin(atan_distance);
        double cos_k = cos(atan_distance);

        Vec3 dir = new Vec3(sin_k * relative_pos.x, -cos_k, sin_k * relative_pos.y);
        return ProportionalGuidance(missilePos.add(dir), Vec3.ZERO, missilePos, missileVel, coefficient, maxG);
    }

    public record SeekerProperties(int diving_coefficient, float max_G) {
        public void appendHoverText(List<Component> components) {
            components.add(Component.literal("Guidance: GNSS")
                    .setStyle(Style.EMPTY.withColor(ChatFormatting.AQUA)));

            components.add(Component.literal(String.format("Diving Horizontal Range: %sm", this.diving_coefficient))
                    .setStyle(Style.EMPTY.withColor(ChatFormatting.WHITE)));
        }
    }


    public static class Factory implements IGuidanceFactory<GNSS> {
        private final SeekerProperties props;

        public Factory(int diving_distance, float max_G) {
            this.props = new SeekerProperties(diving_distance, max_G);
        }

        public GNSS create() {
            return new GNSS(this.props);
        }

        public void appendHoverText(List<Component> components) {
            this.props.appendHoverText(components);
        }
    }

    private GNSS(SeekerProperties props) {
        this.properties = props;
    }

    @Override
    public void tick(MountedMissileEntity missile) {
        Vec3 v_m = missile.getDeltaMovement();

        if (this.targetPos != null && missile.getTicksSinceLaunch() > 10) {
            missile.setDeltaMovement(GPSGuidance(this.targetPos, missile.position(), v_m, 0,
                    this.properties.max_G, this.properties.diving_coefficient));
        }
    }

    @Override
    public boolean launch(MountedMissileEntity mountedMissileEntity) {
        return true;
    }
}