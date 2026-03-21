package net.croc.mw_peripherals;

import net.croc.mw_peripherals.blocks.*;
import net.croc.mw_peripherals.utils.BlockEntry;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RegistryBlocks {
    public static final ArrayList<BlockEntry<?>> entries = new ArrayList<>();

    public static final BlockEntry<Block> MAWS = new BlockEntry<Block>("maws")
            .blockSupplier( () -> new Block(BlockBehaviour.Properties.of()
                    .sound(SoundType.STONE)
                    .strength(10.0f)))
            .saveTo(entries);

    public static final BlockEntry<Block> WEAPONS_MANAGER = new BlockEntry<>("weapons_manager")
            .blockSupplier( () -> new Block(BlockBehaviour.Properties.of()
                    .sound(SoundType.STONE)
                    .strength(10.0f)))
            .saveTo(entries);

    public static final BlockEntry<FacingBlock> RAYCASTER = new BlockEntry<FacingBlock>("raycaster")
            .blockSupplier(() -> new FacingBlock(BlockBehaviour.Properties.of()
                    .sound(SoundType.STONE)
                    .strength(10.0f)))
            .saveTo(entries);

    public static final BlockEntry<RadarBlock> RADAR_MODULE = new BlockEntry<RadarBlock>("radar_module")
            .blockSupplier( () -> new RadarBlock(BlockBehaviour.Properties.of()
                    .sound(SoundType.STONE)
                    .strength(10.0f)
                    .noOcclusion()))
            .saveTo(entries);

    public static final BlockEntry<GyroBlock> GYRO_MODULE = new BlockEntry<GyroBlock>("gyro_module")
            .blockSupplier( () -> new GyroBlock(BlockBehaviour.Properties.of()
                    .sound(SoundType.STONE)
                    .strength(10.0f)))
            .saveTo(entries);

    public static final BlockEntry<JetEngineBlock> JET_ENGINE = new BlockEntry<JetEngineBlock>("jet_engine")
            .blockSupplier( () -> new JetEngineBlock(BlockBehaviour.Properties.of()
                    .sound(SoundType.STONE)
                    .strength(10.0f)
                    .noOcclusion()))
            .saveTo(entries);

    public static final String[] APS_VARIANT_NAMES = {
            "cherenkov", "horizon", "azure", "algae", "type", "pine", "cactus",
            "4bo", "ley", "rota", "scale", "parade", "ardenne", "olive", "gorge",
            "gink", "camel", "gold", "desert", "patton", "gelb", "grizzly", "hide",
            "kat", "coral", "blush", "kampfgrau", "panzergrau", "charcoal", "jet",
            "slate", "gravel", "dust", "snow"
    };

    public static final Map<String, BlockEntry<APSBlock>> APS_VARIANTS = new HashMap<>();

    static {
        BlockEntry<APSBlock> APS;

        for (String variant : APS_VARIANT_NAMES) {
            String id = "aps_"+variant;
            APS = new BlockEntry<APSBlock>(id)
                    .blockSupplier(() -> new APSBlock(BlockBehaviour.Properties.of()
                            .sound(SoundType.STONE)
                            .strength(15.0f)
                            .noOcclusion()))
                    .saveTo(entries);

            APS_VARIANTS.put(id, APS);
        }
    }

    public static void register() {
        entries.forEach(BlockEntry::register);
    }
}