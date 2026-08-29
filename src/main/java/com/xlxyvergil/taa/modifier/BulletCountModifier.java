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

/** 子弹数量 Modifier，用于修改枪械每次射击发射的子弹数量 */
public class BulletCountModifier implements IAttachmentModifier<Modifier, Integer> {
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
        double eval = AttachmentPropertyManager.eval(modifiers, cache.getValue());
        cache.setValue((int) Math.round(eval));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public int getDiagramsDataSize() {
        return 0; // 由 GunPropertyDiagramsMixin 处理显示
    }

    /** 检测是否装了独头弹效果（TACZ 1.1.7+ 起支持） */
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
            
            // 比较版本号
            return isVersionAtLeast(version, "1.1.7");
        } catch (Exception e) {
            // 拿不到版本按旧版处理
            return false;
        }
    }
    
    /** 比较版本号，target 是否 >= base */
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