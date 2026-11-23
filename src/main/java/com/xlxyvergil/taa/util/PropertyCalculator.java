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
 * 基于缓存属性与实体属性计算枪械各项数值
 * <p>
 * 输入是 TACZ 已经算好配件加成的 {@link AttachmentCacheProperty}，再乘上本模组的玩家属性倍率；
 * 输出是 {@link PropertyCalculationResults}，由 {@link PropertyCacheUpdater} 写回缓存。
 * 除换弹速度（见下方静态方法区块）外，所有计算都遵循「缓存原值 × 属性倍率」，倍率默认 1.0 表示无加成。
 */
public class PropertyCalculator {
    
    /** 属性快照，构造时已读好，计算过程中不再访问实体，保证同一把枪的各属性来自同一时刻 */
    private final EntityAttributeHelper entityAttribute;
    
    /**
     * @param entityAttribute 已读取完毕的射击者属性快照
     */
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
     * 按缓存属性逐项算出全部枪械数值
     * 调用顺序固定：由事件链在配件/属性变动时调用一次，结果随后写回缓存，供游戏逻辑与面板共用。
     *
     * @param cacheProperty 枪械当前缓存属性，可能缺少某些键（对应项会按 0 或默认值处理）
     * @return 全部计算结果
     */
    public PropertyCalculationResults calculateAllProperties(AttachmentCacheProperty cacheProperty) {
        PropertyCalculationResults results = new PropertyCalculationResults();
        
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
//        results.setRecoil(calculateRecoil(cacheProperty)); // 后坐力由 CameraSetupEventMixin 直接处理，不走缓存
        results.setSilence(calculateSilence(cacheProperty));
        results.setIgnite(calculateIgnite(cacheProperty));
        
        // 新增属性的计算
        results.setBulletCount(calculateBulletCount(cacheProperty));
        
        // 统一计算爆炸属性
        results.setExplosionData(createExplosionData(cacheProperty));
        
        return results;
    }
    
    // 基础属性计算（缓存原值 × 属性倍率；缓存缺项时返回 0）
    
    /**
     * 开镜时间：原值 × 开镜时间属性，单位秒（属性是耗时倍率，小于 1 表示开镜更快）
     */
    public float calculateAdsTime(AttachmentCacheProperty cacheProperty) {
        Float originalValue = cacheProperty.getCache(GunProperties.ADS_TIME);
        float entityAttributeFactor = (float) entityAttribute.getAdsTime();
        return originalValue != null ? originalValue * entityAttributeFactor : 0.0f;
    }
    
    /**
     * 弹速：原值 × 弹速属性；属性是倍率，结果转 int 时直接截断
     */
    public float calculateAmmoSpeed(AttachmentCacheProperty cacheProperty) {
        Float originalValue = cacheProperty.getCache(GunProperties.AMMO_SPEED);
        float entityAttributeFactor = (float) entityAttribute.getAmmoSpeed();
        // 截断小数取整，不四舍五入
        return originalValue != null ? (int) (originalValue * entityAttributeFactor) : 0.0f;
    }
    
    /**
     * 穿甲比例：原值 × 穿甲属性（比例本身不取整，保持 0~1 精度）
     */
    public float calculateArmorIgnore(AttachmentCacheProperty cacheProperty) {
        Float originalValue = cacheProperty.getCache(GunProperties.ARMOR_IGNORE);
        float entityAttributeFactor = (float) entityAttribute.getArmorIgnore();
        return originalValue != null ? originalValue * entityAttributeFactor : 0.0f;
    }
    
    /**
     * 有效射程：原值 × 射程属性，单位格
     */
    public float calculateEffectiveRange(AttachmentCacheProperty cacheProperty) {
        Float originalValue = cacheProperty.getCache(GunProperties.EFFECTIVE_RANGE);
        float entityAttributeFactor = (float) entityAttribute.getEffectiveRange();
        return originalValue != null ? originalValue * entityAttributeFactor : 0.0f;
    }
    
    /**
     * 爆头倍率：原倍率加上「属性倍率 - 1」的差值（加法而非乘法），最低 0.01
     * 例：原 2.0、属性 1.5 → 2.5，而不是 3.0。
     */
    public float calculateHeadshotMultiplier(AttachmentCacheProperty cacheProperty) {
        Float originalValue = cacheProperty.getCache(GunProperties.HEADSHOT_MULTIPLIER);
        float entityAttributeFactor = (float) entityAttribute.getHeadshotMultiplier();
        // 属性默认值为 1（无加成），先减 1 得到差值再加回；保底 0.01 防止负值
        return originalValue != null ? Math.max(originalValue + (entityAttributeFactor - 1.0f), 0.01f) : 0.0f;
    }
    
