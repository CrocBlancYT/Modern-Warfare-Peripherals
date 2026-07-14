package net.croc.mw_peripherals.mixin;

import com.simibubi.create.AllItems;
import net.croc.mw_peripherals.utils.MixinHatch;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.mcreator.rha.block.*;

@Mixin({
        HatchalgaeBlock.class, HatcholiveBlock.class, Hatch4boBlock.class, HatchardenneBlock.class, HatchazureBlock.class,
        HatchhorizonBlock.class, HatchpattonBlock.class, HatchcactusBlock.class, HatchcamelBlock.class, HatchcharcoalBlock.class,
        HatchdesertBlock.class, HatchdustBlock.class, HatchgelbBlock.class, HatchginkBlock.class, HatchgorgeBlock.class,
        HatchgravelBlock.class, HatchgrizzlyBlock.class, HatchhideBlock.class, HatchjetBlock.class, HatchkampfgrauBlock.class,
        HatchkatBlock.class, HatchpanzergrauBlock.class, HatchpineBlock.class, HatchslateBlock.class, HatchsnowBlock.class,
        HatchscaleBlock.class, HatchleyBlock.class, HatchrotaBlock.class, HatchtypeBlock.class, HatchcoralBlock.class,
        HatchcherenkovBlock.class, HatchparadeBlock.class
})
public class MixinHatchBlock extends Block {
    protected MixinHatchBlock(Properties properties) {
        super(properties);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }

    @Inject(method = "getShape", at = @At("RETURN"), cancellable = true)
    private void onShape(BlockState state, BlockGetter level, BlockPos pos,
                            CollisionContext context, CallbackInfoReturnable<VoxelShape> cir) {
        if (level.getBlockEntity(pos) instanceof MixinHatch hatch) {
            int height = hatch.getHeight();
            VoxelShape original = cir.getReturnValue();
            VoxelShape offseted = original.move(0, ((float) height) * 0.0625F, 0);
            cir.setReturnValue(offseted);
        }
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (hand == InteractionHand.MAIN_HAND) {
            if (level.getBlockEntity(pos) instanceof MixinHatch hatch) {
                ItemStack item = player.getItemInHand(hand);

                if (item.getItem() == AllItems.WRENCH.get()) {
                    hatch.onWrenched(hit);
                    level.playSound(player, pos, SoundEvents.ITEM_FRAME_PLACE, SoundSource.BLOCKS, 1F, 1F);

                    return InteractionResult.SUCCESS;
                }
            }
        }

        return InteractionResult.PASS;
    }
}

