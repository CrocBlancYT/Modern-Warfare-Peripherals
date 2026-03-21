package net.croc.mw_peripherals.integration.computercraft;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaterniond;
import org.joml.Vector3d;

public class LuaUtils {
    public static Map<String, ?> toLua(Quaterniond q) {
        Map<String, Object> luaMap = new HashMap<>();
        luaMap.put("w", q.w());
        luaMap.put("x", q.x());
        luaMap.put("y", q.y());
        luaMap.put("z", q.z());
        return luaMap;
    }

    public static Map<String, ?> toLua(Vector3d vec) {
        Map<String, Object> luaMap = new HashMap<>();
        luaMap.put("x", vec.x());
        luaMap.put("y", vec.y());
        luaMap.put("z", vec.z());
        return luaMap;
    }

    public static Map<String, ?> toLua(Vec3 vec) {
        Map<String, Object> luaMap = new HashMap<>();
        luaMap.put("x", vec.x());
        luaMap.put("y", vec.y());
        luaMap.put("z", vec.z());
        return luaMap;
    }

    public static Map<String, ?> toLua(BlockPos vec) {
        Map<String, Object> luaMap = new HashMap<>();
        luaMap.put("x", vec.getX());
        luaMap.put("y", vec.getY());
        luaMap.put("z", vec.getZ());
        return luaMap;
    }
}
