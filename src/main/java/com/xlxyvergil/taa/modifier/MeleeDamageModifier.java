package com.xlxyvergil.taa.modifier;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

import com.google.gson.annotations.SerializedName;
import com.tacz.guns.api.DefaultAssets;
import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.api.item.attachment.AttachmentType;
import com.tacz.guns.api.modifier.CacheValue;
import com.tacz.guns.api.modifier.IAttachmentModifier;
import com.tacz.guns.api.modifier.JsonProperty;
import com.tacz.guns.resource.CommonAssetsManager;
import com.tacz.guns.resource.index.CommonAttachmentIndex;
import com.tacz.guns.resource.modifier.AttachmentCacheProperty;
import com.tacz.guns.resource.modifier.AttachmentPropertyManager;
import com.tacz.guns.resource.pojo.data.attachment.MeleeData;
import com.tacz.guns.resource.pojo.data.attachment.Modifier;
import com.tacz.guns.resource.pojo.data.gun.GunData;
import com.tacz.guns.resource.pojo.data.gun.GunDefaultMeleeData;
import com.tacz.guns.resource.pojo.data.gun.GunMeleeData;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * 近战伤害 Modifier
 * 用于修改枪械的近战伤害
 * 基于 TACZ 源代码的标准实现模式
 */
public class MeleeDamageModifier implements IAttachmentModifier<Modifier, Float> {
    public static final String ID = "melee_damage";

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public JsonProperty<Modifier> readJson(String json) {
        Data data = CommonAssetsManager.GSON.fromJson(json, Data.class);
        return new MeleeDamageJsonProperty(data.getMeleeDamage());
    }

    @Override
    public CacheValue<Float> initCache(ItemStack gunItem, GunData gunData) {
        // 完全复用 TACZ 原版的近战伤害判断逻辑
        // 参考 ModernKineticGunItem.melee() 方法
        
        GunMeleeData meleeData = gunData.getMeleeData();
        if (meleeData == null) {
            return new CacheValue<>(0f);
        }
        
        IGun iGun = IGun.getIGunOrNull(gunItem);
        if (iGun == null) {
            // 无法获取枪械接口，使用默认伤害
            GunDefaultMeleeData defaultData = meleeData.getDefaultMeleeData();
            return new CacheValue<>(defaultData != null ? defaultData.getDamage() : 0f);
        }
        
        // 1. 检查枪口配件（刺刀）- 与 TACZ 原版逻辑一致
        ResourceLocation muzzleId = iGun.getAttachmentId(gunItem, AttachmentType.MUZZLE);
        MeleeData muzzleData = getMeleeData(muzzleId);
        if (muzzleData != null) {
            // 有刺刀配件，使用刺刀的伤害
            return new CacheValue<>(muzzleData.getDamage());
        }
        
        // 2. 检查枪托配件 - 与 TACZ 原版逻辑一致
        ResourceLocation stockId = iGun.getAttachmentId(gunItem, AttachmentType.STOCK);
        MeleeData stockData = getMeleeData(stockId);
        if (stockData != null) {
            // 有枪托配件，使用枪托的伤害
            return new CacheValue<>(stockData.getDamage());
        }
        
        // 3. 没有近战配件，使用默认近战伤害 - 与 TACZ 原版逻辑一致
        GunDefaultMeleeData defaultData = meleeData.getDefaultMeleeData();
        if (defaultData == null) {
            return new CacheValue<>(0f);
        }
        return new CacheValue<>(defaultData.getDamage());
    }
    
    /**
     * 获取配件的近战数据
     * 与 TACZ 原版的 getMeleeData 方法逻辑一致
     */
    @Nullable
    private MeleeData getMeleeData(ResourceLocation attachmentId) {
        if (DefaultAssets.isEmptyAttachmentId(attachmentId)) {
            return null;
        }
        return TimelessAPI.getCommonAttachmentIndex(attachmentId)
                .map(index -> index.getData().getMeleeData())
                .orElse(null);
    }