    /**
     * 击退：原值 × 击退属性（影响命中生物时的击退强度）
     */
    public float calculateKnockback(AttachmentCacheProperty cacheProperty) {
        Float originalValue = cacheProperty.getCache(GunProperties.KNOCKBACK);
        float entityAttributeFactor = (float) entityAttribute.getKnockback();
        return originalValue != null ? originalValue * entityAttributeFactor : 0.0f;
    }
    
    /**
     * 重量：原值 × 重量属性（TACZ 用它换算持枪减速，倍率越大越沉）
     */
    public float calculateWeight(AttachmentCacheProperty cacheProperty) {
        Float originalValue = cacheProperty.getCache(GunProperties.WEIGHT);
        float entityAttributeFactor = (float) entityAttribute.getWeight();
        return originalValue != null ? originalValue * entityAttributeFactor : 0.0f;
    }
    
    /**
     * 穿透数：原值 × 穿透属性，结果截断取整（0 表示不穿透）
     */
    public int calculatePierce(AttachmentCacheProperty cacheProperty) {
        Integer originalValue = cacheProperty.getCache(GunProperties.PIERCE);
        float entityAttributeFactor = (float) entityAttribute.getPierce();
        // 截断小数取整，不四舍五入
        return originalValue != null ? (int) (originalValue * entityAttributeFactor) : 0;
    }
    
    /**
     * 射速：原值（发/分钟）× 射速属性，结果截断取整
     */
    public int calculateRoundsPerMinute(AttachmentCacheProperty cacheProperty) {
        Integer originalValue = cacheProperty.getCache(GunProperties.ROUNDS_PER_MINUTE);
        float entityAttributeFactor = (float) entityAttribute.getRoundsPerMinute();
        // 截断小数取整，不四舍五入
        return originalValue != null ? (int) (originalValue * entityAttributeFactor) : 0;
    }

    
    // 新增属性计算
    
    /**
     * 弹丸数：缓存值（由弹丸数修改器写入）× 多弹头属性，四舍五入取整
     * 缓存为 null 时按 1 颗起算（没有修改器介入的普通枪）。
     */
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
    
    // 复杂属性计算
    
    /**
     * 移速：三档（腰射/开镜/换弹）倍率各加深玩家的偏移量
     * 加法叠加而非乘法，且刻意不做 0.01 保底，理由见方法内的行内注释。
     */
    public MoveSpeed calculateMoveSpeed(AttachmentCacheProperty cacheProperty) {
        MoveSpeed originalMoveSpeed = cacheProperty.getCache(GunProperties.MOVE_SPEED);
        
        // 属性值作为偏移量叠加：1.0 无影响，>1 加速，<1 减速
        double playerMoveSpeed = entityAttribute.getMoveSpeed();

        // 相对基础值 1.0 的偏移量
        float playerSpeedOffset = (float) (playerMoveSpeed - 1.0D);

        // 偏移量加到原始值上（加法而非乘法），final = original + (倍率 - 1.0)
        // 注意：不能对这里的倍率做 Math.max(...,0.01) 保底。TACZ 的 movement_speed 是
        // MULTIPLY_TOTAL 修饰器，开镜减速是负数（如 aim=-0.2），若被夹到 0.01，
        // 瞄准与腰射速度会变得一样，原版的开镜减速就消失了。
        return new MoveSpeed(
            originalMoveSpeed.getBaseMultiplier() + playerSpeedOffset,
            originalMoveSpeed.getAimMultiplier() + playerSpeedOffset,
            originalMoveSpeed.getReloadMultiplier() + playerSpeedOffset
        );
    }
    
