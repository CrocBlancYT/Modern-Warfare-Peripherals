package net.croc.mw_peripherals.integration.computercraft.peripherals;

import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IPeripheral;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.croc.mw_peripherals.blocks.FacingBlock;
import net.croc.mw_peripherals.utils.VSUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.*;
import net.minecraftforge.registries.ForgeRegistries;
import org.joml.Math;
import org.joml.Vector3d;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static net.croc.mw_peripherals.integration.computercraft.LuaUtils.toLua;

public class RaycasterPeripheral implements IPeripheral {
    private final Level level;
    private final BlockPos pos;

    public RaycasterPeripheral(Level level, BlockPos blockPos) {
        this.level = level;
        this.pos = blockPos;
    }

    @Nonnull
    public String getType() {
        return "vector_raycaster";
    }

    public boolean equals(@Nullable IPeripheral iPeripheral) {
        return false;
    }

    public HitResult raycast(Level level, Vec3 start, Vec3 end) {
        ClipContext ctx = new ClipContext(
                start,
                end,
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE,
                null
        );

        return level.clip(ctx);
    }

    private Vec3 getOffset(Direction facing) {
        return switch (facing) {
            case SOUTH -> new Vec3(0,0,1);
            case EAST  -> new Vec3(1,0,0);
            case NORTH -> new Vec3(0,0,-1);
            case WEST  -> new Vec3(-1,0,0);
            case DOWN  -> new Vec3(0,-1,0);
            default -> new Vec3(0,1,0);
        };
    };

    public static double MAX_RANGE = 400;
    public static double OFFSET = 2.5;

    @LuaFunction
    public final Map<String, Object> raycast(double dx, double dy, double dz) {
        BlockPos pos = this.pos;
        Level level = this.level;

        BlockState block = level.getBlockState(pos);
        Direction facing = block.getValue(FacingBlock.FACING);

        dx = Math.clamp(-MAX_RANGE, MAX_RANGE, dx);
        dy = Math.clamp(-MAX_RANGE, MAX_RANGE, dy);
        dz = Math.clamp(-MAX_RANGE, MAX_RANGE, dz);

        Vec3 offset = getOffset(facing);

        Vec3 startPos = pos.getCenter()
                .add(new Vec3(0.5,0.5,0.5))
                .add(offset.scale(OFFSET));

        Vec3 endPos = startPos
                .add(new Vec3(dx,dy,dz));

        Ship ship = VSGameUtilsKt.getShipObjectManagingPos(level, pos);

        if (ship != null) {
            startPos = VSUtils.toWorldPosition(ship, startPos);
            endPos = VSUtils.toWorldPosition(ship, endPos);

            Vector3d vel = (Vector3d) ship.getVelocity();
            Vec3 velocity_offset = new Vec3(vel.x, vel.y, vel.z);

            startPos = startPos.add(velocity_offset);
            endPos = endPos.add(velocity_offset);
        }

        HitResult hit = raycast(level, startPos, endPos);

        Map<String, Object> result = new HashMap<>();

        if (hit instanceof BlockHitResult blockHit) {
            BlockPos BlockHitPos = blockHit.getBlockPos();
            Vec3 WorldHitPos = blockHit.getLocation();
            String face = blockHit.getDirection().toString();

            Block BlockHit = level.getBlockState(BlockHitPos).getBlock();
            Ship HitShip = VSGameUtilsKt.getShipObjectManagingPos(level, BlockHitPos);

            String BlockId = ForgeRegistries.BLOCKS.getKey(BlockHit).toString();

            if (HitShip != null) {
                result.put("case", "SHIP");
                result.put("shipId", HitShip.getId());
                result.put("shipSlug", HitShip.getSlug());
            } else {
                result.put("case", "BLOCK");
            }

            result.put("worldPos", toLua(WorldHitPos));
            result.put("blockPos", toLua(BlockHitPos));
            result.put("type", BlockId);
            result.put("normalFace", face);
        } else {
            result.put("case", "MISS");
        }

        return result;
    }
}