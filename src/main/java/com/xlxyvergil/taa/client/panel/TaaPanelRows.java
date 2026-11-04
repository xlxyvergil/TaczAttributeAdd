package com.xlxyvergil.taa.client.panel;

import com.tacz.guns.api.GunProperties;
import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.api.item.gun.FireMode;
import com.tacz.guns.api.modifier.ParameterizedCachePair;
import com.tacz.guns.config.sync.SyncConfig;
import com.tacz.guns.resource.modifier.AttachmentCacheProperty;
import com.tacz.guns.resource.modifier.custom.AdsModifier;
import com.tacz.guns.resource.modifier.custom.AmmoSpeedModifier;
import com.tacz.guns.resource.modifier.custom.ArmorIgnoreModifier;
import com.tacz.guns.resource.modifier.custom.DamageModifier;
import com.tacz.guns.resource.modifier.custom.HeadShotModifier;
import com.tacz.guns.resource.modifier.custom.InaccuracyModifier;
import com.tacz.guns.resource.modifier.custom.RecoilModifier;
import com.tacz.guns.resource.modifier.custom.RpmModifier;
import com.tacz.guns.resource.pojo.data.gun.BulletData;
import com.tacz.guns.resource.pojo.data.gun.ExplosionData;
import com.tacz.guns.resource.pojo.data.gun.ExtraDamage;
import com.tacz.guns.resource.pojo.data.gun.ExtraDamage.DistanceDamagePair;
import com.tacz.guns.resource.pojo.data.gun.GunData;
import com.tacz.guns.resource.pojo.data.gun.GunFireModeAdjustData;
import com.tacz.guns.resource.pojo.data.gun.GunHeatData;
import com.tacz.guns.resource.pojo.data.gun.GunMeleeData;
import com.tacz.guns.resource.pojo.data.gun.GunRecoil;
import com.tacz.guns.resource.pojo.data.gun.GunRecoilKeyFrame;
import com.tacz.guns.resource.pojo.data.gun.InaccuracyType;
import com.xlxyvergil.taa.attribute.EntityAttributeRegistry;
import com.xlxyvergil.taa.compat.kubejs.KubeJSEventHelper;
import com.xlxyvergil.taa.config.AttributeConfig;
import com.xlxyvergil.taa.util.ApothicAttributesHelper;
import com.xlxyvergil.taa.util.EntityAttributeHelper;
import com.xlxyvergil.taa.util.GunsmithLibHelper;
import com.xlxyvergil.taa.util.HeatAttributeHelper;
import com.xlxyvergil.taa.util.KuvaLichIntegrationHelper;
import com.xlxyvergil.taa.util.PropertyCalculator;
import com.xlxyvergil.taa.util.ReloadTimeScope;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.ModList;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.tacz.guns.api.modifier.IAttachmentModifier.DiagramsData;

/**
 * 改装界面的属性行数据源。
 * <p>
 * 面板由 TACZ 的 {@code GunPropertyDiagrams} 绘制，本类只产出每行的「基准百分比 / 差值百分比 / 文案」：
 * <ul>
 *     <li>本模组独有的行（爆炸、装填、暴击、近战、过热）由 {@link TaaPanelModifier} 注册进
 *     {@code AttachmentPropertyManager}，TACZ 遍历注册表时自然画出来，无需覆写 draw。</li>
 *     <li>TACZ 自有的行（伤害、爆头、射速、弹丸速度、瞄准、精准度、穿甲、后坐力）由
 *     {@code GunPropertyDiagramsMixin} 在 {@code getPropertyDiagramsData} 返回处调用 {@link #rewrite}
 *     改写数值，补上 KuvaLich / GunsmithLib / KubeJS 的影响。</li>
 * </ul>
 * 所有行都走 {@link #row} 构造，保证与 TACZ 的口径一致：基准条长 = barMaxWidth * defaultPercent，
 * 差值条长 = barMaxWidth * modifierPercent（TACZ 侧会把最终长度夹在条区范围内）。
 */
@OnlyIn(Dist.CLIENT)
public final class TaaPanelRows {

