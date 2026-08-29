package com.xlxyvergil.taa.compat.kubejs.events;

import com.xlxyvergil.taa.util.PropertyCalculationResults;
import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;
import dev.latvian.mods.kubejs.event.EventJS;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

/**
 * 属性计算完成后触发，暴露玩家实体和计算结果
 * 脚本修改后由客户端再进行二次缓存更新
 */
public class TAAContextEvents {
    public static final EventGroup GROUP = EventGroup.of("TAAContextEvents");
    
    /**
     * 属性后处理事件
     */
    public static final EventHandler ATTRIBUTE_POST = GROUP.server("attributePost", () -> AttributePostEventJS.class);
    
    public static class AttributePostEventJS extends EventJS {
        private final LivingEntity shooter;
        private final ItemStack gunItem;
        private final PropertyCalculationResults results;
        
        public AttributePostEventJS(LivingEntity shooter, ItemStack gunItem, PropertyCalculationResults results) {
            this.shooter = shooter;
            this.gunItem = gunItem;
            this.results = results;
        }
        
        /**
         * 获取射击者实体
         */
        public LivingEntity getShooter() {
            return shooter;
        }
        
        public ItemStack getGunItem() {
            return gunItem;
        }
        
        /**
         * 获取计算结果，脚本可修改此结果
         */
        public PropertyCalculationResults getResults() {
            return results;
        }
    }
}