    @Override
    public void eval(List<Modifier> modifiers, CacheValue<Float> cache) {
        if (modifiers.isEmpty()) {
            return;
        }
        double eval = AttachmentPropertyManager.eval(modifiers, cache.getValue());
        cache.setValue((float) eval);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public List<DiagramsData> getPropertyDiagramsData(ItemStack gunItem, GunData gunData, AttachmentCacheProperty cacheProperty) {
        // 基础伤害 = 默认近战伤害（作为显示基准）
        GunMeleeData meleeData = gunData.getMeleeData();
        float baseDamage = 0f;
        if (meleeData != null && meleeData.getDefaultMeleeData() != null) {
            baseDamage = meleeData.getDefaultMeleeData().getDamage();
        }
        
        // 计算配件提供的伤害（用于显示总变动）
        float attachmentDamage = 0f;
        IGun iGun = IGun.getIGunOrNull(gunItem);
        if (iGun != null) {
            // 检查枪口配件
            ResourceLocation muzzleId = iGun.getAttachmentId(gunItem, AttachmentType.MUZZLE);
            MeleeData muzzleData = getMeleeData(muzzleId);
            if (muzzleData != null) {
                attachmentDamage = muzzleData.getDamage();
            } else {
                // 检查枪托配件
                ResourceLocation stockId = iGun.getAttachmentId(gunItem, AttachmentType.STOCK);
                MeleeData stockData = getMeleeData(stockId);
                if (stockData != null) {
                    attachmentDamage = stockData.getDamage();
                }
            }
        }
        
        // 获取修改后的伤害（属性修改后的值，initCache 中已包含配件伤害）
        Float modifiedDamage = cacheProperty.getCache(MeleeDamageModifier.ID);
        if (modifiedDamage == null) {
            modifiedDamage = baseDamage + attachmentDamage;
        }
        
        // 总变动值 = 配件伤害 + 属性修改带来的变动
        float totalDifference = modifiedDamage - baseDamage;
        
        // 计算近战伤害的显示数据
        double damagePercent = Math.min(baseDamage / 20.0, 1);
        double damageModifierPercent = Math.min(Math.abs(totalDifference) / 20.0, 1);

        String damageTitleKey = "gui.tacz.gun_refit.property_diagrams.melee_damage";
        String damagePositivelyString = String.format(" %.1f §a(+%.1f)", modifiedDamage, totalDifference);
        String damageNegativelyString = String.format(" %.1f §c(%.1f)", modifiedDamage, totalDifference);
        String damageDefaultString = String.format(" %.1f", modifiedDamage);
        boolean damagePositivelyBetter = true; // 近战伤害越高越好

        DiagramsData damageDiagramsData = new DiagramsData(
                damagePercent, damageModifierPercent, totalDifference, 
                damageTitleKey, damagePositivelyString, damageNegativelyString, 
                damageDefaultString, damagePositivelyBetter);

        return Collections.singletonList(damageDiagramsData);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public int getDiagramsDataSize() {
        return 1;
    }

    public static class MeleeDamageJsonProperty extends JsonProperty<Modifier> {
        public MeleeDamageJsonProperty(Modifier value) {
            super(value);
        }

        @Override
        public void initComponents() {
            Modifier value = getValue();
            if (value != null) {
                double eval = AttachmentPropertyManager.eval(value, 0.0f);
                float damage = (float) eval;
                if (damage > 0.0f) {
                    components.add(Component.translatable("tooltip.tacz.attachment.melee.damage.increase").withStyle(ChatFormatting.GREEN));
                } else if (damage < 0.0f) {
                    components.add(Component.translatable("tooltip.tacz.attachment.melee.damage.decrease").withStyle(ChatFormatting.RED));
                }
            }
        }
    }

    public static class Data {
        @SerializedName("melee_damage")
        @Nullable
        private Modifier meleeDamage = null;

        @Nullable
        public Modifier getMeleeDamage() {
            return meleeDamage;
        }
    }
}