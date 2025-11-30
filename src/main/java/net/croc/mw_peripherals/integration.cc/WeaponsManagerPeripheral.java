package net.croc.mw_peripherals.integration.cc;

import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IPeripheral;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WeaponsManagerPeripheral implements IPeripheral {
    public static final int range = 7;
    private final Level level;
    private final BlockPos pos;

    public WeaponsManagerPeripheral(Level level, BlockPos blockPos) {
        //this.flap = be;
        this.level = level;
        this.pos = blockPos;
    }

    public List<Entity> getMissilesNearby(Level level, BlockPos pos) {
        AABB box = new AABB(pos).inflate(range);
        List<Entity> entities = level.getEntities(null, box);
        List<Entity> missiles = new ArrayList<>();

        entities.forEach((Entity entity) -> {
            String entityType = EntityType.getKey(entity.getType()).toString();
            if (entityType.equals("tallyho:missile")) {
                missiles.add(entity);
            }
        });

        return missiles;
    };

    public Entity getNearbyFromUUID(Level level, BlockPos pos, String uuid) {
        List<Entity> missiles = getMissilesNearby(level, pos);

        for (Entity missile: missiles) {
            if (missile.getUUID().toString().equals(uuid)) {
                return missile;
            }
        }

        return null;
    };

    public String getMissileId(Entity missile) {
        try { return (String) missile.getClass().getMethod("getMissileId").invoke(missile);
        } catch (Exception e) { return null; }
    }

    public Object getGuidance(Entity missile) {
        try { return (Object) missile.getClass().getMethod("getGuidance").invoke(missile);
        } catch (Exception e) { return null; }
    }

    public void setCode(Object guidance, String slug) {
        try { guidance.getClass().getMethod("setCode").invoke(guidance, slug);
        } catch (Exception e) { }
    }

    public void launch(Entity missile) {
        try { missile.getClass().getMethod("launch").invoke(missile);
        } catch (Exception e) { }
    }

    @Nonnull
    public String getType() {
        return "weapons_manager";
    }

    public boolean equals(@Nullable IPeripheral iPeripheral) {
        return false;
    }

    @LuaFunction
    public final Map<String, Object> scan() {
        List<Entity> missiles = getMissilesNearby(this.level, this.pos);

        Map<String, Object> result = new HashMap<>();

        missiles.forEach((Entity missile) -> {
            String id = getMissileId(missile);

            ArrayList<String> missileList = (ArrayList<String>) result.get(id);

            if (missileList == null) {
                missileList = new ArrayList<>();
                result.put(id, missileList);
            }

            missileList.add(missile.getUUID().toString());
        });

        return result;
    }

    @LuaFunction
    public final String setRotation(String uuid, double yaw, double pitch) {
        Entity missile = getNearbyFromUUID(this.level, this.pos, uuid);

        if (missile == null) return "Not found";

        missile.setXRot((float) pitch);
        missile.setYRot((float) yaw);

        return null;
    }

    @LuaFunction
    public final String fire(String uuid) {
        Ship ship = VSGameUtilsKt.getShipObjectManagingPos(this.level, this.pos);
        Entity missile = getNearbyFromUUID(this.level, this.pos, uuid);

        if (missile == null) return "Not found";

        Object guidance = getGuidance(missile);
        if (guidance != null && ship != null) setCode(guidance, ship.getSlug());

        launch(missile);
        return null;
    }
}
