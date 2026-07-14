package net.croc.mw_peripherals.stuff;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.valkyrienskies.core.api.ships.PhysShip;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.api.ships.ShipForcesInducer;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

import javax.annotation.Nullable;

public class ShipHandle implements ShipForcesInducer {

    private boolean setStatic = false;
    private boolean updated = false;

    public void setStatic(boolean value) {
        setStatic = value;
        updated = false;
    }

    @Override
    public void applyForces(PhysShip physShip) {
        if (!updated) {
            physShip.setStatic(setStatic);
            updated = true;
        }
    }

    public static ShipHandle getOrCreate(ServerShip ship) {
        ShipHandle control = ship.getAttachment(ShipHandle.class);

        if (control == null) {
            control = new ShipHandle();
            ship.saveAttachment(ShipHandle.class, control);
        }

        return control;
    }

    @Nullable
    public static ShipHandle getOrCreate(Level level, BlockPos pos) {
        ServerShip ship = (ServerShip)VSGameUtilsKt.getShipManagingPos(level, pos);
        if (ship == null)
            return null;
        return getOrCreate(ship);
    }
}
