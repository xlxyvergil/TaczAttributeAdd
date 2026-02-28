package com.xlxyvergil.taa.mixin;

import com.tacz.guns.item.ModernKineticGunItem;
import com.xlxyvergil.taa.attribute.EntityAttributeRegistry;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * 修改近战攻击的伤害和距离计算
 * 在 TACZ 原版计算完成后应用我们的属性
 */
@Mixin(value = ModernKineticGunItem.class, remap = false)
public class ModernKineticGunItemMeleeMixin {
    
    /**
     * 修改近战伤害
     * 在 doMelee 方法中修改 damage 参数（第6个参数，ordinal=5）
     * 伤害 = 原版伤害 × melee_damage属性
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
            // 伤害用乘法：原版伤害 × 属性值
            return originalDamage * (float) meleeDamageAttr.getValue();
        }
        return originalDamage;
    }
    
    /**
     * 修改近战距离
     * 在 doMelee 方法中修改 meleeDistance 参数（第3个参数，ordinal=2）
     * 距离 = 原版距离 + (melee_distance属性 - 1)
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
            // 距离用加法：原版距离 + (属性值 - 1)，因为属性默认是1
            return originalMeleeDistance + (float) (meleeDistanceAttr.getValue() - 1.0);
        }
        return originalMeleeDistance;
    }
}
