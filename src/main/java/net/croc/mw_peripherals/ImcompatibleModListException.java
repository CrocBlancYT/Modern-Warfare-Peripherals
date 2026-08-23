package net.croc.mw_peripherals;

public class ImcompatibleModListException extends RuntimeException {
    public ImcompatibleModListException(String incompatible) {
        super("Modern Warfare Peripherals is not supported on this mod list. (Incompatible with " + incompatible + ") You will not receive support for this issue.");
    }
}
