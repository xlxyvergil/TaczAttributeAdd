package com.xlxyvergil.taa.mixin;

import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.entity.IGunOperator;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.api.item.gun.FireMode;
import com.tacz.guns.resource.modifier.AttachmentCacheProperty;
import com.tacz.guns.resource.modifier.AttachmentPropertyManager;
import com.tacz.guns.resource.pojo.data.gun.Bolt;
import com.tacz.guns.resource.pojo.data.gun.GunData;
import com.xlxyvergil.taa.context.ShooterContext;
import com.xlxyvergil.taa.modifier.AmmoCountModifier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * 覆写GunPropertyDiagrams类的draw方法
 * 完整复制原有功能并修改弹匣容量显示逻辑，支持显示小于默认值的情况
 */
@Mixin(value = com.tacz.guns.client.gui.components.refit.GunPropertyDiagrams.class, remap = false)
@OnlyIn(Dist.CLIENT)
public class GunPropertyDiagramsMixin {
    
    /**
     * @author xlxyvergil
     * @reason 完整复制功能并修改弹匣容量显示逻辑
     */
    @Overwrite
    public static void draw(GuiGraphics graphics, Font font, int x, int y) {
        graphics.fill(x, y, x + 288, y + com.tacz.guns.client.gui.components.refit.GunPropertyDiagrams.getHidePropertyButtonYOffset() - 11, 0xAF222222);

        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }
        ItemStack gunItem = player.getMainHandItem();
        IGun iGun = IGun.getIGunOrNull(gunItem);
        if (iGun == null) {
            return;
        }
        AttachmentCacheProperty cacheProperty = IGunOperator.fromLivingEntity(player).getCacheProperty();
        if (cacheProperty == null) {
            return;
        }
        ResourceLocation gunId = iGun.getGunId(gunItem);
        TimelessAPI.getCommonGunIndex(gunId).ifPresent(index -> {
            GunData gunData = index.getGunData();
            FireMode fireMode = iGun.getFireMode(gunItem);

            int barStartX = x + 83;
            int barMaxWidth = 120;
            int barEndX = barStartX + barMaxWidth;

            int barBackgroundColor = 0xFF000000;
            int barBaseColor = 0xFFFFFFFF;
            int barPositivelyColor = 0xFF_55FF55;
            int barNegativeColor = 0xFF_FF5555;

            int fontColor = 0xCCCCCC;
            int nameTextStartX = x + 5;
            int valueTextStartX = x + 210;

            int[] yOffset = new int[]{y + 5};

            // 射击模式
            MutableComponent fireModeText = Component.translatable("gui.tacz.gun_refit.property_diagrams.fire_mode");
            if (fireMode == FireMode.AUTO) {
                fireModeText.append(Component.translatable("gui.tacz.gun_refit.property_diagrams.auto"));
            } else if (fireMode == FireMode.SEMI) {
                fireModeText.append(Component.translatable("gui.tacz.gun_refit.property_diagrams.semi"));
            } else if (fireMode == FireMode.BURST) {
                fireModeText.append(Component.translatable("gui.tacz.gun_refit.property_diagrams.burst"));
            } else {
                fireModeText.append(Component.translatable("gui.tacz.gun_refit.property_diagrams.unknown"));
            }

            graphics.drawString(font, fireModeText, nameTextStartX+12, yOffset[0], fontColor, false);

            yOffset[0] += 10;


            // 弹匣容量 - 修改后的逻辑
            if (iGun.useInventoryAmmo(gunItem)) {
                // 如果使用背包直读，则直接显示满条和 INV 的标注
                graphics.drawString(font, Component.translatable("gui.tacz.gun_refit.property_diagrams.ammo_capacity"), nameTextStartX, yOffset[0], fontColor, false);
                graphics.fill(barStartX, yOffset[0] + 2, barEndX, yOffset[0] + 6, barBackgroundColor);
                graphics.fill(barStartX, yOffset[0] + 2, barStartX + barMaxWidth, yOffset[0] + 6, barBaseColor);
                graphics.drawString(font, Component.literal("INV"), valueTextStartX, yOffset[0], fontColor, false);
            } else {
                // 修改这里：ammoAmount计算包含枪管中的子弹（与原版保持一致）
                int barrelBulletAmount = (iGun.hasBulletInBarrel(gunItem) && index.getGunData().getBolt() != Bolt.OPEN_BOLT) ? 1 : 0;
                int ammoAmount = gunData.getAmmoAmount() + barrelBulletAmount;
                double ammoAmountPercent = Math.min(ammoAmount / 100.0, 1);
                // 修复计算方式，避免自动减1的问题
                int ammoLength = barStartX + (int) (barMaxWidth * ammoAmountPercent);
                
                // 修改这里：只使用我们的缓存值，并加上枪管中的子弹
                int maxAmmoCount = ammoAmount; // 默认值
                
                // 检查是否为背包供弹模式，如果是则不修改
                boolean isUsingInventoryAsMagazine = gunData.getReloadData() != null && 
                    gunData.getReloadData().getType() == com.tacz.guns.resource.pojo.data.gun.FeedType.INVENTORY;
                    
                if (!isUsingInventoryAsMagazine) {
                    // 首先尝试从ShooterContext获取缓存数据（最高优先级）
                    LivingEntity shooter = ShooterContext.getShooter();
                    if (shooter != null) {
                        IGunOperator operator = IGunOperator.fromLivingEntity(shooter);
                        if (operator != null) {
                            AttachmentCacheProperty cache = operator.getCacheProperty();
                            if (cache != null) {
                                Integer modifiedAmmoCount = cache.getCache(AmmoCountModifier.ID);
                                if (modifiedAmmoCount != null) {
                                    maxAmmoCount = modifiedAmmoCount + barrelBulletAmount; // 使用缓存值并加上枪管中的子弹
                                }
                            }
                        }
                    }
                    
                    // 如果ShooterContext中没有，尝试从客户端玩家获取缓存数据（用于配件面板显示）
                    if (maxAmmoCount == ammoAmount) { // 只有在还没有修改值时才尝试
                        try {
                            LocalPlayer clientPlayer = Minecraft.getInstance().player;
                            if (clientPlayer != null) {
                                IGunOperator operator = IGunOperator.fromLivingEntity(clientPlayer);
                                if (operator != null) {
                                    AttachmentCacheProperty cache = operator.getCacheProperty();
                                    if (cache != null) {
                                        Integer modifiedAmmoCount = cache.getCache(AmmoCountModifier.ID);
                                        if (modifiedAmmoCount != null) {
                                            maxAmmoCount = modifiedAmmoCount + barrelBulletAmount; // 使用缓存值并加上枪管中的子弹
                                        }
                                    }
                                }
                            }
                        } catch (Exception ignored) {
                            // 如果出现任何异常（例如在服务器端），忽略并继续
                        }
                    }
                }
                
                // 修改这里：移除Math.max限制，允许负值
                int addAmmoCount = maxAmmoCount - ammoAmount;
                // 修改这里：修复计算方式，基于基准ammoAmount计算百分比
                int addAmmoCountLength = (int) (barMaxWidth * addAmmoCount / (double) Math.max(ammoAmount, 1));

                graphics.drawString(font, Component.translatable("gui.tacz.gun_refit.property_diagrams.ammo_capacity"), nameTextStartX, yOffset[0], fontColor, false);
                graphics.fill(barStartX, yOffset[0] + 2, barEndX, yOffset[0] + 6, barBackgroundColor);
                graphics.fill(barStartX, yOffset[0] + 2, ammoLength, yOffset[0] + 6, barBaseColor);
                // 修改这里：处理正值、负值和零值的情况
                if (addAmmoCount > 0) {
                    int barRight = Math.min(ammoLength + addAmmoCountLength, barEndX);
                    graphics.fill(ammoLength, yOffset[0] + 2, barRight, yOffset[0] + 6, barPositivelyColor);
                    graphics.drawString(font, String.format("%d §a(+%d)", maxAmmoCount, addAmmoCount), valueTextStartX, yOffset[0], fontColor, false);
                } else if (addAmmoCount < 0) {
                    // 处理弹药减少的情况
                    int barLeft = Math.max(ammoLength + addAmmoCountLength, barStartX);
                    graphics.fill(barLeft, yOffset[0] + 2, ammoLength, yOffset[0] + 6, barNegativeColor);
                    graphics.drawString(font, String.format("%d §c(%d)", maxAmmoCount, addAmmoCount), valueTextStartX, yOffset[0], fontColor, false);
                } else {
                    graphics.drawString(font, String.format("%d", maxAmmoCount), valueTextStartX, yOffset[0], fontColor, false);
                }
            }

            yOffset[0] += 10;


            // 跑射延迟
            float sprintTime = gunData.getSprintTime();
            double sprintTimePercent = Mth.clamp(sprintTime / 0.5, 0, 1);
            int sprintLength = (int) (barStartX + barMaxWidth * sprintTimePercent);
            String sprintValueText = String.format("%.2fs", sprintTime);

            graphics.drawString(font, Component.translatable("gui.tacz.gun_refit.property_diagrams.sprint_time"), nameTextStartX, yOffset[0], fontColor, false);
            graphics.fill(barStartX, yOffset[0] + 2, barEndX, yOffset[0] + 6, barBackgroundColor);
            graphics.fill(barStartX, yOffset[0] + 2, sprintLength, yOffset[0] + 6, barBaseColor);
            graphics.drawString(font, sprintValueText, valueTextStartX, yOffset[0], fontColor, false);

            yOffset[0] += 10;

            AttachmentPropertyManager.getModifiers().forEach((key, value) -> value.getPropertyDiagramsData(gunItem, gunData, cacheProperty).forEach(data -> {
                double defaultPercent = data.defaultPercent();
                double modifierPercent = data.modifierPercent();
                double modifier = data.modifier().doubleValue();
                String titleKey = data.titleKey();
                String positivelyString = data.positivelyString();
                String negativeString = data.negativeString();
                String defaultString = data.defaultString();
                boolean positivelyBetter = data.positivelyBetter();

                defaultPercent = Mth.clamp(defaultPercent, 0, 1);
                int defaultLength = (int) (barStartX + barMaxWidth * defaultPercent);
                int modifierLength = Mth.clamp(defaultLength + (int) (barMaxWidth * modifierPercent), barStartX, barEndX);

                graphics.drawString(font, Component.translatable(titleKey), nameTextStartX, yOffset[0], fontColor, false);
                graphics.fill(barStartX, yOffset[0] + 2, barEndX, yOffset[0] + 6, barBackgroundColor);
                graphics.fill(barStartX, yOffset[0] + 2, defaultLength, yOffset[0] + 6, barBaseColor);
                if (modifier > 0) {
                    int barColor = positivelyBetter ? barPositivelyColor : barNegativeColor;
                    graphics.fill(defaultLength, yOffset[0] + 2, modifierLength, yOffset[0] + 6, barColor);
                    graphics.drawString(font, positivelyString, valueTextStartX, yOffset[0], fontColor, false);
                } else if (modifier < 0) {
                    int barColor = positivelyBetter ? barNegativeColor : barPositivelyColor;
                    graphics.fill(modifierLength, yOffset[0] + 2, defaultLength, yOffset[0] + 6, barColor);
                    graphics.drawString(font, negativeString, valueTextStartX, yOffset[0], fontColor, false);
                } else {
                    graphics.drawString(font, defaultString, valueTextStartX, yOffset[0], fontColor, false);
                }
                yOffset[0] += 10;
            }));
        });
    }
}