package com.xlxyvergil.taa.mixin.client;

import com.tacz.guns.api.modifier.IAttachmentModifier;
import com.tacz.guns.resource.modifier.AttachmentPropertyManager;
import com.tacz.guns.resource.modifier.custom.RecoilModifier;
import com.xlxyvergil.taa.client.panel.TaaPanelModifier;
import com.xlxyvergil.taa.client.panel.TaaPanelModifier.Kind;
import com.xlxyvergil.taa.client.panel.TaaPanelRowWrapper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 客户端专用：在 AttachmentPropertyManager 注册完默认 Modifier 之后，
 * <ul>
 *     <li>追加 5 个壳体 {@link TaaPanelModifier}（只提供改装界面的行数据，由 TACZ 自己遍历绘制，
 *     因此面板不再需要覆写 {@code GunPropertyDiagrams}）；</li>
 *     <li>给 TACZ 自带的属性行包一层 {@link TaaPanelRowWrapper}，绘制时补上 GunsmithLib / KuvaLich /
 *     KubeJS 的数值影响，其余方法全部原样转发；</li>
 *     <li>重排注册表，让面板上的行顺序与迁移前一致。</li>
 * </ul>
 * 注册顺序决定面板上的行顺序（{@code MODIFIERS} 是 {@code LinkedHashMap}，{@code getModifiers()}
 * 返回的是活引用）。这里保留 TACZ 的原始相对顺序，只把本模组的行插到合适的位置：
 * 爆炸组放到最前，装填时间跟在「后坐力」之后，暴击/近战/过热顺延到末尾。
 */
@OnlyIn(Dist.CLIENT)
@Mixin(value = AttachmentPropertyManager.class, remap = false)
public class AttachmentPropertyManagerPanelMixin {

    private static final Logger LOGGER = LogManager.getLogger("taa-panel");

    @Inject(method = "registerModifier", at = @At("TAIL"), require = 0)
    private static void taa$registerPanelModifiers(CallbackInfo ci) {
        Map<String, IAttachmentModifier<?, ?>> modifiers = AttachmentPropertyManager.getModifiers();

        for (TaaPanelModifier modifier : TaaPanelModifier.all()) {
            modifiers.put(modifier.getId(), modifier);
        }

        // 把 TACZ 自带的属性行包一层装饰器：面板绘制时补上 GunsmithLib / KuvaLich / KubeJS 的数值影响，
        // 其它转发全部原样，实战数值与面板高度都不受影响
        modifiers.replaceAll((id, modifier) -> modifier instanceof TaaPanelModifier
                ? modifier : TaaPanelRowWrapper.wrap(modifier));

        reorderPanelRows(modifiers);
        LOGGER.debug("[taa] panel rows after registration: {}", describe(modifiers));
    }

    /** 调试用：把注册表里的 id 与行数打成一行，方便确认面板迁移后的行顺序与高度。 */
    private static String describe(Map<String, IAttachmentModifier<?, ?>> modifiers) {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, IAttachmentModifier<?, ?>> entry : modifiers.entrySet()) {
            if (builder.length() > 0) {
                builder.append(", ");
            }
            builder.append(entry.getKey()).append('(').append(entry.getValue().getDiagramsDataSize()).append(')');
        }
        return builder.toString();
    }

    /**
     * 按面板期望的顺序重排注册表。用一个新 map 承接，最后原地 clear + putAll，
     * 保证其它地方拿到的引用不变。
     */
    private static void reorderPanelRows(Map<String, IAttachmentModifier<?, ?>> modifiers) {
        Map<String, IAttachmentModifier<?, ?>> ordered = new LinkedHashMap<>(modifiers.size());

        // 爆炸组常驻面板最上方
        insertIfPresent(ordered, modifiers, Kind.EXPLOSION.getId());

        for (Map.Entry<String, IAttachmentModifier<?, ?>> entry : modifiers.entrySet()) {
            String id = entry.getKey();
            if (isPanelRow(id, Kind.EXPLOSION) || isPanelRow(id, Kind.RELOAD)
                    || isPanelRow(id, Kind.CRIT) || isPanelRow(id, Kind.MELEE) || isPanelRow(id, Kind.HEAT)) {
                continue;
            }
            ordered.put(id, entry.getValue());
            // 装填时间与换弹相关，跟在「后坐力」之后
            if (RecoilModifier.ID.equals(id)) {
                insertIfPresent(ordered, modifiers, Kind.RELOAD.getId());
            }
        }

        // 暴击 / 近战 / 过热顺延到末尾
        insertIfPresent(ordered, modifiers, Kind.CRIT.getId());
        insertIfPresent(ordered, modifiers, Kind.MELEE.getId());
        insertIfPresent(ordered, modifiers, Kind.HEAT.getId());

        modifiers.clear();
        modifiers.putAll(ordered);
    }

    private static boolean isPanelRow(String id, Kind kind) {
        return kind.getId().equals(id);
    }

    private static void insertIfPresent(Map<String, IAttachmentModifier<?, ?>> target,
                                        Map<String, IAttachmentModifier<?, ?>> source, String id) {
        IAttachmentModifier<?, ?> modifier = source.get(id);
        if (modifier != null) {
            target.put(id, modifier);
        }
    }
}
