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
 * 子弹数量 Modifier
 * 用于修改枪械每次射击发射的子弹数量
 * 完全遵循TACZ配件系统的标准模式
 */
public class BulletCountModifier implements IAttachmentModifier<Modifier, Integer> {
    // 使用ExtendedGunProperties中的属性作为ID，与TACZ原版保持一致
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
        // 获取当前的子弹数量，默认为1
        int currentBulletCount = gunData.getBulletData().getBulletAmount();
        if (currentBulletCount <= 0) {
            currentBulletCount = 1;
        }
        // 如果安装了独头弹配件，弹头数量强制为1
        if (hasSlugEffect(gunItem)) {
            currentBulletCount = 1;
        }
        return new CacheValue<>(currentBulletCount);
    }

    @Override
    public void eval(List<Modifier> modifiers, CacheValue<Integer> cache) {
        // 使用标准的Modifier计算逻辑
        double eval = AttachmentPropertyManager.eval(modifiers, cache.getValue());
        // 如果计算结果有小数部分，则向上取整
        if (eval > Math.floor(eval)) {
            cache.setValue((int) Math.ceil(eval));
        } else {
            cache.setValue((int) eval);
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public int getDiagramsDataSize() {
        return 0; // 子弹数量显示由GunPropertyDiagramsMixin自行处理
    }

    /**
     * 检测是否安装了独头弹效果
     * 使用TACZ的标签检测机制，兼容所有使用intrinsic/slug标签的配件
     * 仅在TACZ 1.1.7+版本中启用
     */
    private boolean hasSlugEffect(ItemStack gunItem) {
        // 检查TACZ版本，1.1.7以下不启用独头弹检测
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
            // 如果TACZ版本不兼容或其他异常，返回false
            return false;
        }
    }

    /**
     * 检查TACZ版本是否为1.1.7或更高
     */
    private static boolean isTacz117OrAbove() {
        try {
            String version = net.minecraftforge.fml.ModList.get()
                .getModContainerById("tacz")
                .map(container -> container.getModInfo().getVersion().toString())
                .orElse("0.0.0");
            
            // 解析版本号，检查是否 >= 1.1.7
            return isVersionAtLeast(version, "1.1.7");
        } catch (Exception e) {
            // 如果无法获取版本，假设是旧版本
            return false;
        }
    }
    
    /**
     * 比较版本号，检查target是否 >= base
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
            return true; // 版本相等
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