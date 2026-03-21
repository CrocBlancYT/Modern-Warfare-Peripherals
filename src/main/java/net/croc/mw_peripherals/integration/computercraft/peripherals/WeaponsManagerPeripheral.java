package net.croc.mw_peripherals.integration.computercraft.peripherals;

import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IPeripheral;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import edn.stratodonut.tallyho.camera.entity.RemoteStationEntity;
import edn.stratodonut.tallyho.camera.entity.TargetingPodEntity;
import edn.stratodonut.tallyho.entity.MountedMissileEntity;
import edn.stratodonut.tallyho.missile.GuidanceComponent;
import net.croc.mw_peripherals.integration.computercraft.LuaCROWS;
import net.croc.mw_peripherals.integration.computercraft.LuaMissile;
import net.croc.mw_peripherals.integration.computercraft.LuaTGP;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.valkyrienskies.core.api.ships.LoadedShip;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

public class WeaponsManagerPeripheral implements IPeripheral {
    public static final int range = 7;

    private final Level level;
    private final BlockPos pos;

    public WeaponsManagerPeripheral(Level level, BlockPos blockPos) {
        this.level = level;
        this.pos = blockPos;
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
        AABB box = (new AABB(pos)).inflate(7.0D);
        List<Entity> entities = level.getEntities(null, box);

        entities.forEach(entity -> {
            if (entity instanceof MountedMissileEntity missile) {
                output.add(new LuaMissile(missile, this.level, this.pos));
            } else if (entity instanceof RemoteStationEntity crows) {
                output.add(new LuaCROWS(crows, this.level, this.pos));
            } else if (entity instanceof TargetingPodEntity tgp) {
                output.add(new LuaTGP(tgp, this.level, this.pos));
            }
        });

        return output;
    }
}
