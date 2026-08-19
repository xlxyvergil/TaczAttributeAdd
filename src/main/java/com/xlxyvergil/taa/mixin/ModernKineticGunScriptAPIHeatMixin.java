package com.xlxyvergil.taa.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.tacz.guns.item.ModernKineticGunScriptAPI;
import com.tacz.guns.resource.pojo.data.gun.GunHeatData;
import com.xlxyvergil.taa.util.HeatAttributeHelper;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * 过热体系属性修饰符 - ModernKineticGunScriptAPI 注入
 * 覆盖：
 * 1. Java 蓄热路径（handleShootHeat 的热量上限）
 * 2. Java 热散射路径（shootOnce 的热量上限）
 * 3. Lua 脚本路径（getHeatMax / getCoolingDelay / getOverheatTime / calcHeatReduction）
 * 所有修改均为 原始值 × 实体属性倍率
 */
@Mixin(value = ModernKineticGunScriptAPI.class, remap = false)
public class ModernKineticGunScriptAPIHeatMixin {

    @Shadow
    private LivingEntity shooter;

    // ========== Java 蓄热路径：handleShootHeat ==========

    /**
     * 蓄热时 clamp 上限：min(heat + per_shot, 修正后的max)
     */
    @Redirect(
        method = "handleShootHeat",
        at = @At(value = "INVOKE", target = "Lcom/tacz/guns/resource/pojo/data/gun/GunHeatData;getHeatMax()F", ordinal = 0)
    )
    private float modifyShootHeatMaxForClamp(GunHeatData heatData) {
        return HeatAttributeHelper.getModifiedHeatMax(shooter, heatData.getHeatMax());
    }

    /**
     * 蓄热时锁定判断：newHeat >= 修正后的max
     */
    @Redirect(
        method = "handleShootHeat",
        at = @At(value = "INVOKE", target = "Lcom/tacz/guns/resource/pojo/data/gun/GunHeatData;getHeatMax()F", ordinal = 1)
    )
    private float modifyShootHeatMaxForLock(GunHeatData heatData) {
        return HeatAttributeHelper.getModifiedHeatMax(shooter, heatData.getHeatMax());
    }

    // ========== Java 热散射路径：shootOnce ==========

    /**
     * 热散射百分比的分母（heatMax）使用修正后的上限，保持与蓄热一致
     * 注：该值仍会继续经过 modifyProperty(MAX_HEAT) 供 Lua 脚本调整
     */
    @Redirect(
        method = "shootOnce",
        at = @At(value = "INVOKE", target = "Lcom/tacz/guns/resource/pojo/data/gun/GunHeatData;getHeatMax()F")
    )
    private float modifyShootOnceHeatMax(GunHeatData heatData) {
        return HeatAttributeHelper.getModifiedHeatMax(shooter, heatData.getHeatMax());
    }

    // ========== Lua 脚本路径：ScriptAPI getter ==========

    /**
     * Lua 脚本读取热量上限时返回修正后的值
     */
    @ModifyReturnValue(method = "getHeatMax", at = @At("RETURN"))
    private float modifyGetHeatMax(float original) {
        return HeatAttributeHelper.getModifiedHeatMax(shooter, original);
    }

    /**
     * Lua 脚本读取冷却延迟时返回修正后的值
     */
    @ModifyReturnValue(method = "getCoolingDelay", at = @At("RETURN"))
    private long modifyGetCoolingDelay(long original) {
        return HeatAttributeHelper.getModifiedCoolingDelay(shooter, original);
    }

    /**
     * Lua 脚本读取锁枪时间时返回修正后的值
     */
    @ModifyReturnValue(method = "getOverheatTime", at = @At("RETURN"))
    private long modifyGetOverheatTime(long original) {
        return HeatAttributeHelper.getModifiedOverHeatTime(shooter, original);
    }

    /**
     * Lua 脚本计算散热值时返回修正后的值（乘散热速度倍率）
     */
    @ModifyReturnValue(method = "calcHeatReduction", at = @At("RETURN"))
    private float modifyCalcHeatReduction(float original) {
        return HeatAttributeHelper.getModifiedCoolingMultiplier(shooter, original);
    }
}
