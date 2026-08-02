package net.croc.mw_peripherals.content.aps;

import com.jozufozu.flywheel.core.PartialModel;
import net.croc.mw_peripherals.RegistryBlocks;
import net.croc.mw_peripherals.blocks.APSBlock;
import net.croc.mw_peripherals.blocks.APSFixedBlockEntity;
import net.croc.mw_peripherals.blocks.APSOneAxisBlockEntity;
import net.croc.mw_peripherals.blocks.APSTwoAxisBlockEntity;
import net.croc.mw_peripherals.utils.SimpleBlockEntry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class APSBlockEntry {
    public static final HashMap<String, APSBlockEntry> entries = new HashMap<>();

    public final String id;

    public final int max_charges;
    public final int max_range;
    public final int cooldown_duration;
    public final int fov;

    public float min_yRot;
    public float max_yRot;

    public float min_zRot;
    public float max_zRot;

    private byte type;

    private final static byte NoRot = 0X1;
    private final static byte YRot = 0X2;
    private final static byte ZYRot = 0X3;

    public boolean isNoRot() {
        return type  == NoRot;
    }

    public boolean isYRot() {
        return type  == YRot;
    }

    public boolean isZYRot() {
        return type  == ZYRot;
    }

    public PartialModel MODEL_BASE;
    public PartialModel MODEL_CHARGES;
    public PartialModel MODEL_CRADLE;
    public PartialModel MODEL_TUBES;

    public APSBlockEntry(String id, int max_charges, int max_range, int cooldown_duration, int fov, PartialModel MODEL_BASE, PartialModel MODEL_CHARGES) {
        this.id = id;

        this.type = NoRot;

        this.max_charges = max_charges;
        this.max_range = max_range;
        this.cooldown_duration = cooldown_duration;
        this.fov = fov;

        this.MODEL_BASE = MODEL_BASE;
        this.MODEL_CHARGES = MODEL_CHARGES;
    }

    public APSBlockEntry withYRotation(float min_yRot, float max_yRot, PartialModel MODEL_CRADLE) {
        this.type = YRot;
        this.min_yRot = min_yRot;
        this.max_yRot = max_yRot;

        this.MODEL_CRADLE = MODEL_CRADLE;
        return this;
    }

    public APSBlockEntry withZYRotation(float min_zRot, float max_zRot, PartialModel MODEL_TUBES) {
        this.type = ZYRot;
        this.min_zRot = min_zRot;
        this.max_zRot = max_zRot;

        this.MODEL_TUBES = MODEL_TUBES;
        return this;
    }

    public APSBlockEntry register() {
        entries.put(this.id, this);

        SimpleBlockEntry<APSBlock> APS_BLOCK = new SimpleBlockEntry<APSBlock>(this.id)
                .blockSupplier( () -> new APSBlock(BlockBehaviour.Properties.of()
                        .sound(SoundType.STONE)
                        .strength(15.0f)
                        .noOcclusion(),
                        this))
                .saveTo(RegistryBlocks.entries);

        RegistryBlocks.APS_BLOCKS.put(this.id, APS_BLOCK);

        return this;
    }

    public static List<Block> getNoRotAPSBlocks() {
        ArrayList<Block> noRotAPS = new ArrayList<>();

        entries.forEach((String id, APSBlockEntry entry) -> {
            if (entry.isNoRot()) {
                noRotAPS.add(RegistryBlocks.APS_BLOCKS.get(id).getBlock().get());
            }
        });

        return noRotAPS;
    }

    public static List<Block> getYRotAPSBlocks() {
        ArrayList<Block> YRotAPS = new ArrayList<>();

        entries.forEach((String id, APSBlockEntry entry) -> {
            if (entry.isYRot()) {
                YRotAPS.add(RegistryBlocks.APS_BLOCKS.get(id).getBlock().get());
            }
        });

        return YRotAPS;
    }
    public static List<Block> getZYRotAPSBlocks() {
        ArrayList<Block> ZYRotAPS = new ArrayList<>();

        entries.forEach((String id, APSBlockEntry entry) -> {
            if (entry.isZYRot()) {
                ZYRotAPS.add(RegistryBlocks.APS_BLOCKS.get(id).getBlock().get());
            }
        });

        return ZYRotAPS;
    }

    public BlockEntity getBlockEntity(BlockPos pos, BlockState state) {
        switch (type) {
            case NoRot:
                return new APSFixedBlockEntity(pos, state);
            case YRot:
                return new APSOneAxisBlockEntity(pos, state);
            case ZYRot:
                return new APSTwoAxisBlockEntity(pos, state);
        }

        return null;
    }

}