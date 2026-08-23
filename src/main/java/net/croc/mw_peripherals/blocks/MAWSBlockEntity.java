package net.croc.mw_peripherals.blocks;

import edn.stratodonut.tallyho.entity.MountedMissileEntity;
import net.croc.mw_peripherals.RegistryBlockEntities;
import net.croc.mw_peripherals.utils.VSShipComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;

public class MAWSBlockEntity extends BlockEntity {
    public HashMap<String, Entity> targets = new HashMap<>();

    public MAWSBlockEntity(BlockPos pos, BlockState state) {
        super(RegistryBlockEntities.MAWS_BLOCK_ENTITY.get(),pos, state);
    }

    public List<MountedMissileEntity> getIncoming() {
        Vec3 worldPos = VSGameUtilsKt.toWorldCoordinates(this.level, this.getBlockPos().getCenter());
        AABB box = new AABB(worldPos, worldPos).inflate(100D);

        if (this.level == null) return null;
        return this.level.getEntitiesOfClass(MountedMissileEntity.class, box);
    }

    public void tick() {}

    public void render() {}

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