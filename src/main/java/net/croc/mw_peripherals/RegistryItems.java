package net.croc.mw_peripherals;

import com.tterrag.registrate.util.entry.ItemEntry;
import edn.stratodonut.tallyho.TallyhoMod;
import net.croc.mw_peripherals.items.FlareItem;
import net.croc.mw_peripherals.items.LaserFuzeItem;
import net.croc.mw_peripherals.items.RemoteFuzeItem;
import net.croc.mw_peripherals.items.RemoteScopeCameraItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class RegistryItems {
    public static final DeferredRegister<Item> ITEMS = Main.ITEMS;

    public static RegistryObject<Item> APS_CHARGE;
    public static RegistryObject<Item> REMOTE_FUZE;
    public static RegistryObject<Item> LASER_FUZE;
    public static RegistryObject<Item> REMOTE_OPTICS;
    public static RegistryObject<Item> FLARE_CARTRIDGE;

    public static void register() {
        APS_CHARGE = ITEMS.register("aps_charge", () -> new Item((new Item.Properties())
                .stacksTo(1)
                .rarity(Rarity.COMMON)));

        REMOTE_FUZE = ITEMS.register("remote_fuze", () -> new RemoteFuzeItem((new Item.Properties())
                .stacksTo(8)
                .rarity(Rarity.COMMON)));

        LASER_FUZE = ITEMS.register("laser_fuze", () -> new LaserFuzeItem((new Item.Properties())
                .stacksTo(8)
                .rarity(Rarity.COMMON)));

        REMOTE_OPTICS = ITEMS.register("remote_optics", () -> new RemoteScopeCameraItem((new Item.Properties())
                .stacksTo(1)
                .rarity(Rarity.COMMON)));

        FLARE_CARTRIDGE = ITEMS.register("flare_cartridge", () -> new FlareItem((new Item.Properties())
                .stacksTo(64)
                .rarity(Rarity.COMMON)));
    }
}
