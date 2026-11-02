package com.xlxyvergil.taa.util;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.tacz.guns.api.GunProperties;
import com.tacz.guns.resource.modifier.AttachmentCacheProperty;
import com.tacz.guns.resource.modifier.AttachmentPropertyManager;
import com.tacz.guns.resource.pojo.data.gun.ExplosionData;
import com.tacz.guns.resource.pojo.data.gun.ExtraDamage;
import com.tacz.guns.resource.pojo.data.gun.Ignite;
import com.tacz.guns.resource.pojo.data.gun.InaccuracyType;
import com.tacz.guns.resource.pojo.data.gun.MoveSpeed;
import com.xlxyvergil.taa.api.ExtendedGunProperties;
import com.xlxyvergil.taa.attribute.EntityAttributeRegistry;

import it.unimi.dsi.fastutil.Pair;
import net.minecraft.world.entity.LivingEntity;

/**
 * 属性计算器
 * 纯计算类，基于cacheProperty原始数据和entityAttribute进行计算
 */
public class PropertyCalculator {
    
    private final EntityAttributeHelper entityAttribute;
    
    /**
     * 构造函     * @param entityAttribute 实体属性助     */
    public PropertyCalculator(EntityAttributeHelper entityAttribute) {
        this.entityAttribute = entityAttribute;
    }
    
    // ==================== 换弹速度 ====================
    // 换弹数据（reload.feed / reload.cooldown）不在 AttachmentCacheProperty 里，也没有对应的
    // GunProperties 键，所以换弹速度只能按「基础耗时 × 玩家倍率」现场计算，无法进入
    // calculateAllProperties 的缓存流程。下面这几个静态方法就是换弹速度的唯一计算入口，
    // 换弹时长的三个消费者（GunReloadTime 拦截、服务端脚本时钟、客户端动画速度）以及属性面板
    // 必须全部从这里取值，任何一处自行读属性都会导致各条时间线脱钩。
    
    /**
     * 换弹耗时倍率下限：0.1，即最多加速到 10 倍。
     * <p>
     * 再快没有意义：服务端换弹状态机按 tick（50ms）推进，总时长压到几十毫秒就会被整段吞掉，
     * 反而会重新出现「动画播完却没装弹」的脱钩，因此把倍率夹在这个硬下限上。
     */
    public static final double MIN_RELOAD_SPEED_MULTIPLIER = 0.1D;
    
    /**
     * 换弹耗时倍率（玩家 {@code reload_time} 属性）。
     * <p>
     * 语义是「耗时倍率」而不是「速度倍率」：小于 1 表示换弹更快，1.0 为原速。
     *
     * @return 已夹到 {@link #MIN_RELOAD_SPEED_MULTIPLIER} 的倍率
     */
    public static double getReloadSpeedMultiplier(LivingEntity shooter) {
        double value = EntityAttributeHelper.getAttributeValue(
                shooter, EntityAttributeRegistry.RELOAD_TIME.get(), 1.0D);
        return Math.max(value, MIN_RELOAD_SPEED_MULTIPLIER);
    }
    
    /**
     * 时间推进速率 = 1 / 换弹耗时倍率。
     * <p>
     * 用于「按真实时间推进、需要被加速」的消费者：客户端动画进度（alphaProgress）、
     * 服务端脚本时钟 —— 它们要的是「同一段真实时间里能走过多少动画/脚本时间」。
     *
     * @return 1.0 为原速，2.0 为两倍速
     */
    public static double getReloadTimeScale(LivingEntity shooter) {
        return 1.0D / getReloadSpeedMultiplier(shooter);
    }
    
    /**
     * 换弹时长（秒）= 枪包基础时长 × 换弹耗时倍率。
     *
     * @param baseTime 枪包里的原始换弹时长
     * @return 玩家实际需要的换弹时长
     */
    public static float calculateReloadTime(float baseTime, LivingEntity shooter) {
        return (float) (baseTime * getReloadSpeedMultiplier(shooter));
    }
    
