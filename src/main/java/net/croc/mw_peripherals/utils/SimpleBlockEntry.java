package net.croc.mw_peripherals.utils;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Supplier;

import static net.croc.mw_peripherals.Main.BLOCKS;
import static net.croc.mw_peripherals.Main.ITEMS;

public class SimpleBlockEntry<T extends Block> {
    private static final HashMap<String, RegistryObject<Item>> items = new HashMap<>();
    private static final HashMap<String, RegistryObject<Block>> blocks = new HashMap<>();

    private final String id;
    private Supplier<Item> itemSupplier;
    private Supplier<T> blockSupplier;

    public SimpleBlockEntry(String id) {
        this.id = id;
        this.itemSupplier = () -> new BlockItem(this.getBlock().get(), new Item.Properties());
    }

    public static <T extends Block> SimpleBlockEntry<T> create(String id) {
        return new SimpleBlockEntry<>(id);
    }

    public SimpleBlockEntry<T> blockSupplier(Supplier<T> blockSupplier) {
        this.blockSupplier = blockSupplier;
        return this;
    }

    public SimpleBlockEntry<T> itemSupplier(Supplier<Item> itemSupplier) {
        this.itemSupplier = itemSupplier;
        return this;
    }

    @Nullable
    public RegistryObject<Item> getItem() {
        return items.get(this.id);
    }

    @Nullable
    public RegistryObject<Block> getBlock() {
        return blocks.get(this.id);
    }

    public SimpleBlockEntry<T> saveTo(ArrayList<SimpleBlockEntry<?>> entries) {
        entries.add(this);
        return this;
    }

    public void register() {
        blocks.put(this.id, BLOCKS.register(this.id, this.blockSupplier));
        items.put(this.id, ITEMS.register(this.id, this.itemSupplier));
    }
}