package com.xlxyvergil.taa.modifier;

import com.google.gson.annotations.SerializedName;
import com.tacz.guns.api.DefaultAssets;
import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.api.item.attachment.AttachmentType;
import com.tacz.guns.api.modifier.CacheValue;
import com.tacz.guns.api.modifier.IAttachmentModifier;
import com.tacz.guns.api.modifier.JsonProperty;
import com.tacz.guns.resource.CommonAssetsManager;
import com.tacz.guns.resource.index.CommonAttachmentIndex;
import com.tacz.guns.resource.modifier.AttachmentCacheProperty;
import com.tacz.guns.resource.modifier.AttachmentPropertyManager;
import com.tacz.guns.resource.pojo.data.attachment.MeleeData;
import com.tacz.guns.resource.pojo.data.attachment.Modifier;
import com.tacz.guns.resource.pojo.data.gun.GunData;
import com.tacz.guns.resource.pojo.data.gun.GunDefaultMeleeData;
import com.tacz.guns.resource.pojo.data.gun.GunMeleeData;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import com.xlxyvergil.taa.api.ExtendedGunProperties;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

/**
 * 近战伤害 Modifier
 * 仅计算配件提供的近战伤害，属性计算由 PropertyCalculator 处理
 */
public class MeleeDamageModifier implements IAttachmentModifier<Modifier, Float> {
    // 使用ExtendedGunProperties中的属性作为ID，与TACZ原版保持一致
    public static final String ID = ExtendedGunProperties.MELEE_DAMAGE.name();

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public JsonProperty<Modifier> readJson(String json) {
        Data data = CommonAssetsManager.GSON.fromJson(json, Data.class);
        return new MeleeDamageJsonProperty(data.getMeleeDamage());
    }

    @Override
    public CacheValue<Float> initCache(ItemStack gunItem, GunData gunData) {
        GunMeleeData meleeData = gunData.getMeleeData();
        if (meleeData == null) {
            return new CacheValue<>(0f);
        }
        
        IGun iGun = IGun.getIGunOrNull(gunItem);
        if (iGun == null) {
            GunDefaultMeleeData defaultData = meleeData.getDefaultMeleeData();
            return new CacheValue<>(defaultData != null ? defaultData.getDamage() : 0f);
        }
        
        // 检查枪口配件
        ResourceLocation muzzleId = iGun.getAttachmentId(gunItem, AttachmentType.MUZZLE);
        MeleeData muzzleData = getMeleeData(muzzleId);
        if (muzzleData != null) {
            return new CacheValue<>(muzzleData.getDamage());
        }
        
        // 检查枪托配件
        ResourceLocation stockId = iGun.getAttachmentId(gunItem, AttachmentType.STOCK);
        MeleeData stockData = getMeleeData(stockId);
        if (stockData != null) {
            return new CacheValue<>(stockData.getDamage());
        }
        
        // 没有配件，使用默认近战伤害
        GunDefaultMeleeData defaultData = meleeData.getDefaultMeleeData();
        return new CacheValue<>(defaultData != null ? defaultData.getDamage() : 0f);
    }
    
    @Nullable
    private MeleeData getMeleeData(ResourceLocation attachmentId) {
        if (DefaultAssets.isEmptyAttachmentId(attachmentId)) {
            return null;
        }
        return TimelessAPI.getCommonAttachmentIndex(attachmentId)
                .map(index -> index.getData().getMeleeData())
                .orElse(null);
    }

    @Override
    public void eval(List<Modifier> modifiers, CacheValue<Float> cache) {
        if (modifiers.isEmpty()) {
            return;
        }
        double eval = AttachmentPropertyManager.eval(modifiers, cache.getValue());
        cache.setValue((float) eval);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public int getDiagramsDataSize() {
        return 0; // 近战伤害显示由GunPropertyDiagramsMixin自行处理
    }

    public static class MeleeDamageJsonProperty extends JsonProperty<Modifier> {
        public MeleeDamageJsonProperty(Modifier value) {
            super(value);
        }

        @Override
        public void initComponents() {
            Modifier value = getValue();
            if (value != null) {
                double eval = AttachmentPropertyManager.eval(value, 0.0f);
                float damage = (float) eval;
                if (damage > 0.0f) {
                    components.add(Component.translatable("tooltip.tacz.attachment.melee.damage.increase").withStyle(ChatFormatting.GREEN));
                } else if (damage < 0.0f) {
                    components.add(Component.translatable("tooltip.tacz.attachment.melee.damage.decrease").withStyle(ChatFormatting.RED));
                }
            }
        }
    }

    public static class Data {
        @SerializedName("melee_damage")
        @Nullable
        private Modifier meleeDamage = null;

        @Nullable
        public Modifier getMeleeDamage() {
            return meleeDamage;
        }
    }
}
