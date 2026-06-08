package com.xlxyvergil.taa.compat.kubejs;

import com.xlxyvergil.taa.compat.kubejs.events.TAAPropertyDisplayEvents;
import com.xlxyvergil.taa.compat.kubejs.events.TAAContextEvents;
import com.xlxyvergil.taa.util.PropertyCalculationResults;
import dev.latvian.mods.kubejs.script.ScriptType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModList;

/**
 * KubeJS事件触发工具类
 */
public class KubeJSEventHelper {
    
    /**
     * 触发属性显示事件并获取修改后的值
     * 
     * @param player 玩家
     * @param gunItem 枪械物品
     * @param propertyType 属性类型字符串
     * @param displayValue 当前显示值
     * @param originalValue 原始值
     * @return 修改后的显示值（如果未被修改则返回原值）
     */
    public static double postAndGetDisplayValue(Player player, ItemStack gunItem, String propertyType, double displayValue, double originalValue) {
        try {
            // 检查KubeJS是否加载
            if (!ModList.get().isLoaded("kubejs")) {
                return displayValue;
            }
            
            // 创建事件实例
            TAAPropertyDisplayEvents.PropertyDisplayEventJS event = 
                new TAAPropertyDisplayEvents.PropertyDisplayEventJS(player, gunItem, propertyType, displayValue, originalValue);
            
            // 触发事件（客户端事件需要指定ScriptType.CLIENT）
            TAAPropertyDisplayEvents.PROPERTY_DISPLAY.post(ScriptType.CLIENT, null, event);
            
            // 返回修改后的值
            return event.getDisplayValue();
            
        } catch (Exception e) {
            // 如果KubeJS未加载或发生错误，返回原值
            return displayValue;
        }
    }
    
    /**
     * 触发属性后处理事件
     * 在 TAA 属性计算完成后触发，允许 KubeJS 脚本获取玩家实体后修改计算结果
     * 
     * @param shooter 玩家/射击者实体
     * @param gunItem 枪械物品
     * @param results 计算结果（KubeJS 可以修改此结果）
     */
    public static void postAttributePostEvent(LivingEntity shooter, ItemStack gunItem, PropertyCalculationResults results) {
        // 检查KubeJS是否加载
        if (!ModList.get().isLoaded("kubejs")) {
            return;
        }
        
        // 创建事件实例
        TAAContextEvents.AttributePostEventJS event = 
            new TAAContextEvents.AttributePostEventJS(shooter, gunItem, results);
        
        // 触发事件（服务端事件）
        TAAContextEvents.ATTRIBUTE_POST.post(ScriptType.SERVER, null, event);
    }
}