    /**
     * 换弹时长（毫秒）= 基础毫秒 / 换弹耗时倍率。
     * <p>
     * 脚本枪的 Lua 侧比较的是 {@code api:getReloadTime()}（真实流逝的墙钟毫秒）与
     * {@code script_param} 的毫秒阈值，因此这里把「已流逝的真实时间」换算成
     * 「脚本应当认为已流逝的时间」，让脚本整条时间线等比缩短。
     *
     * @param elapsedMillis 真实流逝的毫秒数
     * @return 换算后的脚本时间
     */
    public static long calculateReloadTime(long elapsedMillis, LivingEntity shooter) {
        return (long) (elapsedMillis / getReloadSpeedMultiplier(shooter));
    }
    
    /**
     * 拉栓时间（毫秒）= 已流逝的真实毫秒 / 换弹耗时倍率。
     * <p>
     * 拉栓（bolt action）与换弹共用同一份「动作耗时倍率」：如果只加速换弹而让拉栓保持原速
     * （如 SPAS-12 的 {@code bolt_time} 0.53s），倍率压得越低，射速瓶颈就越会转移到拉栓上，
     * 换弹加速的收益被吃掉。因此这里和 {@link #calculateReloadTime(long, LivingEntity)}
     * 用同一个倍率、同一套下限，只是命名上把消费者区分开。
     *
     * @param elapsedMillis 真实流逝的毫秒数
     * @return 换算后的脚本/拉栓逻辑时间
     */
    public static long calculateBoltTime(long elapsedMillis, LivingEntity shooter) {
        return calculateReloadTime(elapsedMillis, shooter);
    }
    
    /**
     * 计算所有属性（包含爆炸相关属性）
     * @param cacheProperty 原始缓存属性数
     */
    public PropertyCalculationResults calculateAllProperties(AttachmentCacheProperty cacheProperty) {
        PropertyCalculationResults results = new PropertyCalculationResults();
        
        // 基于原始数据和entityAttribute计算所有属性
        results.setAdsTime(calculateAdsTime(cacheProperty));
        results.setAmmoSpeed(calculateAmmoSpeed(cacheProperty));
        results.setArmorIgnore(calculateArmorIgnore(cacheProperty));
        results.setEffectiveRange(calculateEffectiveRange(cacheProperty));
        results.setHeadshotMultiplier(calculateHeadshotMultiplier(cacheProperty));
        results.setKnockback(calculateKnockback(cacheProperty));
        results.setWeight(calculateWeight(cacheProperty));
        results.setPierce(calculatePierce(cacheProperty));
        results.setRoundsPerMinute(calculateRoundsPerMinute(cacheProperty));
        results.setMoveSpeed(calculateMoveSpeed(cacheProperty));
        results.setDamage(calculateDamage(cacheProperty));
        results.setInaccuracy(calculateInaccuracy(cacheProperty));
//        results.setRecoil(calculateRecoil(cacheProperty)); // 后坐力由CameraSetupEventMixin直接处理，不通过缓存
        results.setSilence(calculateSilence(cacheProperty));
        results.setIgnite(calculateIgnite(cacheProperty));
        
        // 新增属性的计算
        results.setBulletCount(calculateBulletCount(cacheProperty));
        
        // 统一计算爆炸属
        results.setExplosionData(createExplosionData(cacheProperty));
        
        return results;
    }
    
    // 基本属性计算方- 基于cacheProperty原始数据和playerAttribute
    
    public float calculateAdsTime(AttachmentCacheProperty cacheProperty) {
        Float originalValue = cacheProperty.getCache(GunProperties.ADS_TIME);
        float entityAttributeFactor = (float) entityAttribute.getAdsTime();
        return originalValue != null ? originalValue * entityAttributeFactor : 0.0f;
    }
    
    public float calculateAmmoSpeed(AttachmentCacheProperty cacheProperty) {
        Float originalValue = cacheProperty.getCache(GunProperties.AMMO_SPEED);
        float entityAttributeFactor = (float) entityAttribute.getAmmoSpeed();
        // 直接截断小数部分取整，不使用四舍五入
        return originalValue != null ? (int) (originalValue * entityAttributeFactor) : 0.0f;
    }
    
    public float calculateArmorIgnore(AttachmentCacheProperty cacheProperty) {
        Float originalValue = cacheProperty.getCache(GunProperties.ARMOR_IGNORE);
        float entityAttributeFactor = (float) entityAttribute.getArmorIgnore();
        return originalValue != null ? originalValue * entityAttributeFactor : 0.0f;
    }
    
