package net.croc.mw_peripherals;

import com.tterrag.registrate.util.entry.ItemEntry;
import edn.stratodonut.tallyho.TallyhoMod;
import edn.stratodonut.tallyho.missile.MissileRegistry;
import net.croc.mw_peripherals.integration.cbcmodernwarfare.munitions.barrel_launched_missile.MissileMediumcannonRoundItem;
import net.croc.mw_peripherals.integration.createbigcannons.munitions.barrel_launched_missile.BarrelLaunchedMissileItem;
import net.croc.mw_peripherals.items.*;
import net.croc.mw_peripherals.utils.SimpleBlockEntry;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import riftyboi.cbcmodernwarfare.CBCModernWarfare;
import riftyboi.cbcmodernwarfare.munitions.medium_cannon.ap.APMediumcannonRoundItem;

import java.util.HashMap;

public class RegistryItems {
    public static final DeferredRegister<Item> ITEMS = Main.ITEMS;

    public static RegistryObject<Item> APS_CHARGE;
    public static RegistryObject<Item> REMOTE_FUZE;
    public static RegistryObject<Item> LASER_FUZE;
    public static RegistryObject<Item> REMOTE_OPTICS;
    public static RegistryObject<Item> FLARE_CARTRIDGE;
    public static RegistryObject<Item> JOYSTICK;
    public static RegistryObject<Item> TV_GUIDANCE_REMOTE;

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

        JOYSTICK = ITEMS.register("joystick", () -> new MCLOSJoystick((new Item.Properties())
                .stacksTo(1)
                .rarity(Rarity.COMMON)));

        TV_GUIDANCE_REMOTE = ITEMS.register("tv_controller", () -> new TVGuidanceRemote((new Item.Properties())
                .stacksTo(1)
                .rarity(Rarity.COMMON)));

        /*LASER_SENSOR = ITEMS.register("laser_sensor", () -> new LaserIRSensor((new Item.Properties())
                .stacksTo(1)
                .rarity(Rarity.COMMON)));

        MISSILE_SENSOR = ITEMS.register("missile_sensor", () -> new MissileIRSensor((new Item.Properties())
                .stacksTo(1)
                .rarity(Rarity.COMMON)));*/

    }

    public static final HashMap<String, ItemEntry<MissileMediumcannonRoundItem>> MEDIUM_BARREL_LAUNCHED_MISSILES = new HashMap<>();

    public static ItemEntry<MissileMediumcannonRoundItem> registerMediumcannonLaunchedMissile(MissileRegistry.MissileRegistryEntry missile) {
        final ItemEntry<MissileMediumcannonRoundItem> BARREL_LAUNCHED_MISSILE = CBCModernWarfare.REGISTRATE
                .item("barrel_fired_"+missile.id, (properties) -> { return new MissileMediumcannonRoundItem(properties).withMissileId(missile.id); })
                .lang("Mediumcannon Barrel Launched Missile")
                .register();

        MEDIUM_BARREL_LAUNCHED_MISSILES.put(missile.id, BARREL_LAUNCHED_MISSILE);

        RegistryEntities.registerMediumcannonMissile(missile.id, BARREL_LAUNCHED_MISSILE);

        return BARREL_LAUNCHED_MISSILE;
    }
}
