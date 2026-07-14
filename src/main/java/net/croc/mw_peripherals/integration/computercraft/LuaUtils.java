package net.croc.mw_peripherals.integration.computercraft;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaterniond;
import org.joml.Vector3d;
import org.joml.Vector3f;

public class LuaUtils {
    public static Map<String, Double> toLua(Quaterniond q) {
        Map<String, Double> luaMap = new HashMap<>();
        luaMap.put("w", q.w());
        luaMap.put("x", q.x());
        luaMap.put("y", q.y());
        luaMap.put("z", q.z());
        return luaMap;
    }

    public static Map<String, Double> toLua(Vector3d vec) {
        Map<String, Double> luaMap = new HashMap<>();
        luaMap.put("x", vec.x());
        luaMap.put("y", vec.y());
        luaMap.put("z", vec.z());
        return luaMap;
    }

    public static Map<String, Double> toLua(Vector3f vec) {
        Map<String, Double> luaMap = new HashMap<>();
        luaMap.put("x", (double) vec.x());
        luaMap.put("y", (double) vec.y());
        luaMap.put("z", (double) vec.z());
        return luaMap;
    }

    public static Map<String, Double> toLua(Vec3 vec) {
        Map<String, Double> luaMap = new HashMap<>();
        luaMap.put("x", vec.x());
        luaMap.put("y", vec.y());
        luaMap.put("z", vec.z());
        return luaMap;
    }

    public static Map<String, Double> toLua(BlockPos vec) {
        Map<String, Double> luaMap = new HashMap<>();
        luaMap.put("x", (double) vec.getX());
        luaMap.put("y", (double) vec.getY());
        luaMap.put("z", (double) vec.getZ());
        return luaMap;
    }
}