    public float calculateEffectiveRange(AttachmentCacheProperty cacheProperty) {
        Float originalValue = cacheProperty.getCache(GunProperties.EFFECTIVE_RANGE);
        float entityAttributeFactor = (float) entityAttribute.getEffectiveRange();
        return originalValue != null ? originalValue * entityAttributeFactor : 0.0f;
    }
    
    public float calculateHeadshotMultiplier(AttachmentCacheProperty cacheProperty) {
        Float originalValue = cacheProperty.getCache(GunProperties.HEADSHOT_MULTIPLIER);
        float entityAttributeFactor = (float) entityAttribute.getHeadshotMultiplier();
        // 由于Forge会自1，所以计算时需1，然后使用加
        // 保底0.01，防止属性过低导致负值
        return originalValue != null ? Math.max(originalValue + (entityAttributeFactor - 1.0f), 0.01f) : 0.0f;
    }
    
    public float calculateKnockback(AttachmentCacheProperty cacheProperty) {
        Float originalValue = cacheProperty.getCache(GunProperties.KNOCKBACK);
        float entityAttributeFactor = (float) entityAttribute.getKnockback();
        return originalValue != null ? originalValue * entityAttributeFactor : 0.0f;
    }
    
    public float calculateWeight(AttachmentCacheProperty cacheProperty) {
        Float originalValue = cacheProperty.getCache(GunProperties.WEIGHT);
        float entityAttributeFactor = (float) entityAttribute.getWeight();
        return originalValue != null ? originalValue * entityAttributeFactor : 0.0f;
    }
    
    public int calculatePierce(AttachmentCacheProperty cacheProperty) {
        Integer originalValue = cacheProperty.getCache(GunProperties.PIERCE);
        float entityAttributeFactor = (float) entityAttribute.getPierce();
        // 直接截断小数部分取整，不使用四舍五入
        return originalValue != null ? (int) (originalValue * entityAttributeFactor) : 0;
    }
    
    public int calculateRoundsPerMinute(AttachmentCacheProperty cacheProperty) {
        Integer originalValue = cacheProperty.getCache(GunProperties.ROUNDS_PER_MINUTE);
        float entityAttributeFactor = (float) entityAttribute.getRoundsPerMinute();
        // 直接截断小数部分取整，不使用四舍五入
        return originalValue != null ? (int) (originalValue * entityAttributeFactor) : 0;
    }

    
    // 新增属性计算方
    
    public int calculateBulletCount(AttachmentCacheProperty cacheProperty) {
        Integer originalValue = cacheProperty.getCache(ExtendedGunProperties.BULLET_COUNT);
        if (originalValue == null) {
            originalValue = 1; // 默认
        }
        double entityAttributeFactor = entityAttribute.getBulletCount();
        double result = originalValue * entityAttributeFactor;
        // 四舍五入取整
        return (int) Math.round(result);
    }
    
    // 复杂属性计算方
    
    public MoveSpeed calculateMoveSpeed(AttachmentCacheProperty cacheProperty) {
        MoveSpeed originalMoveSpeed = cacheProperty.getCache(GunProperties.MOVE_SPEED);
        
        // 移动速度计算逻辑：属性值直接作为偏移量加到原始值上
        // 属性值解释：1.0 = 无影响，大于1.0表示加速，小于1.0表示减速
        double playerMoveSpeed = entityAttribute.getMoveSpeed();
        
        // 计算相对于基础值1.0的偏移量
        // 例如：属性值1.0 -> 偏移0（无影响）
        //       属性值1.5 -> 偏移+0.5（+50%移速）
        //       属性值0.5 -> 偏移-0.5（-50%移速）
        float playerSpeedOffset = (float) (playerMoveSpeed - 1.0D);
        
        // 直接将偏移量加到原始MoveSpeed上（不是乘，是加）
        // 这样：final = original + (playerAttribute - 1.0)
        // 注意：不能对这里的倍率做 Math.max(...,0.01) 保底。TACZ 的 movement_speed 是
        // MULTIPLY_TOTAL 修饰器，开镜减速是负数（如 aim=-0.2），若被夹到 0.01，
        // 瞄准与腰射速度会变得一样，原版的开镜减速就消失了。
        return new MoveSpeed(
            originalMoveSpeed.getBaseMultiplier() + playerSpeedOffset,
            originalMoveSpeed.getAimMultiplier() + playerSpeedOffset,
            originalMoveSpeed.getReloadMultiplier() + playerSpeedOffset
        );
    }
    
