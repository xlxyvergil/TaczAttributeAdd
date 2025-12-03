package com.xlxyvergil.taa.mixin;

import com.tacz.guns.resource.modifier.AttachmentPropertyManager;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.xlxyvergil.taa.util.TaaIEntityOwned;

/**
 * Makes the AttributeMap aware of its owning entity for TACZ attribute synchronization.
 */
@Mixin(AttributeMap.class)
public class TaaAttributeMapMixin implements TaaIEntityOwned {

    protected LivingEntity taaOwner;

    @Override
    public LivingEntity taaGetOwner() {
        return taaOwner;
    }

    @Override
    public void taaSetOwner(LivingEntity taaOwner) {
        if (this.taaOwner != null) throw new UnsupportedOperationException("Cannot set the owner when it is already set.");
        if (taaOwner == null) throw new UnsupportedOperationException("Cannot set the owner to null.");
        this.taaOwner = taaOwner;
    }
    
    /**
     * Updates TACZ cache when attributes are modified
     */
    @Inject(at = @At(value = "HEAD"), method = "onAttributeModified(Lnet/minecraft/world/entity/ai/attributes/AttributeInstance;)V", require = 1)
    public void taaOnAttributeModified(AttributeInstance inst, CallbackInfo ci) {
        if (taaOwner == null) return;
        
        if (taaOwner instanceof Player player && !player.level().isClientSide) {
            // Get player held items
            ItemStack mainHandItem = player.getMainHandItem();
            ItemStack offHandItem = player.getOffhandItem();
            
            // Check main hand item
            if (mainHandItem.getItem() instanceof com.tacz.guns.api.item.IGun) {
                AttachmentPropertyManager.postChangeEvent(player, mainHandItem);
                return;
            }
            
            // Check off hand item
            if (offHandItem.getItem() instanceof com.tacz.guns.api.item.IGun) {
                AttachmentPropertyManager.postChangeEvent(player, offHandItem);
                return;
            }
            
            // Update with empty stack if no gun is held
            AttachmentPropertyManager.postChangeEvent(player, ItemStack.EMPTY);
        }
    }
}