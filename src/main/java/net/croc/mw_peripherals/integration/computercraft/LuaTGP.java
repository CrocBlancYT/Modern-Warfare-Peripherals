package net.croc.mw_peripherals.integration.computercraft;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import edn.stratodonut.tallyho.camera.entity.TargetingPodEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.croc.mw_peripherals.integration.computercraft.LuaUtils.toLua;

public class LuaTGP {
    private final TargetingPodEntity tgp;

    public LuaTGP(TargetingPodEntity tgp, Level level, BlockPos pos) {
        this.tgp = tgp;
    }

    public static final HashMap<String, TargetingPodEntity.TGP_MODE> MODES = new HashMap<>();
    public static final HashMap<TargetingPodEntity.TGP_MODE, String> NAMES = new HashMap<>();

    static {
        MODES.put("none", TargetingPodEntity.TGP_MODE.NONE);
        MODES.put("INR", TargetingPodEntity.TGP_MODE.INR);
        MODES.put("LSRCH", TargetingPodEntity.TGP_MODE.LSRCH);

        NAMES.put(TargetingPodEntity.TGP_MODE.NONE, "none");
        NAMES.put(TargetingPodEntity.TGP_MODE.INR, "INR");
        NAMES.put(TargetingPodEntity.TGP_MODE.LSRCH, "LSRCH");
    }

    @LuaFunction
    public boolean isTGP() { return true; }

    @LuaFunction
    public void turnX(double x) {
        this.tgp.turnView(0, x);
    }

    @LuaFunction
    public void turnY(double y) {
        this.tgp.turnView(y, 0);
    }

    @LuaFunction
    public Map<String, ?> getPosition() {
        return toLua(this.tgp.getEyePosition());
    }

    @LuaFunction
    public String getUUID() {
        return this.tgp.getUUID().toString();
    }

    @LuaFunction
    public void setMode(String name) throws LuaException {
        TargetingPodEntity.TGP_MODE mode = MODES.get(name);

        if (mode != null) {
            this.tgp.setTGP(mode, this.tgp.getLookAngle());
            return;
        }

        throw new LuaException(name + " is not a mode");
    }

    @LuaFunction
    public String getMode() {
        return NAMES.get(this.tgp.getTGPMode());
    }

    @LuaFunction
    public List<String> getModes() {
        return NAMES.values().stream().toList();
    }

    @LuaFunction
    public void setLazeState(boolean targetState) {
        if (this.tgp.isDesignating() != targetState) {
            this.tgp.toggleLazeMode();
        }
    }

    @LuaFunction
    public boolean getLazeState() {
        return this.tgp.isDesignating();
    }
}
