package com.xlxyvergil.taa.modifier;

import com.google.gson.annotations.SerializedName;
import com.tacz.guns.api.modifier.CacheValue;
import com.tacz.guns.api.modifier.IAttachmentModifier;
import com.tacz.guns.api.modifier.JsonProperty;
import com.tacz.guns.resource.CommonAssetsManager;
import com.tacz.guns.resource.modifier.AttachmentCacheProperty;
import com.tacz.guns.resource.modifier.AttachmentPropertyManager;
import com.tacz.guns.resource.pojo.data.attachment.Modifier;
import com.tacz.guns.resource.pojo.data.gun.GunData;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.api.item.attachment.AttachmentType;
import com.tacz.guns.item.ModernKineticGunItem;
import com.tacz.guns.util.AllowAttachmentTagMatcher;
import com.xlxyvergil.taa.api.ExtendedGunProperties;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

/**
 * 子弹数量 Modifier：修改枪械每次射击发射的弹丸数。
 * <p>
 * 完全走 TACZ 配件系统的标准流程：配件 JSON 的 {@code bullet_count} 被 {@link #readJson} 解析成
 * {@link Modifier}，{@link #initCache} 给出基准值，{@link #eval} 在基准值上叠加倍率链，
 * 结果写进 {@code AttachmentCacheProperty}，供射击逻辑与改装面板读取。
 */
public class BulletCountModifier implements IAttachmentModifier<Modifier, Integer> {
    // 用扩展属性枚举的名字当 ID：缓存键、面板行与配件 JSON 的字段名共用同一套命名
    public static final String ID = ExtendedGunProperties.BULLET_COUNT.name();

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public JsonProperty<Modifier> readJson(String json) {
        BulletCountModifier.Data data = CommonAssetsManager.GSON.fromJson(json, BulletCountModifier.Data.class);
        return new BulletCountModifier.BulletCountJsonProperty(data.getBulletCount());
    }

    @Override
    public CacheValue<Integer> initCache(ItemStack gunItem, GunData gunData) {
        // 取当前子弹数量，小于等于 0 时兜底为 1
        int currentBulletCount = gunData.getBulletData().getBulletAmount();
        if (currentBulletCount <= 0) {
            currentBulletCount = 1;
        }
        // 装了独头弹时弹头数量固定为 1
        if (hasSlugEffect(gunItem)) {
            currentBulletCount = 1;
        }
        return new CacheValue<>(currentBulletCount);
    }

    @Override
    public void eval(List<Modifier> modifiers, CacheValue<Integer> cache) {
        // 与 TACZ 原生 Modifier 同款：拿缓存里的基准值走一遍通用倍率计算
        double eval = AttachmentPropertyManager.eval(modifiers, cache.getValue());
        // 弹丸数是整数，四舍五入后再写回缓存
        cache.setValue((int) Math.round(eval));
    }

    /**
     * 为属性面板预留的行数。
     * <p>
     * 返回 0 表示不在 TACZ 原生属性面板（GunPropertyDiagrams）里占行位，
     * 弹丸数那一行由 {@code TaaPanelRowWrapper} 单独补画，避免同一属性在面板里出现两遍。
     */
    @Override
    @OnlyIn(Dist.CLIENT)
    public int getDiagramsDataSize() {
        return 0; // 不给原生面板留行，弹丸数行由 TaaPanelRowWrapper 单独补画
    }

    /**
     * 检测枪械当前装的扩容弹匣是否带独头弹（slug）标签。
     * <p>
     * 独头弹把一次击发的多颗弹丸并成一颗，弹丸数固定为 1，所以装了它就不能再被倍率放大。
     * 判定交给 TACZ 的标签匹配：拿扩容弹匣（EXTENDED_MAG）配件的 id 去比
     * {@code ModernKineticGunItem.DefaultPropertyModification.SLUGS} 标签。
     * 该机制从 TACZ 1.1.7 才有，低版本、没有枪/配件或匹配抛异常都按“没装”处理。
     */
    private boolean hasSlugEffect(ItemStack gunItem) {
        // 低版本不支持独头弹检测
        if (!isTacz117OrAbove()) {
            return false;
        }
        
        try {
            IGun iGun = IGun.getIGunOrNull(gunItem);
            if (iGun == null) return false;
            
            ResourceLocation extendedMagId = iGun.getAttachmentId(gunItem, AttachmentType.EXTENDED_MAG);
            if (extendedMagId == null) return false;
            
            return AllowAttachmentTagMatcher.matchTag(
                ModernKineticGunItem.DefaultPropertyModification.SLUGS, 
                extendedMagId
            );
        } catch (Exception e) {
            // 版本不兼容或异常时按未安装处理
            return false;
        }
    }

    /** TACZ 版本是否 >= 1.1.7 */
    private static boolean isTacz117OrAbove() {
        try {
            String version = net.minecraftforge.fml.ModList.get()
                .getModContainerById("tacz")
                .map(container -> container.getModInfo().getVersion().toString())
                .orElse("0.0.0");
            
            // 版本串来自 mod 容器元数据；上面取不到容器时已兜底成 0.0.0，这里只比数字段
            return isVersionAtLeast(version, "1.1.7");
        } catch (Exception e) {
            // 拿不到版本按旧版处理
            return false;
        }
    }
    
    /**
     * 逐段比较点分版本号，判断 {@code target} 是否大于等于 {@code base}。
     * <p>
     * 按 "." 切段后从高位往低位比整数，段数少的一侧补 0；段内的非数字后缀
     * （如 {@code 7-beta} 里的 {@code -beta}）先剥掉再解析。任一段解析失败即返回 false。
     */
    private static boolean isVersionAtLeast(String target, String base) {
        try {
            String[] targetParts = target.split("\\.");
            String[] baseParts = base.split("\\.");
            
            for (int i = 0; i < Math.max(targetParts.length, baseParts.length); i++) {
                int targetPart = i < targetParts.length ? Integer.parseInt(targetParts[i].replaceAll("\\D.*", "")) : 0;
                int basePart = i < baseParts.length ? Integer.parseInt(baseParts[i].replaceAll("\\D.*", "")) : 0;
                
                if (targetPart > basePart) return true;
                if (targetPart < basePart) return false;
            }
            // 都相等
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /** 配件 JSON 里 {@code bullet_count} 字段的解析结果，负责把倍率说明挂进 tooltip。 */
    public static class BulletCountJsonProperty extends JsonProperty<Modifier> {
        public BulletCountJsonProperty(Modifier value) {
            super(value);
        }

        @Override
        public void initComponents() {
            Modifier value = getValue();
            if (value != null) {
                components.add(Component.translatable("tooltip.tacz.attachment.bullet_count.change").withStyle(ChatFormatting.GOLD));
            }
        }
    }

    /** 配件 JSON 的顶层结构：只认 {@code bullet_count} 一个字段，缺省为 null 表示该配件不改弹丸数。 */
    public static class Data {
        @SerializedName("bullet_count")
        @Nullable
        private Modifier bulletCount = null;

        @Nullable
        public Modifier getBulletCount() {
            return bulletCount;
        }
    }
}