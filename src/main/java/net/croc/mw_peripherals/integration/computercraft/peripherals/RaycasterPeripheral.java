package net.croc.mw_peripherals.integration.computercraft.peripherals;

import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IPeripheral;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import it.unimi.dsi.fastutil.Hash;
import net.croc.mw_peripherals.blocks.FacingBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;
import org.joml.Vector3d;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

import java.util.HashMap;
import java.util.Map;

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

    public BlockHitResult raycast(Level level, Vector3d start, Vector3d end) {
        ClipContext ctx = new ClipContext(
                new Vec3(start.x, start.y, start.z),
                new Vec3(end.x, end.y, end.z),
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE,
                null
        );

        return level.clip(ctx);
    }

    private Vector3d getOffset(Direction facing) {
        return switch (facing) {
            case SOUTH -> new Vector3d(0,0,1);
            case EAST  -> new Vector3d(1,0,0);
            case NORTH -> new Vector3d(0,0,-1);
            case WEST  -> new Vector3d(-1,0,0);
            case DOWN  -> new Vector3d(0,-1,0);
            default -> new Vector3d(0,1,0);
        };
    };

    @LuaFunction
    public final Map<String, Object> raycast(double dx, double dy, double dz) {
        BlockPos pos = this.pos;
        Level level = this.level;

        BlockState block = level.getBlockState(pos);
        Direction facing = block.getValue(FacingBlock.FACING);

        Vector3d offset = getOffset(facing);

        Vector3d startPos = new Vector3d(pos.getX(), pos.getY(), pos.getZ())
                .add(new Vector3d(0.5,0.5,0.5))
                .add(offset);

        Vector3d endPos = new Vector3d()
                .add(startPos)
                .add(new Vector3d(dx,dy,dz));

        Ship ship = VSGameUtilsKt.getShipObjectManagingPos(level, pos);

        if (ship != null) {
            ship.getShipToWorld().transformPosition(startPos);
            ship.getShipToWorld().transformPosition(endPos);
        }

        BlockHitResult hitResult = raycast(level, startPos, endPos);

        Map<String, Object> result = new HashMap<>();

        result.put("case", "MISS");

        if (hitResult.getType().toString().equals("BLOCK")) {
            BlockPos BlockHitPos = hitResult.getBlockPos();
            Vec3 WorldHitPos = hitResult.getLocation();
            String face = hitResult.getDirection().toString();

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
        }

        return result;
    }
}