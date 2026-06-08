package com.xlxyvergil.taa.compat.kubejs.events;

import com.xlxyvergil.taa.util.PropertyCalculationResults;
import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;
import dev.latvian.mods.kubejs.event.EventJS;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

/**
 * TAA 属性后处理事件
 * 在 TAA 属性计算完成后触发，暴露玩家实体和计算结果
 * 允许 KubeJS 脚本获取玩家实体后，修改计算结果，然后由我们进行二次缓存更新
 */
public class TAAContextEvents {
    public static final EventGroup GROUP = EventGroup.of("TAAContextEvents");
    
    /**
     * 属性后处理事件
     * 在属性计算完成后触发，暴露玩家实体和计算结果
     */
    public static final EventHandler ATTRIBUTE_POST = GROUP.server("attributePost", () -> AttributePostEventJS.class);
    
    /**
     * KubeJS事件包装类
     */
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
         * 获取玩家/射击者实体
         * KubeJS 脚本可以通过此实体获取玩家的自定义属性
         */
        public LivingEntity getShooter() {
            return shooter;
        }
        
        /**
         * 获取枪械物品
         */
        public ItemStack getGunItem() {
            return gunItem;
        }
        
        /**
         * 获取计算结果
         * KubeJS 脚本可以修改此结果，修改后的结果会被二次缓存更新
         */
        public PropertyCalculationResults getResults() {
            return results;
        }
    }
}