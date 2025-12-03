package com.xlxyvergil.taa.mixin;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.xlxyvergil.taa.util.TaaIEntityOwned;

@Mixin(LivingEntity.class)
public class TaaLivingEntityMixin {
    @Shadow
    private AttributeMap attributes;

    @Inject(at = @At(value = "TAIL"), method = "<init>*", remap = false)
    public void taaOwnedAttrMap(CallbackInfo ci) {
        ((TaaIEntityOwned) attributes).taaSetOwner((LivingEntity) (Object) this);
    }
}