package net.croc.mw_peripherals;

import net.croc.mw_peripherals.blocks.*;
import net.croc.mw_peripherals.content.aps.APSBlockEntry;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import edn.stratodonut.tallyho.AllBlocks;

public class RegistryBlockEntities {
    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = Main.BLOCK_ENTITIES;

    public static RegistryObject<BlockEntityType<GyroBlockEntity>> GYRO_BLOCK_ENTITY;
    public static RegistryObject<BlockEntityType<JetEngineBlockEntity>> JET_ENGINE_BLOCK_ENTITY;
    public static RegistryObject<BlockEntityType<FlareDispenserBlockEntity>> FLARE_DISPENSER_BLOCK_ENTITY;

    public static RegistryObject<BlockEntityType<APSFixedBlockEntity>> FIXED_APS_BLOCK_ENTITY;
    public static RegistryObject<BlockEntityType<APSOneAxisBlockEntity>> Y_APS_BLOCK_ENTITY;
    public static RegistryObject<BlockEntityType<APSTwoAxisBlockEntity>> ZY_APS_BLOCK_ENTITY;

    public static RegistryObject<BlockEntityType<RadarBlockEntity>> RADAR_BLOCK_ENTITY;
    public static RegistryObject<BlockEntityType<RadarPanelBlockEntity>> RADAR_PANEL_BLOCK_ENTITY;
    public static RegistryObject<BlockEntityType<RadarFixedBlockEntity>> RADAR_FIXED_BLOCK_ENTITY;

    //public static BlockEntityEntry<CBCBearingBlockEntity> SYNCED_MECHANICAL_BEARING;

    public static void register() {
        FIXED_APS_BLOCK_ENTITY = BLOCK_ENTITIES.register("fixed_aps_block_entity",
                () -> BlockEntityType.Builder.of(APSFixedBlockEntity::new,
                                APSBlockEntry.getNoRotAPSBlocks().toArray(new Block[0]))
                        .build(null));

        Y_APS_BLOCK_ENTITY = BLOCK_ENTITIES.register("y_aps_block_entity",
                () -> BlockEntityType.Builder.of(APSOneAxisBlockEntity::new,
                                APSBlockEntry.getYRotAPSBlocks().toArray(new Block[0]))
                        .build(null));

        ZY_APS_BLOCK_ENTITY = BLOCK_ENTITIES.register("zy_aps_block_entity",
                () -> BlockEntityType.Builder.of(APSTwoAxisBlockEntity::new,
                                APSBlockEntry.getZYRotAPSBlocks().toArray(new Block[0]))
                        .build(null));

        GYRO_BLOCK_ENTITY = BLOCK_ENTITIES.register("gyro_block_entity",
                () -> BlockEntityType.Builder.of(GyroBlockEntity::new,
                                RegistryBlocks.GYRO_MODULE.getBlock().get())
                        .build(null));

        JET_ENGINE_BLOCK_ENTITY = BLOCK_ENTITIES.register("jet_engine_block_entity",
                () -> BlockEntityType.Builder.of(JetEngineBlockEntity::new,
                                RegistryBlocks.JET_ENGINE.getBlock().get())
                        .build(null));

        RADAR_BLOCK_ENTITY = BLOCK_ENTITIES.register("radar_block_entity",
                () -> BlockEntityType.Builder.of(RadarBlockEntity::new,
                                RegistryBlocks.RADAR_MODULE.getBlock().get())
                        .build(null));

        RADAR_PANEL_BLOCK_ENTITY = BLOCK_ENTITIES.register("radar_panel_block_entity",
                () -> BlockEntityType.Builder.of(RadarPanelBlockEntity::new,
                                RegistryBlocks.RADAR_PANEL.getBlock().get())
                        .build(null));

        RADAR_FIXED_BLOCK_ENTITY = BLOCK_ENTITIES.register("radar_fixed_block_entity",
                () -> BlockEntityType.Builder.of(RadarFixedBlockEntity::new,
                                RegistryBlocks.FIXED_RADAR.getBlock().get())
                        .build(null));

        FLARE_DISPENSER_BLOCK_ENTITY = BLOCK_ENTITIES.register("flare_dispenser_block_entity",
                () -> BlockEntityType.Builder.of(FlareDispenserBlockEntity::new,
                                AllBlocks.FLARE_DISPENSER.get())
                        .build(null));

        /*SYNCED_MECHANICAL_BEARING = Main.REGISTRATE
                .blockEntity("synced_mechanical_bearing", CBCBearingBlockEntity::new)
                .validBlocks(RegistryBlocks.SYNCED_MECHANICAL_BEARING)
                .register();*/
    }

}