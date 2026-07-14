package net.croc.mw_peripherals.integration.computercraft.peripherals;

import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IPeripheral;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import edn.stratodonut.tallyho.block.RippleFireBlock;
import edn.stratodonut.tallyho.entity.MountedMissileEntity;
import edn.stratodonut.tallyho.missile.GuidanceComponent;
import net.croc.mw_peripherals.blocks.FlareDispenserBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3dc;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.util.VectorConversionsMCKt;

import java.util.List;
import java.util.Random;

public class RippleFirePeripheral implements IPeripheral {
    Random random = new Random();

    private final Level level;
    private final BlockPos pos;

    public RippleFirePeripheral(Level level, BlockPos blockPos) {
        this.level = level;
        this.pos = blockPos;
    }

    @Nonnull
    public String getType() {
        return "ripple_fire";
    }

    public boolean equals(@Nullable IPeripheral iPeripheral) {
        return false;
    }

    @LuaFunction
    public final boolean fire() {
        Ship ship = VSGameUtilsKt.getShipManagingPos(level, pos);
        if (ship != null && (level.getBlockState(pos).getBlock() instanceof RippleFireBlock)) {
            Vec3 worldPos = VectorConversionsMCKt.toMinecraft(
                    ship.getShipToWorld().transformPosition(
                            VectorConversionsMCKt.toJOML(Vec3.atLowerCornerOf(pos))
                    )
            );

            List<Entity> missiles = level.getEntities((Entity) null,
                    AABB.ofSize(worldPos, 7.0D, 7.0D, 7.0D),
                    entity -> (entity instanceof MountedMissileEntity && entity.isPassenger())
            );

            if (missiles.isEmpty()) return false;

            Entity target = missiles.get((int)(random.nextFloat() * (missiles.size() - 1)));

            if (target instanceof MountedMissileEntity) {
                MountedMissileEntity missile = (MountedMissileEntity) target;
                GuidanceComponent guidance = missile.getGuidance();
                if (guidance != null && ship.getSlug() != null) {
                    guidance.setCode(ship.getSlug());
                }
                missile.launch();
                return true;
            }
        }

        return false;
    }

}
