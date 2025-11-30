package net.croc.mw_peripherals.integration.cc;

import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IPeripheral;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.croc.mw_peripherals.blocks.APSBlockEntity;
import net.croc.mw_peripherals.stuff.APS_HardKill;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class APSPeripheral implements IPeripheral {
    private final Level level;
    private final BlockPos pos;
    private final APSBlockEntity blockEntity;

    public APSPeripheral(Level level, BlockPos blockPos) {
        this.blockEntity = (APSBlockEntity) level.getBlockEntity(blockPos);
        this.level = level;
        this.pos = blockPos;
    }

    @Nonnull
    public String getType() {
        return "aps";
    }

    public boolean equals(@Nullable IPeripheral iPeripheral) {
        return (this.level != null && this.level.getBlockEntity(this.pos) instanceof APSBlockEntity);
    }

    @LuaFunction
    public final double getCharges() {
        return this.blockEntity.getCharges();
    }

    @LuaFunction
    public final double getCooldown() {
        return this.blockEntity.getCooldown();
    }

    private static String getMissileId(Entity missile) {
        try { return (String) missile.getClass().getMethod("getMissileId").invoke(missile);
        } catch (Exception e) { return "unknown"; }
    }

    @LuaFunction
    public final List<Map<String, Object>> detect() {
        List<Entity> projectiles = APS_HardKill.detectProjectiles(this.blockEntity, this.blockEntity.range);

        List<Map<String, Object>> output = new ArrayList();
        projectiles.forEach((Entity proj) -> {
            String entityType = proj.getType().toString();
            String uuid = proj.getUUID().toString();

            if (entityType.equals("tallyho:missile")) {
                entityType = getMissileId(proj);
            }

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
        List<Entity> projectiles = APS_HardKill.detectProjectiles(this.blockEntity, APSBlockEntity.range);

        for (Entity projectile : projectiles) {
            if (projectile.getUUID().toString().equals(uuid)) {
                target = projectile;
                break;
            }
        }

        if (target == null) return "No entity found";

        boolean chargeUsed = this.blockEntity.useCharge();
        if (!chargeUsed) return "Missing charge";

        APS_HardKill.tryKillProjectile(this.blockEntity, target);
        return "Hit";
    }
}
