package com.xlxyvergil.taa.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.world.entity.ai.attributes.AttributeInstance;

@Mixin(AttributeInstance.class)
public interface TaaAttributeInstanceAccessor {

    /**
     * Exposes AttributeInstance.cachedValue so we can read it while checking for changes.
     */
    @Accessor("cachedValue")
    public double taaGetCachedValue();

}