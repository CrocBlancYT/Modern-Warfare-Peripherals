package net.croc.mw_peripherals.blocks.kinetic;

import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.AssemblyException;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.ControlledContraptionEntity;
import com.simibubi.create.content.contraptions.bearing.BearingContraption;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.valkyrienskies.clockwork.content.contraptions.flap.contraption.FlapContraption;
import org.valkyrienskies.clockwork.util.ClockworkConstants;
import rbasamoyai.createbigcannons.cannon_control.ControlPitchContraption;
import rbasamoyai.createbigcannons.cannon_control.cannon_mount.CannonMountBlockEntity;
import rbasamoyai.createbigcannons.cannon_control.contraption.AbstractMountedCannonContraption;
import rbasamoyai.createbigcannons.cannon_control.contraption.PitchOrientedContraptionEntity;

public final class CBCBearingBlockEntity extends SmartBlockEntity implements ControlPitchContraption.Block {
    public final DirectionProperty FACING = BlockStateProperties.FACING;

    public boolean isRunning;

    public boolean assembleNextTick;

    public float pitch;

    public float yaw;

    @Nullable
    private AssemblyException lastException;

    @Nullable
    private PitchOrientedContraptionEntity contraption;

    @Nullable
    private PitchOrientedContraptionEntity synced_cannon_contraption;

    public CBCBearingBlockEntity(@Nullable BlockEntityType type, @NotNull BlockPos pos, @NotNull BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void addBehaviours(@NotNull List<BlockEntityBehaviour> behaviours) { }

    @Override
    public void tick() {
        super.tick();

        if (isRunning) {
            applyRotations();
        }

        Level level = this.getLevel();

        if (level == null) return;
        if (level.isClientSide) return;

        if (this.contraption != null) {
            if (this.synced_cannon_contraption != null) {
                pitch = synced_cannon_contraption.pitch;
                yaw = synced_cannon_contraption.yaw;
                sendData();
            } else {
                Direction direction = this.getBlockState().getValue(FACING);
                BlockPos pos1 = this.getBlockPos()
                        .relative(direction)
                        .relative(direction);

                if (level.getBlockEntity(pos1) instanceof CannonMountBlockEntity mount) {
                    PitchOrientedContraptionEntity cannon = mount.getContraption();

                    if (cannon != null) {
                        synced_cannon_contraption = cannon;
                    }
                }
            }


            this.contraption.tick();
        }

        if (assembleNextTick) {
            assembleNextTick = false;
            assemble();
        }
    }

    public void write(@NotNull CompoundTag compound, boolean clientPacket) {
        compound.putBoolean(ClockworkConstants.Nbt.INSTANCE.getRUNNING(), this.isRunning);
        compound.putFloat("xRot", this.pitch);
        compound.putFloat("yRot", this.yaw);
        AssemblyException.write(compound, this.lastException);
        super.write(compound, clientPacket);
    }

    protected void read(@NotNull CompoundTag compound, boolean clientPacket) {
        this.isRunning = compound.getBoolean(ClockworkConstants.Nbt.INSTANCE.getRUNNING());
        this.pitch = compound.getFloat("xRot");
        this.yaw = compound.getFloat("yRot");
        this.lastException = AssemblyException.read(compound);
        super.read(compound, clientPacket);

        if (!clientPacket) {
            return;
        }

        if (!this.isRunning) {
            this.contraption = null;
        }
    }

    public void assemble() {
        Level level = this.getLevel();
        if (level == null) return;
        if (!(level.getBlockState(this.worldPosition).getBlock() instanceof CBCBearingBlock)) return;

        Direction direction = this.getBlockState().getValue(FACING);
        Contraption contraption = new BearingContraption(false, direction);
        if (contraption.getBlocks().isEmpty()) return;

        contraption.removeBlocksFromWorld(level, BlockPos.ZERO);
        PitchOrientedContraptionEntity contraptionEntity = PitchOrientedContraptionEntity.create(this.getLevel(), contraption,  direction, this);
        this.contraption = contraptionEntity;
        level.addFreshEntity(contraptionEntity);
        this.sendData();
        AllSoundEvents.CONTRAPTION_ASSEMBLE.playOnServer(this.getLevel(), this.worldPosition);
    }

    public void remove() {
        Level level = this.getLevel();
        if (level != null && !level.isClientSide) {
            disassemble();
        }
        super.remove();
    }

    public void disassemble() {
        if (!this.isRunning && this.contraption == null) return;

        this.pitch = 0.0F;
        this.yaw = 0.0F;
        applyRotations();

        if (this.contraption != null) {
            this.contraption.disassemble();
        }

        this.contraption = null;
        this.isRunning = false;

        sendData();
    }

    @Override
    public BlockPos getDismountPositionForContraption(PitchOrientedContraptionEntity pitchOrientedContraptionEntity) {
        Direction vertical = this.getBlockState().getValue(BlockStateProperties.VERTICAL_DIRECTION);
        return this.worldPosition.relative(pitchOrientedContraptionEntity.getInitialOrientation().getOpposite()).relative(vertical.getOpposite());
    }

    @Override
    public BlockState getControllerState() {
        return this.getBlockState();
    }

    private void applyRotations() {
        if (this.contraption == null) return;

        if (this.contraption.canBeTurnedByController(this)) {
            this.contraption.pitch = this.pitch;
            this.contraption.yaw = this.yaw;
        }
    }

    @Override
    public boolean isAttachedTo(AbstractContraptionEntity entity) {
        return this.contraption == entity;
    }

    public void attach(PitchOrientedContraptionEntity contraption) {
        if (contraption.getContraption() instanceof AbstractMountedCannonContraption) {
            this.contraption = contraption;
            if (this.getLevel() != null && !this.getLevel().isClientSide) {
                this.isRunning = true;
                this.sendData();
            }

        }
    }

    @Override
    public void onStall() {
        Level level = this.getLevel();
        if (level != null && !level.isClientSide) {
            sendData();
        }
    }

    @Override
    public BlockPos getControllerBlockPos() {
        return this.worldPosition;
    }
}
