package com.xlxyvergil.taa.compat.kubejs;

import com.xlxyvergil.taa.compat.kubejs.events.TAAPropertyDisplayEvents;
import com.xlxyvergil.taa.compat.kubejs.events.TAAContextEvents;
import com.xlxyvergil.taa.util.PropertyCalculationResults;
import dev.latvian.mods.kubejs.script.ScriptType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModList;

/**
 * KubeJS事件触发工具类
 */
public class KubeJSEventHelper {
    
    /**
     * 触发属性显示事件并返回修改后的显示值
     */
    public static double postAndGetDisplayValue(LivingEntity shooter, ItemStack gunItem, String propertyType, double displayValue, double originalValue) {
        try {
            if (!ModList.get().isLoaded("kubejs")) {
                return displayValue;
            }

            TAAPropertyDisplayEvents.PropertyDisplayEventJS event = 
                new TAAPropertyDisplayEvents.PropertyDisplayEventJS(shooter, gunItem, propertyType, displayValue, originalValue);

            // 客户端事件，需指定 ScriptType.CLIENT
            TAAPropertyDisplayEvents.PROPERTY_DISPLAY.post(ScriptType.CLIENT, null, event);

            return event.getDisplayValue();

        } catch (Exception e) {
            // KubeJS 未加载或执行出错时返回原值
            return displayValue;
        }
    }

    /**
     * 属性计算完成后触发后处理事件，允许脚本修改计算结果
     */
    public static void postAttributePostEvent(LivingEntity shooter, ItemStack gunItem, PropertyCalculationResults results) {
        if (!ModList.get().isLoaded("kubejs")) {
            return;
        }

        TAAContextEvents.AttributePostEventJS event = 
            new TAAContextEvents.AttributePostEventJS(shooter, gunItem, results);

        // 服务端事件
        TAAContextEvents.ATTRIBUTE_POST.post(ScriptType.SERVER, null, event);
    }
}
