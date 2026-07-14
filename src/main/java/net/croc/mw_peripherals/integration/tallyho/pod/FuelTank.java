package net.croc.mw_peripherals.integration.tallyho.pod;

import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import com.tterrag.registrate.util.entry.FluidEntry;
import edn.stratodonut.tallyho.entity.MountedMissileEntity;
import java.util.List;
import java.util.Optional;

import net.croc.mw_peripherals.entity.MountedPodEntity;
import net.croc.mw_peripherals.integration.tallyho.IPodFactory;
import net.croc.mw_peripherals.integration.tallyho.PodComponent;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class FuelTank extends PodComponent {
    private final FuelProperties properties;
    private int amount;
    private int cooldown = 0;

    public record FuelProperties(FluidEntry<ForgeFlowingFluid.Flowing> type, int amount) {
        public void appendHoverText(List<Component> components) {
            components.add(Component.literal("Fuel Type: ")
                    .append(this.type.getType().getDescription())
                    .setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY)));
            components.add(Component.literal("Fuel Amount: "+this.amount)
                    .setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY)));
        }
    }

    public int getMaxAmount() {
        return this.properties.amount;
    }

    public int getAmountLeft() {
        return this.amount;
    }

    public static class Factory implements IPodFactory<FuelTank> {
        private final FuelTank.FuelProperties properties;

        public Factory(FluidEntry<ForgeFlowingFluid.Flowing> type, int amount) {
            this.properties = new FuelTank.FuelProperties(type, amount);
        }

        public FuelTank create() {
            return new FuelTank(this.properties);
        }

        public void appendHoverText(List<Component> list) {
            this.properties.appendHoverText(list);
        }
    }

    public FuelTank(FuelProperties props) {
        this.properties = props;
        this.amount = props.amount;
    }

    private void tryRefuel(Entity e, BlockPos pos) {
        if (this.amount <= 0) return;

        BlockEntity tank = e.level().getBlockEntity(pos);

        if (tank != null) {
            Optional<IFluidHandler> optFluidTank = tank.getCapability(ForgeCapabilities.FLUID_HANDLER, Direction.DOWN).resolve();

            if (optFluidTank.isPresent()) {
                IFluidHandler fluidTank = optFluidTank.get();

                FluidStack fuelStack = new FluidStack(this.properties.type.get(), this.amount);

                int filled = fluidTank.fill(fuelStack, IFluidHandler.FluidAction.EXECUTE);
                if (filled > 0) {
                    this.amount -= filled;
                    tank.setChanged();
                }
            }
        }
    }

    @Override
    public void tick(MountedPodEntity e) {
        if (this.amount <= 0) {
            e.drop();
        }

        this.cooldown--;
        if (this.cooldown > 0) return;

        tryRefuel(e, e.blockPosition().above());
        this.cooldown = 20;
    }

    @Override
    public boolean launch(MountedPodEntity e, Vec3 dir, float boost) {
        return true;
    }
}