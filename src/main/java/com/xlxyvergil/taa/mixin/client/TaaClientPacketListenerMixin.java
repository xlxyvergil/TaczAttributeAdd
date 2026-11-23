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

/**
 * 客户端属性同步钩子。
 * <p>
 * 服务端用 {@code ClientboundUpdateAttributesPacket} 下发属性时，原版只会改写本地的
 * {@code AttributeInstance}；TACZ 的配件缓存、以及依赖这些属性的枪械数值不会自动刷新，
 * 面板就会停在旧值上。这里对 {@code ClientPacketListener#handleUpdateAttributes} 成对注入：
 * 先在基础值被改写前记下旧值，再在客户端修饰符全部重应用后比较新旧值，
 * 变了就重算 TACZ 缓存，并通知服务端刷新它那一侧的缓存。
 */
@Mixin(ClientPacketListener.class)
public class TaaClientPacketListenerMixin {

    /**
     * 记录属性包应用新的客户端修饰符之前的旧属性值。
     * <p>
     * 与下面的 {@code taaUpdateTacZCache} 成对使用：本字段只在一次属性包处理内有效，
     * 每个属性条目覆盖写一次，随后立刻被比较掉。
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
     * <p>
     * 注入点位于 {@code handleUpdateAttributes} 里遍历 {@code AttributeSnapshot#getModifiers()}
     * 的 for 循环之后（{@code addTransientModifier} 调用之后再偏移 5 条指令），此时该属性的
     * 客户端修饰符已全部清除并重新应用，{@code inst.getValue()} 就是最终值。
     * <p>
     * 与 {@link #taaRecordOldAttrValue} 配合：值没变就什么也不做，避免每次属性包都触发一次
     * TACZ 的全量重算。
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