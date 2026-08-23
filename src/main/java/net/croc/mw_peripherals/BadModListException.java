package net.croc.mw_peripherals;

public class BadModListException extends RuntimeException {
    public BadModListException(String missing) {
        super("Modern Warfare Peripherals is not supported on this mod list. (Missing " + missing + ") You will not receive support for this issue.");
    }
}
