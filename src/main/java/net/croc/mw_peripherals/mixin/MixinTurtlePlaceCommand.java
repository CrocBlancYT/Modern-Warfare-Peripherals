package net.croc.mw_peripherals.mixin;

import dan200.computercraft.api.turtle.*;
import dan200.computercraft.shared.turtle.core.*;
import net.croc.mw_peripherals.Main;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Arrays;
import java.util.Optional;

@Mixin(value = TurtlePlaceCommand.class)
public abstract class MixinTurtlePlaceCommand {
    /*private static Direction getDirection(String side) {
        return switch (side) {
            case "north" -> Direction.NORTH;
            case "south" -> Direction.SOUTH;
            case "east" -> Direction.EAST;
            case "west" -> Direction.WEST;
            case "up" -> Direction.UP;
            case "down" -> Direction.DOWN;
            default -> null;
        };
    }

    private static boolean isHorizontal(Direction dir) {
        return !dir.equals(Direction.UP) && !dir.equals(Direction.DOWN);
    }

    @Shadow
    private Object[] extraArguments;

    @Inject(method = "execute", at = @At("TAIL"), remap = false)
    public void mixinPlaceOriented(ITurtleAccess turtle, CallbackInfoReturnable<TurtleCommandResult> cir) {
        Main.LOGGER.info("called");
        if (this.extraArguments == null) return;

        Optional<Object> arg = Arrays.stream(this.extraArguments).findFirst();
        Main.LOGGER.info(arg.toString());

        if (arg.isPresent() && arg.get() instanceof String name) {
            Main.LOGGER.info(name);

            Direction dir = getDirection(name);

            Level level = turtle.getLevel();
            BlockPos pos = turtle.getPosition().relative(turtle.getDirection());

            if (dir != null) Main.LOGGER.info(dir.toString());

            if (dir != null && !(level.getBlockEntity(pos) instanceof SignBlockEntity)) {


                BlockState state = level.getBlockState(pos);

                if (state.hasProperty(BlockStateProperties.FACING)) {
                    state.setValue(BlockStateProperties.FACING, dir);
                } else if (state.hasProperty(BlockStateProperties.HORIZONTAL_FACING) && isHorizontal(dir)) {
                    state.setValue(BlockStateProperties.HORIZONTAL_FACING, dir);
                } else if (state.hasProperty(BlockStateProperties.AXIS)) {
                    state.setValue(BlockStateProperties.AXIS, dir.getAxis());
                } else if (state.hasProperty(BlockStateProperties.HORIZONTAL_AXIS) && isHorizontal(dir)) {
                    state.setValue(BlockStateProperties.HORIZONTAL_AXIS, dir.getAxis());
                } else {
                    Main.LOGGER.info("no facing");
                    return;
                }

                Main.LOGGER.info("update");
                level.setBlock(pos, state, 0x11);
            }
        }
    }*/
}