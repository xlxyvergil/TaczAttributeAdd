package com.xlxyvergil.taa.modifier;

import com.google.gson.annotations.SerializedName;
import com.tacz.guns.api.GunProperties;
import com.tacz.guns.api.modifier.CacheValue;
import com.tacz.guns.api.modifier.IAttachmentModifier;
import com.tacz.guns.api.modifier.JsonProperty;
import com.tacz.guns.resource.CommonAssetsManager;
import com.tacz.guns.resource.modifier.AttachmentCacheProperty;
import com.tacz.guns.resource.modifier.AttachmentPropertyManager;
import com.tacz.guns.resource.pojo.data.attachment.Modifier;
import com.tacz.guns.resource.pojo.data.gun.GunData;
import com.tacz.guns.resource.pojo.data.gun.GunReloadData;
import com.tacz.guns.resource.pojo.data.gun.GunReloadTime;
import com.xlxyvergil.taa.api.ExtendedGunProperties;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

/**
 * 装填时间 Modifier
 * 用于修改枪械的装填时间属性
 * 完全遵循TACZ配件系统的标准模式
 */
public class ReloadModifier implements IAttachmentModifier<ReloadModifier.ReloadModifierData, Float> {
    // 使用字符串常量作为ID，避免架构重复
    public static final String ID = "reload_time";

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public JsonProperty<ReloadModifierData> readJson(String json) {
        ReloadModifier.Data data = CommonAssetsManager.GSON.fromJson(json, ReloadModifier.Data.class);
        return new ReloadModifier.ReloadJsonProperty(data.getReloadModifier());
    }

    @Override
    public CacheValue<Float> initCache(ItemStack gunItem, GunData gunData) {
        // 使用战术装填时间作为基础值，因为这是最常见的换弹情况
        float tacticalTime = 0.0f; // 默认值
        
        // 添加空值检查，避免空指针异常
        if (gunData.getReloadData() != null && gunData.getReloadData().getFeed() != null) {
            tacticalTime = gunData.getReloadData().getFeed().getTacticalTime();
        }
        
        return new CacheValue<>(tacticalTime);
    }

    @Override
    public void eval(List<ReloadModifierData> modifiers, CacheValue<Float> cache) {
        // 计算装填时间加成
        double reloadTimeEval = AttachmentPropertyManager.eval(
                modifiers.stream().map(m -> m.reloadTime).filter(m -> m != null).toList(), 
                cache.getValue()
        );
        
        cache.setValue((float) reloadTimeEval);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public List<DiagramsData> getPropertyDiagramsData(ItemStack gunItem, GunData gunData, AttachmentCacheProperty cacheProperty) {
        float originalTacticalTime = 0.0f; // 默认值
        
        // 添加空值检查，避免空指针异常
        if (gunData.getReloadData() != null && gunData.getReloadData().getFeed() != null) {
            originalTacticalTime = gunData.getReloadData().getFeed().getTacticalTime();
        }
        
        float modifiedValue = cacheProperty.<Float>getCache(ReloadModifier.ID);
        float timeDifference = modifiedValue - originalTacticalTime;

        // 使用枪械实际的装填时间来计算百分比，而不是硬编码的值
        // 为了避免除以0的情况，设置一个最小值
        float maxReloadTime = Math.max(originalTacticalTime, 0.1f);
        double percent = Math.min(originalTacticalTime / (maxReloadTime * 2), 1);
        double modifierPercent = Math.min(Math.abs(timeDifference) / (maxReloadTime * 2), 1);

        String titleKey = "gui.tacz.gun_refit.property_diagrams.reload_time";
        String positivelyString = String.format("%.2fs §c(%.2fs)", modifiedValue, timeDifference);
        String negativelyString = String.format("%.2fs §a(%.2fs)", modifiedValue, -timeDifference);
        String defaultString = String.format("%.2fs", modifiedValue);
        boolean positivelyBetter = false; // 装填时间越短越好

        DiagramsData diagramsData = new DiagramsData(percent, modifierPercent, timeDifference, titleKey, positivelyString, negativelyString, defaultString, positivelyBetter);
        return Collections.singletonList(diagramsData);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public int getDiagramsDataSize() {
        return 1;
    }

    /**
     * 装填修改器数据
     * 按照TACZ标准模式，简化为单个Modifier
     */
    public static class ReloadModifierData {
        @SerializedName("reload_time")
        private Modifier reloadTime = null;

        public Modifier getReloadTime() { return reloadTime; }
    }

    public static class ReloadJsonProperty extends JsonProperty<ReloadModifierData> {
        public ReloadJsonProperty(ReloadModifierData value) {
            super(value);
        }

        @Override
        public void initComponents() {
            ReloadModifierData value = getValue();
            if (value != null && value.getReloadTime() != null) {
                // 不使用硬编码的默认值，而是仅根据Modifier是否存在来判断是否有变化
                // 这样可以避免因为不同枪械的基础装填时间不同而导致的显示问题
                components.add(Component.translatable("tooltip.tacz.attachment.reload_time.change").withStyle(ChatFormatting.GOLD));
            }
        }
    }

    public static class Data {
        @SerializedName("reload_time")
        @Nullable
        private ReloadModifierData reloadModifier = null;

        @Nullable
        public ReloadModifierData getReloadModifier() {
            return reloadModifier;
        }
    }
}