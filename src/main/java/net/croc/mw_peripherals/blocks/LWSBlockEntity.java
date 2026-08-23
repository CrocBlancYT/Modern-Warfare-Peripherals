package net.croc.mw_peripherals.blocks;

import edn.stratodonut.tallyho.entity.LaserPointEntity;
import net.croc.mw_peripherals.RegistryBlockEntities;
import net.croc.mw_peripherals.utils.VSShipComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

import javax.annotation.Nullable;
import java.util.Optional;

public class LWSBlockEntity extends BlockEntity {
    public LWSBlockEntity(BlockPos pos, BlockState state) {
        super(RegistryBlockEntities.LWS_BLOCK_ENTITY.get(),pos, state);
    }

    public boolean isTriggered() {
        Vec3 pos = VSGameUtilsKt.toWorldCoordinates(this.level, this.getBlockPos().getCenter());
        Optional<LaserPointEntity> laser = LaserPointEntity.findUncoded(this.level, pos, new Vec3(0,1,0), 10f, 180f);
        return laser.isPresent();
    }

    public void tick() {}

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        VSShipComponents.subscribe(this);
    }

    @Override
    @Nullable
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        CompoundTag tag = pkt.getTag();
        if (tag != null)
            this.load(tag);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        this.saveAdditional(tag);
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        super.handleUpdateTag(tag);
        this.load(tag);
    }
}