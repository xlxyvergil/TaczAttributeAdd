package com.xlxyvergil.taa.modifier;

import com.google.gson.annotations.SerializedName;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.api.item.attachment.AttachmentType;
import com.tacz.guns.api.modifier.CacheValue;
import com.tacz.guns.api.modifier.IAttachmentModifier;
import com.tacz.guns.api.modifier.JsonProperty;
import com.tacz.guns.resource.CommonAssetsManager;
import com.tacz.guns.resource.modifier.AttachmentPropertyManager;
import com.tacz.guns.resource.pojo.data.attachment.Modifier;
import com.tacz.guns.resource.pojo.data.gun.GunData;
import com.tacz.guns.util.AttachmentDataUtils;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.List;

/**
 * 弹匣容量 Modifier
 * 用于修改枪械的弹匣容量，包括基础弹匣容量和扩展弹匣容量
 * 完全遵循TACZ配件系统的标准模式
 */
public class AmmoCountModifier implements IAttachmentModifier<Modifier, Integer> {
    // 使用字符串常量作为ID，避免架构重复
    public static final String ID = "magazine_capacity";

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public JsonProperty<Modifier> readJson(String json) {
        AmmoCountModifier.Data data = CommonAssetsManager.GSON.fromJson(json, AmmoCountModifier.Data.class);
        return new AmmoCountModifier.AmmoCountJsonProperty(data.getMagazineCapacity());
    }

    @Override
    public CacheValue<Integer> initCache(ItemStack gunItem, GunData gunData) {
        // 获取当前的弹匣容量，考虑是否安装了扩容弹匣
        int currentAmmoCount = getCurrentMagazineCapacity(gunItem, gunData);
        return new CacheValue<>(currentAmmoCount);
    }

    @Override
    public void eval(@Nullable List<Modifier> modifiers, CacheValue<Integer> cache) {
        // 使用标准的Modifier计算逻辑
        double eval = AttachmentPropertyManager.eval(modifiers, cache.getValue());
        cache.setValue((int) Math.round(eval));
    }



    /**
     * 获取当前弹匣容量，考虑是否安装了扩容弹匣
     * @param gunItem 枪械物品
     * @param gunData 枪械数据
     * @return 当前弹匣容量
     */
    private int getCurrentMagazineCapacity(ItemStack gunItem, GunData gunData) {
        // 检查是否安装了扩容弹匣
        IGun iGun = IGun.getIGunOrNull(gunItem);
        if (iGun != null) {
            ItemStack attachment = iGun.getAttachment(gunItem, AttachmentType.EXTENDED_MAG);
            if (!attachment.isEmpty()) {
                // 获取扩容弹匣的等级
                int magLevel = AttachmentDataUtils.getMagExtendLevel(gunItem, gunData);
                if (magLevel > 0 && magLevel <= 3) {
                    int[] extendedMagAmmoAmount = gunData.getExtendedMagAmmoAmount();
                    if (extendedMagAmmoAmount != null && extendedMagAmmoAmount.length >= magLevel) {
                        return extendedMagAmmoAmount[magLevel - 1];
                    }
                }
            }
        }
        // 返回基础弹匣容量
        return gunData.getAmmoAmount();
    }

    public static class AmmoCountJsonProperty extends JsonProperty<Modifier> {
        public AmmoCountJsonProperty(Modifier value) {
            super(value);
        }

        @Override
        public void initComponents() {
            // 弹匣容量modifier不显示tooltip，因为原弹匣容量已能正确显示修改后的数据
        }
    }

    public static class Data {
        @SerializedName("magazine_capacity")
        @Nullable
        private Modifier magazineCapacity = null;

        @Nullable
        public Modifier getMagazineCapacity() {
            return magazineCapacity;
        }
    }
}