package net.croc.mw_peripherals.blocks;

import net.croc.mw_peripherals.RegistryBlockEntities;
import net.croc.mw_peripherals.integration.tallyho.tracker.RadTracker.Angle;
import net.croc.mw_peripherals.utils.IRadarBlockEntity;
import net.croc.mw_peripherals.utils.VSShipComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class RadarPanelBlockEntity extends IRadarBlockEntity {
    private static final int MAX_PANELS = 128;
    private int connected_panels = 0;

    public int getConnectedPanels() {
        return connected_panels;
    }

    public RadarPanelBlockEntity(BlockPos pos, BlockState state) {
        super(RegistryBlockEntities.RADAR_PANEL_BLOCK_ENTITY.get(), pos, state);
    }

    @Override
    public double maxRange() {
        return 300 + connected_panels * 50;
    }

    @Override
    public Angle scanFoV() {
        return Angle.degrees(60);
    }

    @Override
    public RadarType type() {
        return RadarType.AESA;
    }

    public Direction getFacing() {
        assert this.level != null;
        return this.level.getBlockState(this.getBlockPos()).getValue(BlockStateProperties.FACING);
    }

    public Set<Direction> getPanelPlane() {
        Direction dir = getFacing();

        HashSet<Direction> neighboring_directions = new HashSet<>();
        Direction.stream().forEach(neighboring_directions::add);

        neighboring_directions.remove(dir);
        neighboring_directions.remove(dir.getOpposite());

        return neighboring_directions;
    }

    public boolean isFellowPanel(BlockPos pos) {
        if (this.level == null) return false;
        return this.level.getBlockEntity(pos) instanceof RadarPanelBlockEntity panel && panel.getFacing() == this.getFacing();
    }

    private void broadcastPanelSize(HashSet<BlockPos> panels, int size) {
        if (this.level == null) return;

        for (BlockPos pos : panels) {
            if (level.getBlockEntity(pos) instanceof RadarPanelBlockEntity panel) {
                panel.connected_panels = size;
            }
        }
    }

    public void refreshPanels() {
        Set<Direction> directions = getPanelPlane();
        HashSet<BlockPos> panels = new HashSet<>();
        HashSet<BlockPos> visited = new HashSet<>();
        ArrayDeque<BlockPos> to_visit = new ArrayDeque<>();

        // mark origin as panel
        int size = 1;
        BlockPos origin = this.getBlockPos();
        to_visit.add(origin);
        panels.add(origin);
        visited.add(origin);

        while (!to_visit.isEmpty() && size < MAX_PANELS) {
            BlockPos pos = to_visit.poll();

            for (Direction direction : directions) {
                BlockPos neighbor = pos.relative(direction);

                if (!visited.contains(neighbor)) { // neighbor not visited yet
                    if (isFellowPanel(neighbor)) { // neighbor is a panel
                        to_visit.add(neighbor);
                        panels.add(neighbor);
                        visited.add(neighbor); // mark next as visited

                        size++;
                    }
                }
            }
        }

        broadcastPanelSize(panels, size);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, RadarPanelBlockEntity radar) {
        if (level.getBestNeighborSignal(pos) > 0) {
            radar.scan();
        }
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        VSShipComponents.subscribe(this);
    }
}