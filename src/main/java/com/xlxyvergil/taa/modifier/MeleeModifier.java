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
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
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
        // 返回基础的近战距离
        return new CacheValue<>(meleeData.getDistance());
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
        // 获取原始近战距离
        GunMeleeData originalMeleeData = gunData.getMeleeData();
        float originalDistance = originalMeleeData != null ? originalMeleeData.getDistance() : 0.0f;
        
        // 获取修改后的距离
        Float modifiedDistance = cacheProperty.getCache(MeleeModifier.ID);
        if (modifiedDistance == null) {
            modifiedDistance = originalDistance;
        }
        
        float distanceDifference = modifiedDistance - originalDistance;
        
        // 计算近战距离的显示数据
        double distancePercent = Math.min(originalDistance / 5.0, 1);
        double distanceModifierPercent = Math.min(Math.abs(distanceDifference) / 5.0, 1);

        String distanceTitleKey = "gui.tacz.gun_refit.property_diagrams.melee_distance";
        String distancePositivelyString = String.format("%.2fm §a(+%.2fm)", modifiedDistance, distanceDifference);
        String distanceNegativelyString = String.format("%.2fm §c(%.2fm)", modifiedDistance, distanceDifference);
        String distanceDefaultString = String.format("%.2fm", modifiedDistance);
        boolean distancePositivelyBetter = true; // 近战距离越长越好

        DiagramsData distanceDiagramsData = new DiagramsData(
                distancePercent, distanceModifierPercent, distanceDifference, 
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