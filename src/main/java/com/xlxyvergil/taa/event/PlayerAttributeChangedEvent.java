package com.xlxyvergil.taa.event;

import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.Cancelable;

/**
 * 玩家属性变化事件
 * 当玩家的任意属性发生变化时触发此事件
 */
@Cancelable
public class PlayerAttributeChangedEvent extends PlayerEvent {
    private final Attribute attribute;
    private final double oldValue;
    private final double newValue;

    public PlayerAttributeChangedEvent(Player player, Attribute attribute, double oldValue, double newValue) {
        super(player);
        this.attribute = attribute;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    /**
     * 获取发生变化的属性
     * @return 发生变化的属性
     */
    public Attribute getAttribute() {
        return attribute;
    }

    /**
     * 获取属性变化前的值
     * @return 属性变化前的值
     */
    public double getOldValue() {
        return oldValue;
    }

    /**
     * 获取属性变化后的值
     * @return 属性变化后的值
     */
    public double getNewValue() {
        return newValue;
    }

    /**
     * 获取属性变化的差值
     * @return 新值减旧值的差值
     */
    public double getDifference() {
        return newValue - oldValue;
    }
}