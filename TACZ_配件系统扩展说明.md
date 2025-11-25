# TACZ 配件系统扩展

本扩展为TACZ添加了三个新的配件修改器，**完全遵循TACZ原版的Modifier架构和计算公式**。

## 🔍 TACZ配件系统原理

通过深入研究TACZ源代码发现，所有配件修改器都使用相同的模式：

### 核心架构：
1. **IAttachmentModifier<T, K>** - 统一的修改器接口
2. **AttachmentPropertyManager.eval()** - 统一的计算公式
3. **Modifier类** - 标准的JSON数据结构
4. **CacheValue** - 缓存机制

### 标准计算模式：
```java
@Override
public void eval(List<Modifier> modifiers, CacheValue<Float> cache) {
    double eval = AttachmentPropertyManager.eval(modifiers, cache.getValue());
    cache.setValue((float) eval);
}
```

### 统一计算公式：
```java
// 在AttachmentPropertyManager.eval()中
double addend = defaultValue;
double percent = 1;
double multiplier = 1;
for (Modifier modifier : modifiers) {
    addend += modifier.getAddend();
    percent += modifier.getPercent();
    multiplier *= Math.max(modifier.getMultiplier(), 0f);
}
percent = Math.max(percent, 0f);
double value = addend * percent * multiplier;
```

## 🚀 新增的配件修改器

### 1. 装填时间修改器 (ReloadModifier)
- **ID**: `reload_time`
- **功能**: 修改枪械的装填时间（简化为平均装填时间）
- **计算基础**: `(空仓装填时间 + 战术装填时间) / 2.0f`
- **遵循TACZ标准**: 使用统一的AttachmentPropertyManager.eval()计算

### 2. 弹药容量修改器 (AmmoCountModifier)
- **ID**: `ammo_count`
- **功能**: 修改枪械的弹药容量
- **计算基础**: `gunData.getAmmoAmount()`
- **遵循TACZ标准**: 使用统一的AttachmentPropertyManager.eval()计算

### 3. 近战攻击修改器 (MeleeModifier)
- **ID**: `melee`
- **功能**: 修改枪械的近战攻击距离
- **计算基础**: `gunData.getGunMeleeData().getDistance()`
- **遵循TACZ标准**: 使用统一的AttachmentPropertyManager.eval()计算

## 📝 JSON 配置格式

### 标准配置格式
所有修改器都使用相同的Modifier结构：

```json
{
  "modifier_id": {
    "addend": 数值加成,
    "percent": 百分比加成,
    "multiplier": 倍数加成,
    "function": "自定义函数"
  }
}
```

### 装填时间配件示例
```json
{
  "reload_time": {
    "addend": -0.4,
    "percent": -15,
    "multiplier": 0.85
  }
}
```

### 弹药容量配件示例
```json
{
  "ammo_count": {
    "addend": 10,
    "percent": 25,
    "multiplier": 1.25
  }
}
```

### 近战攻击配件示例
```json
{
  "melee": {
    "addend": 0.5,
    "percent": 50,
    "multiplier": 1.5
  }
}
```

## 🧮 计算逻辑详解

### 四种计算方式：
1. **addend**: 直接加值 (基础值 + addend)
2. **percent**: 百分比加成 (基础值 × (1 + percent/100))
3. **multiplier**: 倍数加成 (基础值 × multiplier)
4. **function**: 自定义函数 (Lua脚本)

### TACZ标准公式：
```
最终值 = (基础值 + addend) × (1 + percent/100) × multiplier
```

### 多配件叠加：
```
总addend = 所有配件addend之和
总percent = 1 + 所有配件percent之和
总multiplier = 所有配件multiplier之积
最终值 = (基础值 + 总addend) × 总percent × 总multiplier
```

## 技术实现细节

### 架构设计
- 完全遵循TACZ的 `IAttachmentModifier<T, K>` 接口
- 使用标准的 `AttachmentPropertyManager` 注册机制
- 支持TACZ的UI属性条显示系统

### 数据流
1. `initCache()` - 初始化枪械默认值
2. `readJson()` - 从配件JSON文件读取配置
3. `eval()` - 计算多个配件的叠加效果
4. `getPropertyDiagramsData()` - 提供UI显示数据

### 反射处理
由于TACZ原版数据类的私有字段限制，修改器使用反射来设置修改后的值。这是保持与原版系统兼容性的必要手段。

## 使用方法

### 1. 配件文件配置
在TACZ配件的JSON文件中添加对应的修改器配置。

### 2. 注册修改器
通过Mixin自动注册，无需手动配置。

### 3. 获取属性值
```java
// 获取修改后的装填时间
GunReloadData reloadData = cacheProperty.getCache("reload_time");

// 获取修改后的弹药容量
Integer ammoCount = cacheProperty.getCache("ammo_count");

// 获取修改后的近战数据
GunMeleeData meleeData = cacheProperty.getCache("melee");
```

## 注意事项

1. **兼容性**: 完全兼容TACZ原版系统，不会影响现有功能
2. **性能**: 反射操作在初始化时执行，运行时无性能影响
3. **扩展性**: 可以继续添加更多修改器，扩展系统功能
4. **UI支持**: 所有修改器都支持TACZ改装界面的属性条显示

## 示例配件

参考 `example_modifiers.json` 文件，包含了各种实际使用场景的配置示例。