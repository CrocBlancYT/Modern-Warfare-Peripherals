package net.croc.mw_peripherals.integration.cc;

import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IPeripheral;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.croc.mw_peripherals.RegistryTags;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MAWSPeripheral implements IPeripheral {
    private static final int maxRange = 100;

    private final Level level;
    private final BlockPos pos;

    public MAWSPeripheral(Level level, BlockPos blockPos) {
        //this.flap = be;
        this.level = level;
        this.pos = blockPos;
    }

    @Nonnull
    public String getType() {
        return "maws";
    }

    public boolean equals(@Nullable IPeripheral iPeripheral) {
        return false;
    }

    private static boolean isDeployed(Entity missile) {
        try { return (Boolean) missile.getClass().getMethod("isDeployed").invoke(missile);
        } catch (Exception e) { return false; }
    }

    public List<Entity> detectActiveMissiles(Level level, BlockPos pos, double range) {
        double clampedRange = range;
        if (clampedRange < 0) { clampedRange = 0; }
        if (clampedRange > maxRange) { clampedRange = maxRange; }

        AABB box = new AABB(pos).inflate(clampedRange);
        List<Entity> entities = level.getEntities(null, box);
        List<Entity> missiles = new ArrayList<>();

        entities.forEach((Entity entity) -> {
            String entityType = EntityType.getKey(entity.getType()).toString();


            if (entityType.equals("tallyho:missile") && isDeployed(entity)) {
                // TODO: check missile in config
                missiles.add(entity);
            } else if (RegistryTags.isMissile(entity)) {
                missiles.add(entity);
            }
        });

        return missiles;
    };

    public String getMissileId(Entity missile) {
        try { return (String) missile.getClass().getMethod("getMissileId").invoke(missile);
        } catch (Exception e) { return null; }
    }

    @LuaFunction
    public final ArrayList<Map<String, Object>> detect(double range) {
        ArrayList<Map<String, Object>> output = new ArrayList<>();

        List<Entity> missiles = detectActiveMissiles(this.level, this.pos, range);
        missiles.forEach((Entity missile) -> {
            Map<String, Object> output_missile = new HashMap<>();

            String uuid = missile.getUUID().toString();
            String id = getMissileId(missile);

            output_missile.put("uuid", uuid);
            output_missile.put("id", id);

            output.add(output_missile);
        });

        return output;
    }
}
