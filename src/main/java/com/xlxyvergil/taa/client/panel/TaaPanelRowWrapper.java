package com.xlxyvergil.taa.client.panel;

import com.tacz.guns.api.modifier.CacheValue;
import com.tacz.guns.api.modifier.IAttachmentModifier;
import com.tacz.guns.api.modifier.JsonProperty;
import com.tacz.guns.resource.modifier.AttachmentCacheProperty;
import com.tacz.guns.resource.pojo.data.gun.GunData;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;

import static com.tacz.guns.api.modifier.IAttachmentModifier.DiagramsData;

/**
 * 装饰 TACZ 自带的属性行，把 GunsmithLib / KuvaLich / KubeJS 的影响补进面板数值。
 * <p>
 * 装饰实例在 {@code AttachmentPropertyManager} 注册完成后替换进注册表（见
 * {@code AttachmentPropertyManagerRegisterMixin}）。除了
 * {@link #getPropertyDiagramsData} 之外全部原样转发，所以：
 * <ul>
 *     <li>缓存计算（{@code AttachmentCacheProperty.eval}）走的是同一份 {@code initCache} / {@code eval}，
 *     实战数值完全不受影响；</li>
 *     <li>面板高度（{@code getDiagramsDataSize}）原样转发，条目数与行数始终一致。</li>
 * </ul>
 * 之所以在注册表层面装饰而不是在 {@code GunPropertyDiagrams.draw} 里插桩：绘制代码全部位于
 * {@code CommonGunIndex.ifPresent(index -> ...)} 的 lambda 内，插桩目标会变成编译器生成的
 * {@code lambda$draw$N}，名字不稳定；而注册表是进程内唯一且长期存活的，替换一次就永久生效。
 */
public final class TaaPanelRowWrapper implements IAttachmentModifier<Object, Object> {

    private final IAttachmentModifier<Object, Object> delegate;

    @SuppressWarnings("unchecked")
    private TaaPanelRowWrapper(IAttachmentModifier<?, ?> delegate) {
        this.delegate = (IAttachmentModifier<Object, Object>) delegate;
    }

    /** 包装任意 TACZ 属性行修改器。 */
    public static IAttachmentModifier<?, ?> wrap(IAttachmentModifier<?, ?> delegate) {
        if (delegate instanceof TaaPanelRowWrapper || delegate instanceof TaaPanelModifier) {
            return delegate;
        }
        return new TaaPanelRowWrapper(delegate);
    }

    @Override
    public String getId() {
        return this.delegate.getId();
    }

    @Override
    public String getOptionalFields() {
        return this.delegate.getOptionalFields();
    }

    // getOptionalFieldList() 是 TACZ 1.2.0 才有的接口方法；当前编译目标仍是 1.1.7 的 jar，
    // 所以这里只转发 getOptionalFields()（1.2.0 的默认实现会用它推导列表）。
    // 依赖升到 1.2.0 之后可以补上 getOptionalFieldList() 的直接转发。

    @Override
    public JsonProperty<Object> readJson(String json) {
        return this.delegate.readJson(json);
    }

    @Override
    public CacheValue<Object> initCache(ItemStack gunItem, GunData gunData) {
        return this.delegate.initCache(gunItem, gunData);
    }

    @Override
    public void eval(List<Object> modifiedValues, CacheValue<Object> cache) {
        this.delegate.eval(modifiedValues, cache);
    }

    @Override
    public int getDiagramsDataSize() {
        return this.delegate.getDiagramsDataSize();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public List<DiagramsData> getPropertyDiagramsData(ItemStack gunItem, GunData gunData,
                                                      AttachmentCacheProperty cacheProperty) {
        List<DiagramsData> rows = this.delegate.getPropertyDiagramsData(gunItem, gunData, cacheProperty);
        if (rows == null || rows.isEmpty()) {
            return rows == null ? List.of() : rows;
        }
        // 让行数计算跟着当前面板里的这把枪走
        TaaPanelRows.remember(gunData);
        List<DiagramsData> rewritten = new ArrayList<>(rows.size());
        for (DiagramsData row : rows) {
            rewritten.add(TaaPanelRows.rewrite(gunItem, gunData, cacheProperty, row));
        }
        return rewritten;
    }
}
