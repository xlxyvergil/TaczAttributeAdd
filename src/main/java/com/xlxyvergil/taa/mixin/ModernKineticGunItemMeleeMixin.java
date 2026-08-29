package com.xlxyvergil.taa.mixin;

import com.tacz.guns.item.ModernKineticGunItem;
import com.xlxyvergil.taa.attribute.EntityAttributeRegistry;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * 在 doMelee 中应用近战伤害与距离属性。
 */
@Mixin(value = ModernKineticGunItem.class, remap = false)
public class ModernKineticGunItemMeleeMixin {
    
    /**
     * 修改 damage 参数（ordinal=5）：原版伤害 × 近战伤害属性。
     */
    @ModifyVariable(
        method = "doMelee",
        at = @At(value = "HEAD"),
        ordinal = 5
    )
    private float modifyMeleeDamage(float originalDamage, LivingEntity user, float gunDistance, float meleeDistance, 
                                    float rangeAngle, float knockback, float damage, java.util.List effects) {
        AttributeInstance meleeDamageAttr = user.getAttribute(EntityAttributeRegistry.MELEE_DAMAGE.get());
        if (meleeDamageAttr != null) {
            // 伤害用乘法
            return originalDamage * (float) meleeDamageAttr.getValue();
        }
        return originalDamage;
    }
    
    /**
     * 修改 meleeDistance 参数（ordinal=2）：原版距离 + (近战距离属性 - 1)。
     */
    @ModifyVariable(
        method = "doMelee",
        at = @At(value = "HEAD"),
        ordinal = 2
    )
    private float modifyMeleeDistance(float originalMeleeDistance, LivingEntity user, float gunDistance, float meleeDistance, 
                                      float rangeAngle, float knockback, float damage, java.util.List effects) {
        AttributeInstance meleeDistanceAttr = user.getAttribute(EntityAttributeRegistry.MELEE_DISTANCE.get());
        if (meleeDistanceAttr != null) {
            // 距离用加法，属性默认值为 1
            return originalMeleeDistance + (float) (meleeDistanceAttr.getValue() - 1.0);
        }
        return originalMeleeDistance;
    }
}
