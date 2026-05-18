package com.xlxyvergil.taa.compat.kubejs.events;

import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * TAA属性显示KubeJS事件
 */
public class TAAPropertyDisplayEvents {
    public static final EventGroup GROUP = EventGroup.of("TAAPropertyDisplayEvents");
    
    /**
     * 属性显示值修改事件
     * 在枪械属性面板绘制前触发，允许KubeJS脚本修改显示值
     */
    public static final EventHandler PROPERTY_DISPLAY = GROUP.client("propertyDisplay", () -> PropertyDisplayEventJS.class);
    
    /**
     * KubeJS事件包装类
     */
    public static class PropertyDisplayEventJS extends dev.latvian.mods.kubejs.event.EventJS {
        private final Player player;
        private final ItemStack gunItem;
        private final String propertyType;
        private final double originalValue;
        private double displayValue;
        private boolean modified;
        
        public PropertyDisplayEventJS(Player player, ItemStack gunItem, String propertyType, double displayValue, double originalValue) {
            this.player = player;
            this.gunItem = gunItem;
            this.propertyType = propertyType;
            this.displayValue = displayValue;
            this.originalValue = originalValue;
            this.modified = false;
        }
        
        /**
         * 获取玩家
         */
        public Player getPlayer() {
            return player;
        }
        
        /**
         * 获取枪械物品
         */
        public ItemStack getGunItem() {
            return gunItem;
        }
        
        /**
         * 获取属性类型字符串
         */
        public String getPropertyType() {
            return propertyType;
        }
        
        /**
         * 获取当前显示值
         */
        public double getDisplayValue() {
            return displayValue;
        }
        
        /**
         * 获取原始值（未修改前的值）
         */
        public double getOriginalValue() {
            return originalValue;
        }
        
        /**
         * 设置新的显示值
         */
        public void setDisplayValue(double value) {
            this.displayValue = value;
            this.modified = true;
        }
        
        /**
         * 是否已被修改
         */
        public boolean isModified() {
            return modified;
        }
    }
}
