package com.xlxyvergil.taa.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import com.tacz.guns.api.GunProperties;
import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.entity.IGunOperator;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.api.item.gun.FireMode;
import com.tacz.guns.api.modifier.ParameterizedCachePair;
import com.tacz.guns.resource.modifier.AttachmentCacheProperty;
import com.tacz.guns.resource.modifier.AttachmentPropertyManager;
import com.tacz.guns.resource.pojo.data.gun.Bolt;
import com.tacz.guns.resource.pojo.data.gun.GunData;
import com.tacz.guns.resource.pojo.data.gun.GunRecoil;
import com.tacz.guns.resource.pojo.data.gun.GunRecoilKeyFrame;
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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * 覆写GunPropertyDiagrams类的draw方法
 * 完整复制原有功能并修改弹匣容量显示逻辑，添加后坐力显示功能
 */
@Mixin(value = com.tacz.guns.client.gui.components.refit.GunPropertyDiagrams.class, remap = false)
@OnlyIn(Dist.CLIENT)
public class GunPropertyDiagramsMixin {
    
    /**
     * @author xlxyvergil
     * @reason 重写按钮位置计算，考虑爆炸属性和后坐力属性
     */
    @Overwrite
    public static int getHidePropertyButtonYOffset() {
        int[] startYOffset = new int[]{49}; // 基础偏移
        com.tacz.guns.resource.modifier.AttachmentPropertyManager.getModifiers().forEach((key, value) -> {
            startYOffset[0] += value.getDiagramsDataSize() * 10;
        });
        
        // 检查是否有爆炸属性：配件属性里有爆炸 OR 枪械本身数据开启了爆炸 OR 缓存中的爆炸数据启用了爆炸
        net.minecraft.client.Minecraft minecraft = net.minecraft.client.Minecraft.getInstance();
        if (minecraft != null && minecraft.player != null) {
            net.minecraft.world.entity.player.Player player = minecraft.player;
            net.minecraft.world.item.ItemStack gunItem = player.getMainHandItem();
            com.tacz.guns.api.item.IGun iGun = com.tacz.guns.api.item.IGun.getIGunOrNull(gunItem);
            if (iGun != null) {
                com.tacz.guns.api.TimelessAPI.getCommonGunIndex(iGun.getGunId(gunItem)).ifPresent(index -> {
                    boolean hasExplosionFromAttachments = com.tacz.guns.util.AttachmentDataUtils.isExplodeEnabled(gunItem, index.getGunData());
                    boolean hasExplosionFromGun = index.getGunData().getBulletData().getExplosionData() != null && 
                                                 index.getGunData().getBulletData().getExplosionData().isExplode();
                    
                    // 尝试获取缓存中的爆炸数据
                    boolean hasExplosionFromCache = false;
                    com.tacz.guns.api.entity.IGunOperator operator = com.tacz.guns.api.entity.IGunOperator.fromLivingEntity(player);
                    if (operator != null) {
                        com.tacz.guns.resource.modifier.AttachmentCacheProperty cacheProperty = operator.getCacheProperty();
                        if (cacheProperty != null) {
                            com.tacz.guns.resource.pojo.data.gun.ExplosionData cachedExplosionData = 
                                cacheProperty.getCache(com.tacz.guns.api.GunProperties.EXPLOSION);
                            if (cachedExplosionData != null) {
                                hasExplosionFromCache = cachedExplosionData.isExplode();
                            }
                        }
                    }
                    
                    if (hasExplosionFromAttachments || hasExplosionFromGun || hasExplosionFromCache) {
                        // 加上我们的爆炸属性（爆炸范围+爆炸伤害=20像素）+ 额外间距（15像素）
                        startYOffset[0] += 35;
                    }
                });
            }
        }
        
        // 添加后坐力显示所需的空间（Pitch和Yaw各占10像素，共20像素）
        startYOffset[0] += 20;
        
        return startYOffset[0];
    }
    
    
    /**
     * @author xlxyvergil
     * @reason 完整复制功能并修改弹匣容量显示逻辑，添加后坐力显示
     */
    @Overwrite
    public static void draw(GuiGraphics graphics, Font font, int x, int y) {
        // 使用重写后的方法计算背景高度，与原版保持一致（按钮位置-11）
        graphics.fill(x, y, x + 288, y + com.tacz.guns.client.gui.components.refit.GunPropertyDiagrams.getHidePropertyButtonYOffset() - 11 , 0xAF222222);

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
            
            // 显示后坐力信息（使用缓存的玩家属性修改值）
            // 获取缓存中的后坐力数据
            ParameterizedCachePair<Float, Float> recoilData = cacheProperty.getCache(GunProperties.RECOIL);
            
            // 无论是否有我们的玩家属性修改，TACZ的配件系统都会生成缓存数据
            // 获取原始后坐力数据用于计算差异
            GunRecoil recoil = gunData.getRecoil();
            if (recoil != null) {
                // 获取原始后坐力值
                float originalPitch = getMaxInGunRecoilKeyFrame(recoil.getPitch());
                float originalYaw = getMaxInGunRecoilKeyFrame(recoil.getYaw());
                
                // 获取修改后的后坐力值（最终计算结果，包含配件和玩家属性修改）
                float modifiedPitch = recoilData != null && recoilData.left() != null ? recoilData.left().getDefaultValue() : originalPitch;
                float modifiedYaw = recoilData != null && recoilData.right() != null ? recoilData.right().getDefaultValue() : originalYaw;
                
                // 计算差值
                float pitchDifference = modifiedPitch - originalPitch;
                float yawDifference = modifiedYaw - originalYaw;
                
                // Pitch后坐力显示
                double pitchPercent = Math.min(originalPitch / 5.0, 1);
                double pitchModifierPercent = Math.min(pitchDifference / 5.0, 1);
                int pitchLength = (int) (barStartX + barMaxWidth * pitchPercent);
                int pitchModifierLength = (int) (barMaxWidth * pitchModifierPercent);
                
                graphics.drawString(font, Component.translatable("gui.tacz.gun_refit.property_diagrams.pitch"), nameTextStartX, yOffset[0], fontColor, false);
                graphics.fill(barStartX, yOffset[0] + 2, barEndX, yOffset[0] + 6, barBackgroundColor);
                graphics.fill(barStartX, yOffset[0] + 2, pitchLength, yOffset[0] + 6, barBaseColor);
                
                if (pitchDifference > 0) {
                    int barRight = Math.min(pitchLength + pitchModifierLength, barEndX);
                    graphics.fill(pitchLength, yOffset[0] + 2, barRight, yOffset[0] + 6, barNegativeColor); // 后坐力增加显示为红色（不好）
                    graphics.drawString(font, String.format("%.2f §c(+%.2f)", modifiedPitch, pitchDifference), valueTextStartX, yOffset[0], fontColor, false);
                } else if (pitchDifference < 0) {
                    // 后坐力减少显示为绿色（好）
                    int barLeft = Math.max(pitchLength + pitchModifierLength, barStartX);
                    graphics.fill(barLeft, yOffset[0] + 2, pitchLength, yOffset[0] + 6, barPositivelyColor);
                    graphics.drawString(font, String.format("%.2f §a(%.2f)", modifiedPitch, pitchDifference), valueTextStartX, yOffset[0], fontColor, false);
                } else {
                    graphics.drawString(font, String.format("%.2f", modifiedPitch), valueTextStartX, yOffset[0], fontColor, false);
                }
                
                yOffset[0] += 10;
                
                // Yaw后坐力显示
                double yawPercent = Math.min(originalYaw / 5.0, 1);
                double yawModifierPercent = Math.min(yawDifference / 5.0, 1);
                int yawLength = (int) (barStartX + barMaxWidth * yawPercent);
                int yawModifierLength = (int) (barMaxWidth * yawModifierPercent);
                
                graphics.drawString(font, Component.translatable("gui.tacz.gun_refit.property_diagrams.yaw"), nameTextStartX, yOffset[0], fontColor, false);
                graphics.fill(barStartX, yOffset[0] + 2, barEndX, yOffset[0] + 6, barBackgroundColor);
                graphics.fill(barStartX, yOffset[0] + 2, yawLength, yOffset[0] + 6, barBaseColor);
                
                if (yawDifference > 0) {
                    int barRight = Math.min(yawLength + yawModifierLength, barEndX);
                    graphics.fill(yawLength, yOffset[0] + 2, barRight, yOffset[0] + 6, barNegativeColor); // 后坐力增加显示为红色（不好）
                    graphics.drawString(font, String.format("%.2f §c(+%.2f)", modifiedYaw, yawDifference), valueTextStartX, yOffset[0], fontColor, false);
                } else if (yawDifference < 0) {
                    // 后坐力减少显示为绿色（好）
                    int barLeft = Math.max(yawLength + yawModifierLength, barStartX);
                    graphics.fill(barLeft, yOffset[0] + 2, yawLength, yOffset[0] + 6, barPositivelyColor);
                    graphics.drawString(font, String.format("%.2f §a(%.2f)", modifiedYaw, yawDifference), valueTextStartX, yOffset[0], fontColor, false);
                } else {
                    graphics.drawString(font, String.format("%.2f", modifiedYaw), valueTextStartX, yOffset[0], fontColor, false);
                }
                
                yOffset[0] += 10;
            }
            
            // 在所有属性后添加爆炸范围和爆炸伤害
            // 检查：配件属性里有爆炸 OR 枪械本身数据开启了爆炸 OR 缓存中的爆炸数据启用了爆炸
            boolean hasExplosionFromAttachments = com.tacz.guns.util.AttachmentDataUtils.isExplodeEnabled(gunItem, gunData);
            boolean hasExplosionFromGun = gunData.getBulletData().getExplosionData() != null && 
                                       gunData.getBulletData().getExplosionData().isExplode();
            
            // 检查缓存中的爆炸数据是否启用了爆炸
            boolean hasExplosionFromCache = false;
            com.tacz.guns.resource.pojo.data.gun.ExplosionData cachedExplosionData = cacheProperty.getCache(com.tacz.guns.api.GunProperties.EXPLOSION);
            if (cachedExplosionData != null) {
                hasExplosionFromCache = cachedExplosionData.isExplode();
            }
            
            if (hasExplosionFromAttachments || hasExplosionFromGun || hasExplosionFromCache) {
                // 获取原始爆炸数据
                com.tacz.guns.resource.pojo.data.gun.ExplosionData originalExplosionData = gunData.getBulletData().getExplosionData();
                if (originalExplosionData != null) {
                    // 获取修改后的爆炸数据（如果没有配件修改，则使用原始数据）
                    com.tacz.guns.resource.pojo.data.gun.ExplosionData modifiedExplosionData = cacheProperty.getCache(com.tacz.guns.api.GunProperties.EXPLOSION);
                    if (modifiedExplosionData == null) {
                        modifiedExplosionData = originalExplosionData;
                    }
                    if (modifiedExplosionData != null) {
                        // 爆炸范围
                        float originalExplosionRadius = originalExplosionData.getRadius();
                        float modifiedExplosionRadius = modifiedExplosionData.getRadius();
                        
                        double explosionRadiusPercent = Mth.clamp(originalExplosionRadius / 5.0, 0, 1);
                        int explosionRadiusLength = (int) (barStartX + barMaxWidth * explosionRadiusPercent);
                        float addRadius = modifiedExplosionRadius - originalExplosionRadius;
                        int addRadiusLength = (int) (barMaxWidth * addRadius / 5.0);

                        graphics.drawString(font, Component.literal("爆炸范围"), nameTextStartX, yOffset[0], fontColor, false);
                        graphics.fill(barStartX, yOffset[0] + 2, barEndX, yOffset[0] + 6, barBackgroundColor);
                        graphics.fill(barStartX, yOffset[0] + 2, explosionRadiusLength, yOffset[0] + 6, barBaseColor);
                        
                        if (addRadius > 0) {
                            int barRight = Math.min(explosionRadiusLength + addRadiusLength, barEndX);
                            graphics.fill(explosionRadiusLength, yOffset[0] + 2, barRight, yOffset[0] + 6, barPositivelyColor);
                            graphics.drawString(font, String.format("%.1f §a(+%.1f)m", modifiedExplosionRadius, addRadius), valueTextStartX, yOffset[0], fontColor, false);
                        } else if (addRadius < 0) {
                            int barLeft = Math.max(explosionRadiusLength + addRadiusLength, barStartX);
                            graphics.fill(barLeft, yOffset[0] + 2, explosionRadiusLength, yOffset[0] + 6, barNegativeColor);
                            graphics.drawString(font, String.format("%.1f §c(%.1f)m", modifiedExplosionRadius, addRadius), valueTextStartX, yOffset[0], fontColor, false);
                        } else {
                            graphics.drawString(font, String.format("%.1fm", modifiedExplosionRadius), valueTextStartX, yOffset[0], fontColor, false);
                        }

                        yOffset[0] += 10;

                        // 爆炸伤害
                        float originalExplosionDamage = originalExplosionData.getDamage();
                        float modifiedExplosionDamage = modifiedExplosionData.getDamage();
                        
                        double explosionDamagePercent = Mth.clamp(originalExplosionDamage / 100.0, 0, 1);
                        int explosionDamageLength = (int) (barStartX + barMaxWidth * explosionDamagePercent);
                        float addDamage = modifiedExplosionDamage - originalExplosionDamage;
                        int addDamageLength = (int) (barMaxWidth * addDamage / 100.0);

                        graphics.drawString(font, Component.literal("爆炸伤害"), nameTextStartX, yOffset[0], fontColor, false);
                        graphics.fill(barStartX, yOffset[0] + 2, barEndX, yOffset[0] + 6, barBackgroundColor);
                        graphics.fill(barStartX, yOffset[0] + 2, explosionDamageLength, yOffset[0] + 6, barBaseColor);
                        
                        if (addDamage > 0) {
                            int barRight = Math.min(explosionDamageLength + addDamageLength, barEndX);
                            graphics.fill(explosionDamageLength, yOffset[0] + 2, barRight, yOffset[0] + 6, barPositivelyColor);
                            graphics.drawString(font, String.format("%.1f §a(+%.1f)", modifiedExplosionDamage, addDamage), valueTextStartX, yOffset[0], fontColor, false);
                        } else if (addDamage < 0) {
                            int barLeft = Math.max(explosionDamageLength + addDamageLength, barStartX);
                            graphics.fill(barLeft, yOffset[0] + 2, explosionDamageLength, yOffset[0] + 6, barNegativeColor);
                            graphics.drawString(font, String.format("%.1f §c(%.1f)", modifiedExplosionDamage, addDamage), valueTextStartX, yOffset[0], fontColor, false);
                        } else {
                            graphics.drawString(font, String.format("%.1f", modifiedExplosionDamage), valueTextStartX, yOffset[0], fontColor, false);
                        }

                        yOffset[0] += 10;
                    }
                }
            }
        });
    }
    
    /**
     * 从GunRecoilKeyFrame数组中获取最大后坐力值
     * 与TACZ原版RecoilModifier中的方法相同
     */
    private static float getMaxInGunRecoilKeyFrame(GunRecoilKeyFrame[] frames) {
        if (frames == null || frames.length == 0) {
            return 0;
        }
        float[] value = frames[0].getValue();
        float leftValue = Math.abs(value[0]);
        float rightValue = Math.abs(value[1]);
        return Math.max(leftValue, rightValue);
    }
}