    public LinkedList<ExtraDamage.DistanceDamagePair> calculateDamage(AttachmentCacheProperty cacheProperty) {
        LinkedList<ExtraDamage.DistanceDamagePair> originalDamage = cacheProperty.getCache(GunProperties.DAMAGE);
        if (originalDamage == null || originalDamage.isEmpty()) {
            return new LinkedList<>();
        }
        
        float entityAttributeFactor = (float) entityAttribute.getGunDamageBonus();
        double bulletCountAttribute = entityAttribute.getBulletCount();
        
        LinkedList<ExtraDamage.DistanceDamagePair> calculatedDamage = new LinkedList<>();
        
        for (ExtraDamage.DistanceDamagePair pair : originalDamage) {
            // 伤害计算与属性的弹头属性值相乘，结果保留两位小数
            float rawDamage = pair.getDamage() * entityAttributeFactor * (float) bulletCountAttribute;
            // 保留两位小数
            float finalDamage = (float) (Math.round(rawDamage * 100.0) / 100.0);
            calculatedDamage.add(new ExtraDamage.DistanceDamagePair(
                pair.getDistance(),
                finalDamage
            ));
        }
        
        return calculatedDamage;
    }
    
    public Map<InaccuracyType, Float> calculateInaccuracy(AttachmentCacheProperty cacheProperty) {
        Map<InaccuracyType, Float> originalInaccuracy = cacheProperty.getCache(GunProperties.INACCURACY);
        if (originalInaccuracy == null || originalInaccuracy.isEmpty()) {
            return new java.util.HashMap<>();
        }
        
        // 创建可变的Map来存储计算结果，避免与其他mod（如Gun Durability）的兼容性问题
        java.util.HashMap<InaccuracyType, Float> result = new java.util.HashMap<>();
        
        // 计算方式：综合属性 × 细分属性（乘法叠加）
        float baseFactor = (float) entityAttribute.getInaccuracy();
        result.put(InaccuracyType.STAND, originalInaccuracy.getOrDefault(InaccuracyType.STAND, 0.0f) * baseFactor * (float) entityAttribute.getInaccuracyStand());
        result.put(InaccuracyType.MOVE, originalInaccuracy.getOrDefault(InaccuracyType.MOVE, 0.0f) * baseFactor * (float) entityAttribute.getInaccuracyMove());
        result.put(InaccuracyType.SNEAK, originalInaccuracy.getOrDefault(InaccuracyType.SNEAK, 0.0f) * baseFactor * (float) entityAttribute.getInaccuracySneak());
        result.put(InaccuracyType.LIE, originalInaccuracy.getOrDefault(InaccuracyType.LIE, 0.0f) * baseFactor * (float) entityAttribute.getInaccuracyLie());
        result.put(InaccuracyType.AIM, originalInaccuracy.getOrDefault(InaccuracyType.AIM, 0.0f) * baseFactor * (float) entityAttribute.getInaccuracyAim());
        return result;
    }
    
    public Pair<Integer, Boolean> calculateSilence(AttachmentCacheProperty cacheProperty) {
        Pair<Integer, Boolean> originalSilence = cacheProperty.getCache(GunProperties.SILENCE);
        
        // 如果原始值为null，直接返回默认值（不进行属性计算）
        if (originalSilence == null) {
            return Pair.of(0, false);
        }
        
        // 使用数值乘法因子计算声音距
        double entityAttributeFactor = entityAttribute.getSilence();
        Integer originalLevel = originalSilence.left() != null ? originalSilence.left() : 0;
        Integer level = (int) Math.round(originalLevel * entityAttributeFactor);
        
        // 被动消音效果：当silence属性< 1.0时，自动开启消音效
        boolean originalEnabled = originalSilence.right() != null ? originalSilence.right() : false;
        boolean passiveSilenceEnabled = entityAttributeFactor < 1.0D;
        
        // 合并效果：原配件消音效果 被动属性消音效
        boolean enabled = originalEnabled || passiveSilenceEnabled;
        
        return Pair.of(level, enabled);
    }
    
