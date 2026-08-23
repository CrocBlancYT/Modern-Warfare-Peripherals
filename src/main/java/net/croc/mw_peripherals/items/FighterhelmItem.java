package net.croc.mw_peripherals.items;

import net.croc.mw_peripherals.client.model.Modelflighthelmet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class FighterhelmItem extends ArmorItem {
    private static final int[] BASE_DURABILITY = {13, 15, 16, 11};
    private static final int[] DEFENSE_VALUES = {2, 5, 5, 3};
    private static final int ENCHANTABILITY = 9;
    private static final float TOUGHNESS = 0.0F;
    private static final float KNOCKBACK_RESISTANCE = 0.0F;
    private static final ResourceLocation EQUIP_SOUND = new ResourceLocation("item.armor.equip_leather");
    private static final ResourceLocation ARMOR_TEXTURE = new ResourceLocation("combatgear:textures/entities/flighthelmettex.png");

    private static final ArmorMaterial HELMET_MATERIAL = new HelmetMaterial();

    public FighterhelmItem() {
        super(HELMET_MATERIAL, ArmorItem.Type.HELMET, new Item.Properties());
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            @Override
            public @NotNull HumanoidModel<?> getHumanoidArmorModel(LivingEntity living, ItemStack stack,
                                                                   EquipmentSlot slot, HumanoidModel<?> defaultModel) {
                return createArmorModel(living, defaultModel);
            }
        });
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level,
                                @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.literal("Model by caffeinatedlogis"));
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        return ARMOR_TEXTURE.toString();
    }

    private HumanoidModel<?> createArmorModel(LivingEntity living, HumanoidModel<?> defaultModel) {
        ModelPart helmetPart = getHelmetModelPart();

        Map<String, ModelPart> parts = Map.of(
                "head", helmetPart,
                "hat", new ModelPart(Collections.emptyList(), Collections.emptyMap()),
                "body", new ModelPart(Collections.emptyList(), Collections.emptyMap()),
                "right_arm", new ModelPart(Collections.emptyList(), Collections.emptyMap()),
                "left_arm", new ModelPart(Collections.emptyList(), Collections.emptyMap()),
                "right_leg", new ModelPart(Collections.emptyList(), Collections.emptyMap()),
                "left_leg", new ModelPart(Collections.emptyList(), Collections.emptyMap())
        );

        HumanoidModel<?> armorModel = new HumanoidModel<>(new ModelPart(Collections.emptyList(), parts));

        armorModel.crouching = living.isShiftKeyDown();
        armorModel.riding = defaultModel.riding;
        armorModel.young = living.isBaby();

        return armorModel;
    }

    private ModelPart getHelmetModelPart() {
        return new Modelflighthelmet<>(
                Minecraft.getInstance().getEntityModels().bakeLayer(Modelflighthelmet.LAYER_LOCATION)
        ).helmet2;
    }

    private static class HelmetMaterial implements ArmorMaterial {
        @Override
        public int getDurabilityForType(@NotNull Type type) {
            return BASE_DURABILITY[type.getSlot().getIndex()] * 80;
        }

        @Override
        public int getDefenseForType(@NotNull Type type) {
            return DEFENSE_VALUES[type.getSlot().getIndex()];
        }

        @Override
        public int getEnchantmentValue() {
            return ENCHANTABILITY;
        }

        @Override
        public SoundEvent getEquipSound() {
            return ForgeRegistries.SOUND_EVENTS.getValue(EQUIP_SOUND);
        }

        @Override
        public @NotNull Ingredient getRepairIngredient() {
            return Ingredient.EMPTY;
        }

        @Override
        public @NotNull String getName() {
            return "fighterhelm";
        }

        @Override
        public float getToughness() {
            return TOUGHNESS;
        }

        @Override
        public float getKnockbackResistance() {
            return KNOCKBACK_RESISTANCE;
        }
    }
}