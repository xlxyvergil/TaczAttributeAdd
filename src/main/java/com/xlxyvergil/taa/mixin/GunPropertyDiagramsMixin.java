package com.xlxyvergil.taa.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import com.tacz.guns.api.GunProperties;
import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.entity.IGunOperator;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.api.item.gun.FireMode;
import com.tacz.guns.api.modifier.ParameterizedCachePair;
import com.tacz.guns.client.gui.components.refit.GunPropertyDiagrams;
import com.tacz.guns.config.sync.SyncConfig;
import com.tacz.guns.resource.modifier.AttachmentCacheProperty;
import com.tacz.guns.resource.modifier.AttachmentPropertyManager;
import com.tacz.guns.resource.modifier.custom.*;
import com.tacz.guns.resource.pojo.data.gun.Bolt;
import com.tacz.guns.resource.pojo.data.gun.BulletData;
import com.tacz.guns.resource.pojo.data.gun.ExplosionData;
import com.tacz.guns.resource.pojo.data.gun.FeedType;
import com.tacz.guns.resource.pojo.data.gun.GunData;
import com.tacz.guns.resource.pojo.data.gun.GunFireModeAdjustData;
import com.tacz.guns.resource.pojo.data.gun.GunMeleeData;
import com.tacz.guns.resource.pojo.data.gun.GunRecoil;
import com.tacz.guns.resource.pojo.data.gun.GunRecoilKeyFrame;
import com.tacz.guns.resource.pojo.data.gun.InaccuracyType;
import com.tacz.guns.resource.pojo.data.gun.ExtraDamage;
import com.tacz.guns.resource.pojo.data.gun.ExtraDamage.DistanceDamagePair;
import com.tacz.guns.util.AttachmentDataUtils;
import com.xlxyvergil.taa.config.AttributeConfig;
import com.xlxyvergil.taa.context.ShooterContext;
import com.xlxyvergil.taa.modifier.*;
import com.xlxyvergil.taa.util.ApothicAttributesHelper;
import com.xlxyvergil.taa.util.EntityAttributeHelper;
import com.xlxyvergil.taa.util.GunsmithLibHelper;
import com.xlxyvergil.taa.util.KuvaLichIntegrationHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.LinkedList;
import java.util.Map;

/**
 * 覆写GunPropertyDiagrams类的draw方法
 * 完整复制原有功能并修改弹匣容量显示逻辑，添加后坐力显示功能
 */
@Mixin(value = GunPropertyDiagrams.class, remap = false)
@OnlyIn(Dist.CLIENT)
public class GunPropertyDiagramsMixin {
    
