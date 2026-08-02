package net.croc.mw_peripherals.integration.computercraft.peripherals;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IPeripheral;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.croc.mw_peripherals.mixin.CannonMountAccessor;
import net.croc.mw_peripherals.mixin.FixedCannonMountAccessor;
import net.croc.mw_peripherals.mixinducks.CannonMountDuck;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import org.jetbrains.annotations.NotNull;
import rbasamoyai.createbigcannons.cannon_control.cannon_mount.CannonMountBlockEntity;
import rbasamoyai.createbigcannons.cannon_control.cannon_mount.ExtendsCannonMount;
import rbasamoyai.createbigcannons.cannon_control.contraption.MountedAutocannonContraption;
import rbasamoyai.createbigcannons.cannon_control.contraption.PitchOrientedContraptionEntity;
import rbasamoyai.createbigcannons.cannon_control.fixed_cannon_mount.FixedCannonMountBlockEntity;
import riftyboi.cbcmodernwarfare.cannon_control.compact_mount.CompactCannonMountBlockEntity;
import riftyboi.cbcmodernwarfare.cannon_control.compact_mount.ExtendsCompactCannonMount;

public class CannonMountPeripheral implements IPeripheral {
    private final Level level;

    private final BlockPos pos;

    public CannonMountPeripheral(@NotNull Level level, @NotNull BlockPos blockPos) {
        this.level = level;
        this.pos = blockPos;
    }

    private double clamp(double min, double value, double max) {
        if (value > max) return max;
        if (value < min) return min;
        return value;
    }

    @Nonnull
    public String getType() {
        return "cannon_mount";
    }

    public boolean equals(@Nullable IPeripheral iPeripheral) {
        return false;
    }

    private static final DirectionProperty HORIZONTAL_FACING = BlockStateProperties.HORIZONTAL_FACING;

    public double getBaseYaw() {
        return this.level.getBlockState(this.pos).getValue(HORIZONTAL_FACING).toYRot();
    }

    public static boolean isAutocannon(CannonMountDuck cannon) {
        PitchOrientedContraptionEntity pitchContraption = cannon.IgetContraption();
        return pitchContraption != null && pitchContraption.getContraption() instanceof MountedAutocannonContraption;
    }

    public CannonMountDuck getCannonMount() throws LuaException {
        BlockEntity be = this.level.getBlockEntity(this.pos);

        if (be instanceof FixedCannonMountBlockEntity mount) return (FixedCannonMountAccessor) mount;
        if (be instanceof CannonMountBlockEntity mount) return (CannonMountAccessor) mount;
        if (be instanceof CompactCannonMountBlockEntity mount) return (CannonMountAccessor) mount;

        throw new LuaException("No cannon mount");
    }

    @LuaFunction(mainThread = true)
    public final double getPitch() throws LuaException {
        return getCannonMount().getCannonPitch();
    }

    @LuaFunction(mainThread = true)
    public final double getYaw() throws LuaException {
        return getCannonMount().getCannonYaw();
    }

    @LuaFunction(mainThread = true)
    public final double getMaxDepress() throws LuaException {
        if (getCannonMount() instanceof CannonMountAccessor fixed) return fixed.IgetMaxDepress();
        throw new LuaException("This cannon mount doesn't support this method");
    }

    @LuaFunction(mainThread = true)
    public final double getMaxElevate() throws LuaException {
        if (getCannonMount() instanceof CannonMountAccessor mount) return mount.IgetMaxElevate();
        throw new LuaException("This cannon mount doesn't support this method");
    }

    @LuaFunction(mainThread = true)
    public final void setPitch(double pitch) throws LuaException {
        CannonMountDuck cannon = getCannonMount();
        if (!isAutocannon(cannon)) throw new LuaException("setPitch only accessible to autocannons");
        if (!(cannon instanceof CannonMountAccessor)) throw new LuaException("This cannon mount doesn't support this method");
        cannon.setCannonPitch((float) clamp(-getMaxDepress(), pitch, getMaxElevate()));
        cannon.Itick();
    }

    @LuaFunction(mainThread = true)
    public final void setYaw(double yaw) throws LuaException {
        CannonMountDuck cannon = getCannonMount();
        if (!isAutocannon(cannon)) throw new LuaException("setYaw only accessible to autocannons");
        if (!(cannon instanceof CannonMountAccessor) || cannon instanceof CompactCannonMountBlockEntity)
            throw new LuaException("This cannon mount doesn't support this method");
        cannon.setCannonYaw((float) (yaw + getBaseYaw()));
        cannon.Itick();
    }

    @LuaFunction(mainThread = true)
    public final void assemble() throws LuaException {
        CannonMountDuck cannon = getCannonMount();
        cannon.Iassemble();
        cannon.Itick();
    }

    @LuaFunction(mainThread = true)
    public final void disassemble() throws LuaException {
        CannonMountDuck cannon = getCannonMount();
        cannon.Idisassemble();
        cannon.Itick();
    }

    @LuaFunction(mainThread = true)
    public final void fire(int firepower) throws LuaException {
        CannonMountDuck cannon = getCannonMount();
        if (firepower > 0) {
            cannon.IonRedstoneUpdate(false, false, true, false, firepower);
        } else {
            cannon.IonRedstoneUpdate(false, false, false, true, 0);
        }
        cannon.Itick();
    }
}
