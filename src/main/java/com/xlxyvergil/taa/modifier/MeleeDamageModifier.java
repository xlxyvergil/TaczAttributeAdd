package com.xlxyvergil.taa.modifier;

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
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

/**
 * 近战伤害 Modifier
 * 仅计算配件提供的近战伤害，属性计算由 PropertyCalculator 处理
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
        GunMeleeData meleeData = gunData.getMeleeData();
        if (meleeData == null) {
            return new CacheValue<>(0f);
        }
        
        IGun iGun = IGun.getIGunOrNull(gunItem);
        if (iGun == null) {
            GunDefaultMeleeData defaultData = meleeData.getDefaultMeleeData();
            return new CacheValue<>(defaultData != null ? defaultData.getDamage() : 0f);
        }
        
        // 检查枪口配件
        ResourceLocation muzzleId = iGun.getAttachmentId(gunItem, AttachmentType.MUZZLE);
        MeleeData muzzleData = getMeleeData(muzzleId);
        if (muzzleData != null) {
            return new CacheValue<>(muzzleData.getDamage());
        }
        
        // 检查枪托配件
        ResourceLocation stockId = iGun.getAttachmentId(gunItem, AttachmentType.STOCK);
        MeleeData stockData = getMeleeData(stockId);
        if (stockData != null) {
            return new CacheValue<>(stockData.getDamage());
        }
        
        // 没有配件，使用默认近战伤害
        GunDefaultMeleeData defaultData = meleeData.getDefaultMeleeData();
        return new CacheValue<>(defaultData != null ? defaultData.getDamage() : 0f);
    }
    
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
        // 参考 TACZ 原版 doMelee 计算逻辑
        // 最终伤害 = 玩家攻击伤害(基础值临时设为0) + 枪械近战伤害(加法修饰符) + 其他攻击伤害修饰符
        
        // 参照 TACZ 方式计算：基础值设为0，然后加上枪械近战伤害和其他修饰符
        // 其他攻击伤害修饰符 = 当前攻击伤害总值 - 基础值
        float otherModifiers = 0f;
        Player player = net.minecraft.client.Minecraft.getInstance().player;
        if (player != null && player.getAttribute(Attributes.ATTACK_DAMAGE) != null) {
            var instance = player.getAttribute(Attributes.ATTACK_DAMAGE);
            otherModifiers = (float) (instance.getValue() - instance.getBaseValue());
        }
        
        // 基础值 = 枪械默认近战伤害（作为加法修饰符的基础值）
        GunMeleeData meleeData = gunData.getMeleeData();
        float baseModifierDamage = 0f;
        if (meleeData != null && meleeData.getDefaultMeleeData() != null) {
            baseModifierDamage = meleeData.getDefaultMeleeData().getDamage();
        }
        
        // 最终值 = 缓存中的枪械近战伤害（含配件加成，作为加法修饰符）
        Float finalModifierDamage = cacheProperty.getCache(MeleeDamageModifier.ID);
        if (finalModifierDamage == null) {
            finalModifierDamage = baseModifierDamage;
        }
        
        // 计算最终显示伤害（参照 TACZ：0 + 枪械近战伤害 + 其他修饰符）
        float baseTotalDamage = otherModifiers + baseModifierDamage;
        float finalTotalDamage = otherModifiers + finalModifierDamage;
        float difference = finalTotalDamage - baseTotalDamage;
        
        // 显示进度条用最终总伤害
        double damagePercent = Math.min(finalTotalDamage / 20.0, 1);
        double damageModifierPercent = Math.min(Math.abs(difference) / 20.0, 1);

        String damageTitleKey = "gui.tacz.gun_refit.property_diagrams.melee_damage";
        String damagePositivelyString = String.format(" %.1f §a(+%.1f)", finalTotalDamage, difference);
        String damageNegativelyString = String.format(" %.1f §c(%.1f)", finalTotalDamage, difference);
        String damageDefaultString = String.format(" %.1f", finalTotalDamage);
        boolean damagePositivelyBetter = true;

        DiagramsData damageDiagramsData = new DiagramsData(
                damagePercent, damageModifierPercent, difference, 
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
