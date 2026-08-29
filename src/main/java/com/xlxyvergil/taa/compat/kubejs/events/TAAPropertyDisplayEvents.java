package com.xlxyvergil.taa.compat.kubejs.events;

import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

/**
 * TAA属性显示KubeJS事件
 */
public class TAAPropertyDisplayEvents {
    public static final EventGroup GROUP = EventGroup.of("TAAPropertyDisplayEvents");
    
    /**
     * 在枪械属性面板绘制前触发，允许脚本修改显示值
     */
    public static final EventHandler PROPERTY_DISPLAY = GROUP.client("propertyDisplay", () -> PropertyDisplayEventJS.class);
    
    public static class PropertyDisplayEventJS extends dev.latvian.mods.kubejs.event.EventJS {
        private final LivingEntity shooter;
        private final ItemStack gunItem;
        private final String propertyType;
        private final double originalValue;
        private double displayValue;
        private boolean modified;
        
        public PropertyDisplayEventJS(LivingEntity shooter, ItemStack gunItem, String propertyType, double displayValue, double originalValue) {
            this.shooter = shooter;
            this.gunItem = gunItem;
            this.propertyType = propertyType;
            this.displayValue = displayValue;
            this.originalValue = originalValue;
            this.modified = false;
        }
        
        public LivingEntity getShooter() {
            return shooter;
        }
        
        public ItemStack getGunItem() {
            return gunItem;
        }
        
        public String getPropertyType() {
            return propertyType;
        }
        
        public double getDisplayValue() {
            return displayValue;
        }
        
        public double getOriginalValue() {
            return originalValue;
        }
        
        public void setDisplayValue(double value) {
            this.displayValue = value;
            this.modified = true;
        }
        
        public boolean isModified() {
            return modified;
        }
    }
}
