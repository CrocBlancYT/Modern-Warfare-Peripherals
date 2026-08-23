package net.croc.mw_peripherals;

import com.simibubi.create.AllShapes;
import com.simibubi.create.foundation.utility.VoxelShaper;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.VoxelShape;

public class Shapes {
    public static final VoxelShaper RADAR = shape(0, 0, 0, 16, 6, 16).forDirectional(Direction.DOWN);

    public static final VoxelShaper ROCKET_POD_4 = shape(2.5, 2.5, 0, 16-2.5, 16-2.5, 16).forHorizontal(Direction.NORTH);

    public static final VoxelShaper ROCKET_POD_7 = shape(2.5, 2.5, 0, 16-2.5, 16-2.5, 16).forHorizontal(Direction.NORTH);

    public static final VoxelShaper ROCKET_POD_19 = shape(2.5, 2.5, 0, 16-2.5, 16-2.5, 16).forHorizontal(Direction.NORTH);

    private static AllShapes.Builder shape(VoxelShape shape) {
        return new AllShapes.Builder(shape);
    }

    private static AllShapes.Builder shape(double x1, double y1, double z1, double x2, double y2, double z2) {
        return shape(cuboid(x1, y1, z1, x2, y2, z2));
    }

    private static VoxelShape cuboid(double x1, double y1, double z1, double x2, double y2, double z2) {
        return Block.box(x1, y1, z1, x2, y2, z2);
    }
}