# TAA 药水效果系统使用指南

## 概述

TAA模组现已支持通过药水效果动态调整枪械属性。每个属性对应一个独立的药水效果，支持1-255级线性叠加。

## 核心机制

### 等级计算公式
```
实际倍率 = (amplifier + 1) × 1%
```
- amplifier = 0 → I级 → 1%
- amplifier = 49 → 50级 → 50%
- amplifier = 254 → 255级 → 255%

### 使用方法

#### 1. Java代码中给予药水效果

```java
import com.xlxyvergil.taa.effect.TaaMobEffects;
import net.minecraft.world.effect.MobEffectInstance;

// 给予步枪伤害提升50级（50%增伤）
LivingEntity entity = ...;
entity.addEffect(new MobEffectInstance(
    TaaMobEffects.BULLET_GUNDAMAGE_RIFLE.get(),
    200,  // 持续时间：200 tick = 10秒
    49    // amplifier = 等级 - 1（50级 = amplifier 49）
));

// 给予瞄准时间减少30级（30%加速）
entity.addEffect(new MobEffectInstance(
    TaaMobEffects.ADS_TIME.get(),
    200,
    29  // 30级
));
```

#### 2. 在TACZ射击事件中触发

```java
@SubscribeEvent
public void onGunFire(GunShootEvent event) {
    LivingEntity shooter = event.getShooter();
    if (shooter == null || shooter.level().isClientSide()) return;
    
    GunType type = getGunType(event.getGun());
    
    // 根据枪械类型给予不同等级的效果
    int level = switch (type) {
        case RIFLE -> 30;   // 步枪30级
        case PISTOL -> 20;  // 手枪20级
        default -> 0;
    };
    
    if (level > 0) {
        MobEffect effect = switch (type) {
            case RIFLE -> TaaMobEffects.BULLET_GUNDAMAGE_RIFLE.get();
            case PISTOL -> TaaMobEffects.BULLET_GUNDAMAGE_PISTOL.get();
            default -> null;
        };
        
        if (effect != null) {
            shooter.addEffect(new MobEffectInstance(effect, 200, level - 1));
        }
    }
}
```

#### 3. KubeJS脚本中使用

```javascript
// events.server.js
onEvent('entity.hurt', event => {
    let player = event.getEntity()
    if (player.isPlayer()) {
        // 给予玩家枪械伤害提升100级
        player.addPotionEffect('taa:bullet_gundamage', 200, 99)
    }
})
```

## 可用药水效果列表

### 核心属性
| 效果ID | 中文名 | 每级效果 |
|--------|--------|----------|
| `taa:ads_time` | 瞄准时间提升 | -1% 瞄准时间/级 |
| `taa:ammo_speed` | 弹药速度提升 | +1% 弹药速度/级 |
| `taa:armor_ignore` | 护甲穿透提升 | +1% 护甲穿透/级 |
| `taa:effective_range` | 有效射程提升 | +1% 有效射程/级 |
| `taa:headshot_multiplier` | 爆头伤害提升 | +1% 爆头伤害/级 |

### 枪械伤害
| 效果ID | 中文名 | 每级效果 |
|--------|--------|----------|
| `taa:bullet_gundamage` | 通用枪械伤害 | +1%/级 |
| `taa:bullet_gundamage_pistol` | 手枪伤害 | +1%/级 |
| `taa:bullet_gundamage_rifle` | 步枪伤害 | +1%/级 |
| `taa:bullet_gundamage_shotgun` | 霰弹枪伤害 | +1%/级 |
| `taa:bullet_gundamage_sniper` | 狙击枪伤害 | +1%/级 |
| `taa:bullet_gundamage_smg` | 冲锋枪伤害 | +1%/级 |
| `taa:bullet_gundamage_lmg` | 轻机枪伤害 | +1%/级 |
| `taa:bullet_gundamage_launcher` | 发射器伤害 | +1%/级 |

### 近战属性
| 效果ID | 中文名 | 每级效果 |
|--------|--------|----------|
| `taa:melee_damage` | 近战伤害提升 | +1%/级 |
| `taa:melee_distance` | 近战距离提升 | +1%/级 |

## 注意事项

1. **等级上限**：最大255级（amplifier = 254）
2. **效果独立**：不同类型的枪械伤害效果互不干扰
3. **自动同步**：药水效果会自动网络同步到客户端
4. **UI显示**：效果会显示在玩家的药水效果栏和物品提示中
5. **图标占位**：当前使用占位图标，需替换为正式图标（见 README_ICONS.txt）

## 图标制作

请参考 `src/main/resources/assets/taa/textures/mob_effect/README_ICONS.txt` 中的说明制作或替换图标文件。
