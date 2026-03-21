package net.croc.mw_peripherals.blocks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.croc.mw_peripherals.RegistryBlockEntities;
import net.croc.mw_peripherals.RegistryConfigs;
import net.croc.mw_peripherals.stuff.GyroActor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

import static com.jesz.createdieselgenerators.fluids.FluidRegistry.*;

public class GyroBlockEntity extends BlockEntity {
    public Vector3dc torque = (Vector3dc)new Vector3d(0.0D, 0.0D, 0.0D);

    private boolean powered = false;

    public static final int FLUID_CAPACITY = 8000;

    public static final int FLUID_CONSUMPTION = 16;

    private final FluidTank fluidTank = new FluidTank(8000) {
        protected void onContentsChanged() {
            GyroBlockEntity.this.setChanged();
            if (GyroBlockEntity.this.level != null)
                GyroBlockEntity.this.level.sendBlockUpdated(GyroBlockEntity.this.worldPosition, GyroBlockEntity.this.getBlockState(), GyroBlockEntity.this.getBlockState(), 3);
        }

        public boolean isFluidValid(FluidStack stack) {
            FluidType type = stack.getFluid().getFluidType();

            if (type == BIODIESEL.get().getFluidType()) return true;
            if (type == DIESEL.get().getFluidType()) return true;
            if (type == GASOLINE.get().getFluidType()) return true;
            if (type == ETHANOL.get().getFluidType()) return true;

            return false;
        }
    };

    private final LazyOptional<IFluidHandler> fluidHandler;

    public GyroBlockEntity(BlockPos pos, BlockState state) {
        super(RegistryBlockEntities.GYRO_BLOCK_ENTITY.get(), pos, state);
        this.fluidHandler = LazyOptional.of(() -> this.fluidTank);
    }

    public final FluidTank getFluidTank() {
        return this.fluidTank;
    }

    public int getFluidAmount() {
        return this.fluidTank.getFluidAmount();
    }

    public boolean hasFluid() {
        return !this.fluidTank.isEmpty();
    }

    public void consumeFluid(int amount) {
        this.fluidTank.drain(amount, IFluidHandler.FluidAction.EXECUTE);
        setChanged();
    }

    @Nonnull
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.FLUID_HANDLER)
            return this.fluidHandler.cast();
        return super.getCapability(cap, side);
    }

    public void invalidateCaps() {
        super.invalidateCaps();
        this.fluidHandler.invalidate();
    }

    private static double clamp(double min, double number, double max) {
        return Math.max(Math.min(number, max), min);
    }

    public void setTorque(Vector3dc torque) {
        double max = (RegistryConfigs.Config.GYRO_MAX_OMEGA.get());
        this.torque = new Vector3d(clamp(-max, torque.x(), max), clamp(-max, torque.y(), max), clamp(-max, torque.z(), max));
        setChanged();
    }

    private static void tickRedstone(Level level, BlockPos pos, GyroBlockEntity gyro) {
        int px = level.getSignal(pos.east(), Direction.WEST);
        int py = level.getSignal(pos.above(), Direction.DOWN);
        int pz = level.getSignal(pos.south(), Direction.NORTH);
        int nx = level.getSignal(pos.west(), Direction.EAST);
        int ny = level.getSignal(pos.below(), Direction.UP);
        int nz = level.getSignal(pos.north(), Direction.SOUTH);

        boolean anySignal = (px != 0 || py != 0 || pz != 0 || nx != 0 || ny != 0 || nz != 0);

        if (anySignal) {
            gyro.powered = true;
            gyro.setTorque(new Vector3d((px - nx), (py - ny), (pz - nz)));
        } else if (gyro.powered) {
            gyro.powered = false;
            gyro.setTorque(new Vector3d(0.0D, 0.0D, 0.0D));
        }
    }

    public static void tick(Level level, BlockPos pos, BlockState state, GyroBlockEntity gyro) {
        if (level == null || level.isClientSide()) return;

        ServerShip ship = (ServerShip)VSGameUtilsKt.getShipObjectManagingPos(level, (Vec3i)pos);
        if (ship == null) return;

        tickRedstone(level, pos, gyro);

        if (gyro.getFluidAmount() < FLUID_CONSUMPTION) return;
        gyro.consumeFluid(FLUID_CONSUMPTION);

        GyroActor.getOrCreate(ship).setGyro(pos, (Vector3d)gyro.torque);
    }
}
