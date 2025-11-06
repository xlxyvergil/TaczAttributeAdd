# TAA Mod - Tacz属性增强模组

## 功能特性

- **智能伤害计算** - 根据枪械类型自动选择对应的属性加成
- **灵活配置** - 支持多种伤害计算模式，支持游戏内热重载
- **枪械分类** - 支持手枪、步枪、霰弹枪等7种枪械类型
- **独立属性系统** - 使用独立的属性系统实现伤害加成
- **调试支持** - 详细的日志输出和调试功能

## 属性系统

### 通用属性

- **属性ID**: `taa:tacz.bullet_gundamage`
- **描述**: 枪械通用伤害加成
- **基础值**: 1.0

### 特定枪械属性

- **手枪**: `taa:tacz.bullet_gundamage_pistol` - 手枪伤害加成
- **步枪**: `taa:tacz.bullet_gundamage_rifle` - 步枪伤害加成
- **霰弹枪**: `taa:tacz.bullet_gundamage_shotgun` - 霰弹枪伤害加成
- **狙击枪**: `taa:tacz.bullet_gundamage_sniper` - 狙击枪伤害加成
- **冲锋枪**: `taa:tacz.bullet_gundamage_smg` - 冲锋枪伤害加成
- **轻机枪**: `taa:tacz.bullet_gundamage_lmg` - 轻机枪伤害加成
- **发射器**: `taa:tacz.bullet_gundamage_launcher` - 发射器伤害加成

### 属性值说明

- **基础值**: 1.0 (100%伤害)
- **加成值**: 0.5 = 50%伤害加成，1.0 = 100%伤害加成
- **最大值**: 可配置，默认1024.0

## 配置选项

配置文件位置：`config/taa-common.toml`

### 调试配置

- **enableDebugLogging**: 启用调试日志输出
- **enableDamageCalculationLogging**: 启用伤害计算详细日志

### 属性配置

- **maxDamageMultiplier**: 最大伤害倍率限制
- **enableSpecificGunTypes**: 启用特定枪械类型伤害加成

### 伤害计算模式

- **MAX (默认)**: 取专属属性和通用属性中的最大值
- **ADD**: 专属属性和通用属性相加
- **MULTIPLY**: 专属属性和通用属性相乘，计算公式为 `(1 + 专属加成) × (1 + 通用加成)`

### 配置热重载

配置修改后无需重启游戏，使用 `/reload` 命令即可热重载。热重载支持所有配置项，包括伤害计算模式、最大伤害倍率等。

## 版本信息

- **当前版本**: 1.0.1
- **Minecraft版本**: 1.20.1
- **最后更新**: 2025年10月20日