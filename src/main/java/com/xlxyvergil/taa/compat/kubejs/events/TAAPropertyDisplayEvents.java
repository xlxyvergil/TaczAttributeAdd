package com.xlxyvergil.taa.compat.kubejs.events;

import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

/**
 * TAA属性显示KubeJS事件
 * <p>
 * 面板每画一行属性就触发一次，脚本按 propertyType 认领自己关心的行、改写显示数字；
 * 这里只影响显示，不动 TACZ 缓存里的真实数值（改真实数值走 TAAContextEvents 那条路）。
 * <p>
 * 用 {@code GROUP.client(...)} 注册，只对客户端脚本开放。
 */
public class TAAPropertyDisplayEvents {
    /** 事件组名，脚本里监听时写成 TAAPropertyDisplayEvents.propertyDisplay */
    public static final EventGroup GROUP = EventGroup.of("TAAPropertyDisplayEvents");
    
    /**
     * 在枪械属性面板绘制前触发，允许脚本修改显示值
     * <p>
     * 触发点在 {@code KubeJSEventHelper.postAndGetDisplayValue}：面板行（TaaPanelRows）与弹匣容量
     * （AmmoCapacityHelper）各自算好显示值后调用一次。
     */
    public static final EventHandler PROPERTY_DISPLAY = GROUP.client("propertyDisplay", () -> PropertyDisplayEventJS.class);
    
    /**
     * KubeJS 事件包装类：把一行的上下文交给脚本，再把脚本改过的显示值收回来
     * 除 displayValue 外都是 final，脚本能动的只有这一个数字。
     */
    public static class PropertyDisplayEventJS extends dev.latvian.mods.kubejs.event.EventJS {
        private final LivingEntity shooter;
        private final ItemStack gunItem;
        private final String propertyType;
        private final double originalValue;
        /** 脚本可改的显示值；初始值就是本模组算出来的那个数字。 */
        private double displayValue;
        /** 只要调过 setDisplayValue 就置 true，用来标记这一行有脚本介入过。 */
        private boolean modified;
        
        public PropertyDisplayEventJS(LivingEntity shooter, ItemStack gunItem, String propertyType, double displayValue, double originalValue) {
            this.shooter = shooter;
            this.gunItem = gunItem;
            this.propertyType = propertyType;
            this.displayValue = displayValue;
            this.originalValue = originalValue;
            this.modified = false;
        }
        
        /** 当前观看的玩家（面板绘制时的观看者），脚本可据此按玩家属性重算显示值。 */
        public LivingEntity getShooter() {
            return shooter;
        }
        
        /** 面板里那把枪的物品栈，脚本可从中读 NBT 或枪械数据。 */
        public ItemStack getGunItem() {
            return gunItem;
        }
        
        /**
         * 属性类型键，取值是 TACZ GunProperty 的名字（如 "AMMO_CAPACITY"、"BULLET_COUNT"）
         * 脚本靠它区分自己只处理哪几行。
         */
        public String getPropertyType() {
            return propertyType;
        }
        
        /** 当前显示值；脚本调过 setDisplayValue 之后读到的就是新值。 */
        public double getDisplayValue() {
            return displayValue;
        }
        
        /** 未经脚本改动的原始值，脚本可拿它做对比或按比例调整。 */
        public double getOriginalValue() {
            return originalValue;
        }
        
        /** 设置新的显示值，同时把 modified 置 true；只改显示，不写回缓存。 */
        public void setDisplayValue(double value) {
            this.displayValue = value;
            this.modified = true;
        }
        
        /** 是否被脚本改过（没在监听里调 setDisplayValue 就是 false）。 */
        public boolean isModified() {
            return modified;
        }
    }
}
