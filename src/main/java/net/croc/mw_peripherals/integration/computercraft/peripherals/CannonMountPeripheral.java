package net.croc.mw_peripherals.integration.computercraft.peripherals;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IPeripheral;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.croc.mw_peripherals.mixin.CannonMountAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.gen.Accessor;
import rbasamoyai.createbigcannons.cannon_control.cannon_mount.CannonMountBlockEntity;
import rbasamoyai.createbigcannons.cannon_control.contraption.AbstractMountedCannonContraption;
import rbasamoyai.createbigcannons.cannon_control.contraption.MountedAutocannonContraption;
import rbasamoyai.createbigcannons.cannon_control.contraption.PitchOrientedContraptionEntity;
import riftyboi.cbcmodernwarfare.cannon_control.compact_mount.CompactCannonMountBlockEntity;

public class CannonMountPeripheral implements IPeripheral {
    private final Level level;

    private final BlockPos pos;

    public CannonMountPeripheral(@NotNull Level level, @NotNull BlockPos blockPos) {
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

    public static boolean isAutocannon(CannonMountAccessor cannon) {
        PitchOrientedContraptionEntity pitchContraption = cannon.IgetContraption();
        return pitchContraption != null && pitchContraption.getContraption() instanceof MountedAutocannonContraption;
    }

    public CannonMountAccessor getCannonMount() throws LuaException {
        if (this.level.getBlockEntity(this.pos) instanceof CannonMountBlockEntity cannonMount) {
            return (CannonMountAccessor) cannonMount;
        }

        if (this.level.getBlockEntity(this.pos) instanceof CompactCannonMountBlockEntity cannonMount) {
            return (CannonMountAccessor) cannonMount;
        }

        throw new LuaException("No cannon mount");
    }

    @LuaFunction(mainThread = true)
    public double getPitch() throws LuaException {
        return getCannonMount().getCannonPitch();
    }

    @LuaFunction(mainThread = true)
    public double getYaw() throws LuaException {
        return getCannonMount().getCannonYaw();
    }

    @LuaFunction(mainThread = true)
    public double getMaxDepress() throws LuaException {
        return getCannonMount().IgetMaxDepress();
    }

    @LuaFunction(mainThread = true)
    public double getMaxElevate() throws LuaException {
        return getCannonMount().IgetMaxElevate();
    }

    @LuaFunction(mainThread = true)
    public void setPitch(double pitch) throws LuaException {
        CannonMountAccessor cannon = getCannonMount();
        if (!isAutocannon(cannon)) throw new LuaException("setPitch only accessible to autocannons");
        cannon.IsetPitch((float) pitch);
        cannon.Itick();
    }

    @LuaFunction(mainThread = true)
    public void setYaw(double yaw) throws LuaException {
        CannonMountAccessor cannon = getCannonMount();
        if (!isAutocannon(cannon)) throw new LuaException("setYaw only accessible to autocannons");
        cannon.IsetPitch((float) yaw);
        cannon.Itick();
    }

    @LuaFunction(mainThread = true)
    public void assemble() throws LuaException {
        CannonMountAccessor cannon = getCannonMount();
        cannon.Iassemble();
        cannon.Itick();
    }

    @LuaFunction(mainThread = true)
    public void disassemble() throws LuaException {
        CannonMountAccessor cannon = getCannonMount();
        cannon.Idisassemble();
        cannon.Itick();
    }

    @LuaFunction(mainThread = true)
    public void fire(int firepower) throws LuaException {
        CannonMountAccessor cannon = getCannonMount();
        if (firepower > 0) {
            cannon.IonRedstoneUpdate(false, false, true, false, firepower);
        } else {
            cannon.IonRedstoneUpdate(false, false, false, true, 0);
        }
        cannon.Itick();
    }
}
