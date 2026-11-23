package com.xlxyvergil.taa.compat.kubejs.events;

import com.xlxyvergil.taa.util.PropertyCalculationResults;
import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;
import dev.latvian.mods.kubejs.event.EventJS;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

/**
 * TAA 属性后处理事件的脚本侧定义。
 * <p>
 * 在 TAA 属性计算完成、写回 TACZ 缓存之前触发，把射击者、枪械物品和计算结果一起交给脚本；
 * 脚本改过 {@code results} 之后，由 PropertyCacheUpdater 用新值刷新缓存。
 */
public class TAAContextEvents {
    /** 事件组名，脚本里监听时写成 TAAContextEvents.attributePost */
    public static final EventGroup GROUP = EventGroup.of("TAAContextEvents");
    
    /**
     * 属性后处理事件
     * <p>
     * 用 {@code GROUP.server(...)} 注册，只对服务端脚本开放；触发点在
     * {@code KubeJSEventHelper.postAttributePostEvent}，由 AttachmentPropertyEventHandler 在写回缓存之前调用。
     */
    public static final EventHandler ATTRIBUTE_POST = GROUP.server("attributePost", () -> AttributePostEventJS.class);
    
    /**
     * KubeJS 事件包装类：把三个字段交给脚本读取
     * 字段都在构造时一次性传入、没有 setter；脚本要改数值就经 getResults() 结果对象调它的 setXxx。
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
         * 射击者实体，脚本可据此读玩家的自定义属性或另作判断
         * 可能为 null（那条路径上没有射手上下文时），脚本里需要先判空。
         */
        public LivingEntity getShooter() {
            return shooter;
        }
        
        /** 本次重算对应的枪械物品；可能是空栈（玩家没持枪时也会触发一次重算） */
        public ItemStack getGunItem() {
            return gunItem;
        }
        
        /**
         * 计算结果对象，脚本可在它上面调 setXxx 直接改数值
         * 改完由调用方（PropertyCacheUpdater）写回 TACZ 缓存，脚本不用自己刷新。
         */
        public PropertyCalculationResults getResults() {
            return results;
        }
    }
}