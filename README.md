# TaczAttributeAdd MOD 属性说明

## 1. 属性参与的计算公式

属性在 `BulletDamageMixin.java` 中参与伤害计算，计算公式为：

**最终伤害 = 基础伤害 × (1 + 属性加成值)**

- **基础伤害**: Tacz枪械的原始伤害值
- **属性加成值**: 玩家对应属性的当前值
- **计算位置**: 在子弹初始化时通过Mixin注入到Tacz的伤害计算流程中

## 2. 属性选择规则

伤害计算时采用智能属性选择规则（在 `BulletGunDamageReward.java` 中实现）：

### 属性选择优先级
1. **检查玩家是否手持枪械** - 如果不是枪械，不应用伤害加成
2. **获取手持枪械的类型** - 通过Tacz API获取枪械类型（pistol、rifle、shotgun等）
3. **检查专属属性** - 检查玩家是否有该枪械类型的专属属性加成
4. **检查通用属性** - 检查玩家是否有通用枪械伤害加成属性
5. **选择最大值** - 取专属属性和通用属性中的最大值作为最终加成

### 属性映射关系
| 枪械类型 | 对应属性 |
|---------|---------|
| pistol | `taa:tacz.bullet_gundamage_pistol` |
| rifle | `taa:tacz.bullet_gundamage_rifle` |
| shotgun | `taa:tacz.bullet_gundamage_shotgun` |
| sniper | `taa:tacz.bullet_gundamage_sniper` |
| smg | `taa:tacz.bullet_gundamage_smg` |
| lmg | `taa:tacz.bullet_gundamage_lmg` |
| launcher | `taa:tacz.bullet_gundamage_launcher` |

## 3. 属性列表和使用方式

### 通用属性
- `taa:tacz.bullet_gundamage` - 枪械通用加伤

### 特定枪械类型属性（可选）
- `taa:tacz.bullet_gundamage_pistol` - 手枪伤害加成
- `taa:tacz.bullet_gundamage_rifle` - 步枪伤害加成  
- `taa:tacz.bullet_gundamage_shotgun` - 霰弹枪伤害加成
- `taa:tacz.bullet_gundamage_sniper` - 狙击枪伤害加成
- `taa:tacz.bullet_gundamage_smg` - 冲锋枪伤害加成
- `taa:tacz.bullet_gundamage_lmg` - 轻机枪伤害加成
- `taa:tacz.bullet_gundamage_launcher` - 发射器伤害加成

### 使用示例
```java
// 设置通用枪械伤害加成50%
player.getAttribute(ModAttributes.BULLET_GUNDAMAGE.get()).setBaseValue(0.5);

// 设置手枪伤害加成100%
player.getAttribute(ModAttributes.BULLET_GUNDAMAGE_PISTOL.get()).setBaseValue(1.0);
```

### 属性值说明
- **基础值**: 1.0 (100%伤害)
- **加成值**: 0.5 = 50%伤害加成，1.0 = 100%伤害加成
- **最大值**: 可配置，默认1024.0