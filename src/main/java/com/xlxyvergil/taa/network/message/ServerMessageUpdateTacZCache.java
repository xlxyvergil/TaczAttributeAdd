package com.xlxyvergil.taa.network.message;

import com.tacz.guns.api.item.IGun;
import com.tacz.guns.resource.modifier.AttachmentPropertyManager;
import com.xlxyvergil.taa.TaczAttributeAdd;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public class ServerMessageUpdateTacZCache implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ServerMessageUpdateTacZCache> TYPE = new CustomPacketPayload.Type<>(
        ResourceLocation.fromNamespaceAndPath(TaczAttributeAdd.MODID, "update_tacz_cache")
    );

    // 无字段包：StreamCodec.unit 编码校验要求每次发送必须是同一个实例，故使用单例
    public static final ServerMessageUpdateTacZCache INSTANCE = new ServerMessageUpdateTacZCache();

    public static final StreamCodec<RegistryFriendlyByteBuf, ServerMessageUpdateTacZCache> STREAM_CODEC =
        StreamCodec.unit(INSTANCE);

    public ServerMessageUpdateTacZCache() {
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(ServerMessageUpdateTacZCache message, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!context.flow().isServerbound()) {
                return;
            }
            if (!(context.player() instanceof ServerPlayer player)) {
                return;
            }

            // 获取玩家主手物品
            ItemStack mainHandItem = player.getMainHandItem();
            ItemStack offHandItem = player.getOffhandItem();

            // 检查主手是否是枪械
            if (mainHandItem.getItem() instanceof IGun) {
                AttachmentPropertyManager.postChangeEvent(player, mainHandItem);
                return;
            }

            // 检查副手是否是枪械
            if (offHandItem.getItem() instanceof IGun) {
                AttachmentPropertyManager.postChangeEvent(player, offHandItem);
                return;
            }

            // 如果玩家没有持枪，也触发一次更新以确保属性正确应用
            AttachmentPropertyManager.postChangeEvent(player, ItemStack.EMPTY);
        });
    }
}
