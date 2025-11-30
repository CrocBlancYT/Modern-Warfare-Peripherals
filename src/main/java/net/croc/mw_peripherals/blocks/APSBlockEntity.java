package net.croc.mw_peripherals.blocks;

import net.croc.mw_peripherals.Main;
import net.croc.mw_peripherals.RegistryBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import net.croc.mw_peripherals.stuff.APS_HardKill;

import java.util.List;

import org.valkyrienskies.mod.common.VSGameUtilsKt;

public class APSBlockEntity extends BlockEntity {
    public static final int range = 20;
    private int charges = 0;
    private int cooldown = 0;
    private int heightOffset = 8; // x2 pixels

    public APSBlockEntity(BlockPos pos, BlockState state) {
        //super(ModBlockEntities.APS_BLOCK_ENTITY.get(), pos, state);
        super(RegistryBlockEntities.BLOCK_ENTITIES.get("aps").get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, APSBlockEntity blockEntity) {
        if (blockEntity.cooldown > 0) {
            blockEntity.cooldown--;
        }

        int redstonePower = level.getBestNeighborSignal(pos);
        if (redstonePower > 0 && blockEntity.cooldown <= 0) {
            autoIntercept(level, pos, blockEntity);
        }

        blockEntity.setChanged();
    }

    private static void autoIntercept(Level level, BlockPos pos, APSBlockEntity APS) {
        List<Entity> incoming = APS_HardKill.detectProjectiles(APS, range);
        incoming.forEach((Entity entity) -> {
            if (APS.charges > 0) {
                boolean success = APS_HardKill.tryKillProjectile(APS, entity);

                if (success) {
                    APS.charges--;
                    APS.cooldown = 20 * 5;
                    APS.setChanged();
                }
            }
        });

    }

    public boolean addCharge() {
        if (charges < 2) { // maxCharges = 10
            charges++;
            setChanged();
            return true;
        }
        return false;
    }

    public boolean useCharge() {
        if (charges > 0) { // maxCharges = 10
            charges--;
            setChanged();
            return true;
        }
        return false;
    }


    public int getCharges() {
        return this.charges;
    }
    public int getCooldown() {
        return this.cooldown;
    }
    // public int getHeightOffset() { return this.heightOffset; }
    
    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("charges", this.charges);
        tag.putInt("cooldown", this.cooldown);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);

        if (tag.contains("charge")) {
            this.charges = tag.getInt("charge");
        }

        if (tag.contains("cooldown")) {
            this.cooldown = tag.getInt("cooldown");
        }
    }
}