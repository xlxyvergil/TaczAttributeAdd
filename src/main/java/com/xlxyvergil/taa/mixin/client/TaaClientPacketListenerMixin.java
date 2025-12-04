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
     * Records the old value of the attribute before the attribute packet begins applying new clientside modifiers
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
     * Injected after the for loop iterating {@link AttributeSnapshot#getModifiers()}, which is when after all client attribute modifiers have been cleared and
     * reapplied.
     * <p>
     * Responsible for comparing {@link #taaLastValue} to the new value, and updating TACZ cache if necessary.
     */
    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/attributes/AttributeInstance;addTransientModifier(Lnet/minecraft/world/entity/ai/attributes/AttributeModifier;)V", shift = At.Shift.BY, by = 5), 
            method = "handleUpdateAttributes(Lnet/minecraft/network/protocol/game/ClientboundUpdateAttributesPacket;)V", 
            require = 1, locals = LocalCapture.CAPTURE_FAILHARD)
    public void taaUpdateTacZCache(ClientboundUpdateAttributesPacket packet, CallbackInfo ci, Entity entity, AttributeMap map, 
                                   Iterator<AttributeSnapshot> it, AttributeSnapshot snapshot, AttributeInstance inst) {
        if (inst != null) {
            double newValue = inst.getValue();
            if (newValue != taaLastValue) {
                // Only update if the entity is a player
                if (entity instanceof Player player) {
                    // Get player held items
                    ItemStack mainHandItem = player.getMainHandItem();
                    ItemStack offHandItem = player.getOffhandItem();
                    
                    // Check main hand item
                    if (mainHandItem.getItem() instanceof com.tacz.guns.api.item.IGun) {
                        AttachmentPropertyManager.postChangeEvent(player, mainHandItem);
                    }
                    
                    // Check off hand item
                    else if (offHandItem.getItem() instanceof com.tacz.guns.api.item.IGun) {
                        AttachmentPropertyManager.postChangeEvent(player, offHandItem);
                    }
                    
                    // Update with empty stack if no gun is held
                    else {
                        AttachmentPropertyManager.postChangeEvent(player, ItemStack.EMPTY);
                    }
                    
                    // Send message to server to update server-side cache
                    TaczAttributeAdd.CHANNEL.sendToServer(new ServerMessageUpdateTacZCache());
                }
            }
        }
    }
}