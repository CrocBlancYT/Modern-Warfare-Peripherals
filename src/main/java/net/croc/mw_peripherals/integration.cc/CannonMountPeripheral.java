package net.croc.mw_peripherals.integration.cc;

import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IPeripheral;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.croc.mw_peripherals.Main;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class CannonMountPeripheral implements IPeripheral {
    private final Level level;
    private final BlockPos pos;

    public CannonMountPeripheral(Level level, BlockPos blockPos) {
        //this.flap = be;
        this.level = level;
        this.pos = blockPos;
    }

    @Nonnull
    public String getType() {
        return "cannon_mount";
    }

    public boolean equals(@Nullable IPeripheral iPeripheral) {
        return false;
    }

    private static double getDisplayPitch(BlockEntity cannon) {
        try { return (double) cannon.getClass().getMethod("getDisplayPitch").invoke(cannon);
        } catch (Exception e) { return 0.0; }
    }

    private static double getDisplayYaw(BlockEntity cannon) {
        try { return (double) cannon.getClass().getMethod("getDisplayYaw").invoke(cannon);
        } catch (Exception e) { return 0.0; }
    }

    private static void setPitch(BlockEntity cannon, double pitch) {
        try { cannon.getClass().getMethod("setPitch",
                float.class
        ).invoke(cannon, (float) pitch);
        } catch (Exception e) { Main.LOGGER.info(e.toString()); }
    }

    private static void setYaw(BlockEntity cannon, double yaw) {
        try { cannon.getClass().getMethod("setYaw",
                float.class
        ).invoke(cannon, (float) yaw);
        } catch (Exception e) { Main.LOGGER.info(e.toString()); }
    }

    private static void onRedstoneUpdate(BlockEntity cannon, Boolean a, Boolean b, Boolean c, Boolean d, int p) {
        try { cannon.getClass().getMethod("onRedstoneUpdate",
                Boolean.class, Boolean.class, Boolean.class, Boolean.class, int.class
        ).invoke(cannon, a, b, c, d, p);
        } catch (Exception e) { Main.LOGGER.info(e.toString()); }
    }

    private static void disassemble(BlockEntity cannon) {
        try { cannon.getClass().getMethod("disassemble").invoke(cannon);
        } catch (Exception e) { Main.LOGGER.info(e.toString()); }
    }

    private static void tick(BlockEntity cannon) {
        try { cannon.getClass().getMethod("tick").invoke(cannon);
        } catch (Exception e) { Main.LOGGER.info(e.toString()); }
    }

    @LuaFunction
    public final double getPitch() {
        BlockEntity be = this.level.getBlockEntity(this.pos);
        return getDisplayPitch(be);
    }

    @LuaFunction
    public final double getYaw() {
        BlockEntity be = this.level.getBlockEntity(this.pos);
        return getDisplayYaw(be);
    }

    @LuaFunction
    public final void setPitch(double pitch) {
        BlockEntity be = this.level.getBlockEntity(this.pos);
        setPitch(be, pitch);
    }

    @LuaFunction
    public final void setYaw(double yaw) {
        BlockEntity be = this.level.getBlockEntity(this.pos);
        setYaw(be, yaw);
    }

    @LuaFunction
    public final void assemble(boolean state) {
        BlockEntity be = this.level.getBlockEntity(this.pos);

        if (state) {
            onRedstoneUpdate(be, true, false, false, false, 0);
        } else {
            disassemble(be);
        }

        tick(be);
    }

    @LuaFunction
    public final void fire(int firepower) {
        BlockEntity be = this.level.getBlockEntity(this.pos);

        if (firepower > 0) {
            onRedstoneUpdate(be, false, false, true, false, firepower);
        } else {
            onRedstoneUpdate(be, false, false, false, true, 0);
        }

        tick(be);
    }
}