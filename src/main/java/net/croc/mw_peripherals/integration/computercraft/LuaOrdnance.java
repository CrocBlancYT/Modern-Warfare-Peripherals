package net.croc.mw_peripherals.integration.computercraft;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import edn.stratodonut.tallyho.entity.MountedMissileEntity;
import edn.stratodonut.tallyho.missile.GuidanceComponent;
import net.croc.mw_peripherals.integration.tallyho.guid.DataLink;
import net.croc.mw_peripherals.integration.tallyho.guid.GNSS;
import net.croc.mw_peripherals.integration.tallyho.guid.MultiModeSeeker;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.valkyrienskies.core.api.ships.LoadedShip;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

import java.util.Map;

import static net.croc.mw_peripherals.integration.computercraft.LuaUtils.*;

public class LuaOrdnance {
    MountedMissileEntity missile;
    private final Level level;
    private final BlockPos pos;

    public LuaOrdnance(MountedMissileEntity missile, Level level, BlockPos pos) {
        this.missile = missile;
        this.level = level;
        this.pos = pos;
    }
    
    @LuaFunction
    public boolean isOrdnance() { return true; }

    @LuaFunction
    public String getUUID() {
        return this.missile.getUUID().toString();
    }

    @LuaFunction
    public String getOrdnanceId() {
        return this.missile.getMissileId();
    }

    public record LuaDataLink(BlockPos origin, MountedMissileEntity missile, DataLink dataLink) {
        public static LuaDataLink getFromMissile(BlockPos origin, MountedMissileEntity missile) {
            if (missile.getGuidance() instanceof DataLink dataLink) {
                return new LuaDataLink(origin, missile, dataLink);
            }

            if (missile.getGuidance() instanceof MultiModeSeeker multiModeSeeker) {
                for (GuidanceComponent guid : multiModeSeeker.getGuidances()) {
                    if (guid instanceof DataLink dataLink) {
                        return new LuaDataLink(origin, missile, dataLink);
                    }
                }
            }

            return null;
        }

        @LuaFunction
        public Map<String, ?> getPosition() { return toLua(this.missile.position()); }

        @LuaFunction
        public Map<String, ?> getBearing() { return toLua(this.missile.getLookAngle()); }

        @LuaFunction
        public void setTargetPosition(double x, double y, double z) { this.dataLink.setTargetPosition(this.missile, this.origin, new Vec3(x,y,z)); }

        @LuaFunction
        public void setTargetVelocity(double x, double y, double z) { this.dataLink.setTargetVelocity(this.missile, this.origin, new Vec3(x,y,z)); }

        @LuaFunction
        public void setTargetLock(LuaTargetLock lock) {
            Vec3 pos = lock.target.position();
            Vec3 vel = lock.target.velocity();

            this.setTargetPosition(pos.x, pos.y, pos.z);
            this.setTargetVelocity(vel.x, vel.y, vel.z);
        }
    }

    @LuaFunction
    public LuaDataLink getDataLink() {
        return LuaDataLink.getFromMissile(this.pos, missile);
    }

    public static class LuaGNSS {
        MountedMissileEntity missile;
        GNSS gnss;
        boolean hasDatalink;

        public LuaGNSS(MountedMissileEntity missile, GNSS gnss, boolean hasDatalink) {
            this.missile = missile;
            this.gnss = gnss;
            this.hasDatalink = hasDatalink;
        }

        @LuaFunction
        public void setCoordinates(double x, double z) throws LuaException{
            if (!hasDatalink && this.missile.isDeployed()) {
                throw new LuaException("Missile is deployed!");
            }
            this.gnss.setTargetPos(new Vec3(x, 0, z));
        }

        public static LuaGNSS getFromMissile(MountedMissileEntity missile, boolean hasDatalink) {
            if (missile.getGuidance() instanceof GNSS gnss) {
                return new LuaGNSS(missile, gnss, hasDatalink);
            }

            if (missile.getGuidance() instanceof MultiModeSeeker multiModeSeeker) {
                for (GuidanceComponent guid : multiModeSeeker.getGuidances()) {
                    if (guid instanceof GNSS gnss) {
                        return new LuaGNSS(missile, gnss, hasDatalink);
                    }
                }
            }

            return null;
        }
    }

    @LuaFunction
    public LuaGNSS getGNSS() {
        return LuaGNSS.getFromMissile(missile, getDataLink() != null);
    }

    /*@LuaFunction
    public boolean setTargetLock(Object arg) {
        if (!(arg instanceof LuaTargetLock lock)) return false;

        if (this.missile.isDeployed()) return false;
        if (!this.missile.isAlive()) return false;

        if (this.missile.getGuidance() instanceof TargetAccessor targettingGuid){
            targettingGuid.setTarget(lock.target);
        } else if (this.missile.getGuidance() instanceof MultiModeSeeker multi) {
            for (GuidanceComponent guidance : multi.getGuidances()) {
                if (guidance instanceof TargetAccessor targettingGuid) {
                    targettingGuid.setTarget(lock.target);
                }
            }
        }

        return true;
    }*/

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