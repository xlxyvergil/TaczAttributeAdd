# TAA Mod - Tacz属性增强模组

## 功能特性

- **智能伤害计算** - 根据枪械类型自动选择对应的属性加成
- **灵活配置** - 支持多种伤害计算模式，可根据配置项动态切换
- **枪械分类** - 支持手枪、步枪、霰弹枪等7种枪械类型
- **完整属性系统** - 全面覆盖16个核心枪械属性
- **动态属性获取** - 从玩家身上实时获取属性值
- **配置驱动** - 伤害计算模式可通过配置文件调整

## 属性系统

### 伤害属性

- **通用枪械伤害**: `taa:tacz.bullet_gundamage` - 所有枪械的基础伤害加成

### 特定枪械伤害属性

- **手枪**: `taa:tacz.bullet_gundamage_pistol` - 手枪伤害加成
- **步枪**: `taa:tacz.bullet_gundamage_rifle` - 步枪伤害加成
- **霰弹枪**: `taa:tacz.bullet_gundamage_shotgun` - 霰弹枪伤害加成
- **狙击枪**: `taa:tacz.bullet_gundamage_sniper` - 狙击枪伤害加成
- **冲锋枪**: `taa:tacz.bullet_gundamage_smg` - 冲锋枪伤害加成
- **轻机枪**: `taa:tacz.bullet_gundamage_lmg` - 轻机枪伤害加成
- **发射器**: `taa:tacz.bullet_gundamage_launcher` - 发射器伤害加成

### 16个核心枪械属性

- **瞄准时间**: `taa:tacz.ads_time` - 瞄准速度加成
- **弹药速度**: `taa:tacz.ammo_speed` - 子弹飞行速度加成
- **护甲穿透**: `taa:tacz.armor_ignore` - 护甲穿透效果
- **有效射程**: `taa:tacz.effective_range` - 射击距离加成
- **爆炸半径**: `taa:tacz.explosion_radius` - 爆炸范围加成
- **爆炸伤害**: `taa:tacz.explosion_damage` - 爆炸伤害加成
- **爆炸击退**: `taa:tacz.explosion_knockback` - 爆炸击退效果
- **破坏方块**: `taa:tacz.explosion_destroy_block` - 爆炸破坏方块
- **爆炸延迟**: `taa:tacz.explosion_delay` - 爆炸延迟时间
- **移动速度**: `taa:tacz.move_speed` - 持枪移动速度
- **爆头倍数**: `taa:tacz.headshot_multiplier` - 爆头伤害倍率
- **点燃效果**: `taa:tacz.ignite` - 子弹点燃效果
- **不准确度**: `taa:tacz.inaccuracy` - 射击散布程度
- **击退效果**: `taa:tacz.knockback` - 子弹击退力度
- **穿透能力**: `taa:tacz.pierce` - 子弹穿透能力
- **后坐力**: `taa:tacz.recoil` - 枪械后坐力
- **射速**: `taa:tacz.rounds_per_minute` - 射击速度
- **消音效果**: `taa:tacz.silence` - 消音效果
- **重量**: `taa:tacz.weight` - 枪械重量

### 属性值说明

- **基础值**: 1.0 (100%效果)
- **加成值**: 0.5 = 50%加成，1.0 = 100%加成
- **布尔属性**: 0.0表示false，1.0表示true

## 伤害计算规则

### 根据配置项选择生效规则

模组支持三种伤害计算模式，可通过配置文件动态切换：

#### 1. MAX模式（默认）
- **规则**: 通用与特定取最大值
- **公式**: `Math.max(通用伤害, 特定伤害)`
- **描述**: 取通用伤害加成和特定枪械伤害加成中的最大值

#### 2. ADDITIVE模式
- **规则**: 通用+特定-1
- **公式**: `通用伤害 + 特定伤害 - 1.0D`
- **描述**: 将两种伤害加成相加后减去基础值

#### 3. MULTIPLICATIVE模式
- **规则**: 通用*特定
- **公式**: `通用伤害 * 特定伤害`
- **描述**: 两种伤害加成相乘

### 配置方式

通过修改配置文件 `taa-attributes.toml` 中的 `damageCalculationMode` 选项来切换计算模式。

## 技术实现

### 核心组件

- **PlayerAttributeHelper**: 玩家属性助手类，负责从玩家身上获取属性值
- **GunPropertiesInitializer**: 枪械属性初始化器，动态获取玩家属性值
- **AttributeConfig**: 配置系统，管理伤害计算模式等配置项
- **GunTypeContext**: 枪械类型上下文，跟踪当前处理的枪械类型

### 属性获取流程

1. **事件触发** - 枪械属性处理事件发生时
2. **类型识别** - 通过Tacz API获取当前枪械类型
3. **上下文设置** - 设置当前枪械类型到GunTypeContext
4. **属性计算** - 根据配置选择伤害计算规则
5. **动态应用** - 将计算结果应用到枪械属性

## 配置文件

配置文件位置：`config/taa-attributes.toml`

### 主要配置项

```toml
[枪械伤害计算设置]
# 枪械伤害计算模式
# MAX: 通用与特定取最大值
# ADDITIVE: 通用+特定-1
# MULTIPLICATIVE: 通用*特定
damageCalculationMode = "MAX"
```

## 版本信息

- **当前版本**: 1.0.5（基于时间戳的快照版本）
- **Minecraft版本**: 1.20.1
- **Tacz兼容版本**: 1.1.6-hotfix
- **最后更新**: 2025年11月5日

## 依赖要求

- **Minecraft Forge**: 对应1.20.1版本
- **Tacz Guns Mod**: 1.1.6-hotfix或兼容版本
- **Java**: 17或更高版本

taa:tacz.bullet_gundamage_pistol
taa:move_speed
taa:headshot_multiplier
taa:tacz.bullet_gundamage
taa:ads_time 需要<1
taa:rounds_per_minute
taa:tacz.bullet_gundamage_rifle
taa:tacz.bullet_gundamage_smg
taa:weight
taa:tacz.bullet_gundamage_sniper
taa:recoil
taa:effective_range
taa:pierce
taa:ammo_speed
taa:tacz.bullet_gundamage_shotgun
taa:armor_ignore
taa:inaccuracy需要<1
taa:knockback
taa:tacz.bullet_gundamage_launcher
taa:tacz.bullet_gundamage_lmg



taa:ignite  点燃

taa:silence  消音效果
taa:explosion_radius 爆炸范围
taa:explosion_damage
taa:explosion_delay
taa:explosion_destroy_block