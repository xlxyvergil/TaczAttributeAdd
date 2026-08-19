# TAA Mod - Tacz属性增强模组 (NeoForge 1.21.1)

基于 **NeoForge 1.21.1** 的 TaczAttributeAdd 版本。

## 功能特性

- **智能伤害计算** - 根据枪械类型自动选择对应的属性加成
- **灵活配置** - 支持多种伤害计算模式，可根据配置项动态切换
- **枪械分类** - 支持手枪、步枪、霰弹枪等7种枪械类型
- **完整属性系统** - 全面覆盖31个核心枪械属性（含7个细分属性）
- **动态属性获取** - 从玩家身上实时获取属性值
- **配置驱动** - 伤害计算模式可通过配置文件调整
- **兼容性保障** - 兼容TACZ原版配件系统，不覆盖配件效果
- **客户端/服务端分离** - 客户端UI显示与服务端逻辑完全分离，确保服务端稳定运行
- **全新Modifier系统** - 支持通过配件修改枪械近战伤害、近战距离、弹匣容量、装填时间和子弹数量
- **扩展玩家属性** - 新增多个与枪械相关的玩家属性，进一步提升个性化体验
- **过热体系属性** - 新增4个过热相关乘法属性：过热上限、散热速度、冷却延迟、锁枪时间

## 属性系统

### 伤害属性

- **通用枪械伤害**: `taa:bullet_gundamage` - 所有枪械的基础伤害加成

### 特定枪械伤害属性

- **手枪**: `taa:bullet_gundamage_pistol` - 手枪伤害加成
- **步枪**: `taa:bullet_gundamage_rifle` - 步枪伤害加成
- **霰弹枪**: `taa:bullet_gundamage_shotgun` - 霰弹枪伤害加成
- **狙击枪**: `taa:bullet_gundamage_sniper` - 狙击枪伤害加成
- **冲锋枪**: `taa:bullet_gundamage_smg` - 冲锋枪伤害加成
- **轻机枪**: `taa:bullet_gundamage_lmg` - 轻机枪伤害加成
- **发射器**: `taa:bullet_gundamage_launcher` - 发射器伤害加成

### 20个核心枪械属性

- **瞄准时间**: `taa:ads_time` - 瞄准速度加成（数值越小越好）
- **弹药速度**: `taa:ammo_speed` - 子弹飞行速度加成
- **护甲穿透**: `taa:armor_ignore` - 护甲穿透效果
- **有效射程**: `taa:effective_range` - 射击距离加成
- **移动速度**: `taa:move_speed` - 持枪移动速度
- **爆头倍数**: `taa:headshot_multiplier` - 爆头伤害倍率
- **击退效果**: `taa:knockback` - 子弹击退力度
- **穿透能力**: `taa:pierce` - 子弹穿透能力
- **射速**: `taa:rounds_per_minute` - 射击速度
- **后坐力**: `taa:recoil` - 枪械后坐力（综合属性）
- **后坐力（垂直）**: `taa:recoil_pitch` - 垂直方向后坐力（细分属性）
- **后坐力（水平）**: `taa:recoil_yaw` - 水平方向后坐力（细分属性）
- **扩散倍率**: `taa:inaccuracy` - 射击扩散程度（综合属性）
- **扩散倍率（站立）**: `taa:inaccuracy_stand` - 站立时射击扩散（细分属性）
- **扩散倍率（移动）**: `taa:inaccuracy_move` - 移动时射击扩散（细分属性）
- **扩散倍率（蹲下）**: `taa:inaccuracy_sneak` - 蹲下时射击扩散（细分属性）
- **扩散倍率（趴下）**: `taa:inaccuracy_lie` - 趴下时射击扩散（细分属性）
- **扩散倍率（瞄准）**: `taa:inaccuracy_aim` - 瞄准时射击扩散（细分属性）
- **重量**: `taa:weight` - 枪械重量
- **弹药容量加成**: `taa:magazine_capacity` - 弹匣容量的额外加成
- **装填时间加成**: `taa:reload_time` - 装填时间的额外加成
- **近战伤害加成**: `taa:melee_damage` - 枪械近战攻击伤害的额外加成
- **近战距离加成**: `taa:melee_distance` - 枪械近战攻击距离的额外加成
- **子弹数量加成**: `taa:bullet_count` - 每次射击发射子弹数量的额外加成

### 特殊效果属性

- **消音效果**: `taa:silencenew` - 消音效果（<1.0时自动开启被动消音）
- **点燃效果**: `taa:ignitefire` - 子弹点燃效果

