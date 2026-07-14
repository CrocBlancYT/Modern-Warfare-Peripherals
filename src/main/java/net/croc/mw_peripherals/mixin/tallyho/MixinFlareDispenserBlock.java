package net.croc.mw_peripherals.mixin.tallyho;

import edn.stratodonut.tallyho.block.FlareDispenserBlock;
import net.croc.mw_peripherals.blocks.FlareDispenserBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RedstoneTorchBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

@Mixin(FlareDispenserBlock.class)
public class MixinFlareDispenserBlock extends Block implements EntityBlock {
    protected MixinFlareDispenserBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new FlareDispenserBlockEntity(pos, state);
    }

    private static final BooleanProperty LIT = RedstoneTorchBlock.LIT;

    @Inject(method = "neighborChanged", at = @At("HEAD"), cancellable = true)
    public void flareItemOverride(BlockState blockState, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean isMoving, CallbackInfo ci) {
        if (level.getBlockEntity(pos) instanceof FlareDispenserBlockEntity flareDispenser && !level.isClientSide()) {
            boolean isPowered = level.hasNeighborSignal(pos);

            if (isPowered != blockState.getValue(LIT)) {
                if (isPowered) {
                    if (VSGameUtilsKt.getShipManagingPos(level, pos) instanceof ServerShip) {
                        if (!flareDispenser.removeFlare()) {
                            ci.cancel();
                        }
                    }
                }
                level.setBlock(pos, blockState.setValue(LIT, isPowered), 3);
            }
        }
    }
}