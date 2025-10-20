# TAA Mod - Tacz属性增强模组

## 概述

TAA Mod 是一个为 Minecraft Tacz 枪械模组提供属性增强系统的插件。通过添加智能伤害加成计算，让玩家的属性能够直接影响枪械伤害输出。

## 功能特性

- 🔫 **智能伤害计算** - 根据枪械类型自动选择对应的属性加成
- ⚙️ **灵活配置** - 支持多种伤害计算模式
- 🎯 **枪械分类** - 支持手枪、步枪、霰弹枪等7种枪械类型
- 🔧 **兼容性** - 支持 Puffish Skills 集成
- 📊 **调试支持** - 详细的日志输出和调试功能

## 安装要求

- Minecraft 1.20.1
- Forge 47.2.0+
- Tacz 枪械模组

## 快速开始

### 1. 伤害计算公式

属性在 `BulletDamageMixin.java` 中参与伤害计算：

```
最终伤害 = 基础伤害 × (1 + 属性加成值)
```

- **基础伤害**: Tacz枪械的原始伤害值
- **属性加成值**: 玩家对应属性的当前值

### 2. 属性选择流程

伤害计算采用智能属性选择规则：

1. **检查枪械** - 确认玩家手持的是枪械
2. **识别类型** - 通过Tacz API获取枪械类型
3. **检查专属属性** - 查找对应枪械类型的专属加成
4. **检查通用属性** - 查找通用枪械伤害加成
5. **计算加成** - 根据配置模式计算最终加成

## 属性系统

### 通用属性

| 属性ID | 描述 | 基础值 |
|--------|------|--------|
| `taa:tacz.bullet_gundamage` | 枪械通用伤害加成 | 1.0 |

### 特定枪械属性

| 枪械类型 | 属性ID | 描述 |
|----------|--------|------|
| 手枪 | `taa:tacz.bullet_gundamage_pistol` | 手枪伤害加成 |
| 步枪 | `taa:tacz.bullet_gundamage_rifle` | 步枪伤害加成 |
| 霰弹枪 | `taa:tacz.bullet_gundamage_shotgun` | 霰弹枪伤害加成 |
| 狙击枪 | `taa:tacz.bullet_gundamage_sniper` | 狙击枪伤害加成 |
| 冲锋枪 | `taa:tacz.bullet_gundamage_smg` | 冲锋枪伤害加成 |
| 轻机枪 | `taa:tacz.bullet_gundamage_lmg` | 轻机枪伤害加成 |
| 发射器 | `taa:tacz.bullet_gundamage_launcher` | 发射器伤害加成 |

### 属性值说明

- **基础值**: 1.0 (100%伤害)
- **加成值**: 0.5 = 50%伤害加成，1.0 = 100%伤害加成
- **最大值**: 可配置，默认1024.0

## 配置选项

配置文件位置：`config/taa-common.toml`

### 调试配置

```toml
[debug]
# 启用调试日志输出
enableDebugLogging = false
# 启用伤害计算详细日志
enableDamageCalculationLogging = false
```

### 属性配置

```toml
[attributes]
# 最大伤害倍率限制
maxDamageMultiplier = 1024.0
# 启用特定枪械类型伤害加成
enableSpecificGunTypes = true
```

### 技能集成

```toml
[skills]
# 启用Puffish Skills集成
enablePuffishSkillsIntegration = true
```

### 伤害计算模式

```toml
[damage_calculation]
# 伤害计算模式: MAX(取最大值)、ADD(相加)、MULTIPLY(相乘)
damageCalculationMode = "MAX"
```

#### 计算模式说明

- **MAX (默认)**: 取专属属性和通用属性中的最大值
- **ADD**: 专属属性和通用属性相加
- **MULTIPLY**: 专属属性和通用属性相乘，计算公式为 `(1 + 专属加成) × (1 + 通用加成)`

## 使用示例

### 代码示例

```java
// 设置通用枪械伤害加成50%
player.getAttribute(ModAttributes.BULLET_GUNDAMAGE.get()).setBaseValue(0.5);

// 设置手枪伤害加成100%
player.getAttribute(ModAttributes.BULLET_GUNDAMAGE_PISTOL.get()).setBaseValue(1.0);
```

### 配置示例

```toml
[damage_calculation]
# 使用相加模式计算伤害
damageCalculationMode = "ADD"

[attributes]
# 限制最大伤害倍率为500%
maxDamageMultiplier = 5.0
```

## 技术细节

### 核心类说明

- **`BulletGunDamageReward.java`** - 智能伤害计算核心逻辑
- **`ModConfig.java`** - 配置管理和伤害计算模式定义
- **`BulletDamageMixin.java`** - Tacz伤害计算的Mixin注入点

### 依赖关系

- **必需**: Tacz API
- **可选**: Puffish Skills (用于技能系统集成)

## 故障排除

### 常见问题

1. **伤害加成不生效**
   - 检查玩家是否手持枪械
   - 确认属性是否正确设置
   - 查看调试日志输出

2. **特定枪械类型不识别**
   - 检查枪械类型是否在支持列表中
   - 确认Tacz API调用正常

3. **配置不生效**
   - 重启游戏应用配置更改
   - 检查配置文件语法是否正确

### 调试模式

启用调试模式查看详细的计算过程：

```toml
[debug]
enableDebugLogging = true
enableDamageCalculationLogging = true
```

## 版本信息

- **当前版本**: 1.0.0
- **Minecraft版本**: 1.20.1
- **最后更新**: 2024年

## 许可证

本项目采用开源许可证，详见 LICENSE 文件。