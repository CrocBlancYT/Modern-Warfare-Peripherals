package net.croc.mw_peripherals.integration.computercraft;

import dan200.computercraft.api.lua.LuaFunction;
import edn.stratodonut.tallyho.entity.MountedMissileEntity;
import edn.stratodonut.tallyho.missile.GuidanceComponent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.valkyrienskies.core.api.ships.LoadedShip;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

public class LuaMissile {
    MountedMissileEntity missile;
    private final Level level;
    private final BlockPos pos;

    public LuaMissile(MountedMissileEntity missile, Level level, BlockPos pos) {
        this.missile = missile;
        this.level = level;
        this.pos = pos;
    }
    
    @LuaFunction
    public boolean isMissile() { return true; }

    @LuaFunction
    public String getUUID() {
        return this.missile.getUUID().toString();
    }

    @LuaFunction
    public String getMissileId() {
        return this.missile.getMissileId();
    }

    @LuaFunction
    public void launch() {
        LoadedShip loadedShip = VSGameUtilsKt.getShipObjectManagingPos(this.level, this.pos);

        GuidanceComponent guidance = this.missile.getGuidance();
        if (guidance != null && loadedShip != null) {
            guidance.setCode(loadedShip.getSlug());
        }

        this.missile.launch();
    }

}