package net.croc.mw_peripherals.integration.computercraft.peripherals;

import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IPeripheral;
import java.util.ArrayList;
import java.util.HashMap;
import javax.annotation.Nullable;

import edn.stratodonut.tallyho.missile.tracker.RadEmissionTracker;
import net.croc.mw_peripherals.blocks.RadarBlockEntity;
import net.croc.mw_peripherals.integration.computercraft.LuaRotation;
import net.croc.mw_peripherals.integration.computercraft.LuaUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.joml.Quaterniond;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.primitives.AABBdc;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

public class RadarPeripheral implements IPeripheral {
    private final Level level;

    private final BlockPos pos;

    private final RadarBlockEntity blockEntity;

    public String getType() {
        return "radar";
    }

    public boolean equals(@Nullable IPeripheral iPeripheral) {
        return false;
    }

    public RadarPeripheral(Level level, BlockPos blockPos) {
        this.level = level;
        this.pos = blockPos;
        this.blockEntity = (RadarBlockEntity)level.getBlockEntity(blockPos);
    }

    private HashMap<String, ?> formatShip(Ship ship, Quaterniond rot) {
        HashMap<String, Object> output_ship = new HashMap<>();
        output_ship.put("rotation", new LuaRotation(rot));
        AABBdc ship_area = ship.getWorldAABB();
        double size_x = ship_area.maxX() - ship_area.minX();
        double size_y = ship_area.maxY() - ship_area.minY();
        double size_z = ship_area.maxZ() - ship_area.minZ();
        double size = Math.sqrt(size_x * size_x + size_y * size_y + size_z * size_z);
        output_ship.put("size", (size));
        double speed = ship.getVelocity().length();
        output_ship.put("speed", (speed));
        return output_ship;
    }

    public static final double DISH_FOV = 45D;
    public static final double MIN_DISTANCE = 10D;

    public void emitRadOnScan(double distance) {
        RadEmissionTracker.emit(this.level, this.pos, (distance > 0.0D) ? (int)distance : 200000);
    }

    @LuaFunction
    public ArrayList<HashMap<String, ?>> scan(double distance) {
        emitRadOnScan(distance);

        Vector3d dir = this.blockEntity.getRadarDirection();
        Vector3f radar_pos = this.pos.getCenter().toVector3f();

        ArrayList<HashMap<String, ?>> output = new ArrayList<>();

        Ship current_ship = VSGameUtilsKt.getShipObjectManagingPos(this.level, this.pos);
        if (current_ship != null) {
            radar_pos = current_ship.getShipToWorld().transformPosition( radar_pos.get(new Vector3d()) ).get(new Vector3f());
        }
        AABB area = (new AABB(new BlockPos((int) radar_pos.x,(int) radar_pos.y,(int) radar_pos.z))).inflate(distance);

        for (Ship ship : VSGameUtilsKt.getShipsIntersecting(this.level, area)) {
            Vector3d ship_pos = ship.getWorldAABB().center(new Vector3d());
            Vector3d radar_to_ship = ship_pos.sub(radar_pos);

            double angle = radar_to_ship.angle(dir);
            float degrees = (float)Math.toDegrees(angle);

            Quaterniond rot = dir.rotationTo(radar_to_ship, new Quaterniond());
            if (radar_to_ship.length() >= MIN_DISTANCE && degrees <= DISH_FOV) output.add(formatShip(ship, rot));
        }

        return output;
    }

    @LuaFunction
    public void setYawSpeed(double yawing) {
        this.blockEntity.setYawSpeed((float)yawing);
    }

    @LuaFunction
    public double getYaw() {
        return this.blockEntity.getYaw();
    }

    @LuaFunction
    public void setPitchSpeed(double pitching) {
        this.blockEntity.setPitchSpeed((float)pitching);
    }

    @LuaFunction
    public double getPitch() {
        return this.blockEntity.getPitch();
    }

    @LuaFunction
    public double getDishFOV() { return DISH_FOV; }
}