    // TACZ 自有的 lang key（用于 rewrite 时按行分派）
    private static final String KEY_DAMAGE = "gui.tacz.gun_refit.property_diagrams.damage";
    private static final String KEY_HEAD_SHOT = "gui.tacz.gun_refit.property_diagrams.head_shot";
    private static final String KEY_RPM = "gui.tacz.gun_refit.property_diagrams.rpm";
    private static final String KEY_AMMO_SPEED = "gui.tacz.gun_refit.property_diagrams.ammo_speed";
    private static final String KEY_ADS = "gui.tacz.gun_refit.property_diagrams.ads";
    private static final String KEY_HIPFIRE_INACCURACY = "gui.tacz.gun_refit.property_diagrams.hipfire_inaccuracy";
    private static final String KEY_SNEAK_INACCURACY = "gui.tacz.gun_refit.property_diagrams.sneak_inaccuracy";
    private static final String KEY_LIE_INACCURACY = "gui.tacz.gun_refit.property_diagrams.lie_inaccuracy";
    private static final String KEY_AIM_INACCURACY = "gui.tacz.gun_refit.property_diagrams.aim_inaccuracy";
    private static final String KEY_ARMOR_IGNORE = "gui.tacz.gun_refit.property_diagrams.armor_ignore";
    private static final String KEY_PITCH = "gui.tacz.gun_refit.property_diagrams.pitch";
    private static final String KEY_YAW = "gui.tacz.gun_refit.property_diagrams.yaw";

    // 本模组独有的 lang key；TACZ 的语料里没有对应条目，Component.translatable 会回落到原文
    public static final String TITLE_EXPLOSION_RADIUS = "gui.tacz.gun_refit.property_diagrams.explosion_radius";
    public static final String TITLE_EXPLOSION_DAMAGE = "gui.tacz.gun_refit.property_diagrams.explosion_damage";
    public static final String TITLE_RELOAD = "gui.tacz.gun_refit.property_diagrams.reload_time";
    public static final String TITLE_MELEE_DAMAGE = "gui.tacz.gun_refit.property_diagrams.melee_damage";
    public static final String TITLE_MELEE_DISTANCE = "gui.tacz.gun_refit.property_diagrams.melee_distance";
    public static final String TITLE_HEAT_MAX = "gui.tacz.gun_refit.property_diagrams.heat_max";
    public static final String TITLE_HEAT_COOLING = "gui.tacz.gun_refit.property_diagrams.heat_cooling";
    public static final String TITLE_HEAT_COOLING_DELAY = "gui.tacz.gun_refit.property_diagrams.heat_cooling_delay";
    public static final String TITLE_HEAT_OVERHEAT_TIME = "gui.tacz.gun_refit.property_diagrams.heat_overheat_time";

    // ===== TACZ 自有行的条长基准，与各 Modifier 的 getPropertyDiagramsData 一致 =====
    private static final double SCALE_DAMAGE = 50.0;
    private static final double SCALE_HEAD_SHOT = 5.0;
    private static final double SCALE_RPM = 1200.0;
    private static final double SCALE_AMMO_SPEED = 600.0;
    private static final double SCALE_ADS = 0.5;
    private static final double SCALE_INACCURACY = 10.0;
    private static final double SCALE_RECOIL = 5.0;

    // ===== 本模组独有行的条长基准 =====
    private static final double SCALE_EXPLOSION_RADIUS = 5.0;
    private static final double SCALE_EXPLOSION_DAMAGE = 100.0;
    private static final double SCALE_RELOAD = 5.0;
    private static final double SCALE_MELEE_DAMAGE = 20.0;
    private static final double SCALE_MELEE_DISTANCE = 5.0;
    private static final double SCALE_HEAT_MAX = 500.0;
    private static final double SCALE_HEAT_COOLING = 3.0;
    private static final double SCALE_HEAT_DELAY_MS = 2000.0;
    private static final double SCALE_HEAT_LOCK_MS = 5000.0;

    private TaaPanelRows() {
    }

    // ==================== 本模组独有的行 ====================