    public Ignite calculateIgnite(AttachmentCacheProperty cacheProperty) {
        Ignite originalIgnite = cacheProperty.getCache(GunProperties.IGNITE);
        if (originalIgnite == null) {
            originalIgnite = new Ignite(false, false);
        }
        
        boolean entityAttributeValue = entityAttribute.isIgniteEnabled();
        

        
        // 如果没有相关的属性增强，则直接返回原始
        if (!entityAttributeValue) {
            return originalIgnite;
        }
        
        // 有属性增强，在原有基础上进一步增强点燃效
        // entity保持原有值（如果配件已提供则为true，否则为false）或增强为true
        // block从原有值增强为true
        return new Ignite(originalIgnite.isIgniteEntity() || entityAttributeValue, true);
    }
    
    // 爆炸相关属性计
    
    public float calculateExplosionRadius(AttachmentCacheProperty cacheProperty) {
        ExplosionData originalExplosion = cacheProperty.getCache(GunProperties.EXPLOSION);
        if (originalExplosion == null) {
            return 0.0f;
        }
        
        float entityAttributeFactor = (float) entityAttribute.getExplosionRadius();
        // 考虑Forge默认+1，保底0.01防止负值
        return Math.max(originalExplosion.getRadius() + (entityAttributeFactor - 1.0f), 0.01f);
    }
    
    public float calculateExplosionDamage(AttachmentCacheProperty cacheProperty) {
        ExplosionData originalExplosion = cacheProperty.getCache(GunProperties.EXPLOSION);
        if (originalExplosion == null) {
            return 0.0f;
        }
        
        float entityAttributeFactor = (float) entityAttribute.getExplosionDamage();
        return originalExplosion.getDamage() * entityAttributeFactor;
    }
    
    public boolean calculateExplosionKnockback(AttachmentCacheProperty cacheProperty) {
        ExplosionData originalExplosion = cacheProperty.getCache(GunProperties.EXPLOSION);
        if (originalExplosion == null) {
            return false;
        }
        
        boolean entityAttributeValue = entityAttribute.isExplosionKnockbackEnabled();
        List<Boolean> knockbackValues = List.of(originalExplosion.isKnockback(), entityAttributeValue);
        return AttachmentPropertyManager.eval(knockbackValues, false);
    }
    
    public boolean calculateExplosionDestroyBlock(AttachmentCacheProperty cacheProperty) {
        ExplosionData originalExplosion = cacheProperty.getCache(GunProperties.EXPLOSION);
        if (originalExplosion == null) {
            return false;
        }
        
        boolean entityAttributeValue = entityAttribute.isExplosionDestroyBlockEnabled();
        List<Boolean> destroyBlockValues = List.of(originalExplosion.isDestroyBlock(), entityAttributeValue);
        return AttachmentPropertyManager.eval(destroyBlockValues, false);
    }
    
    public int calculateExplosionDelay(AttachmentCacheProperty cacheProperty) {
        ExplosionData originalExplosion = cacheProperty.getCache(GunProperties.EXPLOSION);
        if (originalExplosion == null) {
            return 0;
        }
        
        float entityAttributeFactor = (float) entityAttribute.getExplosionDelay();
        return Math.round(originalExplosion.getDelay() * entityAttributeFactor);
    }
    
    public ExplosionData createExplosionData(AttachmentCacheProperty cacheProperty) {
        ExplosionData originalExplosion = cacheProperty.getCache(GunProperties.EXPLOSION);
        if (originalExplosion == null) {
            return new ExplosionData(false, 0.0f, 0.0f, false, 0.0f, false);
        }
        
        boolean explode;
        if (originalExplosion.isExplode()) {
            explode = true;
        } else {
            // 判断属性爆炸是否开启的值是不是大于2
            explode = entityAttribute.getExplosionEnabled() > 2.0D;
        }
        
        float radius = calculateExplosionRadius(cacheProperty);
        float damage = calculateExplosionDamage(cacheProperty);
        boolean knockback = calculateExplosionKnockback(cacheProperty);
        boolean destroyBlock = calculateExplosionDestroyBlock(cacheProperty);
        int delay = calculateExplosionDelay(cacheProperty);
        
        return new ExplosionData(explode, radius, damage, knockback, (float)delay, destroyBlock);
    }
}
