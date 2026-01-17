package net.croc.mw_peripherals.integration.cc;

import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IPeripheral;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.croc.mw_peripherals.Main;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;

public class CannonMountPeripheral implements IPeripheral {
    @NotNull
    private final Level level;
    @NotNull
    private final BlockPos pos;

    public CannonMountPeripheral(@NotNull Level level, @NotNull BlockPos blockPos) {
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

    private static float getDisplayPitch(BlockEntity cannon) {
        try { return (float) cannon.getClass().getMethod("getDisplayPitch").invoke(cannon);
        } catch (Exception e) {
            Main.LOGGER.info(e.toString());
            return 0.0f;
        }
    }

    private static float getDisplayYaw(BlockEntity cannon) {
        try { return (float) cannon.getClass().getMethod("getDisplayYaw").invoke(cannon);
        } catch (Exception e) {
            Main.LOGGER.info(e.toString());
            return 0.0f;
        }
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
                boolean.class, boolean.class, boolean.class, boolean.class, int.class
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

    @LuaFunction(mainThread = true)
    public float getPitch() {
        BlockEntity be = this.level.getBlockEntity(this.pos);
        return getDisplayPitch(be);
    }

    @LuaFunction(mainThread = true)
    public float getYaw() {
        BlockEntity be = this.level.getBlockEntity(this.pos);
        return getDisplayYaw(be);
    }

    @LuaFunction(mainThread = true)
    public void setPitch(double pitch) {
        BlockEntity be = this.level.getBlockEntity(this.pos);
        setPitch(be, pitch);
    }

    @LuaFunction(mainThread = true)
    public void setYaw(double yaw) {
        BlockEntity be = this.level.getBlockEntity(this.pos);
        setYaw(be, yaw);
    }

    @LuaFunction(mainThread = true)
    public void assemble(boolean state) {
        BlockEntity be = this.level.getBlockEntity(this.pos);

        if (state) {
            onRedstoneUpdate(be, true, false, false, false, 0);
        } else {
            disassemble(be);
        }

        tick(be);
    }

    @LuaFunction(mainThread = true)
    public void fire(int firepower) {
        BlockEntity be = this.level.getBlockEntity(this.pos);

        if (firepower > 0) {
            onRedstoneUpdate(be, false, false, true, false, firepower);
        } else {
            onRedstoneUpdate(be, false, false, false, true, 0);
        }

        tick(be);
    }
}