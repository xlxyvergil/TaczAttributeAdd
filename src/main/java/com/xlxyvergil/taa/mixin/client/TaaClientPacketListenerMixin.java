package com.xlxyvergil.taa.mixin.client;

import com.tacz.guns.resource.modifier.AttachmentPropertyManager;
import com.xlxyvergil.taa.TaczAttributeAdd;
import com.xlxyvergil.taa.network.message.ServerMessageUpdateTacZCache;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundUpdateAttributesPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateAttributesPacket.AttributeSnapshot;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Iterator;

@Mixin(ClientPacketListener.class)
public class TaaClientPacketListenerMixin {

    /**
     * 记录属性包应用新的客户端修饰符之前的旧属性值。
     */
    private double taaLastValue;

    @Inject(at = @At(value = "INVOKE", target = "net/minecraft/world/entity/ai/attributes/AttributeInstance.setBaseValue(D)V"), 
            method = "handleUpdateAttributes(Lnet/minecraft/network/protocol/game/ClientboundUpdateAttributesPacket;)V", 
            require = 1, locals = LocalCapture.CAPTURE_FAILHARD)
    public void taaRecordOldAttrValue(ClientboundUpdateAttributesPacket packet, CallbackInfo ci, Entity entity, AttributeMap map, 
                                      Iterator<AttributeSnapshot> it, AttributeSnapshot snapshot, AttributeInstance inst) {
        this.taaLastValue = inst.getValue();
    }

    /**
     * 在清除并重应用所有客户端属性修饰符之后注入，比较新旧属性值，必要时更新 TACZ 缓存。
     */
    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/attributes/AttributeInstance;addTransientModifier(Lnet/minecraft/world/entity/ai/attributes/AttributeModifier;)V", shift = At.Shift.BY, by = 5), 
            method = "handleUpdateAttributes(Lnet/minecraft/network/protocol/game/ClientboundUpdateAttributesPacket;)V", 
            require = 1, locals = LocalCapture.CAPTURE_FAILHARD)
    public void taaUpdateTacZCache(ClientboundUpdateAttributesPacket packet, CallbackInfo ci, Entity entity, AttributeMap map, 
                                   Iterator<AttributeSnapshot> it, AttributeSnapshot snapshot, AttributeInstance inst) {
        if (inst != null) {
            double newValue = inst.getValue();
            if (newValue != taaLastValue) {
                // 仅处理玩家
                if (entity instanceof Player player) {
                    ItemStack mainHandItem = player.getMainHandItem();
                    ItemStack offHandItem = player.getOffhandItem();
                    
                    // 优先更新主手，其次副手
                    if (mainHandItem.getItem() instanceof com.tacz.guns.api.item.IGun) {
                        AttachmentPropertyManager.postChangeEvent(player, mainHandItem);
                    } else if (offHandItem.getItem() instanceof com.tacz.guns.api.item.IGun) {
                        AttachmentPropertyManager.postChangeEvent(player, offHandItem);
                    } else {
                        AttachmentPropertyManager.postChangeEvent(player, ItemStack.EMPTY);
                    }
                    
                    // 通知服务器更新服务端缓存
                    TaczAttributeAdd.CHANNEL.sendToServer(new ServerMessageUpdateTacZCache());
                }
            }
        }
    }
}