package net.croc.mw_peripherals;

import com.simibubi.create.Create;
import com.tterrag.registrate.util.entry.BlockEntityEntry;
import net.croc.mw_peripherals.blocks.*;
import net.croc.mw_peripherals.blocks.aps.APS;
import net.croc.mw_peripherals.blocks.aps.APSEntry;
import net.croc.mw_peripherals.blocks.aps.YRotatedAPS;
import net.croc.mw_peripherals.blocks.aps.ZYRotatedAPS;
import net.croc.mw_peripherals.blocks.kinetic.CBCBearingBlockEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import edn.stratodonut.tallyho.AllBlocks;

public class RegistryBlockEntities {
    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = Main.BLOCK_ENTITIES;

    public static RegistryObject<BlockEntityType<GyroBlockEntity>> GYRO_BLOCK_ENTITY;
    public static RegistryObject<BlockEntityType<JetEngineBlockEntity>> JET_ENGINE_BLOCK_ENTITY;
    public static RegistryObject<BlockEntityType<RadarBlockEntity>> RADAR_BLOCK_ENTITY;
    public static RegistryObject<BlockEntityType<FlareDispenserBlockEntity>> FLARE_DISPENSER_BLOCK_ENTITY;

    public static RegistryObject<BlockEntityType<APS>> FIXED_APS_BLOCK_ENTITY;
    public static RegistryObject<BlockEntityType<YRotatedAPS>> Y_APS_BLOCK_ENTITY;
    public static RegistryObject<BlockEntityType<ZYRotatedAPS>> ZY_APS_BLOCK_ENTITY;

    //public static BlockEntityEntry<CBCBearingBlockEntity> SYNCED_MECHANICAL_BEARING;

    public static void register() {
        FIXED_APS_BLOCK_ENTITY = BLOCK_ENTITIES.register("fixed_aps_block_entity",
                () -> BlockEntityType.Builder.of(APS::new,
                                APSEntry.getNoRotAPSBlocks().toArray(new Block[0]))
                        .build(null));

        Y_APS_BLOCK_ENTITY = BLOCK_ENTITIES.register("y_aps_block_entity",
                () -> BlockEntityType.Builder.of(YRotatedAPS::new,
                                APSEntry.getYRotAPSBlocks().toArray(new Block[0]))
                        .build(null));

        ZY_APS_BLOCK_ENTITY = BLOCK_ENTITIES.register("zy_aps_block_entity",
                () -> BlockEntityType.Builder.of(ZYRotatedAPS::new,
                        APSEntry.getZYRotAPSBlocks().toArray(new Block[0]))
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