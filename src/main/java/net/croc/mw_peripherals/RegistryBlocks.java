package net.croc.mw_peripherals;

import net.croc.mw_peripherals.blocks.*;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class RegistryBlocks {
    @SuppressWarnings("deprecation")
    private static final DeferredRegister<Block> BLOCK = Main.BLOCKS;
    private static final DeferredRegister<Item> ITEM = Main.ITEMS;

    public static final Map<String, RegistryObject<Block>> BLOCKS = new HashMap<>();
    public static final Map<String, RegistryObject<Item>> ITEMS = new HashMap<>();

    public static final Map<String, RegistryObject<Block>> APS_VARIANT_BLOCKS = new HashMap<>();
    public static final Map<String, RegistryObject<Item>> APS_VARIANT_ITEMS = new HashMap<>();

    public static final String[] APS_VARIANT_NAMES = {"desert", "parade", "snow", "4bo", "charcoal", "grizzly", "gravel"};

    private static void registerBlock(String id, Supplier<Block> blockSupplier,
                                      Map<String, RegistryObject<Block>> blocksMap,
                                      Map<String, RegistryObject<Item>> itemsMap) {

        RegistryObject<Block> block = BLOCK.register(id, blockSupplier);
        RegistryObject<Item> item = ITEM.register(id, () -> new BlockItem(block.get(), new Item.Properties()));

        blocksMap.put(id, block);
        itemsMap.put(id, item);
    }

    public static void register() {
        registerBlock("maws",
                () -> new Block(BlockBehaviour.Properties.of()
                        .sound(SoundType.STONE)
                        .strength(10.0f)),
                BLOCKS, ITEMS);

        registerBlock("weapons_manager",
                () -> new Block(BlockBehaviour.Properties.of()
                        .sound(SoundType.STONE)
                        .strength(10.0f)),
                BLOCKS, ITEMS);

        registerBlock("raycaster",
                () -> new RaycasterBlock(BlockBehaviour.Properties.of()
                        .sound(SoundType.STONE)
                        .strength(10.0f)),
                BLOCKS, ITEMS);

        registerBlock("gyro_module",
                () -> new GyroBlock(BlockBehaviour.Properties.of()
                        .sound(SoundType.STONE)
                        .strength(10.0f)),
                BLOCKS, ITEMS);

        for (String variant : APS_VARIANT_NAMES) {
            registerBlock("aps_" + variant,
                    () -> new APSBlock(BlockBehaviour.Properties.of()
                            .sound(SoundType.STONE)
                            .strength(15.0f)),
                    APS_VARIANT_BLOCKS, APS_VARIANT_ITEMS);
        }
    }
}