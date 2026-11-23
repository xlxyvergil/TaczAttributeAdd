package com.xlxyvergil.taa.event;

import com.tacz.guns.api.event.common.AttachmentPropertyEvent;
import com.tacz.guns.resource.modifier.AttachmentCacheProperty;
import com.xlxyvergil.taa.compat.kubejs.KubeJSEventHelper;
import com.xlxyvergil.taa.context.ShooterContext;
import com.xlxyvergil.taa.context.GunTypeContext;
import com.xlxyvergil.taa.util.EntityAttributeHelper;
import com.xlxyvergil.taa.util.PropertyCalculator;
import com.xlxyvergil.taa.util.PropertyCalculationResults;
import com.xlxyvergil.taa.util.PropertyCacheUpdater;

import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;

/**
 * TACZ 配件属性重算的接管点。
 * <p>
 * {@code AttachmentPropertyEvent} 由 TACZ 的 {@code AttachmentPropertyManager#postChangeEvent} 在重算配件属性时
 * 同步派发，本项目在那次调用期间已经压好了射手与枪型（见 {@code AttachmentPropertyManagerMixin}），
 * 这里取出来交给 {@link EntityAttributeHelper}，按玩家属性值算出一整套新数值再写回缓存。
 * <p>
 * 客户端与服务端都走这一条：客户端刷新面板/提示显示用的缓存，服务端重算结算伤害用的缓存。
 */
@Mod.EventBusSubscriber
public class AttachmentPropertyEventHandler {
    
    /**
     * 用上下文里的射手与枪型重算全部属性，并写回 TACZ 的配件缓存
     * 射手取不到时属性全部按 1.0 处理（等于不加成），枪型取不到时只吃通用伤害属性。
     */
    @SubscribeEvent
    public static void onAttachmentPropertyEvent(AttachmentPropertyEvent event) {
        // 射手与枪型由 AttachmentPropertyManagerMixin 在 postChangeEvent 期间压入，这里直接取
        LivingEntity shooter = ShooterContext.getShooter();
        String gunType = GunTypeContext.getGunType();
        
        // 用上下文构造属性读取器；顺便把 TACZ 本轮的缓存对象也取出来
        EntityAttributeHelper entityAttribute = new EntityAttributeHelper(shooter, gunType);
        AttachmentCacheProperty cacheProperty = event.getCacheProperty();
        PropertyCalculator calculator = new PropertyCalculator(entityAttribute);
        // 在 TACZ 原始数值上叠加本模组的属性链，得到最终结果
        PropertyCalculationResults results = calculator.calculateAllProperties(cacheProperty);
        
        // KubeJS 是软依赖：装了才派发扩展事件，给整合包改数值的机会
        if (ModList.get().isLoaded("kubejs")) {
            KubeJSEventHelper.postAttributePostEvent(shooter, event.getGunItem(), results);
        }
        
        // 结果写回缓存，之后射击结算与面板取值就直接命中新值
        PropertyCacheUpdater.updateCacheProperties(cacheProperty, results);
    }
}