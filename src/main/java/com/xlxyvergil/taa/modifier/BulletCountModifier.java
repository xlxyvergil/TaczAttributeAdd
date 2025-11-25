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
import com.xlxyvergil.taa.api.ExtendedGunProperties;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
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
    // 使用字符串常量作为ID，避免架构重复
    public static final String ID = "bullet_count";

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
        return new CacheValue<>(currentBulletCount);
    }

    @Override
    public void eval(List<Modifier> modifiers, CacheValue<Integer> cache) {
        // 使用标准的Modifier计算逻辑
        double eval = AttachmentPropertyManager.eval(modifiers, cache.getValue());
        cache.setValue((int) Math.round(eval));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public List<DiagramsData> getPropertyDiagramsData(ItemStack gunItem, GunData gunData, AttachmentCacheProperty cacheProperty) {
        // 获取原始子弹数量
        int originalBulletCount = gunData.getBulletData().getBulletAmount();
        if (originalBulletCount <= 0) {
            originalBulletCount = 1;
        }
        
        // 获取修改后的子弹数量
        int modifiedBulletCount = cacheProperty.<Integer>getCache(BulletCountModifier.ID);
        
        int bulletCountDifference = modifiedBulletCount - originalBulletCount;
        
        // 计算显示数据
        double bulletCountPercent = Math.min(originalBulletCount / 10.0, 1);
        double bulletCountModifierPercent = Math.min(Math.abs(bulletCountDifference) / 10.0, 1);

        String bulletCountTitleKey = "gui.tacz.gun_refit.property_diagrams.bullet_count";
        String bulletCountPositivelyString = String.format("%d §a(+%d)", modifiedBulletCount, bulletCountDifference);
        String bulletCountNegativelyString = String.format("%d §c(%d)", modifiedBulletCount, bulletCountDifference);
        String bulletCountDefaultString = String.format("%d", modifiedBulletCount);
        boolean bulletCountPositivelyBetter = true; // 子弹数量越多越好

        DiagramsData bulletCountDiagramsData = new DiagramsData(
                bulletCountPercent, bulletCountModifierPercent, bulletCountDifference,
                bulletCountTitleKey, bulletCountPositivelyString, bulletCountNegativelyString,
                bulletCountDefaultString, bulletCountPositivelyBetter);

        return List.of(bulletCountDiagramsData);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public int getDiagramsDataSize() {
        return 1; // 子弹数量一个属性
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