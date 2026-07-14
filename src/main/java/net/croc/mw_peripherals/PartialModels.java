package net.croc.mw_peripherals;

import com.jozufozu.flywheel.core.PartialModel;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;

public class PartialModels {
    public static void init() {}

    public static final PartialModel RADAR_BASE = new PartialModel(new ResourceLocation(Main.MOD_ID+":other/radar/base"));
    public static final PartialModel RADAR_DISH = new PartialModel(new ResourceLocation(Main.MOD_ID+":other/radar/dish"));
    public static final PartialModel RADAR_CRADLE = new PartialModel(new ResourceLocation(Main.MOD_ID+":other/radar/cradle"));

    public static final PartialModel APS_2_BASE = new PartialModel(new ResourceLocation(Main.MOD_ID+":other/2_charge_aps/base"));
    public static final PartialModel APS_2_CHARGES = new PartialModel(new ResourceLocation(Main.MOD_ID+":other/2_charge_aps/charges"));
    public static final PartialModel APS_2_CRADLE = new PartialModel(new ResourceLocation(Main.MOD_ID+":other/2_charge_aps/cradle"));
    public static final PartialModel APS_2_TUBES = new PartialModel(new ResourceLocation(Main.MOD_ID+":other/2_charge_aps/tubes"));

    public static final PartialModel APS_TROPHY_BASE = new PartialModel(new ResourceLocation(Main.MOD_ID+":other/trophy_aps/base"));
    public static final PartialModel APS_TROPHY_CHARGE = new PartialModel(new ResourceLocation(Main.MOD_ID+":other/trophy_aps/charge"));
    public static final PartialModel APS_TROPHY_CRADLE = new PartialModel(new ResourceLocation(Main.MOD_ID+":other/trophy_aps/cradle"));

    public static final PartialModel JOYSTICK_UP = new PartialModel(new ResourceLocation(Main.MOD_ID+":other/joystick/joystick_up"));
    public static final PartialModel JOYSTICK_DOWN = new PartialModel(new ResourceLocation(Main.MOD_ID+":other/joystick/joystick_down"));
    public static final PartialModel JOYSTICK_RIGHT = new PartialModel(new ResourceLocation(Main.MOD_ID+":other/joystick/joystick_right"));
    public static final PartialModel JOYSTICK_LEFT = new PartialModel(new ResourceLocation(Main.MOD_ID+":other/joystick/joystick_left"));
    
    public static final PartialModel JOYSTICK_DEFAULT = new PartialModel(new ResourceLocation(Main.MOD_ID+":other/joystick/joystick"));
}