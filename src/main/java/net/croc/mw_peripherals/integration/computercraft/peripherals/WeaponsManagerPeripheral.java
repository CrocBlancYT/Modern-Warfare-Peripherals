package net.croc.mw_peripherals.integration.computercraft.peripherals;

import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IPeripheral;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import edn.stratodonut.tallyho.camera.entity.RemoteStationEntity;
import edn.stratodonut.tallyho.camera.entity.TargetingPodEntity;
import edn.stratodonut.tallyho.entity.MountedMissileEntity;
import net.croc.mw_peripherals.blocks.WPMBlockEntity;
import net.croc.mw_peripherals.integration.computercraft.LuaCROWS;
import net.croc.mw_peripherals.integration.computercraft.LuaOrdnance;
import net.croc.mw_peripherals.integration.computercraft.LuaTGP;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

public class WeaponsManagerPeripheral implements IPeripheral {
    public static final int range = 7;

    private final Level level;
    private final BlockPos pos;
    private final WPMBlockEntity wpm;

    public WeaponsManagerPeripheral(Level level, BlockPos blockPos) {
        this.level = level;
        this.pos = blockPos;
        this.wpm = (WPMBlockEntity) this.level.getBlockEntity(blockPos);
    }

    @Nonnull
    public String getType() {
        return "weapons_manager";
    }

    public boolean equals(@Nullable IPeripheral iPeripheral) {
        return false;
    }

    @LuaFunction
    public final ArrayList<Object> scan() {
        ArrayList<Object> output = new ArrayList<>();

        for (WPMBlockEntity.Weapon<?> weapon : this.wpm.scanForWeapons()) {
            output.add(weapon.asLua(level, pos));
        }

        return output;
    }
}
