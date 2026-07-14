package net.croc.mw_peripherals.integration.cbcmodernwarfare.munitions.barrel_launched_missile;

import java.util.List;

import net.croc.mw_peripherals.Main;
import net.croc.mw_peripherals.RegistryEntities;
import net.croc.mw_peripherals.RegistryItems;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import riftyboi.cbcmodernwarfare.index.CBCModernWarfareEntityTypes;
import riftyboi.cbcmodernwarfare.index.CBCModernWarfareItem;
import riftyboi.cbcmodernwarfare.index.CBCModernWarfareMunitionPropertiesHandlers;
import riftyboi.cbcmodernwarfare.munitions.medium_cannon.AbstractMediumcannonProjectile;
import riftyboi.cbcmodernwarfare.munitions.medium_cannon.MediumcannonRoundItem;
import riftyboi.cbcmodernwarfare.munitions.medium_cannon.config.InertMediumcannonProjectileProperties;
import riftyboi.cbcmodernwarfare.munitions.medium_cannon.config.MediumcannonProjectilePropertiesComponent;

import javax.annotation.Nonnull;

public class MissileMediumcannonRoundItem extends MediumcannonRoundItem {
    private String missileId = "unknown";

    public MissileMediumcannonRoundItem(Item.Properties properties) {
        super(properties);
    }

    public MissileMediumcannonRoundItem withMissileId(String id) {
        this.missileId = id;
        return this;
    }

    @Override
    public AbstractMediumcannonProjectile getMediumcannonProjectile(ItemStack stack, Level level) {
        return (AbstractMediumcannonProjectile) RegistryEntities.missileId_to_entry.get(missileId).create(level);
    }

    @Nonnull
    public MediumcannonProjectilePropertiesComponent getMediumcannonProperties(ItemStack itemStack) {
        return (CBCModernWarfareMunitionPropertiesHandlers.INERT_MEDIUMCANNON_PROJECTILE.getPropertiesOf(this.getEntityType(itemStack))).mediumcannonProperties();
    }

    @Override
    public EntityType<?> getEntityType(ItemStack stack) {
        return RegistryEntities.getTypeFromMissileId(this.missileId);
    }

    @Override
    public ItemStack getCartridgeType() {
        return RegistryItems.MEDIUM_BARREL_LAUNCHED_MISSILES.get(missileId).asStack();
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);

        pTooltipComponents.add(Component.literal("Missile: ").append(Component.translatable(this.missileId))
                .withStyle(ChatFormatting.GRAY));
    }
}