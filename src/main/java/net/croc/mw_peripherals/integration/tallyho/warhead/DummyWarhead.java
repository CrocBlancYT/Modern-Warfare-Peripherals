package net.croc.mw_peripherals.integration.tallyho.warhead;

import edn.stratodonut.tallyho.missile.IWarhead;
import java.util.List;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public final class DummyWarhead implements IWarhead {
    public DummyWarhead() { }

    public boolean detonate(Level level, Vec3 hit, Vec3 dir) {
        return true;
    }

    public void save(CompoundTag nbt) {}

    public void read(CompoundTag nbt) {}

    public void appendHoverText(List<Component> components) { }
}