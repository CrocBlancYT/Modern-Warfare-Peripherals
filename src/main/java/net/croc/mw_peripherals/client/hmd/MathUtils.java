package net.croc.mw_peripherals.client.hmd;

import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class MathUtils {
    public static Vec3 getEulerAngles(Quaternionf rot) {
        Vector3f zxy = rot.getEulerAnglesZXY(new Vector3f());
        return new Vec3(zxy.x, zxy.y, zxy.z);
    }


}