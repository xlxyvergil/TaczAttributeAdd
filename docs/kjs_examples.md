# TAA 属性后处理 KubeJS 示例

## 概述

`TAAContextEvents.attributePost` 事件在 TAA 完成属性计算后触发，允许你获取玩家实体并修改伤害计算结果。

## 事件信息

- **事件ID**: `TAAContextEvents.attributePost`
- **脚本类型**: Server 脚本
- **触发时机**: TAA 属性计算完成后，缓存更新前

## 可访问的数据

| 属性 | 类型 | 说明 |
|------|------|------|
| `shooter` | `LivingEntity` | 射击者实体，可以获取玩家的自定义属性 |
| `gunItem` | `ItemStack` | 枪械物品 |
| `results` | `PropertyCalculationResults` | 计算结果，可修改 |

## 涉及的模组弹射物属性

| 模组 | 属性ID | 默认值 | 说明 |
|------|--------|--------|------|
| L2DamageTracker (L2Artifacts依赖) | `l2damagetracker:bow_strength` | 1.0 | 弓强度乘数，L2Artifacts的bow_strength_add统计类型引用此属性 |
| Apothic-Attributes | `attributeslib:arrow_damage` | 1.0 | 弹射物伤害乘数 |

---

## 示例：使用 L2DamageTracker 和 Apothic-Attributes 弹射物加成

```javascript
// server_scripts/taa_combined_with_attack_damage.js
// 获取 DistanceDamagePair 类（KubeJS 2001 使用 Java.loadClass）
var DistanceDamagePair = Java.loadClass('com.tacz.guns.resource.pojo.data.gun.ExtraDamage$DistanceDamagePair');
// 获取 LinkedList 类，用于创建 damage List
var LinkedList = Java.loadClass('java.util.LinkedList');

TAAContextEvents.attributePost(event => {
    var shooter = event.shooter;
    var results = event.results;

    // 获取 L2DamageTracker 的弓强度乘数（默认值 1.0）
    var bowStrengthMultiplier = 1.0;
    var bowStrength = shooter.getAttribute('l2damagetracker:bow_strength');
    if (bowStrength) {
        bowStrengthMultiplier = bowStrength.value;
    }

    // 获取 Apothic-Attributes 的弹射物伤害乘数（默认值 1.0）
    var arrowDamageMultiplier = 1.0;
    var arrowDamage = shooter.getAttribute('attributeslib:arrow_damage');
    if (arrowDamage) {
        arrowDamageMultiplier = arrowDamage.value;
    }

    // 获取攻击伤害实体属性加成（加法）
    var attackDamageBonus = 0;
    var attackDamage = shooter.getAttribute('minecraft:generic.attack_damage');
    if (attackDamage) {
        attackDamageBonus = attackDamage.value;
    }

    var damageList = results.damage;
    if (damageList && damageList.length > 0) {
        // 使用 Java LinkedList 而不是 JS 数组
        var newDamageList = new LinkedList();
        for (var i = 0; i < damageList.length; i++) {
            var pair = damageList.get(i);
            var distance = pair.getDistance();
            var currentDamage = pair.getDamage();

            // 公式: 原伤害 × 古遗物 × 神化 + 攻击伤害加成
            // 乘数默认值为 1.0，乘以 1 还是原值，不需要判断
            var newDamage = currentDamage * bowStrengthMultiplier * arrowDamageMultiplier + attackDamageBonus;

            // 使用 new 关键字直接创建 DistanceDamagePair 实例
            newDamageList.add(new DistanceDamagePair(distance, newDamage));
        }

        results.setDamage(newDamageList);
    }
});
```

---

## 更多示例

完整示例文件位于 `docs/examples/` 目录：

| 文件 | 说明 |
|------|------|
| `example_l2artifacts_only/server_scripts/taa_l2artifacts_bow_strength.js` | 只使用古遗物加成 |
| `example_apothic_only/server_scripts/taa_apothic_arrow_damage.js` | 只使用神化属性加成 |
| `example_both_mods/server_scripts/taa_l2artifacts_apothic_combined.js` | 两者都使用 |
| `example_combined_with_attack/server_scripts/taa_combined_with_attack_damage.js` | 两者 + 攻击伤害加成 |

---

## 注意事项

1. **修改后会自动应用**: 修改 `results` 对象后，TAA 会自动进行二次缓存更新
2. **属性可能不存在**: 使用 `.getAttribute()` 后需要检查是否为 `null`
3. **属性值默认值**: 使用 `.value` 获取属性值，乘数类型默认值为 1.0
4. **伤害列表是只读的**: `DistanceDamagePair` 只有 getter 没有 setter，需要创建新对象并替换整个列表

## 相关链接

- [Tacz 属性系统文档](https://github.com/TaczOrg/TaczDocs)
- [KubeJS 官方文档](https://kubejs.com/wiki/)