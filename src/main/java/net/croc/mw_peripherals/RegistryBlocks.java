package net.croc.mw_peripherals;

import com.simibubi.create.AllTags;
import com.simibubi.create.content.kinetics.BlockStressDefaults;
import com.simibubi.create.foundation.data.TagGen;
import edn.stratodonut.tallyho.missile.MissileRegistry;
import net.croc.mw_peripherals.blocks.*;
import net.croc.mw_peripherals.blocks.aps.APSBlock;
import net.croc.mw_peripherals.blocks.aps.APSEntry;
import net.croc.mw_peripherals.blocks.kinetic.CBCBearingBlock;
import net.croc.mw_peripherals.integration.cbcmodernwarfare.munitions.barrel_launched_missile.MissileMediumcannonRoundItem;
import net.croc.mw_peripherals.integration.createbigcannons.munitions.barrel_launched_missile.BarrelLaunchedMissileBlock;
import net.croc.mw_peripherals.integration.createbigcannons.munitions.barrel_launched_missile.BarrelLaunchedMissileItem;
import net.croc.mw_peripherals.integration.tallyho.ForeignMissileRegistry;
import net.croc.mw_peripherals.utils.SimpleBlockEntry;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import com.tterrag.registrate.util.entry.BlockEntry;

import java.util.ArrayList;
import java.util.HashMap;

public class RegistryBlocks {
    public static final ArrayList<SimpleBlockEntry<?>> entries = new ArrayList<>();

    public static final SimpleBlockEntry<Block> LWS = new SimpleBlockEntry<>("lws")
            .blockSupplier( () -> new Block(BlockBehaviour.Properties.of()
                    .sound(SoundType.STONE)
                    .strength(5.0f)))
            .saveTo(entries);

    public static final SimpleBlockEntry<Block> MAWS = new SimpleBlockEntry<>("maws")
            .blockSupplier( () -> new Block(BlockBehaviour.Properties.of()
                    .sound(SoundType.STONE)
                    .strength(5.0f)))
            .saveTo(entries);

    public static final SimpleBlockEntry<Block> WEAPONS_MANAGER = new SimpleBlockEntry<>("weapons_manager")
            .blockSupplier( () -> new Block(BlockBehaviour.Properties.of()
                    .sound(SoundType.STONE)
                    .strength(5.0f)))
            .saveTo(entries);

    public static final SimpleBlockEntry<FacingBlock> RAYCASTER = new SimpleBlockEntry<FacingBlock>("raycaster")
            .blockSupplier(() -> new FacingBlock(BlockBehaviour.Properties.of()
                    .sound(SoundType.STONE)
                    .strength(5.0f)))
            .saveTo(entries);

    public static final SimpleBlockEntry<RadarBlock> RADAR_MODULE = new SimpleBlockEntry<RadarBlock>("radar_module")
            .blockSupplier( () -> new RadarBlock(BlockBehaviour.Properties.of()
                    .sound(SoundType.STONE)
                    .strength(5.0f)
                    .noOcclusion()))
            .saveTo(entries);

    public static final SimpleBlockEntry<RadarFixedBlock> FIXED_RADAR = new SimpleBlockEntry<RadarFixedBlock>("aesa_fixed_radar")
            .blockSupplier( () -> new RadarFixedBlock(BlockBehaviour.Properties.of()
                    .sound(SoundType.STONE)
                    .strength(5.0f)
                    .noOcclusion()))
            .saveTo(entries);

    public static final SimpleBlockEntry<RadarPanelBlock> RADAR_PANEL = new SimpleBlockEntry<RadarPanelBlock>("aesa_panel")
            .blockSupplier( () -> new RadarPanelBlock(BlockBehaviour.Properties.of()
                    .sound(SoundType.STONE)
                    .strength(5.0f)
                    .noOcclusion()))
            .saveTo(entries);

    public static final SimpleBlockEntry<Block> RWR = new SimpleBlockEntry<>("rwr")
            .blockSupplier( () -> new RWRBlock(BlockBehaviour.Properties.of()
                    .sound(SoundType.STONE)
                    .strength(5.0f)
                    .noOcclusion()))
            .saveTo(entries);

