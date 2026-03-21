package net.croc.mw_peripherals;

import net.croc.mw_peripherals.blocks.*;
import net.croc.mw_peripherals.utils.BlockEntry;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import edn.stratodonut.tallyho.AllBlocks;

public class RegistryBlockEntities {
    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = Main.BLOCK_ENTITIES;

    public static RegistryObject<BlockEntityType<GyroBlockEntity>> GYRO_BLOCK_ENTITY;
    public static RegistryObject<BlockEntityType<APSBlockEntity>> APS_BLOCK_ENTITY;
    public static RegistryObject<BlockEntityType<JetEngineBlockEntity>> JET_ENGINE_BLOCK_ENTITY;
    public static RegistryObject<BlockEntityType<RadarBlockEntity>> RADAR_BLOCK_ENTITY;
    public static RegistryObject<BlockEntityType<FlareDispenserBlockEntity>> FLARE_DISPENSER_BLOCK_ENTITY;


    public static void register() {
        GYRO_BLOCK_ENTITY = BLOCK_ENTITIES.register("gyro_block_entity",
                () -> BlockEntityType.Builder.of(GyroBlockEntity::new,
                                RegistryBlocks.GYRO_MODULE.getBlock().get())
                        .build(null));

        APS_BLOCK_ENTITY = BLOCK_ENTITIES.register("aps_block_entity",
                () -> BlockEntityType.Builder.of(APSBlockEntity::new,
                                RegistryBlocks.APS_VARIANTS.values().stream().map(BlockEntry::getBlock).map(RegistryObject::get).toArray(Block[]::new))
                        .build(null));

        JET_ENGINE_BLOCK_ENTITY = BLOCK_ENTITIES.register("jet_engine_block_entity",
                () -> BlockEntityType.Builder.of(JetEngineBlockEntity::new,
                                RegistryBlocks.JET_ENGINE.getBlock().get())
                        .build(null));

        RADAR_BLOCK_ENTITY = BLOCK_ENTITIES.register("radar_block_entity",
                () -> BlockEntityType.Builder.of(RadarBlockEntity::new,
                                RegistryBlocks.RADAR_MODULE.getBlock().get())
                        .build(null));

        FLARE_DISPENSER_BLOCK_ENTITY = BLOCK_ENTITIES.register("flare_dispenser_block_entity",
                () -> BlockEntityType.Builder.of(FlareDispenserBlockEntity::new,
                                AllBlocks.FLARE_DISPENSER.get())
                        .build(null));
    }

}