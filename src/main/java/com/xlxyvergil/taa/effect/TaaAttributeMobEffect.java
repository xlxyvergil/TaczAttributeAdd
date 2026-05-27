package com.xlxyvergil.taa.effect;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraftforge.client.extensions.common.IClientMobEffectExtensions;

import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;

public class TaaAttributeMobEffect extends MobEffect {

    private final double perLevelValue;
    private final ResourceLocation iconTexture; // null means no icon

    public TaaAttributeMobEffect(MobEffectCategory category, int color,
                                  Attribute attribute, String uuid, double perLevelValue,
                                  AttributeModifier.Operation operation,
                                  ResourceLocation iconTexture) {
        super(category, color);
        this.perLevelValue = perLevelValue;
        this.iconTexture = iconTexture; // Can be null
        // Validate UUID and add attribute modifier
        UUID validUuid = UUID.fromString(uuid);
        this.addAttributeModifier(attribute, validUuid.toString(), perLevelValue, operation);
    }

    @Override
    public double getAttributeModifierValue(int amplifier, AttributeModifier modifier) {
        return (amplifier + 1) * perLevelValue;
    }

    @Override
    public void initializeClient(Consumer<IClientMobEffectExtensions> consumer) {
        consumer.accept(new IClientMobEffectExtensions() {
            @Override
            public boolean renderInventoryIcon(MobEffectInstance instance,
                    EffectRenderingInventoryScreen<?> screen,
                    GuiGraphics guiGraphics, int x, int y, int blitOffset) {
                if (iconTexture == null) return false;
                RenderSystem.setShaderTexture(0, iconTexture);
                RenderSystem.enableBlend();
                guiGraphics.blit(iconTexture, x, y, 0, 0, 18, 18);
                return true;
            }

            @Override
            public boolean renderGuiIcon(MobEffectInstance instance, Gui gui,
                    GuiGraphics guiGraphics, int x, int y, float z, float alpha) {
                if (iconTexture == null) return false;
                RenderSystem.setShaderTexture(0, iconTexture);
                RenderSystem.enableBlend();
                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha);
                guiGraphics.blit(iconTexture, x, y, 0, 0, 18, 18);
                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                return true;
            }

            @Override
            public boolean isVisibleInInventory(MobEffectInstance instance) {
                return true;
            }

            @Override
            public boolean isVisibleInGui(MobEffectInstance instance) {
                return true;
            }
        });
    }

    public double getPerLevelValue() {
        return perLevelValue;
    }

    public ResourceLocation getIconTexture() {
        return iconTexture;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private MobEffectCategory category = MobEffectCategory.BENEFICIAL;
        private int color = 0xFFFFFF;
        private Attribute attribute;
        private String uuid;
        private double perLevelValue = 0.01;
        private AttributeModifier.Operation operation = AttributeModifier.Operation.MULTIPLY_BASE;
        private ResourceLocation iconTexture; // Optional, can be null

        private Builder() {}

        public Builder category(MobEffectCategory category) {
            this.category = Objects.requireNonNull(category);
            return this;
        }

        public Builder color(int color) {
            this.color = color;
            return this;
        }

        public Builder attribute(Attribute attribute) {
            this.attribute = Objects.requireNonNull(attribute);
            return this;
        }

        public Builder uuid(String uuid) {
            this.uuid = Objects.requireNonNull(uuid);
            return this;
        }

        public Builder perLevelValue(double perLevelValue) {
            this.perLevelValue = perLevelValue;
            return this;
        }

        public Builder operation(AttributeModifier.Operation operation) {
            this.operation = Objects.requireNonNull(operation);
            return this;
        }

        public Builder icon(ResourceLocation iconTexture) {
            this.iconTexture = iconTexture;
            return this;
        }

        public TaaAttributeMobEffect build() {
            Objects.requireNonNull(attribute, "attribute must not be null");
            Objects.requireNonNull(uuid, "uuid must not be null");
            return new TaaAttributeMobEffect(category, color, attribute, uuid, perLevelValue, operation, iconTexture);
        }
    }
}
