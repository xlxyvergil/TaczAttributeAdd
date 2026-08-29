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
import com.xlxyvergil.taa.api.ExtendedGunProperties;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.List;

/** 弹匣容量 Modifier，用于修改枪械的弹匣容量 */
public class AmmoCountModifier implements IAttachmentModifier<Modifier, Integer> {
    public static final String ID = ExtendedGunProperties.MAGAZINE_CAPACITY.name();

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
        // GunsmithLib 的弹匣容量加成由 AmmoCapacityHelper.computeFinalAmmoCapacity 统一应用，
        // 这里只算基础容量，避免客户端/服务端反射结果不一致。
        int currentAmmoCount = getCurrentMagazineCapacity(gunItem, gunData);
        return new CacheValue<>(currentAmmoCount);
    }

    @Override
    public void eval(@Nullable List<Modifier> modifiers, CacheValue<Integer> cache) {
        if (modifiers != null && !modifiers.isEmpty()) {
            double eval = AttachmentPropertyManager.eval(modifiers, cache.getValue());
            // 直接截断，不四舍五入
            int result = (int) eval;
            // 结果不小于 1
            if (result < 1) {
                result = 1;
            }
            cache.setValue(result);
        }
    }



    /** 获取当前弹匣容量，考虑是否安装了扩容弹匣 */
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
        return gunData.getAmmoAmount();
    }

    public static class AmmoCountJsonProperty extends JsonProperty<Modifier> {
        public AmmoCountJsonProperty(Modifier value) {
            super(value);
        }

        @Override
        public void initComponents() {
            // 原弹匣容量显示已包含修改，这里不再重复展示
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