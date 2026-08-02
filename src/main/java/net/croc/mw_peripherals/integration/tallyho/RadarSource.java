package net.croc.mw_peripherals.integration.tallyho;

import edn.stratodonut.tallyho.missile.Target;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RadarSource {
    public record Source<T>(T source, long gameTime, short duration) {
        public boolean isAlive(Level level) {
            return gameTime + duration >= level.getGameTime();
        }
    }

    private final static HashMap<Object, Source<Transmitter>> raw_sources = new HashMap<>();
    private final static HashMap<Object, Source<Target<?>>> reflected_sources = new HashMap<>();

    public static List<Transmitter> getAllActiveTransmitters() {
        ArrayList<Transmitter> transmitters = new ArrayList<>();

        raw_sources.forEach((Object o, Source<Transmitter> source) -> {
            transmitters.add(source.source);
        });

        return transmitters;
    }

    public static final short DURATION = 40;

    public static void transmit(Transmitter transmitter) {
        raw_sources.put(transmitter.origin.get(), new Source<>(transmitter, transmitter.level.getGameTime(), DURATION));
    }

    public static void illuminate(Level level, Target<?> target) {
        reflected_sources.put(target.get(), new Source<>(target, level.getGameTime(), DURATION));
    };

    public static boolean isIlluminated(Level level, Target<?> target) {
        Source<?> source = reflected_sources.get(target.get());
        if (source == null) return false;
        return source.isAlive(level);
    };

    public static boolean isTransmitting(Level level, Target<?> target) {
        Source<?> source = raw_sources.get(target.get());
        if (source == null) return false;
        return source.isAlive(level);
    }

    public record Transmitter(Level level, Target<?> origin, double range) {
        public boolean canIlluminate(Target<?> target) { return this.origin.position().subtract(target.position()).lengthSqr() <= range*range; }
    }

    public record Receiver(Level level, Target<?> origin, double range) {
        public boolean canReceive(Target<?> target) { return this.origin.position().subtract(target.position()).lengthSqr() <= range*range; }
    }
}