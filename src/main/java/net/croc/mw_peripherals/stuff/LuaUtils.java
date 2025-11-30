package net.croc.mw_peripherals.stuff;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Map;

public class LuaUtils {
    public static Map<String, Object> toLua(Vec3 vec) {
        Map<String, Object> luaMap = new HashMap<>();
        luaMap.put("x", vec.x());
        luaMap.put("y", vec.y());
        luaMap.put("z", vec.z());
        return luaMap;
    };

    public static Map<String, Object> toLua(BlockPos vec) {
        Map<String, Object> luaMap = new HashMap<>();
        luaMap.put("x", vec.getX());
        luaMap.put("y", vec.getY());
        luaMap.put("z", vec.getZ());
        return luaMap;
    };
}