package net.croc.mw_peripherals.integration.computercraft.peripherals;

import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IPeripheral;
import edn.stratodonut.tallyho.entity.LaserPointEntity;
import edn.stratodonut.tallyho.entity.MountedMissileEntity;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.croc.mw_peripherals.blocks.LWSBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

public class LWSPeripheral implements IPeripheral {
    private final Level level;
    private final BlockPos pos;
    private final LWSBlockEntity lws;

    public LWSPeripheral(Level level, BlockPos blockPos) {
      this.level = level;
      this.pos = blockPos;
      this.lws = (LWSBlockEntity) level.getBlockEntity(pos);
    }

    @Nonnull
    public String getType() {
      return "lws";
    }

    public boolean equals(@Nullable IPeripheral iPeripheral) {
      return false;
    }

    @LuaFunction
    public boolean isTriggered() {
        return lws.isTriggered();
    }
}
