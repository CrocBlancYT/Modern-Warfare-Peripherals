package net.croc.mw_peripherals;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.registries.ForgeRegistries;

public class RegistryTags {
    public static TagKey<EntityType<?>> KINETIC_ENERGY = create("mwp_kinetic");
    public static TagKey<EntityType<?>> CHEMICAL_ENERGY = create("mwp_chemical");
    public static TagKey<EntityType<?>> MISSILE = create("mwp_missile");

    private static TagKey<EntityType<?>> create(String name) {
        return TagKey.create(ForgeRegistries.ENTITY_TYPES.getRegistryKey(), new ResourceLocation(Main.MOD_ID, name));
    }

    public static boolean isKinetic(Entity entity) {
        return entity.getType().is(KINETIC_ENERGY);
    }

    public static boolean isChemical(Entity entity) {
        return entity.getType().is(CHEMICAL_ENERGY);
    }

    public static boolean isMissile(Entity entity) {
        return entity.getType().is(MISSILE);
    }

    public static void register() { }
}