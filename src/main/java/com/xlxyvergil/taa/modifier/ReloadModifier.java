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
        // 初始化时存储默认乘数1.0f
        float reloadMultiplier = 1.0f;
        
        // 尝试使用GunsmithLib的GsHelper来计算RELOAD_SPEED属性
        try {
            // 获取RELOAD_SPEED属性
            Class<?> gunAttributesClass = Class.forName("mod.chloeprime.gunsmithlib.api.common.GunAttributes");
            java.lang.reflect.Field reloadSpeedField = gunAttributesClass.getField("RELOAD_SPEED");
            Object reloadSpeedAttributeObj = reloadSpeedField.get(null);
            
            // 使用GunsmithLib的GsHelper工具类计算属性值
            Class<?> gsHelperClass = Class.forName("mod.chloeprime.gunsmithlib.common.util.GsHelper");
            java.lang.reflect.Method evaluateItemAttributeMethod = gsHelperClass.getMethod(
                "evaluateItemAttribute", 
                ItemStack.class, 
                java.util.function.Supplier.class, 
                double.class
            );
            
            // 创建Supplier函数接口实例
            java.util.function.Supplier<Object> attributeSupplier = () -> {
                try {
                    Class<?> registryObjectClass = Class.forName("net.minecraftforge.registries.RegistryObject");
                    java.lang.reflect.Method getMethod = registryObjectClass.getMethod("get");
                    return getMethod.invoke(reloadSpeedAttributeObj);
                } catch (Exception e) {
                    return null;
                }
            };
            
            // 调用方法计算GunsmithLib修改后的值
            double gunsmithLibModifiedValue = (Double) evaluateItemAttributeMethod.invoke(
                null, 
                gunItem,  // 传入实际的枪械物品
                attributeSupplier, 
                1.0  // 使用1.0作为基础值
            );
            
            // 使用GunsmithLib的值作为我们的基础值
            // 注意：我们需要存储倒数，因为实际计算是 original / multiplier
            reloadMultiplier = (float) (1.0 / gunsmithLibModifiedValue);
        } catch (Exception e) {
            // GunsmithLib不存在或调用失败，使用原始值1.0f
        }
        
        return new CacheValue<>(reloadMultiplier);
    }

    @Override
    public void eval(List<ReloadModifierData> modifiers, CacheValue<Float> cache) {
        // 计算装填时间乘数，直接使用Modifier的加法操作
        double reloadTimeAddition = AttachmentPropertyManager.eval(
                modifiers.stream().map(m -> m.reloadTime).filter(m -> m != null).toList(), 
                0.0f  // 基础值为0，因为我们只关心加法部分
        );
        
        // 获取初始缓存值（可能包含GunsmithLib的修改，已经是倒数形式）
        float baseMultiplier = cache.getValue();
        
        // 将我们的修改转换为倒数形式
        // 使用 1.0 + addition 作为我们的乘数，然后取倒数
        float ourMultiplier = 1.0f + (float) reloadTimeAddition;
        float ourInverseMultiplier = 1.0f / ourMultiplier;
        
        // 总倒数 = GunsmithLib倒数 * 我们的倒数
        cache.setValue(baseMultiplier * ourInverseMultiplier);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public List<DiagramsData> getPropertyDiagramsData(ItemStack gunItem, GunData gunData, AttachmentCacheProperty cacheProperty) {
        float originalTacticalTime = 0.0f; // 默认值
        
        // 添加空值检查，避免空指针异常
        if (gunData.getReloadData() != null && gunData.getReloadData().getFeed() != null) {
            originalTacticalTime = gunData.getReloadData().getFeed().getTacticalTime();
        }
        
        float reloadInverseMultiplier = cacheProperty.<Float>getCache(ReloadModifier.ID);
        // 转换回正常的乘数用于显示
        float reloadMultiplier = 1.0f / reloadInverseMultiplier;
        
        // 尝试获取GunsmithLib的RELOAD_SPEED属性值用于显示
        float gunsmithLibReloadMultiplier = 1.0f;
        try {
            // 获取RELOAD_SPEED属性
            Class<?> gunAttributesClass = Class.forName("mod.chloeprime.gunsmithlib.api.common.GunAttributes");
            java.lang.reflect.Field reloadSpeedField = gunAttributesClass.getField("RELOAD_SPEED");
            Object reloadSpeedAttributeObj = reloadSpeedField.get(null);
            
            // 使用GunsmithLib的GsHelper工具类计算属性值
            Class<?> gsHelperClass = Class.forName("mod.chloeprime.gunsmithlib.common.util.GsHelper");
            java.lang.reflect.Method evaluateItemAttributeMethod = gsHelperClass.getMethod(
                "evaluateItemAttribute", 
                ItemStack.class, 
                java.util.function.Supplier.class, 
                double.class
            );
            
            // 创建Supplier函数接口实例
            java.util.function.Supplier<Object> attributeSupplier = () -> {
                try {
                    Class<?> registryObjectClass = Class.forName("net.minecraftforge.registries.RegistryObject");
                    java.lang.reflect.Method getMethod = registryObjectClass.getMethod("get");
                    return getMethod.invoke(reloadSpeedAttributeObj);
                } catch (Exception e) {
                    return null;
                }
            };
            
            // 调用方法计算GunsmithLib修改后的值
            double gunsmithLibModifiedValue = (Double) evaluateItemAttributeMethod.invoke(
                null, 
                gunItem,  // 传入实际的枪械物品
                attributeSupplier, 
                1.0  // 使用1.0作为基础值
            );
            
            gunsmithLibReloadMultiplier = (float) gunsmithLibModifiedValue;
        } catch (Exception e) {
            // GunsmithLib不存在或调用失败
        }
        
        // 计算总的修改后装填时间
        // 实际装填时间 = 原始装填时间 / (GunsmithLib乘数 * 我们的乘数)
        float totalMultiplier = gunsmithLibReloadMultiplier * reloadMultiplier;
        float modifiedValue = originalTacticalTime / totalMultiplier;
        float timeDifference = modifiedValue - originalTacticalTime;

        // 使用枪械实际的装填时间来计算百分比，而不是硬编码的值
        // 为了避免除以0的情况，设置一个最小值
        float maxReloadTime = Math.max(originalTacticalTime, 0.1f);
        double percent = Math.min(originalTacticalTime / (maxReloadTime * 2), 1);
        double modifierPercent = Math.min(Math.abs(timeDifference) / (maxReloadTime * 2), 1);

        String titleKey = "gui.tacz.gun_refit.property_diagrams.reload_time";
        // 装填时间越短越好，所以时间减少用绿色，时间增加用红色
        if (timeDifference <= 0) {
            // 时间减少（装填加速）用绿色显示
            String positivelyString = String.format("%.2fs §a(%.2fs)", modifiedValue, -timeDifference);
            String defaultString = String.format("%.2fs", modifiedValue);
            DiagramsData diagramsData = new DiagramsData(percent, modifierPercent, timeDifference, titleKey, positivelyString, positivelyString, defaultString, false);
            return Collections.singletonList(diagramsData);
        } else {
            // 时间增加（装填减速）用红色显示
            String negativelyString = String.format("%.2fs §c(%.2fs)", modifiedValue, timeDifference);
            String defaultString = String.format("%.2fs", modifiedValue);
            DiagramsData diagramsData = new DiagramsData(percent, modifierPercent, timeDifference, titleKey, negativelyString, negativelyString, defaultString, false);
            return Collections.singletonList(diagramsData);
        }
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