    /**
     * 爆炸范围 + 爆炸伤害，常驻显示。
     * 与 TACZ ExplosionModifier.initCache 的兜底一致：枪械没配爆炸数据时用默认值（半径 0.5、伤害 2），
     * 否则原值算成 0，无加成时也会错误地显示差值。
     */
    public static List<DiagramsData> explosion(@Nullable ItemStack gunItem, GunData gunData) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) {
            return Collections.emptyList();
        }
        ExplosionData original = gunData.getBulletData().getExplosionData();
        if (original == null) {
            original = new ExplosionData(false, 0.5f, 2, false, 30, false);
        }
        float originalRadius = original.getRadius();
        float originalDamage = original.getDamage();

        ExplosionData modified = null;
        if (gunItem != null && !gunItem.isEmpty()) {
            // 取缓存里的爆炸数据（已含配件修改）
            IGun iGun = IGun.getIGunOrNull(gunItem);
            if (iGun != null) {
                AttachmentCacheProperty cache = com.tacz.guns.api.entity.IGunOperator
                        .fromLivingEntity(player).getCacheProperty();
                if (cache != null) {
                    modified = cache.getCache(GunProperties.EXPLOSION);
                }
            }
        }
        if (modified == null) {
            modified = original;
        }

        float modifiedRadius = Math.max((float) kubejsDisplayValue(player, gunItem,
                "EXPLOSION_RADIUS", modified.getRadius(), originalRadius), 0f);
        float modifiedDamage = Math.max((float) kubejsDisplayValue(player, gunItem,
                "EXPLOSION_DAMAGE", modified.getDamage(), originalDamage), 0f);

        List<DiagramsData> rows = new ArrayList<>(2);
        rows.add(row(TITLE_EXPLOSION_RADIUS, originalRadius, modifiedRadius, SCALE_EXPLOSION_RADIUS,
                String.format("%.1f §a(+%.1f)m", modifiedRadius, modifiedRadius - originalRadius),
                String.format("%.1f §c(%.1f)m", modifiedRadius, modifiedRadius - originalRadius),
                String.format("%.1fm", modifiedRadius), true));
        rows.add(row(TITLE_EXPLOSION_DAMAGE, originalDamage, modifiedDamage, SCALE_EXPLOSION_DAMAGE,
                String.format("%.1f §a(+%.1f)", modifiedDamage, modifiedDamage - originalDamage),
                String.format("%.1f §c(%.1f)", modifiedDamage, modifiedDamage - originalDamage),
                String.format("%.1f", modifiedDamage), true));
        return rows;
    }

    /**
     * 装填时间。
     * 原始值必须走 {@link ReloadTimeScope} 的 authored 通道读取，否则会被本模组的换弹倍率二次缩放。
     * 实际值统一由 {@link PropertyCalculator#calculateReloadTime(float, net.minecraft.world.entity.LivingEntity)}
     * 计算（含 0.1s 下限），与换弹时长、脚本时钟、动画速度同源。
     */
    public static List<DiagramsData> reload(@Nullable ItemStack gunItem, GunData gunData) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null || gunItem == null) {
            return Collections.emptyList();
        }
        float original = 0.0f;
        if (gunData.getReloadData() != null && gunData.getReloadData().getFeed() != null) {
            ReloadTimeScope.enterAuthoredTimeRead();
            try {
                original = gunData.getReloadData().getFeed().getTacticalTime();
            } finally {
                ReloadTimeScope.exitAuthoredTimeRead();
            }
        }
        float modified = PropertyCalculator.calculateReloadTime(original, player);
        float kuvaReloadSpeed = KuvaLichIntegrationHelper.getReloadSpeedMod(gunItem, player);
        if (kuvaReloadSpeed != 0) {
            modified /= (1 + kuvaReloadSpeed);
        }
        if (GunsmithLibHelper.isGunsmithLibLoaded()) {
            modified /= (float) GunsmithLibHelper.getReloadSpeed(gunItem, 1.0);
        }
        modified = Math.max((float) kubejsDisplayValue(player, gunItem, "RELOAD_TIME", modified, original), 0f);
        float diff = modified - original;
        // 装填时间越短越好
        String positive = String.format("%.2fs §c(+%.2f)", modified, diff);
        String negative = String.format("%.2fs §a(%.2f)", modified, diff);
        return List.of(row(TITLE_RELOAD, original, modified, SCALE_RELOAD,
                positive, negative, String.format("%.2fs", modified), false));
    }

    /** 暴击率 + 暴击伤害（Apothic Attributes 存在时才有数据）。 */
    public static List<DiagramsData> crit(@Nullable ItemStack gunItem, GunData gunData) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) {
            return Collections.emptyList();
        }
        List<DiagramsData> rows = new ArrayList<>(2);
        ApothicAttributesHelper.CritAttributeData chance = ApothicAttributesHelper.getCritChanceData(player);
        if (chance != null) {
            double denominator = chance.isDecimalFormat ? 1.0 : 1000.0;
            rows.add(critRow(AttributeConfig.getCritChanceName(), chance, denominator));
        }
        ApothicAttributesHelper.CritAttributeData damage = ApothicAttributesHelper.getCritDamageData(player);
        if (damage != null) {
            double denominator = damage.isDecimalFormat ? 100.0 : 1000.0;
            rows.add(critRow(AttributeConfig.getCritDamageName(), damage, denominator));
        }
        return rows;
    }

    /** 暴击行数：供 {@code getDiagramsDataSize()} 使用。 */
    public static int critRowCount() {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) {
            return 0;
        }
        int count = 0;
        if (ApothicAttributesHelper.getCritChanceData(player) != null) {
            count++;
        }
        if (ApothicAttributesHelper.getCritDamageData(player) != null) {
            count++;
        }
        return count;
    }

    private static DiagramsData critRow(String titleKey, ApothicAttributesHelper.CritAttributeData data,
                                        double denominator) {
        double defaultPercent = Mth.clamp(data.baseValue / denominator, 0.0, 1.0);
        double modifiedPercent = Mth.clamp(data.modifiedValue / denominator, 0.0, 1.0);
        return new DiagramsData(defaultPercent, modifiedPercent - defaultPercent, data.difference,
                titleKey,
                data.formatValue(data.modifiedValue) + " §a(+" + data.formatValue(data.difference) + ")",
                data.formatValue(data.modifiedValue) + " §c(" + data.formatValue(data.difference) + ")",
                data.formatValue(data.modifiedValue),
                data.difference >= 0);
    }

    /** 近战伤害 + 近战距离。 */
    public static List<DiagramsData> melee(@Nullable ItemStack gunItem, GunData gunData) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null || gunItem == null) {
            return Collections.emptyList();
        }
        float otherModifiers = 0f;
        var attackDamage = player.getAttribute(Attributes.ATTACK_DAMAGE);
        if (attackDamage != null) {
            otherModifiers = (float) (attackDamage.getValue() - attackDamage.getBaseValue());
        }
        GunMeleeData meleeData = gunData.getMeleeData();
        float baseModifierDamage = 0f;
        if (meleeData != null && meleeData.getDefaultMeleeData() != null) {
            baseModifierDamage = meleeData.getDefaultMeleeData().getDamage();
        }
        float finalModifierDamage = EntityAttributeHelper.applyMeleeDamageAttribute(player, baseModifierDamage);
        float baseTotalDamage = otherModifiers + baseModifierDamage;
        float finalTotalDamage = otherModifiers + finalModifierDamage;
        finalTotalDamage = Math.max((float) kubejsDisplayValue(player, gunItem,
                "MELEE_DAMAGE", finalTotalDamage, baseTotalDamage), 0f);

        float distanceBase = meleeData != null ? meleeData.getDistance() : 0.0f;
        float distanceModified = (float) (distanceBase + EntityAttributeHelper.getAttributeValueNonNegative(
                player, EntityAttributeRegistry.MELEE_DISTANCE.get(), 0.0D));
        distanceModified = Math.max((float) kubejsDisplayValue(player, gunItem,
                "MELEE_DISTANCE", distanceModified, distanceBase), 0f);

        List<DiagramsData> rows = new ArrayList<>(2);
        rows.add(row(TITLE_MELEE_DAMAGE, baseTotalDamage, finalTotalDamage, SCALE_MELEE_DAMAGE,
                String.format("%.1f §a(+%.1f)", finalTotalDamage, finalTotalDamage - baseTotalDamage),
                String.format("%.1f §c(%.1f)", finalTotalDamage, finalTotalDamage - baseTotalDamage),
                String.format("%.1f", finalTotalDamage), true));
        rows.add(row(TITLE_MELEE_DISTANCE, distanceBase, distanceModified, SCALE_MELEE_DISTANCE,
                String.format("%.2fm §a(+%.2fm)", distanceModified, distanceModified - distanceBase),
                String.format("%.2fm §c(%.2fm)", distanceModified, distanceModified - distanceBase),
                String.format("%.2fm", distanceModified), true));
        return rows;
    }

    /** 过热上限 / 散热速度 / 冷却延迟 / 锁枪时长；无热数据的枪返回空。 */
    public static List<DiagramsData> heat(@Nullable ItemStack gunItem, GunData gunData) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) {
            return Collections.emptyList();
        }
        GunHeatData heatData = gunData.getHeatData();
        if (heatData == null) {
            return Collections.emptyList();
        }
        List<DiagramsData> rows = new ArrayList<>(4);

        float rawHeatMax = heatData.getHeatMax();
        float modHeatMax = HeatAttributeHelper.getModifiedHeatMax(player, rawHeatMax);
        rows.add(row(TITLE_HEAT_MAX, rawHeatMax, modHeatMax, SCALE_HEAT_MAX,
                String.format("%.0f §a(+%.0f)", modHeatMax, modHeatMax - rawHeatMax),
                String.format("%.0f §c(%.0f)", modHeatMax, modHeatMax - rawHeatMax),
                String.format("%.0f", modHeatMax), true));

        float rawCooling = heatData.getCoolingMultiplier();
        float modCooling = HeatAttributeHelper.getModifiedCoolingMultiplier(player, rawCooling);
        rows.add(row(TITLE_HEAT_COOLING, rawCooling, modCooling, SCALE_HEAT_COOLING,
                String.format("%.2f §a(+%.2f)", modCooling, modCooling - rawCooling),
                String.format("%.2f §c(%.2f)", modCooling, modCooling - rawCooling),
                String.format("%.2f", modCooling), true));

        long rawDelay = heatData.getCoolingDelay();
        long modDelay = HeatAttributeHelper.getModifiedCoolingDelay(player, rawDelay);
        rows.add(row(TITLE_HEAT_COOLING_DELAY, rawDelay, modDelay, SCALE_HEAT_DELAY_MS,
                // 冷却延迟越短越好：变大是负收益，用红字
                String.format("%.1fs §c(+%.1fs)", modDelay / 1000.0, (modDelay - rawDelay) / 1000.0),
                String.format("%.1fs §a(%.1fs)", modDelay / 1000.0, (modDelay - rawDelay) / 1000.0),
                String.format("%.1fs", modDelay / 1000.0), false));

        long rawLock = heatData.getOverHeatTime();
        long modLock = HeatAttributeHelper.getModifiedOverHeatTime(player, rawLock);
        rows.add(row(TITLE_HEAT_OVERHEAT_TIME, rawLock, modLock, SCALE_HEAT_LOCK_MS,
                String.format("%.1fs §c(+%.1fs)", modLock / 1000.0, (modLock - rawLock) / 1000.0),
                String.format("%.1fs §a(%.1fs)", modLock / 1000.0, (modLock - rawLock) / 1000.0),
                String.format("%.1fs", modLock / 1000.0), false));
        return rows;
    }

    /** 上一次绘制时面板里的那把枪，用于让行数计算与面板内容保持一致。 */
    private static GunData lastGunData;

    /** 由 {@link TaaPanelRowWrapper} / {@link TaaPanelModifier} 在每次绘制时回调。 */
    static void remember(@Nullable GunData gunData) {
        if (gunData != null) {
            lastGunData = gunData;
        }
    }

    /** 过热行数：供 {@code getDiagramsDataSize()} 使用。 */
    public static int heatRowCount() {
        GunData remembered = lastGunData;
        if (remembered != null) {
            return remembered.getHeatData() != null ? 4 : 0;
        }
        // 面板还没画过一次时退化成主手武器
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) {
            return 0;
        }
        ItemStack stack = player.getMainHandItem();
        IGun iGun = IGun.getIGunOrNull(stack);
        if (iGun == null) {
            return 0;
        }
        return TimelessAPI.getCommonGunIndex(iGun.getGunId(stack))
                .map(index -> index.getGunData().getHeatData() != null ? 4 : 0)
                .orElse(0);
    }

    // ==================== TACZ 自有行的改写 ====================

    /**
     * 改写 TACZ 自己算出来的一行：保留 TACZ 的百分比口径，只把 KuvaLich / GunsmithLib / KubeJS 的影响补上。
     * 认不出来的行原样返回。
     */
    public static DiagramsData rewrite(ItemStack gunItem, GunData gunData, AttachmentCacheProperty cache,
                                       DiagramsData original) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null || gunItem == null) {
            return original;
        }
        IGun iGun = IGun.getIGunOrNull(gunItem);
        if (iGun == null) {
            return original;
        }
        FireMode fireMode = iGun.getFireMode(gunItem);
        GunFireModeAdjustData adjust = gunData.getFireModeAdjustData(fireMode);
        String key = original.titleKey();
        if (key == null) {
            return original;
        }
        switch (key) {
            case KEY_DAMAGE:
                return rewriteDamage(player, gunItem, gunData, cache, adjust, original);
            case KEY_HEAD_SHOT:
                return rewriteHeadShot(player, gunItem, gunData, adjust, original);
            case KEY_RPM:
                // 连发模式显示的是 bpm，与本模组的射速属性不是同一口径，原样保留
                return fireMode == FireMode.BURST ? original : rewriteRpm(player, gunItem, gunData, cache, fireMode, original);
            case KEY_AMMO_SPEED:
                return rewriteAmmoSpeed(player, gunItem, gunData, cache, adjust, original);
            case KEY_ADS:
                return rewriteAds(player, gunItem, gunData, cache, original);
            case KEY_HIPFIRE_INACCURACY:
                return rewriteInaccuracy(player, gunItem, gunData, cache, adjust, InaccuracyType.STAND, original);
            case KEY_SNEAK_INACCURACY:
                return rewriteInaccuracy(player, gunItem, gunData, cache, adjust, InaccuracyType.SNEAK, original);
            case KEY_LIE_INACCURACY:
                return rewriteInaccuracy(player, gunItem, gunData, cache, adjust, InaccuracyType.LIE, original);
            case KEY_AIM_INACCURACY:
                return rewriteAimInaccuracy(player, gunItem, gunData, cache, adjust, original);
            case KEY_ARMOR_IGNORE:
                return rewriteArmorIgnore(player, gunItem, gunData, cache, adjust, original);
            case KEY_PITCH:
            case KEY_YAW:
                return rewriteRecoil(player, gunItem, gunData, cache, KEY_PITCH.equals(key), original);
            default:
                return original;
        }
    }

    private static DiagramsData rewriteDamage(LocalPlayer player, ItemStack gunItem, GunData gunData,
                                              AttachmentCacheProperty cache, @Nullable GunFireModeAdjustData adjust,
                                              DiagramsData original) {
        BulletData bulletData = gunData.getBulletData();
        ExtraDamage extraDamage = bulletData.getExtraDamage();
        float base = adjust != null ? adjust.getDamageAmount() : 0f;
        if (extraDamage != null && extraDamage.getDamageAdjust() != null) {
            base += extraDamage.getDamageAdjust().get(0).getDamage();
        } else {
            base += bulletData.getDamageAmount();
        }
        base *= SyncConfig.DAMAGE_BASE_MULTIPLIER.get();

        LinkedList<DistanceDamagePair> damageModifier = cache.getCache(DamageModifier.ID);
        float modified = base;
        if (damageModifier != null && !damageModifier.isEmpty()) {
            modified = damageModifier.getFirst().getDamage();
        }
        if (GunsmithLibHelper.isGunsmithLibLoaded()) {
            modified = (float) GunsmithLibHelper.getBulletDamage(gunItem, modified, Math.max(bulletData.getBulletAmount(), 1));
        }
        float kuva = KuvaLichIntegrationHelper.getGunDamageMod(gunItem, player);
        if (kuva != 0) {
            modified *= (1 + kuva);
        }
        modified = Math.max((float) kubejsDisplayValue(player, gunItem, "DAMAGE", modified, base), 0f);
        return row(KEY_DAMAGE, base, modified, SCALE_DAMAGE,
                String.format("%.1f §a(+%.1f)", modified, modified - base),
                String.format("%.1f §c(%.1f)", modified, modified - base),
                String.format("%.1f", modified), true);
    }

    private static DiagramsData rewriteHeadShot(LocalPlayer player, ItemStack gunItem, GunData gunData,
                                                @Nullable GunFireModeAdjustData adjust, DiagramsData original) {
        ExtraDamage extraDamage = gunData.getBulletData().getExtraDamage();
        float base = extraDamage != null ? extraDamage.getHeadShotMultiplier() : 0;
        if (adjust != null) {
            base += adjust.getHeadShotMultiplier();
        }
        base *= SyncConfig.HEAD_SHOT_BASE_MULTIPLIER.get();
        float modified = base;
        if (GunsmithLibHelper.isGunsmithLibLoaded()) {
            modified = (float) GunsmithLibHelper.getHeadshotMultiplier(gunItem, modified);
        }
        float kuva = KuvaLichIntegrationHelper.getHeadshotDamageMod(gunItem, player);
        if (kuva != 0) {
            modified *= (1 + kuva);
        }
        modified = Math.max((float) kubejsDisplayValue(player, gunItem, "HEADSHOT", modified, base), 0f);
        return row(KEY_HEAD_SHOT, base, modified, SCALE_HEAD_SHOT,
                String.format("x%.2f §a(+%.2f)", modified, modified - base),
                String.format("x%.2f §c(%.2f)", modified, modified - base),
                String.format("x%.2f", modified), true);
    }

    private static DiagramsData rewriteRpm(LocalPlayer player, ItemStack gunItem, GunData gunData,
                                           AttachmentCacheProperty cache, FireMode fireMode, DiagramsData original) {
        int base = gunData.getRoundsPerMinute(fireMode);
        Integer cached = cache.getCache(RpmModifier.ID);
        int modified = cached != null ? cached : base;
        if (GunsmithLibHelper.isGunsmithLibLoaded()) {
            modified = (int) Math.round(GunsmithLibHelper.getRpm(gunItem, modified));
        }
        float kuva = KuvaLichIntegrationHelper.getFireRateMod(gunItem, player);
        if (kuva != 0) {
            modified = (int) (modified * (1 + kuva));
        }
        modified = Math.max((int) kubejsDisplayValue(player, gunItem, "RPM", modified, base), 0);
        return row(KEY_RPM, base, modified, SCALE_RPM,
                String.format("%drpm §a(+%d)", modified, modified - base),
                String.format("%drpm §c(%d)", modified, modified - base),
                String.format("%drpm", modified), true);
    }

    private static DiagramsData rewriteAmmoSpeed(LocalPlayer player, ItemStack gunItem, GunData gunData,
                                                 AttachmentCacheProperty cache, @Nullable GunFireModeAdjustData adjust,
                                                 DiagramsData original) {
        float base = gunData.getBulletData().getSpeed();
        if (adjust != null) {
            base += adjust.getSpeed();
        }
        Float cached = cache.getCache(AmmoSpeedModifier.ID);
        float modified = cached != null ? cached : base;
        if (GunsmithLibHelper.isGunsmithLibLoaded()) {
            modified = (float) GunsmithLibHelper.getBulletSpeed(gunItem, modified);
        }
        float kuva = KuvaLichIntegrationHelper.getProjectileSpeedMod(gunItem, player);
        if (kuva != 0) {
            modified *= (1 + kuva);
        }
        modified = Math.max((float) kubejsDisplayValue(player, gunItem, "BULLET_SPEED", modified, base), 0f);
        return row(KEY_AMMO_SPEED, base, modified, SCALE_AMMO_SPEED,
                String.format("%dm/s §a(+%d)", Math.round(modified), Math.round(modified - base)),
                String.format("%dm/s §c(%d)", Math.round(modified), Math.round(modified - base)),
                String.format("%dm/s", Math.round(modified)), true);
    }

    private static DiagramsData rewriteAds(LocalPlayer player, ItemStack gunItem, GunData gunData,
                                           AttachmentCacheProperty cache, DiagramsData original) {
        float base = gunData.getAimTime();
        Float cached = cache.getCache(AdsModifier.ID);
        float modified = cached != null ? cached : base;
        float kuva = KuvaLichIntegrationHelper.getAimTimeMod(gunItem, player);
        if (kuva != 0) {
            modified /= (1 + kuva);
        }
        modified = Math.max((float) kubejsDisplayValue(player, gunItem, "ADS_TIME", modified, base), 0f);
        float diff = modified - base;
        // 瞄准时间越短越好
        return row(KEY_ADS, base, modified, SCALE_ADS,
                String.format("%.2fs §c(+%.2f)", modified, diff),
                String.format("%.2fs §a(%.2f)", modified, diff),
                String.format("%.2fs", modified), false);
    }

    private static DiagramsData rewriteInaccuracy(LocalPlayer player, ItemStack gunItem, GunData gunData,
                                                  AttachmentCacheProperty cache, @Nullable GunFireModeAdjustData adjust,
                                                  InaccuracyType type, DiagramsData original) {
        float base = gunData.getInaccuracy(type);
        if (adjust != null) {
            base += adjust.getOtherInaccuracy();
        }
        Map<InaccuracyType, Float> cached = cache.getCache(InaccuracyModifier.ID);
        float modified = base;
        if (cached != null && cached.containsKey(type)) {
            modified = cached.get(type);
        }
        float kuva = KuvaLichIntegrationHelper.getAccuracyMod(gunItem, player);
        if (kuva != 0) {
            modified *= (1 - kuva);
        }
        modified = Math.max((float) kubejsDisplayValue(player, gunItem, "INACCURACY", modified, base), 0f);
        float diff = modified - base;
        // 扩散越小越好
        return row(original.titleKey(), base, modified, SCALE_INACCURACY,
                String.format("%.2f §c(+%.2f)", modified, diff),
                String.format("%.2f §a(%.2f)", modified, diff),
                String.format("%.2f", modified), false);
    }

    /**
     * 瞄准状态下的精准度：TACZ 这一行显示的是 1 - 扩散，越大越好。
     * 只补 KuvaLich 的精度修正（作用在扩散上），不触发 KubeJS 的 INACCURACY 钩子——
     * 该钩子约定传平射扩散值，两处语义不同，混用会让脚本作者算错。
     */
    private static DiagramsData rewriteAimInaccuracy(LocalPlayer player, ItemStack gunItem, GunData gunData,
                                                    AttachmentCacheProperty cache,
                                                    @Nullable GunFireModeAdjustData adjust, DiagramsData original) {
        Map<InaccuracyType, Float> cached = cache.getCache(InaccuracyModifier.ID);
        if (cached == null || !cached.containsKey(InaccuracyType.AIM)) {
            return original;
        }
        // TACZ 这一行显示的是「1 - 瞄准扩散」，基础值来自枪械数据，不能用 original 的百分比反推
        float baseSpread = gunData.getInaccuracy(InaccuracyType.AIM);
        if (adjust != null) {
            baseSpread += adjust.getAimInaccuracy();
        }
        float base = Mth.clamp(1f - baseSpread, 0f, 1f);
        float rawAimSpread = cached.get(InaccuracyType.AIM);
        float kuva = KuvaLichIntegrationHelper.getAccuracyMod(gunItem, player);
        if (kuva != 0) {
            rawAimSpread *= (1 - kuva);
        }
        float modified = Mth.clamp(1f - rawAimSpread, 0f, 1f);
        // 不触发 kubejs "INACCURACY" 钩子：该钩子约定传平射扩散值，与此行语义不同
        return row(original.titleKey(), base, modified, 1.0,
                String.format("%.2f §a(+%.2f)", modified, modified - base),
                String.format("%.2f §c(%.2f)", modified, modified - base),
                String.format("%.2f", modified), true);
    }

    private static DiagramsData rewriteArmorIgnore(LocalPlayer player, ItemStack gunItem, GunData gunData,
                                                    AttachmentCacheProperty cache, @Nullable GunFireModeAdjustData adjust,
                                                    DiagramsData original) {
        BulletData bulletData = gunData.getBulletData();
        ExtraDamage extraDamage = bulletData.getExtraDamage();
        float base = extraDamage != null ? extraDamage.getArmorIgnore() : 0f;
        if (adjust != null) {
            base += adjust.getArmorIgnore();
        }
        base *= SyncConfig.ARMOR_IGNORE_BASE_MULTIPLIER.get();
        Float cached = cache.getCache(ArmorIgnoreModifier.ID);
        float modified = cached != null ? cached : base;
        if (GunsmithLibHelper.isGunsmithLibLoaded()) {
            modified = (float) GunsmithLibHelper.getArmorPiercingRatio(gunItem, modified);
        }
        modified = Math.max((float) kubejsDisplayValue(player, gunItem, "ARMOR_IGNORE", modified, base), 0f);
        float diff = modified - base;
        return row(KEY_ARMOR_IGNORE, base, modified, 1.0,
                String.format("%.1f%% §a(+%.1f%%)", modified * 100, diff * 100),
                String.format("%.1f%% §c(%.1f%%)", modified * 100, diff * 100),
                String.format("%.1f%%", modified * 100), true);
    }

    private static DiagramsData rewriteRecoil(LocalPlayer player, ItemStack gunItem, GunData gunData,
                                              AttachmentCacheProperty cache, boolean pitch, DiagramsData original) {
        GunRecoil recoil = gunData.getRecoil();
        if (recoil == null) {
            return original;
        }
        float base = getMaxInGunRecoilKeyFrame(pitch ? recoil.getPitch() : recoil.getYaw());
        ParameterizedCachePair<Float, Float> recoilData = cache.getCache(GunProperties.RECOIL);
        float attachmentModified = base;
        if (recoilData != null) {
            var function = pitch ? recoilData.left() : recoilData.right();
            if (function != null) {
                attachmentModified = (float) function.eval(base);
            }
        }
        EntityAttributeHelper entityAttribute = new EntityAttributeHelper(player, "");
        float factor = (float) (entityAttribute.getRecoil() * (pitch ? entityAttribute.getRecoilPitch() : entityAttribute.getRecoilYaw()));
        float modified = attachmentModified * factor;
        if (GunsmithLibHelper.isGunsmithLibLoaded()) {
            modified = (float) (pitch ? GunsmithLibHelper.getVRecoil(gunItem, modified)
                    : GunsmithLibHelper.getHRecoil(gunItem, modified));
        }
        float kuva = KuvaLichIntegrationHelper.getRecoilReductionMod(gunItem, player);
        if (kuva != 0) {
            modified *= (1 - kuva);
        }
        modified = Math.max((float) kubejsDisplayValue(player, gunItem,
                pitch ? "RECOIL_PITCH" : "RECOIL_YAW", modified, base), 0f);
        float diff = modified - base;
        // 后坐力越大越差
        return row(original.titleKey(), base, modified, SCALE_RECOIL,
                String.format("%.2f §c(+%.2f)", modified, diff),
                String.format("%.2f §a(%.2f)", modified, diff),
                String.format("%.2f", modified), false);
    }

    /** 与 TACZ RecoilModifier 同款：取首个关键帧的绝对值最大的分量。 */
    private static float getMaxInGunRecoilKeyFrame(@Nullable GunRecoilKeyFrame[] frames) {
        if (frames == null || frames.length == 0) {
            return 0;
        }
        float[] value = frames[0].getValue();
        if (value == null || value.length < 2) {
            return 0;
        }
        return Math.max(Math.abs(value[0]), Math.abs(value[1]));
    }

    // ==================== 工具 ====================

    /**
     * 按 TACZ 口径构造一行：
     * 基准百分比 = base / scale（夹到 0..1），差值百分比 = (modified - base) / scale。
     * TACZ 侧会把最终条长夹在条区范围内，所以这里不需要再夹。
     */
    private static DiagramsData row(String titleKey, double base, double modified, double scale,
                                    String positivelyString, String negativeString, String defaultString,
                                    boolean positivelyBetter) {
        double defaultPercent = scale <= 0.0 ? 0.0 : Mth.clamp(base / scale, 0.0, 1.0);
        double modifierPercent = scale <= 0.0 ? 0.0 : (modified - base) / scale;
        return new DiagramsData(defaultPercent, modifierPercent, modified - base,
                titleKey, positivelyString, negativeString, defaultString, positivelyBetter);
    }

    private static double kubejsDisplayValue(@Nullable LocalPlayer player, @Nullable ItemStack gunItem,
                                             String propertyType, double displayValue, double originalValue) {
        if (player == null || gunItem == null || !ModList.get().isLoaded("kubejs")) {
            return displayValue;
        }
        return KubeJSEventHelper.postAndGetDisplayValue(player, gunItem, propertyType, displayValue, originalValue);
    }
}
