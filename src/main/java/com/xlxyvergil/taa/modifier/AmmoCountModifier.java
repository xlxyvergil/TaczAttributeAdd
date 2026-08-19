package com.xlxyvergil.taa.modifier;

import com.google.gson.annotations.SerializedName;
import com.tacz.guns.api.item.IGun;
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

/**
 * 弹匣容量 Modifier
 * 用于修改枪械的弹匣容量，包括基础弹匣容量和扩展弹匣容量
 * 完全遵循TACZ配件系统的标准模式
 */
public class AmmoCountModifier implements IAttachmentModifier<Modifier, Integer> {
    // 使用ExtendedGunProperties中的属性作为ID，与TACZ原版保持一致
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
        // 获取当前的弹匣容量，考虑是否安装了扩容弹匣
        // 注意：GunsmithLib 的弹匣容量加成不在 initCache 中计算，
        // 而是在 AmmoCapacityHelper.computeFinalAmmoCapacity 中统一应用，
        // 避免客户端/服务端反射结果不一致导致缓存值不同。
        int currentAmmoCount = getCurrentMagazineCapacity(gunItem, gunData);
        return new CacheValue<>(currentAmmoCount);
    }

    @Override
    public void eval(@Nullable List<Modifier> modifiers, CacheValue<Integer> cache) {
        // 只有当存在修饰符时才进行计算
        if (modifiers != null && !modifiers.isEmpty()) {
            // 使用标准的Modifier计算逻辑
            double eval = AttachmentPropertyManager.eval(modifiers, cache.getValue());
            // 修改为直接截断，不使用四舍五入
            int result = (int) eval;
            // 如果结果小于1，则设置为1
            if (result < 1) {
                result = 1;
            }
            cache.setValue(result);
        }
        // 如果没有修饰符，则保持原始值不变
    }



    /**
     * 获取当前弹匣容量，考虑是否安装了扩容弹匣
     * @param gunItem 枪械物品
     * @param gunData 枪械数据
     * @return 当前弹匣容量
     */
    private int getCurrentMagazineCapacity(ItemStack gunItem, GunData gunData) {
        // 检查是否安装了扩容弹匣（通过 TACZ 原版工具方法，无需 Provider）
        IGun iGun = IGun.getIGunOrNull(gunItem);
        if (iGun != null) {
            int magLevel = AttachmentDataUtils.getMagExtendLevel(gunItem, gunData);
            if (magLevel > 0 && magLevel <= 3) {
                int[] extendedMagAmmoAmount = gunData.getExtendedMagAmmoAmount();
                if (extendedMagAmmoAmount != null && extendedMagAmmoAmount.length >= magLevel) {
                    return extendedMagAmmoAmount[magLevel - 1];
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