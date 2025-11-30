package net.croc.mw_peripherals;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class RegistryItems {
    public static final DeferredRegister<Item> ITEMS = Main.ITEMS;

    public static RegistryObject<Item> APS_CHARGE;

    public static void register() {
        APS_CHARGE = ITEMS.register("aps_charge",
                () -> new Item(new Item.Properties()
                        .stacksTo(1)
                        .rarity(Rarity.COMMON)));
    }
}