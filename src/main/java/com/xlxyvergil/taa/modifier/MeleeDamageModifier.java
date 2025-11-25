package com.xlxyvergil.taa.modifier;

import com.google.gson.annotations.SerializedName;
import com.tacz.guns.api.modifier.CacheValue;
import com.tacz.guns.api.modifier.IAttachmentModifier;
import com.tacz.guns.api.modifier.JsonProperty;
import com.tacz.guns.resource.CommonAssetsManager;
import com.tacz.guns.resource.modifier.AttachmentCacheProperty;
import com.tacz.guns.resource.modifier.AttachmentPropertyManager;
import com.tacz.guns.resource.pojo.data.attachment.Modifier;
import com.tacz.guns.resource.pojo.data.gun.GunData;
import com.tacz.guns.resource.pojo.data.gun.GunMeleeData;
import com.tacz.guns.resource.pojo.data.gun.GunDefaultMeleeData;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

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
        // 获取枪械的近战数据
        GunMeleeData meleeData = gunData.getMeleeData();
        if (meleeData == null || meleeData.getDefaultMeleeData() == null) {
            return new CacheValue<>(0f);
        }
        // 返回基础的近战伤害
        return new CacheValue<>(meleeData.getDefaultMeleeData().getDamage());
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
        // 获取原始近战伤害
        GunMeleeData originalMeleeData = gunData.getMeleeData();
        float originalDamage = 0f;
        if (originalMeleeData != null && originalMeleeData.getDefaultMeleeData() != null) {
            originalDamage = originalMeleeData.getDefaultMeleeData().getDamage();
        }
        
        // 获取修改后的伤害
        Float modifiedDamage = cacheProperty.getCache(MeleeDamageModifier.ID);
        if (modifiedDamage == null) {
            modifiedDamage = originalDamage;
        }
        
        float damageDifference = modifiedDamage - originalDamage;
        
        // 计算近战伤害的显示数据 - 显示为基础近战伤害
        double damagePercent = Math.min(modifiedDamage / 20.0, 1);
        double damageModifierPercent = Math.min(Math.abs(damageDifference) / 20.0, 1);

        String damageTitleKey = "gui.tacz.gun_refit.property_diagrams.melee_damage";
        // 显示为基础近战伤害，而不是增量
        String damagePositivelyString = String.format("基础近战伤害: %.1f", modifiedDamage);
        String damageNegativelyString = String.format("基础近战伤害: %.1f", modifiedDamage);
        String damageDefaultString = String.format("基础近战伤害: %.1f", modifiedDamage);
        boolean damagePositivelyBetter = true; // 近战伤害越高越好

        DiagramsData damageDiagramsData = new DiagramsData(
                damagePercent, damageModifierPercent, damageDifference, 
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
                double eval = AttachmentPropertyManager.eval(value, 5.0f);
                float damage = (float) eval;
                if (damage > 5.0f) {
                    components.add(Component.translatable("tooltip.tacz.attachment.melee.damage.increase").withStyle(ChatFormatting.GREEN));
                } else if (damage < 5.0f) {
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