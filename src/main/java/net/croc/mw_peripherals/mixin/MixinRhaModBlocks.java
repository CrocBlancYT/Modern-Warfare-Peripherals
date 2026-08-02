package net.croc.mw_peripherals.mixin;

import net.mcreator.rha.block.Hardsteel4boBlock;
import net.mcreator.rha.block.Layeredsteel4boBlock;
import net.mcreator.rha.block.Rivetedsteel4boBlock;
import net.mcreator.rha.block.Tiledsteel4boBlock;
import net.mcreator.rha.init.RhaModBlocks;
import net.mcreator.rha.init.RhaModItems;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.RegistryObject;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.croc.mw_peripherals.integration.rha.RHACreativeTabIntegration.*;

@Mixin(value = RhaModBlocks.class)
public class MixinRhaModBlocks {

    private static RegistryObject<Item> itemForBlock(RegistryObject<Block> block) {
        return RhaModItems.REGISTRY.register(block.getId().getPath(), () -> new BlockItem(block.get(), new Item.Properties()));
    }

    @Inject(method = "<clinit>", at = @At("RETURN"))
    private static void register(CallbackInfo ci) {
        for (String steel : steels) {
            for (String color : colors) {

                String id = steel+"steel"+color;
                RegistryObject<Block> block;

                switch (steel) {
                    case "layered":
                        block = RhaModBlocks.REGISTRY.register(id, Layeredsteel4boBlock::new);
                        break;
                    case "tiled":
                        block = RhaModBlocks.REGISTRY.register(id, Tiledsteel4boBlock::new);
                        break;
                    default:
                        block = RhaModBlocks.REGISTRY.register(id, Hardsteel4boBlock::new);
                }

                RegistryObject<Item> item = itemForBlock(block);
                blocks.put(id, block);
                items.put(id, item);
            }
        }
    }
}