### 爆炸系统属性

- **爆炸半径**: `taa:explosion_radius` - 爆炸范围加成
- **爆炸伤害**: `taa:explosion_damage` - 爆炸伤害加成
- **爆炸击退**: `taa:explosion_knockbacknew` - 爆炸击退效果
- **破坏方块**: `taa:explosion_destroy_blocknew` - 爆炸破坏方块
- **爆炸延迟**: `taa:explosion_delay` - 爆炸延迟时间
- **爆炸启用**: `taa:explosion_enabled` - 爆炸是否开启（布尔属性）

### 过热体系属性（乘法倍率，默认1.0）

- **过热上限**: `taa:heat_max` - 满能量值（过热上限）倍率
- **散热速度**: `taa:heat_cooling` - 散热量随停火时长增长的速度倍率
- **冷却延迟**: `taa:heat_cooling_delay` - 停火后多久开始散热（毫秒）的倍率
- **锁枪时间**: `taa:heat_overheat_time` - 完全过热后的锁枪时长（毫秒）的倍率

### 配件Modifier系统属性

- **近战伤害**: `melee_damage` - 枪械近战攻击造成的伤害
- **近战距离**: `melee_distance` - 枪械近战攻击的有效距离
- **弹匣容量**: `magazine_capacity` - 枪械弹匣可装载的子弹数量
- **装填时间**: `reload_time` - 枪械装填子弹所需的时间
- **子弹数量**: `bullet_count` - 每次射击发射的子弹数量

### 属性值说明

- **基础值**: 1.0 (100%效果)
- **加成值**: 0.5 = 50%加成，1.0 = 100%加成
- **布尔属性**: 0.0表示false，1.0表示true
- **被动属性**: 某些属性在特定条件下自动触发（如消音效果<1.0时）

## 伤害计算规则

模组支持三种伤害计算模式，可通过配置文件动态切换：

#### 1. MAX模式（默认）
- **规则**: 通用与特定取最大值
- **公式**: `Math.max(通用伤害, 特定伤害)`

#### 2. ADDITIVE模式
- **规则**: 通用+特定-1
- **公式**: `通用伤害 + 特定伤害 - 1.0D`

#### 3. MULTIPLICATIVE模式
- **规则**: 通用*特定
- **公式**: `通用伤害 * 特定伤害`

### 配置方式

通过修改配置文件 `taa-attributes.toml` 中的 `damageCalculationMode` 选项来切换计算模式。

## 技术实现

### 核心组件

- **PropertyCalculator**: 属性计算器，负责所有属性的计算逻辑
- **PlayerAttributeHelper**: 玩家属性助手类，负责从玩家身上获取属性值
- **PropertyCacheUpdater**: 缓存更新器，将计算结果更新到附件缓存
- **GunPropertiesInitializer**: 枪械属性初始化器，动态获取玩家属性值
- **AttributeConfig**: 配置系统，管理伤害计算模式等配置项
- **GunTypeContext**: 枪械类型上下文，跟踪当前处理的枪械类型

### 客户端/服务端兼容性

- 所有客户端UI显示方法均使用 `@OnlyIn(Dist.CLIENT)` 注解标识
- 客户端专用的Mixin类被正确配置在 `taa.mixins.json` 的 `client` 数组中
- 服务端不会加载任何客户端专用代码，确保服务端稳定性

### 属性计算特性

- 基于乘法因子模式：`原始值 × 玩家属性因子`
- 兼容配件系统，不覆盖已有配件效果

## 配置文件

配置文件位置：`config/taa-attributes.toml`

```toml
[枪械伤害计算设置]
# 枪械伤害计算模式
# MAX: 通用与特定取最大值
# ADDITIVE: 通用+特定-1
# MULTIPLICATIVE: 通用*特定
damageCalculationMode = "MAX"

[调试日志设置]
# 是否启用调试日志记录
# true: 启用调试日志，将记录属性计算等详细信息
# false: 禁用调试日志，不记录任何调试信息（默认）
enableDebugLogging = false
```

## 版本信息

- **当前版本**: 1.3.8
- **Minecraft版本**: 1.21.1
- **模组加载器**: NeoForge
- **Tacz兼容版本**: 1.21.1

## 依赖要求

- **NeoForge**: 对应1.21.1版本
- **Tacz Guns Mod**: 1.21.1或兼容版本
- **Java**: 21或更高版本

## 构建

```bash
./gradlew build
```
