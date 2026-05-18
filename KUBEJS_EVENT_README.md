# TAA KubeJS 属性显示事件系统

## 概述

TAA（TaczAttributeAdd）提供了一个KubeJS事件系统，允许整合包开发者通过KubeJS脚本自定义枪械属性面板中显示的数值。

## 安装要求

- **必需模组**：TaczAttributeAdd (TAA)
- **可选模组**：KubeJS（如果未安装，事件系统将自动禁用，不影响游戏运行）

## 事件说明

### 事件名称
```
TAAPropertyDisplayEvents:propertyDisplay
```

### 触发时机
在枪械属性面板绘制每个属性之前触发，允许修改显示值。

### 事件参数

| 方法 | 返回类型 | 说明 |
|------|---------|------|
| `getPlayer()` | Player | 当前查看属性的玩家 |
| `getGunItem()` | ItemStack | 当前查看的枪械物品 |
| `getPropertyType()` | String | 属性类型字符串 |
| `getDisplayValue()` | double | 当前显示值（可修改） |
| `getOriginalValue()` | double | 原始值（未修改前） |
| `setDisplayValue(double)` | void | 设置新的显示值 |
| `isModified()` | boolean | 是否已修改过值 |

### 可用属性类型

```javascript
'DAMAGE'           // 伤害
'HEADSHOT'         // 爆头倍率
'AMMO_CAPACITY'    // 弹匣容量
'RPM'              // 射速
'BULLET_SPEED'     // 弹丸速度
'RELOAD_TIME'      // 装填时间
'ADS_TIME'         // 瞄准时间
'INACCURACY'       // 精准度
'RECOIL_PITCH'     // 垂直后坐力
'RECOIL_YAW'       // 水平后坐力
'BULLET_COUNT'     // 子弹数量/多重射击
'ARMOR_IGNORE'     // 穿甲
'MELEE_DAMAGE'     // 近战伤害
'MELEE_DISTANCE'   // 近战距离
'EXPLOSION_RADIUS' // 爆炸范围
'EXPLOSION_DAMAGE' // 爆炸伤害
```

## 使用示例

### 基础用法

创建文件：`kubejs/client_scripts/taa_property_display.js`

```javascript
// 监听TAA属性显示事件
listen('TAAPropertyDisplayEvents:propertyDisplay', event => {
    const player = event.getPlayer()
    const gunItem = event.getGunItem()
    const propertyType = event.getPropertyType()
    const currentValue = event.getDisplayValue()
    
    let newValue = currentValue
    
    // 根据属性类型处理
    if (propertyType === 'BULLET_COUNT') {
        // 获取自定义属性
        const multipleShootAttr = player.getAttribute('last_one:multiple_shoot')
        if (multipleShootAttr) {
            const multishotMultiplier = multipleShootAttr.getValue() / 100.0
            if (multishotMultiplier > 0) {
                newValue = currentValue * (1 + multishotMultiplier)
            }
        }
    }
    
    // 如果修改了值，设置新值
    if (newValue !== currentValue) {
        event.setDisplayValue(newValue)
    }
})
```

### Last One Test 整合包示例

针对Last One Test的多重射击支持：

```javascript
listen('TAAPropertyDisplayEvents:propertyDisplay', event => {
    const player = event.getPlayer()
    const propertyType = event.getPropertyType()
    const currentValue = event.getDisplayValue()
    
    // 只处理子弹数量
    if (propertyType === 'BULLET_COUNT') {
        const multipleShootAttr = player.getAttribute('last_one:multiple_shoot')
        if (multipleShootAttr) {
            const multipleShootValue = multipleShootAttr.getValue()
            const multishotMultiplier = multipleShootValue / 100.0
            
            if (multishotMultiplier > 0) {
                event.setDisplayValue(currentValue * (1 + multishotMultiplier))
            }
        }
    }
})
```

### 高级用法 - 多属性修正

```javascript
listen('TAAPropertyDisplayEvents:propertyDisplay', event => {
    const player = event.getPlayer()
    const propertyType = event.getPropertyType()
    const currentValue = event.getDisplayValue()
    
    switch (propertyType) {
        case 'DAMAGE':
            // 根据玩家等级增加伤害
            const level = player.experienceLevel
            event.setDisplayValue(currentValue * (1 + level * 0.01))
            break
            
        case 'RPM':
            // 根据手持物品增加射速
            const mainHand = player.getMainHandItem()
            if (mainHand.hasTag('my_mod:rapid_fire_grip')) {
                event.setDisplayValue(currentValue * 1.2)
            }
            break
            
        case 'RECOIL_PITCH':
        case 'RECOIL_YAW':
            // 减少后坐力
            event.setDisplayValue(currentValue * 0.8)
            break
    }
})
```

## 注意事项

1. **客户端事件**：这是一个客户端事件，脚本必须放在 `client_scripts` 文件夹中
2. **性能考虑**：事件会在每次打开枪械属性面板时触发，避免在事件中进行复杂计算
3. **类型安全**：`propertyType` 是字符串类型，比较时使用字符串而非枚举
4. **可选依赖**：如果玩家未安装KubeJS，事件系统会自动禁用，不会影响游戏运行
5. **调试信息**：可以使用 `console.log()` 输出调试信息，但生产环境建议注释掉

## 技术实现

### 架构设计

```
Mixin (GunPropertyDiagramsMixin)
    ↓ 调用
KubeJSEventHelper.postAndGetDisplayValue()
    ↓ 检查
KubeJS.isLoaded()
    ↓ 触发
TAAPropertyDisplayEvents.PROPERTY_DISPLAY.post()
    ↓ 监听
KubeJS脚本 (listen事件)
    ↓ 返回
修改后的显示值
```

### 关键类

- `TAAKubeJSPlugin.java` - KubeJS插件入口
- `TAAPropertyDisplayEvents.java` - 事件定义和包装类
- `KubeJSEventHelper.java` - 事件触发工具类
- `kubejs.plugins.txt` - KubeJS插件注册文件

## 常见问题

### Q: 为什么我的脚本没有生效？
A: 确保：
1. 脚本放在 `client_scripts` 文件夹
2. 使用了正确的事件名称 `TAAPropertyDisplayEvents:propertyDisplay`
3. KubeJS已正确安装并加载

### Q: 如何调试事件是否触发？
A: 在事件开头添加：
```javascript
console.log(`[TAA Debug] Property: ${propertyType}, Value: ${currentValue}`)
```

### Q: 可以同时修改多个属性吗？
A: 不可以。事件是针对单个属性触发的，每次只会传入一个属性类型。如果需要修改多个属性，需要在事件中分别处理不同的 `propertyType`。

### Q: 会影响性能吗？
A: 影响很小。事件只在打开枪械属性面板时触发，且KubeJS事件系统本身已经过优化。避免在事件中进行大量循环或复杂计算即可。

## 更新日志

### v1.0.0 (2026-05-18)
- 初始版本发布
- 支持16种属性类型的显示值修改
- 提供完整的KubeJS事件API
- 兼容Last One Test等多重射击整合包