    /**
     * 伤害表：每个距离段的伤害 ×（伤害加成 × 弹丸数），逐段保留两位小数
     * 距离键原样保留——距离段由枪包决定，属性只改每段的数值。
     */
    public LinkedList<ExtraDamage.DistanceDamagePair> calculateDamage(AttachmentCacheProperty cacheProperty) {
        LinkedList<ExtraDamage.DistanceDamagePair> originalDamage = cacheProperty.getCache(GunProperties.DAMAGE);
        if (originalDamage == null || originalDamage.isEmpty()) {
            return new LinkedList<>();
        }
        
        float entityAttributeFactor = (float) entityAttribute.getGunDamageBonus();
        double bulletCountAttribute = entityAttribute.getBulletCount();
        
        LinkedList<ExtraDamage.DistanceDamagePair> calculatedDamage = new LinkedList<>();
        
        for (ExtraDamage.DistanceDamagePair pair : originalDamage) {
            // 伤害乘属性加成与弹头数，结果保留两位小数
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
    
    /**
     * 散布：综合属性 × 各姿态细分属性，两项都乘（都是倍率，1.0 无影响）
     * 结果用可变 HashMap 承载，便于其他模组（如 Gun Durability）在其上继续调整。
     */
    public Map<InaccuracyType, Float> calculateInaccuracy(AttachmentCacheProperty cacheProperty) {
        Map<InaccuracyType, Float> originalInaccuracy = cacheProperty.getCache(GunProperties.INACCURACY);
        if (originalInaccuracy == null || originalInaccuracy.isEmpty()) {
            return new java.util.HashMap<>();
        }
        
        // 用可变 Map 存储结果，避免与其他 mod（如 Gun Durability）的兼容问题
        java.util.HashMap<InaccuracyType, Float> result = new java.util.HashMap<>();
        
        // 综合属性 × 细分属性（乘法叠加）
        float baseFactor = (float) entityAttribute.getInaccuracy();
        result.put(InaccuracyType.STAND, originalInaccuracy.getOrDefault(InaccuracyType.STAND, 0.0f) * baseFactor * (float) entityAttribute.getInaccuracyStand());
        result.put(InaccuracyType.MOVE, originalInaccuracy.getOrDefault(InaccuracyType.MOVE, 0.0f) * baseFactor * (float) entityAttribute.getInaccuracyMove());
        result.put(InaccuracyType.SNEAK, originalInaccuracy.getOrDefault(InaccuracyType.SNEAK, 0.0f) * baseFactor * (float) entityAttribute.getInaccuracySneak());
        result.put(InaccuracyType.LIE, originalInaccuracy.getOrDefault(InaccuracyType.LIE, 0.0f) * baseFactor * (float) entityAttribute.getInaccuracyLie());
        result.put(InaccuracyType.AIM, originalInaccuracy.getOrDefault(InaccuracyType.AIM, 0.0f) * baseFactor * (float) entityAttribute.getInaccuracyAim());
        return result;
    }
    
    /**
     * 消音：等级 = 原等级 × 消音属性后四舍五入；属性倍率小于 1 时额外开启被动消音
     * 返回（等级, 是否消音），被动消音与配件消音取或，任一成立即生效。
     */
    public Pair<Integer, Boolean> calculateSilence(AttachmentCacheProperty cacheProperty) {
        Pair<Integer, Boolean> originalSilence = cacheProperty.getCache(GunProperties.SILENCE);
        
        if (originalSilence == null) {
            return Pair.of(0, false);
        }
        
        // 用乘法因子计算消音等级
        double entityAttributeFactor = entityAttribute.getSilence();
        Integer originalLevel = originalSilence.left() != null ? originalSilence.left() : 0;
        Integer level = (int) Math.round(originalLevel * entityAttributeFactor);
        
        // 消音属性 < 1.0 时自动开启被动消音
        boolean originalEnabled = originalSilence.right() != null ? originalSilence.right() : false;
        boolean passiveSilenceEnabled = entityAttributeFactor < 1.0D;
        
        // 配件消音或被动消音任一开启即生效
        boolean enabled = originalEnabled || passiveSilenceEnabled;
        
        return Pair.of(level, enabled);
    }
    
    /**
     * 点燃：属性达到开启阈值（>= 2.0）时强制开启点燃，并把「点燃方块」一并打开
     * 属性未开启时原样返回缓存里的点燃配置——本模组只会加强，不会关闭枪包自带的点火。
     */
    public Ignite calculateIgnite(AttachmentCacheProperty cacheProperty) {
        Ignite originalIgnite = cacheProperty.getCache(GunProperties.IGNITE);
        if (originalIgnite == null) {
            originalIgnite = new Ignite(false, false);
        }
        
        boolean entityAttributeValue = entityAttribute.isIgniteEnabled();

        if (!entityAttributeValue) {
            return originalIgnite;
        }
        
        // 有属性增强时：entity 保留原值或被增强为 true，block 强制为 true
        return new Ignite(originalIgnite.isIgniteEntity() || entityAttributeValue, true);
    }
    
    // 爆炸相关属性计算（缓存无爆炸配置时按关闭处理）
    
    /**
     * 爆炸半径：原半径加「属性倍率 - 1」的差值，最低 0.01
     * 与爆头倍率同一套加法语义；保底 0.01 防止削减属性后半径为 0 或负数。
     */
    public float calculateExplosionRadius(AttachmentCacheProperty cacheProperty) {
        ExplosionData originalExplosion = cacheProperty.getCache(GunProperties.EXPLOSION);
        if (originalExplosion == null) {
            return 0.0f;
        }
        
        float entityAttributeFactor = (float) entityAttribute.getExplosionRadius();
        // 属性默认值为 1（无加成），先减 1 得到差值再加回；保底 0.01 防止负值
        return Math.max(originalExplosion.getRadius() + (entityAttributeFactor - 1.0f), 0.01f);
    }
    
    /**
     * 爆炸伤害：原值 × 爆炸伤害属性（这里用乘法，不减不加差值）
     */
    public float calculateExplosionDamage(AttachmentCacheProperty cacheProperty) {
        ExplosionData originalExplosion = cacheProperty.getCache(GunProperties.EXPLOSION);
        if (originalExplosion == null) {
            return 0.0f;
        }
        
        float entityAttributeFactor = (float) entityAttribute.getExplosionDamage();
        return originalExplosion.getDamage() * entityAttributeFactor;
    }
    
    /**
     * 爆炸击退开关：枪包原值与属性开关交给 TACZ 的 {@link AttachmentPropertyManager#eval} 合并
     * 用 eval 而不是直接「或」，是为了尊重 TACZ 自己的布尔合并规则（配件里的同类开关）。
     */
    public boolean calculateExplosionKnockback(AttachmentCacheProperty cacheProperty) {
        ExplosionData originalExplosion = cacheProperty.getCache(GunProperties.EXPLOSION);
        if (originalExplosion == null) {
            return false;
        }
        
        boolean entityAttributeValue = entityAttribute.isExplosionKnockbackEnabled();
        List<Boolean> knockbackValues = List.of(originalExplosion.isKnockback(), entityAttributeValue);
        return AttachmentPropertyManager.eval(knockbackValues, false);
    }
    
    /**
     * 爆炸破坏方块开关：合并方式同上，交给 {@link AttachmentPropertyManager#eval}
     */
    public boolean calculateExplosionDestroyBlock(AttachmentCacheProperty cacheProperty) {
        ExplosionData originalExplosion = cacheProperty.getCache(GunProperties.EXPLOSION);
        if (originalExplosion == null) {
            return false;
        }
        
        boolean entityAttributeValue = entityAttribute.isExplosionDestroyBlockEnabled();
        List<Boolean> destroyBlockValues = List.of(originalExplosion.isDestroyBlock(), entityAttributeValue);
        return AttachmentPropertyManager.eval(destroyBlockValues, false);
    }
    
    /**
     * 爆炸延迟：原延迟 × 爆炸延迟属性，四舍五入取整，单位为 tick
     */
    public int calculateExplosionDelay(AttachmentCacheProperty cacheProperty) {
        ExplosionData originalExplosion = cacheProperty.getCache(GunProperties.EXPLOSION);
        if (originalExplosion == null) {
            return 0;
        }
        
        float entityAttributeFactor = (float) entityAttribute.getExplosionDelay();
        return Math.round(originalExplosion.getDelay() * entityAttributeFactor);
    }
    
    /**
     * 组装最终爆炸配置
     * 是否爆炸的判定：枪包本身已开启则保持开启，否则看属性是否达到开启阈值（{@code explosion_enabled > 2.0}，
     * 与点燃共用同一套「倍率 2.0 才算开启」的约定）。
     * 构造顺序为（是否爆炸, 半径, 伤害, 击退, 延迟, 破坏方块），延迟是 int 算出后按 float 传入。
     */
    public ExplosionData createExplosionData(AttachmentCacheProperty cacheProperty) {
        ExplosionData originalExplosion = cacheProperty.getCache(GunProperties.EXPLOSION);
        if (originalExplosion == null) {
            return new ExplosionData(false, 0.0f, 0.0f, false, 0.0f, false);
        }
        
        boolean explode;
        if (originalExplosion.isExplode()) {
            explode = true;
        } else {
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
