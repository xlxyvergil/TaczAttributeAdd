package com.xlxyvergil.taa.mixin;

import com.tacz.guns.item.ModernKineticGunScriptAPI;
import com.xlxyvergil.taa.context.ShooterContext;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * ScriptAPI 的各入口方法自带宽限的射击者，进入时压入上下文，
 * 使其内部对 GunData 子对象的读取（热量、弹匣容量）自动带上玩家属性。
 */
@Mixin(value = ModernKineticGunScriptAPI.class, remap = false)
public class ModernKineticGunScriptAPIContextMixin {

    @Shadow
    private LivingEntity shooter;

    @Unique
    private void taa$pushShooter() {
        ShooterContext.pushShooter(this.shooter);
    }

    @Unique
    private void taa$popShooter() {
        ShooterContext.popShooter();
    }

    // ========== 射击与蓄热 ==========

    @Inject(method = "shootOnce", at = @At("HEAD"))
    private void taa$shootOnceHead(boolean consumeAmmo, CallbackInfo ci) {
        taa$pushShooter();
    }

    @Inject(method = "shootOnce", at = @At("RETURN"))
    private void taa$shootOnceReturn(boolean consumeAmmo, CallbackInfo ci) {
        taa$popShooter();
    }

    @Inject(method = "handleShootHeat", at = @At("HEAD"))
    private void taa$handleShootHeatHead(CallbackInfo ci) {
        taa$pushShooter();
    }

    @Inject(method = "handleShootHeat", at = @At("RETURN"))
    private void taa$handleShootHeatReturn(CallbackInfo ci) {
        taa$popShooter();
    }

    // ========== 供 Lua 读取的热量相关 getter ==========

    @Inject(method = "getHeatMax", at = @At("HEAD"))
    private void taa$getHeatMaxHead(CallbackInfoReturnable<Float> cir) {
        taa$pushShooter();
    }

    @Inject(method = "getHeatMax", at = @At("RETURN"))
    private void taa$getHeatMaxReturn(CallbackInfoReturnable<Float> cir) {
        taa$popShooter();
    }

    @Inject(method = "getOverheatTime", at = @At("HEAD"))
    private void taa$getOverheatTimeHead(CallbackInfoReturnable<Long> cir) {
        taa$pushShooter();
    }

    @Inject(method = "getOverheatTime", at = @At("RETURN"))
    private void taa$getOverheatTimeReturn(CallbackInfoReturnable<Long> cir) {
        taa$popShooter();
    }

    @Inject(method = "getCoolingDelay", at = @At("HEAD"))
    private void taa$getCoolingDelayHead(CallbackInfoReturnable<Long> cir) {
        taa$pushShooter();
    }

    @Inject(method = "getCoolingDelay", at = @At("RETURN"))
    private void taa$getCoolingDelayReturn(CallbackInfoReturnable<Long> cir) {
        taa$popShooter();
    }

    @Inject(method = "calcHeatReduction", at = @At("HEAD"))
    private void taa$calcHeatReductionHead(long heatTimestamp, CallbackInfoReturnable<Float> cir) {
        taa$pushShooter();
    }

    @Inject(method = "calcHeatReduction", at = @At("RETURN"))
    private void taa$calcHeatReductionReturn(long heatTimestamp, CallbackInfoReturnable<Float> cir) {
        taa$popShooter();
    }

    // ========== 弹匣容量相关入口 ==========

    @Inject(method = "getNeededAmmoAmount", at = @At("HEAD"))
    private void taa$getNeededAmmoAmountHead(CallbackInfoReturnable<Integer> cir) {
        taa$pushShooter();
    }

    @Inject(method = "getNeededAmmoAmount", at = @At("RETURN"))
    private void taa$getNeededAmmoAmountReturn(CallbackInfoReturnable<Integer> cir) {
        taa$popShooter();
    }

    @Inject(method = "getMaxAmmoCount", at = @At("HEAD"))
    private void taa$getMaxAmmoCountHead(CallbackInfoReturnable<Integer> cir) {
        taa$pushShooter();
    }

    @Inject(method = "getMaxAmmoCount", at = @At("RETURN"))
    private void taa$getMaxAmmoCountReturn(CallbackInfoReturnable<Integer> cir) {
        taa$popShooter();
    }

    @Inject(method = "putAmmoInMagazine", at = @At("HEAD"))
    private void taa$putAmmoInMagazineHead(int amount, CallbackInfoReturnable<Integer> cir) {
        taa$pushShooter();
    }

    @Inject(method = "putAmmoInMagazine", at = @At("RETURN"))
    private void taa$putAmmoInMagazineReturn(int amount, CallbackInfoReturnable<Integer> cir) {
        taa$popShooter();
    }
}
