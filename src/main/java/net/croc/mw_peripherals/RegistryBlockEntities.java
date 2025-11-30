package net.croc.mw_peripherals;

import net.croc.mw_peripherals.blocks.GyroBlockEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import net.croc.mw_peripherals.blocks.APSBlockEntity;

import java.util.HashMap;
import java.util.Map;

public class RegistryBlockEntities {
    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY = Main.BLOCK_ENTITIES;
    public static final Map<String, RegistryObject<BlockEntityType<?>>> BLOCK_ENTITIES = new HashMap<>();
    
    public static void register() {
        RegistryObject<BlockEntityType<?>> GYRO_BLOCK_ENTITY = BLOCK_ENTITY.register("gyro_block_entity",() -> BlockEntityType.Builder.of(GyroBlockEntity::new,
                        RegistryBlocks.BLOCKS.get("gyro_module").get())
                .build(null));

        RegistryObject<BlockEntityType<?>> APS_BLOCK_ENTITY = BLOCK_ENTITY.register("aps_block_entity", () -> BlockEntityType.Builder.of(APSBlockEntity::new,
                        RegistryBlocks.APS_VARIANT_BLOCKS.values().stream().map(RegistryObject::get).toArray(Block[]::new))
                .build(null));

        BLOCK_ENTITIES.put("gyro_module", GYRO_BLOCK_ENTITY);
        BLOCK_ENTITIES.put("aps", APS_BLOCK_ENTITY);
    }
}