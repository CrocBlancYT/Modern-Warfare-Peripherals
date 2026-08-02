package net.croc.mw_peripherals.integration.computercraft.peripherals;

import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IPeripheral;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import edn.stratodonut.tallyho.entity.MountedMissileEntity;
import net.croc.mw_peripherals.blocks.APSTwoAxisBlockEntity;
import net.croc.mw_peripherals.content.aps.APSIntercept;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.joml.Vector3d;

public class APSZYRotPeripheral implements IPeripheral {
    private final Level level;

    private final BlockPos pos;

    private final APSTwoAxisBlockEntity aps;

    public APSZYRotPeripheral(Level level, BlockPos blockPos) {
        this.aps = (APSTwoAxisBlockEntity) level.getBlockEntity(blockPos);
        this.level = level;
        this.pos = blockPos;
    }

    @Nonnull
    public String getType() {
        return "aps";
    }

    public boolean equals(@Nullable IPeripheral iPeripheral) {
        return (this.level != null && this.level.getBlockEntity(this.pos) instanceof APSTwoAxisBlockEntity);
    }

    @LuaFunction
    public final double getCharges() {
        return this.aps.entry.max_charges;
    }

    @LuaFunction
    public final double getCooldown() {
        return this.aps.cooldown;
    }

    @LuaFunction
    public final List<Map<String, Object>> detect() {
        List<Entity> projectiles = APSIntercept.detectProjectiles(this.aps, this.aps.entry.max_range);
        List<Map<String, Object>> output = new ArrayList<>();
        projectiles.forEach(proj -> {
            String entityType = proj.getType().toString();
            String uuid = proj.getUUID().toString();

            if (entityType.equals("tallyho:missile"))
                entityType = ((MountedMissileEntity) proj).getMissileId();

            Map<String, Object> data = new HashMap<>();
            data.put("uuid", uuid);
            data.put("type", entityType);
            data.put("x", proj.getX());
            data.put("y", proj.getY());
            data.put("z", proj.getZ());
            output.add(data);
        });
        return output;
    }

    @LuaFunction
    public final String intercept(String uuid) {
        Entity target = null;
        List<Entity> projectiles = APSIntercept.detectProjectiles(this.aps, 20);
        for (Entity projectile : projectiles) {
            if (projectile.getUUID().toString().equals(uuid)) {
                target = projectile;
                break;
            }
        }
        if (target == null) return "No entity found";

        boolean chargeUsed = this.aps.useCharge();
        if (!chargeUsed) return "Missing charge";

        APSIntercept.tryKillProjectile(this.aps, target);

        this.aps.pointAt(target.position().toVector3f().get(new Vector3d()));

        return "Hit";
    }

    @LuaFunction
    public final void setYaw(double yaw) {
        this.aps.setYRot((float)yaw);
    }

    @LuaFunction
    public final void setPitch(double pitch) {
        this.aps.setZRot((float)pitch);
    }
}
