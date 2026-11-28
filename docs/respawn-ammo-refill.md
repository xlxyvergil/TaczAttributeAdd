# 重生后弹匣不满：成因与修复

TaCZ 开启「重生自动装填」（`AutoReloadWhenRespawn`）时，玩家重生后的弹匣只会被补到**未加成时的上限**，
下一 tick 容量变大后就显得"没补满"，且不会自动再补。

## 现象

- 配件通过 GunsmithLib 挂在枪上、再以主手物品属性修饰符的形式作用于玩家 -> `taa:magazine_capacity` 提高。
- 玩家死亡重生，TaCZ 立即补弹，弹匣 = 原生上限；一 tick 后容量上限变成加成后的值，弹匣仍停在原生上限。
- 之后再手动换弹、开枪都正常，只是重生这一次补不满。

实测（AK47，`ammo_amount` 30，修饰符 `MULTIPLY_TOTAL +1.0` -> 上限 60）：

| `AutoReloadWhenRespawn` | 重生后弹匣 | 容量上限 | 消耗弹药 |
| --- | --- | --- | --- |
| 开（未装本修复） | 30 | 60 | 30 |
| 开（装本修复） | 60 | 60 | 60 |

两种情况扣弹都等于实际补进弹匣的量，没有重复消耗。

## 根因：时序

重生时的事件顺序：

1. `PlayerList.respawn` 构造新的 `ServerPlayer`，调用 `ServerPlayer.restoreFrom`。
   该方法只在 `keepInventory` 或旁观者时把背包拷过来，**属性不在拷贝范围内**。
2. `ForgeEventFactory.firePlayerRespawnEvent` 触发 `PlayerEvent.PlayerRespawnEvent`。
3. TaCZ 的 `PlayerRespawnEvent` 处理器在**这个事件里**立刻补弹。
4. 新实体第一次 `tick`：`LivingEntity.collectEquipmentChanges` 才把主手物品的
   `AttributeModifiers` 用 `addTransientAttributeModifiers` 挂到玩家身上；
   Curios 的 `LivingTickEvent` 也在同一 tick 中重新挂饰品属性。

所以第 3 步读 `taa:magazine_capacity` 时，玩家身上还没有任何修饰符，读到的就是默认值 1.0，
`AttachmentDataUtils.getAmmoCountWithAttachment` 返回原生容量，弹匣被填到原生上限。

用原版机制可以等价复现（不需要枪包）：主手拿一根带 `max_health +10` 的物品，重生瞬间血量 20、
`max_health` 已经是 30——说明补血发生在修饰符挂上之前，与弹匣问题同源。

## 为什么不在重生事件里修

三个候选：

- **A. 在 TaCZ 补弹之前，把玩家的属性修饰符补回去**。被否决：修饰符的来源有两类，主手物品和饰品。
  主手物品可以自己解析 `ItemStack.getAttributeModifiers(slot)` 再挂，但饰品修饰符由 Curios 遍历槽位现算，
  重生事件触发时新玩家的饰品栏还是空的。抢 `EventPriority.HIGHEST` 时饰品数据没就绪，
  抢 `LOWEST` 时 TaCZ 已经补完弹。要绕开就得复刻 Curios 的槽位遍历与 `SlotAttribute` 特判，跨版本易碎。
- **B. 属性恢复后再补一次**（本修复采用的方案）。
- **C. 事后持续对比容量与弹匣**。限时对比一次等价于 B；持续对比会让"打空自动回满"，不能取。

## 实现

1. `src/main/java/com/xlxyvergil/taa/mixin/PlayerRespawnEventMixin.java`
   在 TaCZ 的 `PlayerRespawnEvent#onPlayerRespawn` 头部 `cancel`，即**取消重生时那次补弹**——
   它读到的是未加成容量，补了也会把满弹匣削到原生上限，还会白扣备弹。
2. `src/main/java/com/xlxyvergil/taa/event/RespawnAmmoRefillHandler.java`
   - `PlayerEvent.PlayerRespawnEvent`（`LOWEST`）：只把玩家 UUID 记进 `PENDING_REFILL`，此刻不算容量。
   - 新实体第一个 tick 末尾命中登记后，统一执行一次补弹。Forge 该阶段由
     `ForgeEventFactory.onPlayerPostTick` 在 `Player#tick` 尾部发出，NeoForge 用 `PlayerTickEvent.Post`；
     此时 `collectEquipmentChanges` 与 Curios 的属性挂载都已跑完，容量天然包含加成。
   - 补弹本身不自己实现，直接静态调用 TaCZ 的
     `com.tacz.guns.event.PlayerRespawnEvent.onPlayerRespawn(new PlayerEvent.PlayerRespawnEvent(player, false))`，
     不 post 到事件总线，避免触发其它模组的重生监听器。
   - 重入标记 `REENTRANT`（`ThreadLocal`）保证 mixin 放行这次主动调用、只取消真正由总线触发的那次。

复用 TaCZ 自身的实现而不是照抄镜像，是为了在 TaCZ 以后改动补弹规则（燃料枪、背包直读、
创造模式等分支）时不必跟着同步；代价是依赖了 TaCZ 的非 API 类。该方法是 `public static`，
1.20.1 与 1.21.1 的 TACZ 1.2.0 里都存在；一旦 TaCZ 改名或挪包，编译期就会直接失败，不会静默失效。

是否补弹仍由 TaCZ 的 `AutoReloadWhenRespawn` 控制，本模组不再另设开关。

## 已知代价与语义

- 补弹只执行一次，燃料枪消耗 1 份燃料（与 TaCZ 原生行为一致）。
- 补弹遍历的是背包里所有枪，用的都是玩家当前属性（即主手那支枪的配件修饰符）。
  拿着扩容枪时，背包里没装扩容配件的枪也会被补到加成后的容量——这与 TaCZ 自身的重生补弹、
  以及本模组平时的容量语义一致，不是本次修复引入的行为。

## 附带改动：容量读取统一

- `AttachmentDataUtilsMixin` 是 `getAmmoCountWithAttachment` 的**唯一**拦截点，不要再叠加其它 mixin，
  否则会重复套用属性、容量翻倍。射击者依次取 `ShooterContext`（游戏内调用）→ `PanelDrawScope`
  （属性面板绘制）→ 客户端本地玩家（HUD、tooltip 等无上下文读取）；服务端无上下文时保持原值。
  客户端兜底经 `Dist` 守卫隔离，专用服务器不会加载客户端类。
- `AmmoCapacityHelper.applyMagazineAttribute` 用 `Math.round` 而非截断，
  避免 `1.15 × 30 = 34.499…` 这类浮点误差把容量少算 1。
