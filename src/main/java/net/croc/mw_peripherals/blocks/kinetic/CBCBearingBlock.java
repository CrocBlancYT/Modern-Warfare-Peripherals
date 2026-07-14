package net.croc.mw_peripherals.blocks.kinetic;

import com.simibubi.create.content.contraptions.bearing.BearingBlock;
import com.simibubi.create.foundation.block.IBE;
import net.croc.mw_peripherals.RegistryBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class CBCBearingBlock extends BearingBlock implements IBE<CBCBearingBlockEntity> {
    public CBCBearingBlock(@Nullable BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected void createBlockStateDefinition(@NotNull StateDefinition.Builder builder) {
        super.createBlockStateDefinition(builder);
    }

    @NotNull
    @Override
    public Class<CBCBearingBlockEntity> getBlockEntityClass() {
        return CBCBearingBlockEntity.class;
    }

    @NotNull
    @Override
    public InteractionResult use(@NotNull BlockState state, @NotNull Level worldIn, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand handIn, @NotNull BlockHitResult hit) {
        if (!player.mayBuild())
            return InteractionResult.FAIL;
        if (player.isShiftKeyDown())
            return InteractionResult.FAIL;
        if (player.getItemInHand(handIn).isEmpty()) {
            if (!worldIn.isClientSide)
                withBlockEntityDo(
                        worldIn,
                        pos, (CBCBearingBlockEntity te) -> {
                            if (te.isRunning) {
                                te.disassemble();
                                return;
                            }
                            te.assembleNextTick = true;
                        });
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @NotNull
    @Override
    public BlockEntityType<? extends CBCBearingBlockEntity> getBlockEntityType() {
        return null;
        //return RegistryBlockEntities.SYNCED_MECHANICAL_BEARING.get();
    }

    @NotNull
    @Override
    public InteractionResult onWrenched(@NotNull BlockState state, @NotNull UseOnContext context) {
        InteractionResult resultType = super.onWrenched(state, context);
        if (!(context.getLevel()).isClientSide && resultType.consumesAction())
            withBlockEntityDo(
                    context.getLevel(),
                    context.getClickedPos(),
                    CBCBearingBlockEntity::disassemble
            );
        return resultType;
    }
}
