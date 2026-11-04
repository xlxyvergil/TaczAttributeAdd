package com.xlxyvergil.taa.client.panel;

import com.tacz.guns.api.modifier.CacheValue;
import com.tacz.guns.api.modifier.IAttachmentModifier;
import com.tacz.guns.api.modifier.JsonProperty;
import com.tacz.guns.resource.modifier.AttachmentCacheProperty;
import com.tacz.guns.resource.pojo.data.gun.GunData;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Collections;
import java.util.List;

import static com.tacz.guns.api.modifier.IAttachmentModifier.DiagramsData;

/**
 * 把本模组独有的面板行当作「属性」注册进 TACZ 的 {@code AttachmentPropertyManager}。
 * <p>
 * TACZ 的改装界面会遍历注册表逐个问「你有几行、每行画什么」，所以用这个壳体注册进来之后，
 * 面板的绘制与高度计算都由 TACZ 自己完成，本模组不需要再覆写
 * {@code GunPropertyDiagrams.draw()} 或 {@code getHidePropertyButtonYOffset()}。
 * <p>
 * 注意这个壳体的 {@link #readJson(String)} / {@link #initCache(ItemStack, GunData)} 必须返回非 null：
 * <ul>
 *     <li>{@code AttachmentCacheProperty.eval()} 会对注册表里每个 id 调 {@code initCache} 并把结果放进 map，
 *     之后再 {@code getCache(id)} 直接取值，返回 null 会在任何一次属性重算时 NPE。</li>
 *     <li>{@code AttachmentDataManager.initModifiers()} 只在配件 json 里出现同名 key 时才调 {@code readJson}，
 *     本模组不写这些 key，所以实际不会命中，但仍保持非 null 以防第三方枪包写了同名键。</li>
 * </ul>
 * 这些壳体的缓存值本身没有意义（{@link #eval} 是空实现），行数据每次绘制时按枪械数据 + 玩家属性现算。
 */
@OnlyIn(Dist.CLIENT)
public final class TaaPanelModifier implements IAttachmentModifier<Object, Object> {

    /** 五个行组；顺序即 {@code AttachmentPropertyManager} 里的注册顺序。 */
    public enum Kind {
        /** 爆炸范围 + 爆炸伤害，常驻 2 行。 */
        EXPLOSION("taa_explosion", 2),
        /** 装填时间，1 行。 */
        RELOAD("taa_reload", 1),
        /** 暴击率 + 暴击伤害，行数取决于 Apothic Attributes 的数据。 */
        CRIT("taa_crit", -1),
        /** 近战伤害 + 近战距离，2 行。 */
        MELEE("taa_melee", 2),
        /** 过热 4 行，仅带热数据的枪显示。 */
        HEAT("taa_heat", -1);

        private final String id;
        /** 固定行数；-1 表示运行时决定。 */
        private final int fixedSize;

        Kind(String id, int fixedSize) {
            this.id = id;
            this.fixedSize = fixedSize;
        }

        public String getId() {
            return this.id;
        }
    }

    private final Kind kind;

    public TaaPanelModifier(Kind kind) {
        this.kind = kind;
    }

    public static TaaPanelModifier of(Kind kind) {
        return new TaaPanelModifier(kind);
    }

    @Override
    public String getId() {
        return this.kind.getId();
    }

    /**
     * 壳体没有可配置的字段：枪包 json 里不该出现这些 id，
     * 万一出现也解析成空属性而不是抛异常，避免整个配件加载失败。
     */
    @Override
    public JsonProperty<Object> readJson(String json) {
        return new EmptyJsonProperty();
    }

    @Override
    public CacheValue<Object> initCache(ItemStack gunItem, GunData gunData) {
        // 非 null 是硬性要求，见类注释
        return new CacheValue<>(Boolean.TRUE);
    }

    @Override
    public void eval(List<Object> modifiedValues, CacheValue<Object> cache) {
        // 行数据不经过属性缓存，绘制时现算
    }

    @Override
    public int getDiagramsDataSize() {
        return switch (this.kind) {
            case EXPLOSION, RELOAD, MELEE -> this.kind.fixedSize;
            case CRIT -> TaaPanelRows.critRowCount();
            case HEAT -> TaaPanelRows.heatRowCount();
        };
    }

    @Override
    public List<DiagramsData> getPropertyDiagramsData(ItemStack gunItem, GunData gunData,
                                                      AttachmentCacheProperty cacheProperty) {
        TaaPanelRows.remember(gunData);
        return switch (this.kind) {
            case EXPLOSION -> TaaPanelRows.explosion(gunItem, gunData);
            case RELOAD -> TaaPanelRows.reload(gunItem, gunData);
            case CRIT -> TaaPanelRows.crit(gunItem, gunData);
            case MELEE -> TaaPanelRows.melee(gunItem, gunData);
            case HEAT -> TaaPanelRows.heat(gunItem, gunData);
        };
    }

    /** 空属性：没有 value，也没有 tooltip 组件。 */
    private static final class EmptyJsonProperty extends JsonProperty<Object> {

        private EmptyJsonProperty() {
            super(null);
        }

        @Override
        public void initComponents() {
        }
    }

    /** 便利：一次拿到全部实例，注册时按 {@link Kind} 顺序遍历。 */
    public static List<TaaPanelModifier> all() {
        return Collections.unmodifiableList(List.of(
                of(Kind.EXPLOSION), of(Kind.RELOAD), of(Kind.CRIT), of(Kind.MELEE), of(Kind.HEAT)));
    }
}
