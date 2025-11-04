package com.xlxyvergil.taa.data;

/**
 * 基于GunDataFieldMapper.java中的标识符重构
 * 枚举所有枪械属性键值，定义各种枪支属性的默认值
 */
public class GunPropertiesInitializer {
    
    // 16个核心枪械属性
    /** 瞄准时间 - 从正常状态进入瞄准状态所需的时间（秒） */
    public static final float ADS_TIME = 2.0f;
    /** 弹药速度 - 子弹的飞行速度 */
    public static final float AMMO_SPEED = 2.0f;
    /** 护甲穿透 - 忽略目标护甲的伤害比例 */
    public static final float ARMOR_IGNORE = 2.0f;
    /** 伤害值 - 子弹对目标造成的伤害值 */
    public static final float DAMAGE = 2.0f;
    /** 有效射程 - 枪械的有效射击距离 */
    public static final float EFFECTIVE_RANGE = 2.0f;
    // 爆炸的子属性
    /** 爆炸半径 - 爆炸的影响范围半径 */
    public static final float EXPLOSION_RADIUS = 2.0f;
    /** 爆炸伤害 - 爆炸产生的伤害值 */
    public static final float EXPLOSION_DAMAGE = 2.0f;
    /** 爆炸击退 - 爆炸是否产生击退效果 */
    public static final boolean EXPLOSION_KNOCKBACK = true;
    /** 破坏方块 - 爆炸是否能够破坏方块 */
    public static final boolean EXPLOSION_DESTROY_BLOCK = true;
    /** 爆炸延迟 - 从子弹命中到爆炸发生的时间间隔（tick） */
    public static final int EXPLOSION_DELAY = 0;
    
    /** 移动速度 - 持枪时对玩家移动速度的影响 */
    public static final float MOVE_SPEED = 2.0f;
    /** 爆头倍数 - 爆头攻击的伤害倍率 */
    public static final float HEADSHOT_MULTIPLIER = 2.0f;
    /** 点燃效果 - 子弹是否能点燃实体或方块 */
    public static final boolean IGNITE = true;
    /** 不准确度 - 射击时的散布程度 */
    public static final float INACCURACY = 2.0f;
    /** 击退效果 - 子弹命中目标时的击退力度 */
    public static final float KNOCKBACK = 2.0f;
    /** 穿透能力 - 子弹可以穿透的实体数量 */
    public static final int PIERCE = 2;
    /** 后坐力 - 枪械射击时的后坐力大小 */
    public static final float RECOIL = 2.0f;
    /** 射速 - 每分钟发射的子弹数量 */
    public static final int ROUNDS_PER_MINUTE = 2;
    /** 消音效果 - 开火音效的消音系数 */
    public static final float SILENCE = 2.0f;
    /** 重量 - 枪支的重量值，影响移动速度等 */
    public static final float WEIGHT = 2.0f;
}