package net.croc.mw_peripherals.blocks;

import edn.stratodonut.tallyho.entity.FlareEntity;
import net.croc.mw_peripherals.RegistryBlockEntities;
import net.croc.mw_peripherals.utils.VSShipComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

public class FlareDispenserBlockEntity extends BlockEntity {
    private int flares = 50;

    public static final DirectionProperty FACING = BlockStateProperties.FACING;

    public FlareDispenserBlockEntity(BlockPos pos, BlockState state) {
        super(RegistryBlockEntities.FLARE_DISPENSER_BLOCK_ENTITY.get(), pos, state);
    }

    public boolean addFlare() {
        if (this.flares < 50) {
            this.flares++;
            return true;
        }
        return false;
    }

    public boolean removeFlare() {
        if (this.flares > 0) {
            this.flares--;
            return true;
        }
        return false;
    }

    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("flares", this.flares);
    }

    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("flares")) {
            this.flares = tag.getInt("flares");
        }
        VSShipComponents.subscribe(this);
    }

    public int getFlares() {
        return this.flares;
    }

    public void triggerFlare() {
        if (!this.removeFlare()) { return; }

        BlockPos pos = this.getBlockPos();
        Ship s = VSGameUtilsKt.getShipManagingPos(level, pos);

        if (s instanceof ServerShip serverShip) {
            Direction facing = this.getBlockState().getValue(FACING);
            FlareEntity.dropFromShip(level, serverShip, pos,
                    Vec3.atLowerCornerOf(facing.getNormal()).scale(2.0D));
        }
    }
}