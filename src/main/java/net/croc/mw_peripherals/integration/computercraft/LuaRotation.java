package net.croc.mw_peripherals.integration.computercraft;

import dan200.computercraft.api.lua.LuaFunction;
import java.util.Map;
import org.joml.Quaterniond;
import org.joml.Vector3d;

public class LuaRotation {
    private final Quaterniond rotation;

    public LuaRotation(Quaterniond rot) {
        this.rotation = rot;
    }

    @LuaFunction
    public Map<String, ?> getEulerAnglesXYZ() {
        return LuaUtils.toLua(this.rotation.getEulerAnglesXYZ(new Vector3d()));
    }

    @LuaFunction
    public Map<String, ?> getEulerAnglesYXZ() {
        return LuaUtils.toLua(this.rotation.getEulerAnglesYXZ(new Vector3d()));
    }

    @LuaFunction
    public Map<String, ?> getEulerAnglesZXY() {
        return LuaUtils.toLua(this.rotation.getEulerAnglesZXY(new Vector3d()));
    }

    @LuaFunction
    public Map<String, ?> getEulerAnglesZYX() {
        return LuaUtils.toLua(this.rotation.getEulerAnglesZYX(new Vector3d()));
    }

    @LuaFunction
    public Map<String, ?> getQuaternion() {
        return LuaUtils.toLua(this.rotation);
    }
}