    /**
     * @author xlxyvergil
     * @reason 重写按钮位置计算，考虑爆炸属性和后坐力属性
     */
    @Overwrite
    public static int getHidePropertyButtonYOffset() {
        int[] startYOffset = new int[]{49}; // 基础偏移
        AttachmentPropertyManager.getModifiers().forEach((key, value) -> {
            startYOffset[0] += value.getDiagramsDataSize() * 10;
        });
        
        // 检查是否有爆炸属性：配件属性里有爆炸 OR 枪械本身数据开启了爆炸 OR 缓存中的爆炸数据启用了爆炸
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft != null && minecraft.player != null) {
            Player player = minecraft.player;
            ItemStack gunItem = player.getMainHandItem();
            IGun iGun = IGun.getIGunOrNull(gunItem);
            if (iGun != null) {
                TimelessAPI.getCommonGunIndex(iGun.getGunId(gunItem)).ifPresent(index -> {
                    boolean hasExplosionFromAttachments = AttachmentDataUtils.isExplodeEnabled(gunItem, index.getGunData());
                    boolean hasExplosionFromGun = index.getGunData().getBulletData().getExplosionData() != null && 
                                                 index.getGunData().getBulletData().getExplosionData().isExplode();
                    
                    // 尝试获取缓存中的爆炸数据
                    boolean hasExplosionFromCache = false;
                    IGunOperator operator = IGunOperator.fromLivingEntity(player);
                    if (operator != null) {
                        AttachmentCacheProperty cacheProperty = operator.getCacheProperty();
                        if (cacheProperty != null) {
                            ExplosionData cachedExplosionData = 
                                cacheProperty.getCache(GunProperties.EXPLOSION);
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
        
        // 添加暴击属性显示所需的空间（暴击率+暴击伤害=20像素）
        startYOffset[0] += 20;
        
        return startYOffset[0];
    }
    
    
    /**
     * @author xlxyvergil
     * @reason 完整复制功能并修改弹匣容量显示逻辑，添加后坐力显示
     */
    @Overwrite
    public static void draw(GuiGraphics graphics, Font font, int x, int y) {
        // 计算是否需要显示暴击属性
        boolean showCritAttributes = true; // 始终尝试显示，由工具类处理null情况
        // 使用重写后的方法计算背景高度，与原版保持一致（按钮位置-11）
        // getHidePropertyButtonYOffset已经包含了暴击属性的额外高度
        graphics.fill(x, y, x + 288, y + GunPropertyDiagrams.getHidePropertyButtonYOffset() - 11, 0xAF222222);

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
            
            // 在所有属性后添加爆炸范围和爆炸伤害
            // 检查：配件属性里有爆炸 OR 枪械本身数据开启了爆炸 OR 缓存中的爆炸数据启用了爆炸
            boolean hasExplosionFromAttachments = AttachmentDataUtils.isExplodeEnabled(gunItem, gunData);
            boolean hasExplosionFromGun = gunData.getBulletData().getExplosionData() != null && 
                                       gunData.getBulletData().getExplosionData().isExplode();
            
            // 检查缓存中的爆炸数据是否启用了爆炸
            boolean hasExplosionFromCache = false;
            ExplosionData cachedExplosionData = cacheProperty.getCache(GunProperties.EXPLOSION);
            if (cachedExplosionData != null) {
                hasExplosionFromCache = cachedExplosionData.isExplode();
            }
            
            if (hasExplosionFromAttachments || hasExplosionFromGun || hasExplosionFromCache) {
                // 获取原始爆炸数据
                ExplosionData originalExplosionData = gunData.getBulletData().getExplosionData();
                if (originalExplosionData != null) {
                    // 获取修改后的爆炸数据（如果没有配件修改，则使用原始数据）
                    ExplosionData modifiedExplosionData = cacheProperty.getCache(GunProperties.EXPLOSION);
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
            
            // ========== 枪械伤害显示（整合KuvaLich）==========
            // 必要数据获取
            BulletData bulletData = gunData.getBulletData();
            GunFireModeAdjustData fireModeAdjustData = gunData.getFireModeAdjustData(fireMode);

            // 获取最原始的数值
            float rawDamage = bulletData.getDamageAmount();
            // 额外伤害
            ExtraDamage extraDamage = bulletData.getExtraDamage();
            // 开火模式调整
            // 最终的 base 伤害
            float originalDamage = fireModeAdjustData != null ? fireModeAdjustData.getDamageAmount() : 0f;
            if (extraDamage != null && extraDamage.getDamageAdjust() != null) {
                originalDamage += extraDamage.getDamageAdjust().get(0).getDamage();
            } else {
                originalDamage += rawDamage;
            }
            // 应用基础伤害倍率
            originalDamage *= SyncConfig.DAMAGE_BASE_MULTIPLIER.get();
            
            LinkedList<DistanceDamagePair> taaDamageMod = cacheProperty.getCache(DamageModifier.ID);
            float modifiedDamage = originalDamage;
            if (taaDamageMod != null && !taaDamageMod.isEmpty()) {
                modifiedDamage = taaDamageMod.getFirst().getDamage();
            }
            
            // 整合GunsmithLib
            if (GunsmithLibHelper.isGunsmithLibLoaded()) {
                int shrapnel = Math.max(bulletData.getBulletAmount(), 1);
                modifiedDamage = (float) GunsmithLibHelper.getBulletDamage(gunItem, modifiedDamage, shrapnel);
            }
            
            // 整合KuvaLich: 最终 = 我们计算的 * (1 + gun_damage)
            float kuvaDamageMod = KuvaLichIntegrationHelper.getGunDamageMod(gunItem, player);
            if (kuvaDamageMod != 0) {
                modifiedDamage *= (1 + kuvaDamageMod);
            }
            float damageDiff = modifiedDamage - originalDamage;
            
            double damagePercent = Mth.clamp(originalDamage / 100.0, 0, 1);
            int damageLength = (int) (barStartX + barMaxWidth * damagePercent);
            int damageDiffLength = (int) (barMaxWidth * damageDiff / 100.0);
            
            graphics.drawString(font, Component.translatable("gui.tacz.gun_refit.property_diagrams.damage"), nameTextStartX, yOffset[0], fontColor, false);
            graphics.fill(barStartX, yOffset[0] + 2, barEndX, yOffset[0] + 6, barBackgroundColor);
            graphics.fill(barStartX, yOffset[0] + 2, damageLength, yOffset[0] + 6, barBaseColor);
            if (damageDiff > 0) {
                int barRight = Math.min(damageLength + damageDiffLength, barEndX);
                graphics.fill(damageLength, yOffset[0] + 2, barRight, yOffset[0] + 6, barPositivelyColor);
                graphics.drawString(font, String.format("%.1f §a(+%.1f)", modifiedDamage, damageDiff), valueTextStartX, yOffset[0], fontColor, false);
            } else if (damageDiff < 0) {
                int barLeft = Math.max(damageLength + damageDiffLength, barStartX);
                graphics.fill(barLeft, yOffset[0] + 2, damageLength, yOffset[0] + 6, barNegativeColor);
                graphics.drawString(font, String.format("%.1f §c(%.1f)", modifiedDamage, damageDiff), valueTextStartX, yOffset[0], fontColor, false);
            } else {
                graphics.drawString(font, String.format("%.1f", modifiedDamage), valueTextStartX, yOffset[0], fontColor, false);
            }
            yOffset[0] += 10;
            
            // ========== 爆头伤害显示（整合KuvaLich）==========
            // 额外伤害
            // 开火模式调整
            // 最终的 base
            float originalHeadshot = extraDamage != null ? extraDamage.getHeadShotMultiplier() : 0;
            originalHeadshot = fireModeAdjustData != null ? originalHeadshot + fireModeAdjustData.getHeadShotMultiplier() : originalHeadshot;
            originalHeadshot *= SyncConfig.HEAD_SHOT_BASE_MULTIPLIER.get();
            
            Float taaModifiedHeadshot = cacheProperty.<Float>getCache(HeadShotModifier.ID);
            float modifiedHeadshot = taaModifiedHeadshot != null ? taaModifiedHeadshot : originalHeadshot;
            
            // 整合GunsmithLib
            if (GunsmithLibHelper.isGunsmithLibLoaded()) {
                modifiedHeadshot = (float) GunsmithLibHelper.getHeadshotMultiplier(gunItem, modifiedHeadshot);
            }
            
            // 整合KuvaLich: 最终 = 我们计算的 * (1 + headshot_damage)
            float kuvaHeadshotMod = KuvaLichIntegrationHelper.getHeadshotDamageMod(gunItem, player);
            if (kuvaHeadshotMod != 0) {
                modifiedHeadshot *= (1 + kuvaHeadshotMod);
            }
            float headshotDiff = modifiedHeadshot - originalHeadshot;
            
            double headshotPercent = Mth.clamp(originalHeadshot / 5.0, 0, 1);
            int headshotLength = (int) (barStartX + barMaxWidth * headshotPercent);
            int headshotDiffLength = (int) (barMaxWidth * headshotDiff / 5.0);
            
            graphics.drawString(font, Component.translatable("gui.tacz.gun_refit.property_diagrams.head_shot"), nameTextStartX, yOffset[0], fontColor, false);
            graphics.fill(barStartX, yOffset[0] + 2, barEndX, yOffset[0] + 6, barBackgroundColor);
            graphics.fill(barStartX, yOffset[0] + 2, headshotLength, yOffset[0] + 6, barBaseColor);
            if (headshotDiff > 0) {
                int barRight = Math.min(headshotLength + headshotDiffLength, barEndX);
                graphics.fill(headshotLength, yOffset[0] + 2, barRight, yOffset[0] + 6, barPositivelyColor);
                graphics.drawString(font, String.format("%.2f §a(+%.2f)", modifiedHeadshot, headshotDiff), valueTextStartX, yOffset[0], fontColor, false);
            } else if (headshotDiff < 0) {
                int barLeft = Math.max(headshotLength + headshotDiffLength, barStartX);
                graphics.fill(barLeft, yOffset[0] + 2, headshotLength, yOffset[0] + 6, barNegativeColor);
                graphics.drawString(font, String.format("%.2f §c(%.2f)", modifiedHeadshot, headshotDiff), valueTextStartX, yOffset[0], fontColor, false);
            } else {
                graphics.drawString(font, String.format("%.2f", modifiedHeadshot), valueTextStartX, yOffset[0], fontColor, false);
            }
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
                    gunData.getReloadData().getType() == FeedType.INVENTORY;
                    
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
                                            
                                            // 整合GunsmithLib弹匣容量属性
                                            if (GunsmithLibHelper.isGunsmithLibLoaded()) {
                                                maxAmmoCount = GunsmithLibHelper.getAmmoCapacity(gunItem, maxAmmoCount);
                                            }
                                            
                                            // 整合KuvaLich弹匣容量公式: 最终 = 我们计算的 * (1 + magazine_size)
                                            float kuvaMagazineMod = KuvaLichIntegrationHelper.getMagazineSizeMod(gunItem);
                                            if (kuvaMagazineMod != 0) {
                                                maxAmmoCount = (int) (maxAmmoCount * (1 + kuvaMagazineMod));
                                            }
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
            
            // ========== 射速显示（整合KuvaLich + GunsmithLib）==========
            int originalRpm = gunData.getRoundsPerMinute(fireMode);
            Integer taaModifiedRpm = cacheProperty.<Integer>getCache(RpmModifier.ID);
            int modifiedRpm = taaModifiedRpm != null ? taaModifiedRpm : originalRpm;
            
            // 整合GunsmithLib
            if (GunsmithLibHelper.isGunsmithLibLoaded()) {
                modifiedRpm = (int) Math.round(GunsmithLibHelper.getRpm(gunItem, modifiedRpm));
            }
            
            // 整合KuvaLich: 最终 = 我们计算的 * (1 + firing_rate)
            float kuvaFireRateMod = KuvaLichIntegrationHelper.getFireRateMod(gunItem, player);
            if (kuvaFireRateMod != 0) {
                modifiedRpm = (int) (modifiedRpm * (1 + kuvaFireRateMod));
            }
            int rpmDiff = modifiedRpm - originalRpm;
            
            double rpmPercent = Math.min(originalRpm / 1200.0, 1);
            int rpmLength = (int) (barStartX + barMaxWidth * rpmPercent);
            int rpmDiffLength = (int) (barMaxWidth * rpmDiff / 1200.0);
            
            graphics.drawString(font, Component.translatable("gui.tacz.gun_refit.property_diagrams.rpm"), nameTextStartX, yOffset[0], fontColor, false);
            graphics.fill(barStartX, yOffset[0] + 2, barEndX, yOffset[0] + 6, barBackgroundColor);
            graphics.fill(barStartX, yOffset[0] + 2, rpmLength, yOffset[0] + 6, barBaseColor);
            if (rpmDiff > 0) {
                int barRight = Math.min(rpmLength + rpmDiffLength, barEndX);
                graphics.fill(rpmLength, yOffset[0] + 2, barRight, yOffset[0] + 6, barPositivelyColor);
                graphics.drawString(font, String.format("%drpm §a(+%d)", modifiedRpm, rpmDiff), valueTextStartX, yOffset[0], fontColor, false);
            } else if (rpmDiff < 0) {
                int barLeft = Math.max(rpmLength + rpmDiffLength, barStartX);
                graphics.fill(barLeft, yOffset[0] + 2, rpmLength, yOffset[0] + 6, barNegativeColor);
                graphics.drawString(font, String.format("%drpm §c(%d)", modifiedRpm, rpmDiff), valueTextStartX, yOffset[0], fontColor, false);
            } else {
                graphics.drawString(font, String.format("%drpm", modifiedRpm), valueTextStartX, yOffset[0], fontColor, false);
            }
            yOffset[0] += 10;
            
            // ========== 弹丸速度显示（整合KuvaLich + GunsmithLib）==========
            float originalAmmoSpeed = gunData.getBulletData().getSpeed();
            if (fireModeAdjustData != null) {
                originalAmmoSpeed += fireModeAdjustData.getSpeed();
            }
            Float taaModifiedAmmoSpeed = cacheProperty.<Float>getCache(AmmoSpeedModifier.ID);
            float modifiedAmmoSpeed = taaModifiedAmmoSpeed != null ? taaModifiedAmmoSpeed : originalAmmoSpeed;
            
            // 整合GunsmithLib
            if (GunsmithLibHelper.isGunsmithLibLoaded()) {
                modifiedAmmoSpeed = (float) GunsmithLibHelper.getBulletSpeed(gunItem, modifiedAmmoSpeed);
            }
            
            // 整合KuvaLich: 最终 = 我们计算的 * (1 + projectile_speed)
            float kuvaProjectileSpeedMod = KuvaLichIntegrationHelper.getProjectileSpeedMod(gunItem, player);
            if (kuvaProjectileSpeedMod != 0) {
                modifiedAmmoSpeed *= (1 + kuvaProjectileSpeedMod);
            }
            float ammoSpeedDiff = modifiedAmmoSpeed - originalAmmoSpeed;
            
            double ammoSpeedPercent = Math.min(originalAmmoSpeed / 600.0, 1);
            int ammoSpeedLength = (int) (barStartX + barMaxWidth * ammoSpeedPercent);
            int ammoSpeedDiffLength = (int) (barMaxWidth * ammoSpeedDiff / 600.0);
            
            graphics.drawString(font, Component.translatable("gui.tacz.gun_refit.property_diagrams.ammo_speed"), nameTextStartX, yOffset[0], fontColor, false);
            graphics.fill(barStartX, yOffset[0] + 2, barEndX, yOffset[0] + 6, barBackgroundColor);
            graphics.fill(barStartX, yOffset[0] + 2, ammoSpeedLength, yOffset[0] + 6, barBaseColor);
            if (ammoSpeedDiff > 0) {
                int barRight = Math.min(ammoSpeedLength + ammoSpeedDiffLength, barEndX);
                graphics.fill(ammoSpeedLength, yOffset[0] + 2, barRight, yOffset[0] + 6, barPositivelyColor);
                graphics.drawString(font, String.format("%dm/s §a(+%d)", Math.round(modifiedAmmoSpeed), Math.round(ammoSpeedDiff)), valueTextStartX, yOffset[0], fontColor, false);
            } else if (ammoSpeedDiff < 0) {
                int barLeft = Math.max(ammoSpeedLength + ammoSpeedDiffLength, barStartX);
                graphics.fill(barLeft, yOffset[0] + 2, ammoSpeedLength, yOffset[0] + 6, barNegativeColor);
                graphics.drawString(font, String.format("%dm/s §c(%d)", Math.round(modifiedAmmoSpeed), Math.round(ammoSpeedDiff)), valueTextStartX, yOffset[0], fontColor, false);
            } else {
                graphics.drawString(font, String.format("%dm/s", Math.round(modifiedAmmoSpeed)), valueTextStartX, yOffset[0], fontColor, false);
            }
            yOffset[0] += 10;
            
            // ========== 装填时间显示 ==========
            float originalReloadTime = 0.0f;
            if (gunData.getReloadData() != null && gunData.getReloadData().getFeed() != null) {
                originalReloadTime = gunData.getReloadData().getFeed().getTacticalTime();
            }
            Float reloadInverseMultiplier = cacheProperty.<Float>getCache(ReloadModifier.ID);
            float reloadMultiplier = reloadInverseMultiplier != null ? (1.0f / reloadInverseMultiplier) : 1.0f;
            float modifiedReloadTime = originalReloadTime / reloadMultiplier;
            // 整合KuvaLich装填速度公式: 最终 = 我们计算的 / (1 + reload_speed)
            float kuvaReloadSpeedMod = KuvaLichIntegrationHelper.getReloadSpeedMod(gunItem, player);
            if (kuvaReloadSpeedMod != 0) {
                modifiedReloadTime /= (1 + kuvaReloadSpeedMod);
            }
            float reloadDiff = modifiedReloadTime - originalReloadTime;
            
            double reloadPercent = Math.min(originalReloadTime / 5.0, 1);
            int reloadLength = (int) (barStartX + barMaxWidth * reloadPercent);
            int reloadDiffLength = (int) (barMaxWidth * reloadDiff / 5.0);
            
            graphics.drawString(font, Component.translatable("gui.tacz.gun_refit.property_diagrams.reload_time"), nameTextStartX, yOffset[0], fontColor, false);
            graphics.fill(barStartX, yOffset[0] + 2, barEndX, yOffset[0] + 6, barBackgroundColor);
            graphics.fill(barStartX, yOffset[0] + 2, reloadLength, yOffset[0] + 6, barBaseColor);
            if (reloadDiff < 0) {
                int barRight = Math.min(reloadLength + reloadDiffLength, barEndX);
                graphics.fill(reloadLength, yOffset[0] + 2, barRight, yOffset[0] + 6, barPositivelyColor);
                graphics.drawString(font, String.format("%.2fs §a(%.2f)", modifiedReloadTime, reloadDiff), valueTextStartX, yOffset[0], fontColor, false);
            } else if (reloadDiff > 0) {
                int barLeft = Math.max(reloadLength + reloadDiffLength, barStartX);
                graphics.fill(barLeft, yOffset[0] + 2, reloadLength, yOffset[0] + 6, barNegativeColor);
                graphics.drawString(font, String.format("%.2fs §c(+%.2f)", modifiedReloadTime, reloadDiff), valueTextStartX, yOffset[0], fontColor, false);
            } else {
                graphics.drawString(font, String.format("%.2fs", modifiedReloadTime), valueTextStartX, yOffset[0], fontColor, false);
            }
            yOffset[0] += 10;
            
            // ========== 瞄准速度显示（整合KuvaLich）==========
            float originalAdsTime = gunData.getAimTime();
            Float taaModifiedAdsTime = cacheProperty.<Float>getCache(AdsModifier.ID);
            float modifiedAdsTime = taaModifiedAdsTime != null ? taaModifiedAdsTime : originalAdsTime;
            // 整合KuvaLich: 最终 = 我们计算的 / (1 + aim_time)
            float kuvaAimTimeMod = KuvaLichIntegrationHelper.getAimTimeMod(gunItem, player);
            if (kuvaAimTimeMod != 0) {
                modifiedAdsTime /= (1 + kuvaAimTimeMod);
            }
            float adsDiff = modifiedAdsTime - originalAdsTime;
            
            double adsPercent = Mth.clamp(originalAdsTime / 1.0, 0, 1);
            int adsLength = (int) (barStartX + barMaxWidth * adsPercent);
            int adsDiffLength = (int) (barMaxWidth * adsDiff / 1.0);
            
            graphics.drawString(font, Component.translatable("gui.tacz.gun_refit.property_diagrams.ads"), nameTextStartX, yOffset[0], fontColor, false);
            graphics.fill(barStartX, yOffset[0] + 2, barEndX, yOffset[0] + 6, barBackgroundColor);
            graphics.fill(barStartX, yOffset[0] + 2, adsLength, yOffset[0] + 6, barBaseColor);
            if (adsDiff < 0) {
                int barRight = Math.min(adsLength + adsDiffLength, barEndX);
                graphics.fill(adsLength, yOffset[0] + 2, barRight, yOffset[0] + 6, barPositivelyColor);
                graphics.drawString(font, String.format("%.2fs §a(%.2f)", modifiedAdsTime, adsDiff), valueTextStartX, yOffset[0], fontColor, false);
            } else if (adsDiff > 0) {
                int barLeft = Math.max(adsLength + adsDiffLength, barStartX);
                graphics.fill(barLeft, yOffset[0] + 2, adsLength, yOffset[0] + 6, barNegativeColor);
                graphics.drawString(font, String.format("%.2fs §c(+%.2f)", modifiedAdsTime, adsDiff), valueTextStartX, yOffset[0], fontColor, false);
            } else {
                graphics.drawString(font, String.format("%.2fs", modifiedAdsTime), valueTextStartX, yOffset[0], fontColor, false);
            }
            yOffset[0] += 10;
            
            // ========== 精准度显示（整合KuvaLich）==========
            // 腰射扩散
            float originalInaccuracy = gunData.getInaccuracy(InaccuracyType.STAND);
            if (fireModeAdjustData != null) {
                originalInaccuracy += fireModeAdjustData.getOtherInaccuracy();
            }
            
            Map<InaccuracyType, Float> taaInaccuracyMod = cacheProperty.getCache(InaccuracyModifier.ID);
            float modifiedInaccuracy = originalInaccuracy;
            if (taaInaccuracyMod != null && taaInaccuracyMod.containsKey(InaccuracyType.STAND)) {
                modifiedInaccuracy = taaInaccuracyMod.get(InaccuracyType.STAND);
            }
            // 整合KuvaLich: 最终 = 我们计算的 * (1 - accuracy)
            float kuvaAccuracyMod = KuvaLichIntegrationHelper.getAccuracyMod(gunItem, player);
            if (kuvaAccuracyMod != 0) {
                modifiedInaccuracy *= (1 - kuvaAccuracyMod);
            }
            float inaccuracyDiff = modifiedInaccuracy - originalInaccuracy;
            
            double inaccuracyPercent = Mth.clamp(originalInaccuracy / 10.0, 0, 1);
            int inaccuracyLength = (int) (barStartX + barMaxWidth * inaccuracyPercent);
            int inaccuracyDiffLength = (int) (barMaxWidth * inaccuracyDiff / 10.0);
            
            graphics.drawString(font, Component.translatable("gui.tacz.gun_refit.property_diagrams.aim_inaccuracy"), nameTextStartX, yOffset[0], fontColor, false);
            graphics.fill(barStartX, yOffset[0] + 2, barEndX, yOffset[0] + 6, barBackgroundColor);
            graphics.fill(barStartX, yOffset[0] + 2, inaccuracyLength, yOffset[0] + 6, barBaseColor);
            if (inaccuracyDiff < 0) {
                int barRight = Math.min(inaccuracyLength + inaccuracyDiffLength, barEndX);
                graphics.fill(inaccuracyLength, yOffset[0] + 2, barRight, yOffset[0] + 6, barPositivelyColor);
                graphics.drawString(font, String.format("%.2f §a(%.2f)", modifiedInaccuracy, inaccuracyDiff), valueTextStartX, yOffset[0], fontColor, false);
            } else if (inaccuracyDiff > 0) {
                int barLeft = Math.max(inaccuracyLength + inaccuracyDiffLength, barStartX);
                graphics.fill(barLeft, yOffset[0] + 2, inaccuracyLength, yOffset[0] + 6, barNegativeColor);
                graphics.drawString(font, String.format("%.2f §c(+%.2f)", modifiedInaccuracy, inaccuracyDiff), valueTextStartX, yOffset[0], fontColor, false);
            } else {
                graphics.drawString(font, String.format("%.2f", modifiedInaccuracy), valueTextStartX, yOffset[0], fontColor, false);
            }
            yOffset[0] += 10;
            
            // ========== 后坐力显示（整合KuvaLich）==========
            // 获取缓存中的后坐力数据（包含TACZ配件修改）
            ParameterizedCachePair<Float, Float> recoilData = cacheProperty.getCache(GunProperties.RECOIL);
            
            // 获取玩家实体属性
            EntityAttributeHelper entityAttribute = new EntityAttributeHelper(player, "");
            // 计算方式：综合属性 × 细分属性（乘法叠加）
            float recoilPitchFactor = (float) (entityAttribute.getRecoil() * entityAttribute.getRecoilPitch());
            float recoilYawFactor = (float) (entityAttribute.getRecoil() * entityAttribute.getRecoilYaw());

            // 获取原始后坐力数据用于计算差异
            GunRecoil recoil = gunData.getRecoil();
            if (recoil != null) {
                // 获取原始后坐力值
                float originalPitch = getMaxInGunRecoilKeyFrame(recoil.getPitch());
                float originalYaw = getMaxInGunRecoilKeyFrame(recoil.getYaw());

                // 获取配件修改后的值
                float attachmentModifiedPitch = recoilData != null && recoilData.left() != null ?
                    (float) recoilData.left().eval(originalPitch) : originalPitch;
                float attachmentModifiedYaw = recoilData != null && recoilData.right() != null ?
                    (float) recoilData.right().eval(originalYaw) : originalYaw;

                // 应用玩家属性修改（在配件基础上）- 使用拆分后的计算方式
                float finalPitch = attachmentModifiedPitch * recoilPitchFactor;
                float finalYaw = attachmentModifiedYaw * recoilYawFactor;

                // 整合GunsmithLib后坐力属性
                if (GunsmithLibHelper.isGunsmithLibLoaded()) {
                    finalPitch = (float) GunsmithLibHelper.getVRecoil(gunItem, finalPitch);
                    finalYaw = (float) GunsmithLibHelper.getHRecoil(gunItem, finalYaw);
                }

                // 整合KuvaLich后坐力减少公式: 最终 = 我们计算的 * (1 - recoil_reduction)
                float kuvaRecoilReduction = KuvaLichIntegrationHelper.getRecoilReductionMod(gunItem, player);
                if (kuvaRecoilReduction != 0) {
                    finalPitch *= (1 - kuvaRecoilReduction);
                    finalYaw *= (1 - kuvaRecoilReduction);
                }
                
                // 计算总差值（相对于原始值）
                float pitchDifference = finalPitch - originalPitch;
                float yawDifference = finalYaw - originalYaw;
                
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
                    graphics.drawString(font, String.format("%.2f §c(+%.2f)", finalPitch, pitchDifference), valueTextStartX, yOffset[0], fontColor, false);
                } else if (pitchDifference < 0) {
                    // 后坐力减少显示为绿色（好）
                    int barLeft = Math.max(pitchLength + pitchModifierLength, barStartX);
                    graphics.fill(barLeft, yOffset[0] + 2, pitchLength, yOffset[0] + 6, barPositivelyColor);
                    graphics.drawString(font, String.format("%.2f §a(%.2f)", finalPitch, pitchDifference), valueTextStartX, yOffset[0], fontColor, false);
                } else {
                    graphics.drawString(font, String.format("%.2f", finalPitch), valueTextStartX, yOffset[0], fontColor, false);
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
                    graphics.drawString(font, String.format("%.2f §c(+%.2f)", finalYaw, yawDifference), valueTextStartX, yOffset[0], fontColor, false);
                } else if (yawDifference < 0) {
                    // 后坐力减少显示为绿色（好）
                    int barLeft = Math.max(yawLength + yawModifierLength, barStartX);
                    graphics.fill(barLeft, yOffset[0] + 2, yawLength, yOffset[0] + 6, barPositivelyColor);
                    graphics.drawString(font, String.format("%.2f §a(%.2f)", finalYaw, yawDifference), valueTextStartX, yOffset[0], fontColor, false);
                } else {
                    graphics.drawString(font, String.format("%.2f", finalYaw), valueTextStartX, yOffset[0], fontColor, false);
                }
                
                yOffset[0] += 10;
            }
            
            // ========== 子弹数量显示 ==========
            // 获取原始子弹数量
            int barrelBulletAmount = (iGun.hasBulletInBarrel(gunItem) && gunData.getBolt() != Bolt.OPEN_BOLT) ? 1 : 0;
            int originalBulletCount = gunData.getBulletData().getBulletAmount();
            if (originalBulletCount <= 0) {
                originalBulletCount = 1;
            }
            int ammoAmount = originalBulletCount + barrelBulletAmount;
            // 获取缓存中的子弹数量（已包含配件加成）
            Integer cachedBulletCount = cacheProperty.<Integer>getCache(BulletCountModifier.ID);
            int displayBulletCount;
            if (cachedBulletCount != null) {
                displayBulletCount = cachedBulletCount + barrelBulletAmount;
            } else {
                displayBulletCount = ammoAmount;
            }
            // 整合KuvaLich多重射击公式: 最终 = 我们计算的 * (1 + multishot)
            float kuvaMultishotMod = KuvaLichIntegrationHelper.getMultishotMod(gunItem, player);
            if (kuvaMultishotMod != 0) {
                displayBulletCount = (int) (displayBulletCount * (1 + kuvaMultishotMod));
            }
            int bulletCountDiff = displayBulletCount - ammoAmount;
            
            double bulletCountPercent = Math.min(ammoAmount / 10.0, 1);
            int bulletCountLength = (int) (barStartX + barMaxWidth * bulletCountPercent);
            int bulletCountDiffLength = (int) (barMaxWidth * bulletCountDiff / 10.0);
            
            graphics.drawString(font, Component.translatable("gui.tacz.gun_refit.property_diagrams.bullet_count"), nameTextStartX, yOffset[0], fontColor, false);
            graphics.fill(barStartX, yOffset[0] + 2, barEndX, yOffset[0] + 6, barBackgroundColor);
            graphics.fill(barStartX, yOffset[0] + 2, bulletCountLength, yOffset[0] + 6, barBaseColor);
            if (bulletCountDiff > 0) {
                int barRight = Math.min(bulletCountLength + bulletCountDiffLength, barEndX);
                graphics.fill(bulletCountLength, yOffset[0] + 2, barRight, yOffset[0] + 6, barPositivelyColor);
                graphics.drawString(font, String.format("%d §a(+%d)", displayBulletCount, bulletCountDiff), valueTextStartX, yOffset[0], fontColor, false);
            } else if (bulletCountDiff < 0) {
                int barLeft = Math.max(bulletCountLength + bulletCountDiffLength, barStartX);
                graphics.fill(barLeft, yOffset[0] + 2, bulletCountLength, yOffset[0] + 6, barNegativeColor);
                graphics.drawString(font, String.format("%d §c(%d)", displayBulletCount, bulletCountDiff), valueTextStartX, yOffset[0], fontColor, false);
            } else {
                graphics.drawString(font, String.format("%d", displayBulletCount), valueTextStartX, yOffset[0], fontColor, false);
            }
            yOffset[0] += 10;
            
            // ========== 穿甲倍率显示（整合GunsmithLib）==========
            // 获取原始穿甲值
            float originalArmorIgnore = gunData.getBulletData().getExtraDamage() != null ? 
                gunData.getBulletData().getExtraDamage().getArmorIgnore() : 0f;
            GunFireModeAdjustData fireModeAdjustDataForAp = gunData.getFireModeAdjustData(fireMode);
            if (fireModeAdjustDataForAp != null) {
                originalArmorIgnore += fireModeAdjustDataForAp.getArmorIgnore();
            }
            originalArmorIgnore *= SyncConfig.ARMOR_IGNORE_BASE_MULTIPLIER.get();
            
            // 获取缓存中的穿甲值（包含配件修改）
            Float taaModifiedArmorIgnore = cacheProperty.<Float>getCache(ArmorIgnoreModifier.ID);
            float modifiedArmorIgnore = taaModifiedArmorIgnore != null ? taaModifiedArmorIgnore : originalArmorIgnore;
            
            // 整合GunsmithLib
            if (GunsmithLibHelper.isGunsmithLibLoaded()) {
                modifiedArmorIgnore = (float) GunsmithLibHelper.getArmorPiercingRatio(gunItem, modifiedArmorIgnore);
            }
            
            float armorIgnoreDiff = modifiedArmorIgnore - originalArmorIgnore;
            
            double armorIgnorePercent = Mth.clamp(originalArmorIgnore, 0, 1);
            int armorIgnoreLength = (int) (barStartX + barMaxWidth * armorIgnorePercent);
            int armorIgnoreDiffLength = (int) (barMaxWidth * armorIgnoreDiff);
            
            graphics.drawString(font, Component.translatable("gui.tacz.gun_refit.property_diagrams.armor_ignore"), nameTextStartX, yOffset[0], fontColor, false);
            graphics.fill(barStartX, yOffset[0] + 2, barEndX, yOffset[0] + 6, barBackgroundColor);
            graphics.fill(barStartX, yOffset[0] + 2, armorIgnoreLength, yOffset[0] + 6, barBaseColor);
            if (armorIgnoreDiff > 0) {
                int barRight = Math.min(armorIgnoreLength + armorIgnoreDiffLength, barEndX);
                graphics.fill(armorIgnoreLength, yOffset[0] + 2, barRight, yOffset[0] + 6, barPositivelyColor);
                graphics.drawString(font, String.format("%.1f%% §a(+%.1f%%)", modifiedArmorIgnore * 100, armorIgnoreDiff * 100), valueTextStartX, yOffset[0], fontColor, false);
            } else if (armorIgnoreDiff < 0) {
                int barLeft = Math.max(armorIgnoreLength + armorIgnoreDiffLength, barStartX);
                graphics.fill(barLeft, yOffset[0] + 2, armorIgnoreLength, yOffset[0] + 6, barNegativeColor);
                graphics.drawString(font, String.format("%.1f%% §c(%.1f%%)", modifiedArmorIgnore * 100, armorIgnoreDiff * 100), valueTextStartX, yOffset[0], fontColor, false);
            } else {
                graphics.drawString(font, String.format("%.1f%%", modifiedArmorIgnore * 100), valueTextStartX, yOffset[0], fontColor, false);
            }
            yOffset[0] += 10;
            
            // ========== 暴击属性显示 ==========
            if (showCritAttributes) {
                ApothicAttributesHelper.CritAttributeData critChanceData = ApothicAttributesHelper.getCritChanceData(player);
                ApothicAttributesHelper.CritAttributeData critDamageData = ApothicAttributesHelper.getCritDamageData(player);
                
                if (critChanceData != null) {
                    String critChanceName = AttributeConfig.getCritChanceName();
                    
                    // 暴击率条：使用最终值计算进度条长度
                    double critChancePercent = Mth.clamp(critChanceData.modifiedValue / (critChanceData.isDecimalFormat ? 1.0 : 1000.0), 0, 1);
                    int critChanceLength = (int) (barStartX + barMaxWidth * critChancePercent);
                    int critChanceBaseLength = (int) (barStartX + barMaxWidth * Mth.clamp(critChanceData.baseValue / (critChanceData.isDecimalFormat ? 1.0 : 1000.0), 0, 1));
                    
                    graphics.drawString(font, Component.literal(critChanceName), nameTextStartX, yOffset[0], fontColor, false);
                    graphics.fill(barStartX, yOffset[0] + 2, barEndX, yOffset[0] + 6, barBackgroundColor);
                    graphics.fill(barStartX, yOffset[0] + 2, critChanceBaseLength, yOffset[0] + 6, barBaseColor);
                    
                    if (critChanceData.difference > 0) {
                        int barRight = Math.min(critChanceLength, barEndX);
                        graphics.fill(critChanceBaseLength, yOffset[0] + 2, barRight, yOffset[0] + 6, barPositivelyColor);
                        graphics.drawString(font, critChanceData.formatValue(critChanceData.modifiedValue) + " §a(+" + critChanceData.formatValue(critChanceData.difference) + ")", valueTextStartX, yOffset[0], fontColor, false);
                    } else if (critChanceData.difference < 0) {
                        int barLeft = Math.max(critChanceLength, barStartX);
                        graphics.fill(barLeft, yOffset[0] + 2, critChanceBaseLength, yOffset[0] + 6, barNegativeColor);
                        graphics.drawString(font, critChanceData.formatValue(critChanceData.modifiedValue) + " §c(" + critChanceData.formatValue(critChanceData.difference) + ")", valueTextStartX, yOffset[0], fontColor, false);
                    } else {
                        graphics.drawString(font, critChanceData.formatValue(critChanceData.modifiedValue), valueTextStartX, yOffset[0], fontColor, false);
                    }
                    yOffset[0] += 10;
                }
                
                if (critDamageData != null) {
                    String critDamageName = AttributeConfig.getCritDamageName();
                    
                    // 暴击伤害条：使用最终值计算进度条长度
                    double critDamagePercent = Mth.clamp(critDamageData.modifiedValue / (critDamageData.isDecimalFormat ? 100.0 : 1000.0), 0, 1);
                    int critDamageLength = (int) (barStartX + barMaxWidth * critDamagePercent);
                    int critDamageBaseLength = (int) (barStartX + barMaxWidth * Mth.clamp(critDamageData.baseValue / (critDamageData.isDecimalFormat ? 100.0 : 1000.0), 0, 1));
                    
                    graphics.drawString(font, Component.literal(critDamageName), nameTextStartX, yOffset[0], fontColor, false);
                    graphics.fill(barStartX, yOffset[0] + 2, barEndX, yOffset[0] + 6, barBackgroundColor);
                    graphics.fill(barStartX, yOffset[0] + 2, critDamageBaseLength, yOffset[0] + 6, barBaseColor);
                    
                    if (critDamageData.difference > 0) {
                        int barRight = Math.min(critDamageLength, barEndX);
                        graphics.fill(critDamageBaseLength, yOffset[0] + 2, barRight, yOffset[0] + 6, barPositivelyColor);
                        graphics.drawString(font, critDamageData.formatValue(critDamageData.modifiedValue) + " §a(+" + critDamageData.formatValue(critDamageData.difference) + ")", valueTextStartX, yOffset[0], fontColor, false);
                    } else if (critDamageData.difference < 0) {
                        int barLeft = Math.max(critDamageLength, barStartX);
                        graphics.fill(barLeft, yOffset[0] + 2, critDamageBaseLength, yOffset[0] + 6, barNegativeColor);
                        graphics.drawString(font, critDamageData.formatValue(critDamageData.modifiedValue) + " §c(" + critDamageData.formatValue(critDamageData.difference) + ")", valueTextStartX, yOffset[0], fontColor, false);
                    } else {
                        graphics.drawString(font, critDamageData.formatValue(critDamageData.modifiedValue), valueTextStartX, yOffset[0], fontColor, false);
                    }
                    yOffset[0] += 10;
                }
            }
            
            // ========== 绘制剩下的 modifier 属性 ==========
            // 跳过后坐力和已经由我们绘制的 modifier，剩下的在这里绘制
            AttachmentPropertyManager.getModifiers().forEach((key, value) -> {
                // 跳过后坐力modifier
                if (RecoilModifier.ID.equals(key)) {
                    return;
                }
                // 跳过需要KuvaLich/GunsmithLib整合的modifier，由我们自己重新绘制
                if (DamageModifier.ID.equals(key) ||
                    RpmModifier.ID.equals(key) ||
                    InaccuracyModifier.ID.equals(key) ||
                    AdsModifier.ID.equals(key) ||
                    HeadShotModifier.ID.equals(key) ||
                    AmmoSpeedModifier.ID.equals(key) ||
                    ArmorIgnoreModifier.ID.equals(key)) {
                    return;
                }
                value.getPropertyDiagramsData(gunItem, gunData, cacheProperty).forEach(data -> {
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
                });
            });
            
            // ========== 近战伤害显示 ==========
            // 参考 TACZ 原版 doMelee 计算逻辑
            float otherModifiers = 0f;
            LocalPlayer player1 = Minecraft.getInstance().player;
            if (player1 != null && player1.getAttribute(Attributes.ATTACK_DAMAGE) != null) {
                var instance = player1.getAttribute(Attributes.ATTACK_DAMAGE);
                otherModifiers = (float) (instance.getValue() - instance.getBaseValue());
            }
            GunMeleeData meleeData = gunData.getMeleeData();
            float baseModifierDamage = 0f;
            if (meleeData != null && meleeData.getDefaultMeleeData() != null) {
                baseModifierDamage = meleeData.getDefaultMeleeData().getDamage();
            }
            Float finalModifierDamage = cacheProperty.getCache(MeleeDamageModifier.ID);
            if (finalModifierDamage == null) {
                finalModifierDamage = baseModifierDamage;
            }
            float baseTotalDamage = otherModifiers + baseModifierDamage;
            float finalTotalDamage = otherModifiers + finalModifierDamage;
            float meleeDamageDiff = finalTotalDamage - baseTotalDamage;
            
            double meleeDamagePercent = Math.min(finalTotalDamage / 20.0, 1);
            int meleeDamageLength = (int) (barStartX + barMaxWidth * meleeDamagePercent);
            int meleeDamageDiffLength = (int) (barMaxWidth * meleeDamageDiff / 20.0);
            
            graphics.drawString(font, Component.translatable("gui.tacz.gun_refit.property_diagrams.melee_damage"), nameTextStartX, yOffset[0], fontColor, false);
            graphics.fill(barStartX, yOffset[0] + 2, barEndX, yOffset[0] + 6, barBackgroundColor);
            graphics.fill(barStartX, yOffset[0] + 2, meleeDamageLength, yOffset[0] + 6, barBaseColor);
            if (meleeDamageDiff > 0) {
                int barRight = Math.min(meleeDamageLength + meleeDamageDiffLength, barEndX);
                graphics.fill(meleeDamageLength, yOffset[0] + 2, barRight, yOffset[0] + 6, barPositivelyColor);
                graphics.drawString(font, String.format("%.1f §a(+%.1f)", finalTotalDamage, meleeDamageDiff), valueTextStartX, yOffset[0], fontColor, false);
            } else if (meleeDamageDiff < 0) {
                int barLeft = Math.max(meleeDamageLength + meleeDamageDiffLength, barStartX);
                graphics.fill(barLeft, yOffset[0] + 2, meleeDamageLength, yOffset[0] + 6, barNegativeColor);
                graphics.drawString(font, String.format("%.1f §c(%.1f)", finalTotalDamage, meleeDamageDiff), valueTextStartX, yOffset[0], fontColor, false);
            } else {
                graphics.drawString(font, String.format("%.1f", finalTotalDamage), valueTextStartX, yOffset[0], fontColor, false);
            }
            yOffset[0] += 10;
            
            // ========== 近战距离显示 ==========
            float baseDistance = meleeData != null ? meleeData.getDistance() : 0.0f;
            Float modifiedDistance = cacheProperty.getCache(MeleeModifier.ID);
            if (modifiedDistance == null) {
                modifiedDistance = baseDistance;
            }
            float meleeDistanceDiff = modifiedDistance - baseDistance;
            
            double meleeDistancePercent = Math.min(baseDistance / 5.0, 1);
            int meleeDistanceLength = (int) (barStartX + barMaxWidth * meleeDistancePercent);
            int meleeDistanceDiffLength = (int) (barMaxWidth * meleeDistanceDiff / 5.0);
            
            graphics.drawString(font, Component.translatable("gui.tacz.gun_refit.property_diagrams.melee_distance"), nameTextStartX, yOffset[0], fontColor, false);
            graphics.fill(barStartX, yOffset[0] + 2, barEndX, yOffset[0] + 6, barBackgroundColor);
            graphics.fill(barStartX, yOffset[0] + 2, meleeDistanceLength, yOffset[0] + 6, barBaseColor);
            if (meleeDistanceDiff > 0) {
                int barRight = Math.min(meleeDistanceLength + meleeDistanceDiffLength, barEndX);
                graphics.fill(meleeDistanceLength, yOffset[0] + 2, barRight, yOffset[0] + 6, barPositivelyColor);
                graphics.drawString(font, String.format("%.2fm §a(+%.2fm)", modifiedDistance, meleeDistanceDiff), valueTextStartX, yOffset[0], fontColor, false);
            } else if (meleeDistanceDiff < 0) {
                int barLeft = Math.max(meleeDistanceLength + meleeDistanceDiffLength, barStartX);
                graphics.fill(barLeft, yOffset[0] + 2, meleeDistanceLength, yOffset[0] + 6, barNegativeColor);
                graphics.drawString(font, String.format("%.2fm §c(%.2fm)", modifiedDistance, meleeDistanceDiff), valueTextStartX, yOffset[0], fontColor, false);
            } else {
                graphics.drawString(font, String.format("%.2fm", modifiedDistance), valueTextStartX, yOffset[0], fontColor, false);
            }
            yOffset[0] += 10;
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