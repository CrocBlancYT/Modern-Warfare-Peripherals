package net.croc.mw_peripherals;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import com.simibubi.create.AllSoundEvents;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class RegistrySounds {
    public static final Map<ResourceLocation, AllSoundEvents.SoundEntry> ALL = new HashMap<>();

    public static final AllSoundEvents.SoundEntry MISSILE_LAUNCH = create("missile_launch").subtitle("Missile launch")
            .category(SoundSource.BLOCKS)
            .build();

    public static final AllSoundEvents.SoundEntry BREECH_OPEN = create("breech_open").subtitle("Breech opens")
            .addVariant("breech_open1")
            .addVariant("breech_open2")
            .addVariant("breech_open3")
            .category(SoundSource.BLOCKS)
            .build();

    public static final AllSoundEvents.SoundEntry BREECH_CLOSE = create("breech_close").subtitle("Breech closes")
            .addVariant("breech_close1")
            .addVariant("breech_close2")
            .addVariant("breech_close3")
            .category(SoundSource.BLOCKS)
            .build();

    public static final AllSoundEvents.SoundEntry FIRE_ROTARY = create("fire_rotary").subtitle("Rotary fires")
            .category(SoundSource.BLOCKS)
            .build();

    public static final AllSoundEvents.SoundEntry FIRE_HEAVY_AC = create("fire_heavy_autocannon").subtitle("Heavy autocannon fires")
            .category(SoundSource.BLOCKS)
            .build();

    private static AllSoundEvents.SoundEntryBuilder create(String id) {
        return new MWPSoundEntryBuilder(Main.resource(id));
    }

    public static class MWPSoundEntryBuilder extends AllSoundEvents.SoundEntryBuilder {
        public MWPSoundEntryBuilder(ResourceLocation id) {
            super(id);
        }

        public AllSoundEvents.SoundEntryBuilder addVariant(String name) {
            return addVariant(Main.resource(name));
        }

        public AllSoundEvents.SoundEntry build() {
            AllSoundEvents.SoundEntry entry = super.build();
            ALL.put(entry.getId(), entry);
            return entry;
        }
    }
    public static void prepare() {
        for (AllSoundEvents.SoundEntry entry : ALL.values())
            entry.prepare();
    }

    public static void register(Consumer<AllSoundEvents.SoundEntry> consumer) {
        for (AllSoundEvents.SoundEntry entry : ALL.values())
            consumer.accept(entry);
    }
}