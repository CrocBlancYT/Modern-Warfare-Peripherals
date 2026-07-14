package net.croc.mw_peripherals.integration.computercraft;

import dan200.computercraft.api.lua.LuaFunction;
import edn.stratodonut.tallyho.missile.Target;
import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.HashCommon;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

public class LuaTargetLock {
    public static final short LIFETIME = 3;

    public final Level level;

    private final long start;

    @Nonnull
    public final Target<?> target;

    @Nonnull
    private Vec3 cachedPosition;

    @Nonnull
    private Vec3 cachedVelocity;

    private boolean cached_valid = false;

    public LuaTargetLock(Level level, Target<?> target) {
        this.level = level;
        this.start = level.getGameTime();
        this.target = target;

        cachedPosition = target.position();
        cachedVelocity = target.velocity();
    }

    @LuaFunction
    public boolean isValid() {
        if (!cached_valid) return false;
        if (this.level != null && this.start + LIFETIME > this.level.getGameTime() && this.target.isAlive()) {
            return true;
        }
        cached_valid = false;
        return false;
    }

    @LuaFunction
    public Map<String, Double> position() {
        if (isValid()) cachedPosition = target.position();
        return LuaUtils.toLua(cachedPosition);
    }

    @LuaFunction
    public Map<String, Double> velocity() {
        if (isValid()) cachedVelocity = target.velocity();
        return LuaUtils.toLua(cachedVelocity);
    }
}