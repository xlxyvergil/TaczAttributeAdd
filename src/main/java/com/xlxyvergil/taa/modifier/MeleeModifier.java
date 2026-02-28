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
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

/**
 * 近战距离 Modifier
 * 用于修改枪械的近战距离
 * 基于 TACZ 源代码的标准实现模式
 */
public class MeleeModifier implements IAttachmentModifier<Modifier, Float> {
    public static final String ID = "melee_distance";

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public JsonProperty<Modifier> readJson(String json) {
        Data data = CommonAssetsManager.GSON.fromJson(json, Data.class);
        return new MeleeJsonProperty(data.getMeleeDistance());
    }

    @Override
    public CacheValue<Float> initCache(ItemStack gunItem, GunData gunData) {
        // 获取枪械的近战数据
        GunMeleeData meleeData = gunData.getMeleeData();
        if (meleeData == null) {
            return new CacheValue<>(0.0f);
        }
        
        // 只返回枪械基础距离，不添加默认近战距离
        // 默认近战距离由 TACZ 原版逻辑在 doMelee 中添加
        float baseDistance = meleeData.getDistance();
        
        // 获取当前安装的配件（枪口或枪托）的近战距离
        IGun iGun = IGun.getIGunOrNull(gunItem);
        if (iGun != null) {
            // 检查枪口配件
            ResourceLocation muzzleId = iGun.getAttachmentId(gunItem, AttachmentType.MUZZLE);
            float attachmentDistance = getMeleeAttachmentDistance(muzzleId);
            if (attachmentDistance > 0) {
                baseDistance += attachmentDistance;
            } else {
                // 检查枪托配件
                ResourceLocation stockId = iGun.getAttachmentId(gunItem, AttachmentType.STOCK);
                attachmentDistance = getMeleeAttachmentDistance(stockId);
                if (attachmentDistance > 0) {
                    baseDistance += attachmentDistance;
                }
                // 无配件时不添加默认近战距离，由 mixin 处理
            }
        }
        
        return new CacheValue<>(baseDistance);
    }
    
    /**
     * 获取配件的近战距离
     */
    private float getMeleeAttachmentDistance(ResourceLocation attachmentId) {
        if (DefaultAssets.isEmptyAttachmentId(attachmentId)) {
            return 0.0f;
        }
        return TimelessAPI.getCommonAttachmentIndex(attachmentId)
                .map(CommonAttachmentIndex::getData)
                .map(data -> data.getMeleeData())
                .map(MeleeData::getDistance)
                .orElse(0.0f);
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
        // 基础距离 = 枪械基础距离（作为显示基准）
        GunMeleeData meleeData = gunData.getMeleeData();
        float baseDistance = meleeData != null ? meleeData.getDistance() : 0.0f;
        
        // 计算配件提供的距离（用于显示总变动）
        float attachmentDistance = 0.0f;
        IGun iGun = IGun.getIGunOrNull(gunItem);
        if (iGun != null) {
            // 检查枪口配件
            ResourceLocation muzzleId = iGun.getAttachmentId(gunItem, AttachmentType.MUZZLE);
            float muzzleDistance = getMeleeAttachmentDistance(muzzleId);
            if (muzzleDistance > 0) {
                attachmentDistance = muzzleDistance;
            } else {
                // 检查枪托配件
                ResourceLocation stockId = iGun.getAttachmentId(gunItem, AttachmentType.STOCK);
                float stockDistance = getMeleeAttachmentDistance(stockId);
                if (stockDistance > 0) {
                    attachmentDistance = stockDistance;
                }
            }
        }
        
        // 获取修改后的距离（属性修改后的值，initCache 中已包含配件距离）
        Float modifiedDistance = cacheProperty.getCache(MeleeModifier.ID);
        if (modifiedDistance == null) {
            modifiedDistance = baseDistance + attachmentDistance;
        }
        
        // 总变动值 = 配件距离 + 属性修改带来的变动
        float totalDifference = modifiedDistance - baseDistance;
        
        // 计算近战距离的显示数据
        double distancePercent = Math.min(baseDistance / 5.0, 1);
        double distanceModifierPercent = Math.min(Math.abs(totalDifference) / 5.0, 1);

        String distanceTitleKey = "gui.tacz.gun_refit.property_diagrams.melee_distance";
        String distancePositivelyString = String.format("%.2fm §a(+%.2fm)", modifiedDistance, totalDifference);
        String distanceNegativelyString = String.format("%.2fm §c(%.2fm)", modifiedDistance, totalDifference);
        String distanceDefaultString = String.format("%.2fm", modifiedDistance);
        boolean distancePositivelyBetter = true; // 近战距离越长越好

        DiagramsData distanceDiagramsData = new DiagramsData(
                distancePercent, distanceModifierPercent, totalDifference, 
                distanceTitleKey, distancePositivelyString, distanceNegativelyString, 
                distanceDefaultString, distancePositivelyBetter);

        return Collections.singletonList(distanceDiagramsData);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public int getDiagramsDataSize() {
        return 1;
    }

    public static class MeleeJsonProperty extends JsonProperty<Modifier> {
        public MeleeJsonProperty(Modifier value) {
            super(value);
        }

        @Override
        public void initComponents() {
            Modifier value = getValue();
            if (value != null) {
                double eval = AttachmentPropertyManager.eval(value, 0.0f);
                float distance = (float) eval;
                if (distance > 0.0f) {
                    components.add(Component.translatable("tooltip.tacz.attachment.melee.distance.increase").withStyle(ChatFormatting.GREEN));
                } else if (distance < 0.0f) {
                    components.add(Component.translatable("tooltip.tacz.attachment.melee.distance.decrease").withStyle(ChatFormatting.RED));
                }
            }
        }
    }

    public static class Data {
        @SerializedName("melee_distance")
        @Nullable
        private Modifier meleeDistance = null;

        @Nullable
        public Modifier getMeleeDistance() {
            return meleeDistance;
        }
    }
}