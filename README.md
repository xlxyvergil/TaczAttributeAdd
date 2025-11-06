# TaczAttributeAdd Mod - 全面的TACZ属性增强系统

## 概述

TaczAttributeAdd 是一个为TACZ（Timeless & Classics Guns: Zero）模组提供全面属性增强的扩展模组。它通过注册28个独立的玩家属性，为TACZ枪械系统添加了深度的自定义和属性增强功能。

## 核心特性

### 🎯 全面的属性系统
- **28个独立属性** - 涵盖枪械性能的各个方面
- **双端同步** - 所有属性都支持客户端-服务器同步
- **无限范围** - 属性值无上限，支持任意数值扩展

### 🔧 智能事件处理
- **LOWEST优先级** - 确保在TACZ配件计算完成后执行
- **兼容性优先** - 与TACZ 1.19.2版本完全兼容
- **Mixin集成** - 通过Mixin传递实体上下文信息

## 已注册的属性列表

### 📊 基础性能属性
- **`taa:ads_time`** - 瞄准时间，影响瞄准速度
- **`taa:ammo_speed`** - 弹药速度，控制子弹飞行速度
- **`taa:armor_ignore`** - 护甲穿透，忽略目标护甲的能力
- **`taa:effective_range`** - 有效射程，枪械的有效射击距离

### 💥 爆炸相关属性
- **`taa:explosion_radius`** - 爆炸半径，爆炸影响范围
- **`taa:explosion_damage`** - 爆炸伤害，爆炸产生的伤害值
- **`taa:explosion_knockbacknew`** - 爆炸击退，爆炸是否产生击退
- **`taa:explosion_destroy_blocknew`** - 破坏方块，爆炸能否破坏方块
- **`taa:explosion_delay`** - 爆炸延迟，子弹命中到爆炸的时间

### 🎮 操控性属性
- **`taa:move_speed`** - 移动速度，持枪时移动速度影响
- **`taa:inaccuracy`** - 不准确度，射击散布程度
- **`taa:recoil`** - 后坐力，射击时的后坐力大小
- **`taa:rounds_per_minute`** - 射速，每分钟发射子弹数量
- **`taa:weight`** - 重量，枪支的重量值

### 🎯 特殊效果属性
- **`taa:headshot_multiplier`** - 爆头倍数，爆头攻击伤害倍率
- **`taa:ignitefire`** - 点燃效果，子弹能否点燃实体
- **`taa:knockback`** - 击退效果，命中目标时的击退力度
- **`taa:pierce`** - 穿透能力，子弹可穿透实体数量
- **`taa:silence`** - 消音效果，开火音效的消音系数

### 💥 伤害加成属性（8个）
- **`taa:bullet_gundamage`** - 通用伤害加成
- **`taa:bullet_gundamage_pistol`** - 手枪专用伤害加成
- **`taa:bullet_gundamage_rifle`** - 步枪专用伤害加成
- **`taa:bullet_gundamage_shotgun`** - 霰弹枪专用伤害加成
- **`taa:bullet_gundamage_sniper`** - 狙击枪专用伤害加成
- **`taa:bullet_gundamage_smg`** - 冲锋枪专用伤害加成
- **`taa:bullet_gundamage_lmg`** - 轻机枪专用伤害加成
- **`taa:bullet_gundamage_launcher`** - 发射器专用伤害加成

## 属性配置说明

### 基础值配置
所有属性都有相同的配置模式：
- **基础值**: 1.0 (100%效果)
- **最小值**: 0.0
- **最大值**: Double.MAX_VALUE (无上限)
- **同步**: 所有属性都启用同步

### 属性值含义
- **1.0** = 100% 基础效果
- **0.5** = 50% 效果 (减半)
- **2.0** = 200% 效果 (加倍)

## 技术架构

### 注册机制
- **DeferredRegister模式** - 使用Forge的延迟注册系统
- **统一事件处理** - 通过EntityAttributeModificationEvent注册到所有实体
- **TACZ兼容模式** - 采用与TACZ相同的注册方式

### 事件处理优先级
```java
EventPriority.LOWEST // 确保在TACZ计算后执行
```

## 配置选项

### 配置文件位置
`config/taa-common.toml`

### 主要配置项
- **`enableDebugLogging`** - 启用调试日志
- **`enableDamageCalculationLogging`** - 详细伤害计算日志
- **`maxDamageMultiplier`** - 最大伤害倍率限制
- **`enableSpecificGunTypes`** - 启用特定枪械类型加成

### 伤害计算模式
- **MAX (默认)**: 取专属属性和通用属性中的最大值
- **ADD**: 专属属性和通用属性相加
- **MULTIPLY**: 专属属性和通用属性相乘，计算公式为 `(1 + 专属加成) × (1 + 通用加成)`

### 配置热重载
配置修改后无需重启游戏，使用 `/reload` 命令即可热重载。热重载支持所有配置项，包括伤害计算模式、最大伤害倍率等。

## 依赖与兼容性

### 必需依赖
- **Minecraft**: 1.19.2
- **Forge**: 43.2.0+
- **TACZ**: 1.1.4+

### 兼容特性
- ✅ 与TACZ 1.19.2完全兼容
- ✅ 正确处理TACZ配件计算顺序
- ✅ 支持所有实体类型
- ✅ 客户端-服务器同步

## 版本信息

- **当前版本**: 1.0.5
- **Minecraft版本**: 1.19.2
- **Forge版本**: 43.2.0+
- **TACZ版本**: 1.1.4+
- **最后更新**: 2025年11月6日
- **开发者**: xlxyvergil

## 使用说明

1. **安装依赖** - 确保已安装TACZ模组
2. **配置属性** - 通过命令或配置文件调整属性值
3. **热重载** - 使用 `/reload` 命令应用配置更改
4. **测试验证** - 检查属性加成效果

## 技术实现细节

### PlayerAttributeRegistry 架构
本模组的核心是`PlayerAttributeRegistry`类，它负责：
- 使用Forge的`DeferredRegister`系统注册所有属性
- 通过`EntityAttributeModificationEvent`将属性绑定到所有实体类型
- 确保属性值与TACZ的配件系统无缝集成

### 属性分类
- **性能属性**: 影响枪械基础性能（射速、精度等）
- **爆炸属性**: 控制爆炸相关效果
- **特效属性**: 提供特殊战斗效果
- **伤害属性**: 专门针对不同类型枪械的伤害加成

---

**注意**: 本模组设计为与TACZ模组协同工作，单独安装可能无法正常使用。