package net.croc.mw_peripherals.client.hmd;

import com.mojang.blaze3d.platform.Window;
import com.simibubi.create.content.contraptions.actors.seat.SeatEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

import javax.annotation.Nullable;

public class HMDRenderContext {
    public final Minecraft mc;
    public final Window window;
    public Direction cached_forward;

    public HMDRenderContext(Minecraft mc, Window window) {
        this.mc = mc;
        this.window = window;
    }

    public int screenHeight() {
        return window.getGuiScaledHeight();
    }

    public int screenWidth() {
        return window.getGuiScaledWidth();
    }

    public Direction forwardCardinal() {
        Player player = player();
        if (cached_forward != null) return cached_forward;

        if (!player.isPassenger()) return null;
        Entity vehicle = player.getVehicle();

        if (vehicle instanceof SeatEntity seat) {
            Vec3 dir = seat.getLookAngle();
            cached_forward = Direction.getNearest(dir.x, dir.y, dir.z);
        } else {
            Vec3 dir = player.getLookAngle();
            cached_forward = Direction.getNearest(dir.x, dir.y, dir.z);
        }

        return cached_forward;
    }

    public Vec3 forwardVector() {
        Vector3f dir = forwardCardinal().step();
        return new Vec3(dir.x, dir.y, dir.z);
    }

    public @Nullable Player player() {
        return this.mc.player;
    }

    public @Nullable Level level() {
        Player player = player();
        if (player == null) return null;
        return player.level();
    }

    public @Nullable Ship ship() {
        Player player = player();
        if (player == null) return null;

        if (!player.isPassenger()) return null;
        Entity vehicle = player.getVehicle();

        if (vehicle instanceof SeatEntity seat) {
            return VSGameUtilsKt.getShipManagingPos(seat.level(), seat.blockPosition());
        }

        return null;
    }
}