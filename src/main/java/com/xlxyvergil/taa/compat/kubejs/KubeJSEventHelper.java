package com.xlxyvergil.taa.compat.kubejs;

import com.xlxyvergil.taa.compat.kubejs.events.TAAPropertyDisplayEvents;
import com.xlxyvergil.taa.compat.kubejs.events.TAAContextEvents;
import com.xlxyvergil.taa.util.PropertyCalculationResults;
import dev.latvian.mods.kubejs.script.ScriptType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModList;

/**
 * KubeJS 事件触发工具类
 * <p>
 * 把本模组的两个扩展点包装成 KubeJS 事件：属性显示事件（客户端脚本，改面板上显示的数字）
 * 与属性后处理事件（服务端脚本，在写回缓存前做最后一轮修正）。
 * <p>
 * KubeJS 是软依赖：每个入口都先查 ModList 里有没有 kubejs，没装就直接返回原值或直接跳过，
 * 主流程不用关心它装没装。触发时必须指定正确的 ScriptType——客户端脚本用 CLIENT、服务端脚本用
 * SERVER，端传错了对应脚本里根本收不到这次事件。
 */
public class KubeJSEventHelper {
    
    /**
     * 触发属性显示事件并返回修改后的显示值（没被脚本改过就是入参原值）
     * 面板每画一行属性都会调一次，属于高频路径：KubeJS 没装时提前返回，连事件对象都不创建；
     * 脚本抛异常也在这里兜住并回退成原值，不让脚本问题把面板搞崩。
     */
    public static double postAndGetDisplayValue(LivingEntity shooter, ItemStack gunItem, String propertyType, double displayValue, double originalValue) {
        try {
            if (!ModList.get().isLoaded("kubejs")) {
                return displayValue;
            }

            TAAPropertyDisplayEvents.PropertyDisplayEventJS event = 
                new TAAPropertyDisplayEvents.PropertyDisplayEventJS(shooter, gunItem, propertyType, displayValue, originalValue);

            // 客户端事件必须标 CLIENT，否则写在 client_scripts 里的脚本收不到
            TAAPropertyDisplayEvents.PROPERTY_DISPLAY.post(ScriptType.CLIENT, null, event);

            return event.getDisplayValue();

        } catch (Exception e) {
            // KubeJS 未加载或执行出错时返回原值
            return displayValue;
        }
    }

    /**
     * 属性计算完成后触发后处理事件，允许脚本修改计算结果
     * 调用点在写回 TACZ 缓存之前，所以脚本在这一步改掉的数值会直接体现在最终缓存里。
     */
    public static void postAttributePostEvent(LivingEntity shooter, ItemStack gunItem, PropertyCalculationResults results) {
        if (!ModList.get().isLoaded("kubejs")) {
            return;
        }

        TAAContextEvents.AttributePostEventJS event = 
            new TAAContextEvents.AttributePostEventJS(shooter, gunItem, results);

        // 服务端事件；post 的第二个参数是 KubeJS 的额外上下文，这里不需要，传 null
        TAAContextEvents.ATTRIBUTE_POST.post(ScriptType.SERVER, null, event);
    }
}
