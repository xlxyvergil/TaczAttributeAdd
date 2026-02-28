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
 * 仅计算配件提供的近战距离，属性计算由 PropertyCalculator 处理
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
        GunMeleeData meleeData = gunData.getMeleeData();
        if (meleeData == null) {
            return new CacheValue<>(0.0f);
        }
        
        // 只计算枪械基础距离 + 配件距离
        float baseDistance = meleeData.getDistance();
        
        IGun iGun = IGun.getIGunOrNull(gunItem);
        if (iGun != null) {
            ResourceLocation muzzleId = iGun.getAttachmentId(gunItem, AttachmentType.MUZZLE);
            float attachmentDistance = getMeleeAttachmentDistance(muzzleId);
            if (attachmentDistance > 0) {
                baseDistance += attachmentDistance;
            } else {
                ResourceLocation stockId = iGun.getAttachmentId(gunItem, AttachmentType.STOCK);
                attachmentDistance = getMeleeAttachmentDistance(stockId);
                if (attachmentDistance > 0) {
                    baseDistance += attachmentDistance;
                }
            }
        }
        
        return new CacheValue<>(baseDistance);
    }
    
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
        // 基础值 = data 中的原始近战距离
        GunMeleeData meleeData = gunData.getMeleeData();
        float baseDistance = meleeData != null ? meleeData.getDistance() : 0.0f;
        
        // 最终值 = 缓存中的值（已包含配件加成）
        Float modifiedDistance = cacheProperty.getCache(MeleeModifier.ID);
        if (modifiedDistance == null) {
            modifiedDistance = baseDistance;
        }
        
        // 变动值 = 配件提供的加成
        float difference = modifiedDistance - baseDistance;
        
        double distancePercent = Math.min(baseDistance / 5.0, 1);
        double distanceModifierPercent = Math.min(Math.abs(difference) / 5.0, 1);

        String distanceTitleKey = "gui.tacz.gun_refit.property_diagrams.melee_distance";
        String distancePositivelyString = String.format("%.2fm §a(+%.2fm)", modifiedDistance, difference);
        String distanceNegativelyString = String.format("%.2fm §c(%.2fm)", modifiedDistance, difference);
        String distanceDefaultString = String.format("%.2fm", modifiedDistance);
        boolean distancePositivelyBetter = true;

        DiagramsData distanceDiagramsData = new DiagramsData(
                distancePercent, distanceModifierPercent, difference, 
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