    public static final SimpleBlockEntry<GyroBlock> GYRO_MODULE = new SimpleBlockEntry<GyroBlock>("gyro_module")
            .blockSupplier( () -> new GyroBlock(BlockBehaviour.Properties.of()
                    .sound(SoundType.STONE)
                    .strength(5.0f)))
            .saveTo(entries);

    public static final SimpleBlockEntry<JetEngineBlock> JET_ENGINE = new SimpleBlockEntry<JetEngineBlock>("jet_engine")
            .blockSupplier( () -> new JetEngineBlock(BlockBehaviour.Properties.of()
                    .sound(SoundType.STONE)
                    .strength(10.0f)
                    .noOcclusion()))
            .saveTo(entries);

    public static final HashMap<String, SimpleBlockEntry<APSBlock>> APS_BLOCKS = new HashMap<>();

    public static final HashMap<String, SimpleBlockEntry<BarrelLaunchedMissileBlock>> BARREL_LAUNCHED_MISSILES = new HashMap<>();

    public static SimpleBlockEntry<BarrelLaunchedMissileBlock> registerBarrelLaunchedMissile(MissileRegistry.MissileRegistryEntry missile) {
        final SimpleBlockEntry<BarrelLaunchedMissileBlock> BARREL_LAUNCHED_MISSILE = new SimpleBlockEntry<BarrelLaunchedMissileBlock>("barrel_launched_"+missile.id)
                .blockSupplier( () -> new BarrelLaunchedMissileBlock(BlockBehaviour.Properties.of()
                        .sound(SoundType.STONE)
                        .strength(5.0f)
                        .noOcclusion(),
                        missile.id))
                .itemSupplier(() -> new BarrelLaunchedMissileItem(BARREL_LAUNCHED_MISSILES.get(missile.id).getBlock().get(), new Item.Properties()
                        .stacksTo(1)
                        .rarity(Rarity.COMMON))
                        .withMissileId(missile.id))
                .saveTo(entries);

        BARREL_LAUNCHED_MISSILES.put(missile.id, BARREL_LAUNCHED_MISSILE);
        RegistryEntities.registerBarrelLaunchedMissile(missile.id, BARREL_LAUNCHED_MISSILE);

        return BARREL_LAUNCHED_MISSILE;
    }

    /*public static final BlockEntry<CBCBearingBlock> SYNCED_MECHANICAL_BEARING =
            Main.REGISTRATE
                    .block("synced_mechanical_bearing", CBCBearingBlock::new)
                    .properties((p) -> p.mapColor(MapColor.PODZOL))
                    .transform(TagGen.axeOrPickaxe())
                    .transform(BlockStressDefaults.setImpact(0.0F))
                    .tag(AllTags.AllBlockTags.SAFE_NBT.tag)
                    .simpleItem()
                    .register();*/

    static {
        new APSEntry("aps",
                2, 32, 15, 360,
                PartialModels.APS_2_BASE, PartialModels.APS_2_CHARGES)
                .withYRotation(-360, 360, PartialModels.APS_2_CRADLE)
                .withZYRotation(-12, 26, PartialModels.APS_2_TUBES)
                .register();

        new APSEntry("trophy",
                2, 48, 30, 10,
                PartialModels.APS_TROPHY_BASE, PartialModels.APS_TROPHY_CHARGE)
                .withYRotation(-60, 60, PartialModels.APS_TROPHY_CRADLE)
                .register();

        registerBarrelLaunchedMissile(MissileRegistry.TOW_2B);
        registerBarrelLaunchedMissile(ForeignMissileRegistry.SPIKE_LR2);
        registerBarrelLaunchedMissile(ForeignMissileRegistry.KOBRA_9K112);

        //RegistryItems.registerMediumcannonLaunchedMissile(MissileRegistry.TOW_BB);
    }

    public static void register() {
        entries.forEach(SimpleBlockEntry::register);
    }
}