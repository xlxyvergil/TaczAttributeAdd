package com.xlxyvergil.taa.network.message;

import com.tacz.guns.resource.modifier.AttachmentPropertyManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ServerMessageUpdateTacZCache {
    public ServerMessageUpdateTacZCache() {
    }

    public static void encode(ServerMessageUpdateTacZCache message, FriendlyByteBuf buf) {
    }

    public static ServerMessageUpdateTacZCache decode(FriendlyByteBuf buf) {
        return new ServerMessageUpdateTacZCache();
    }

    public static void handle(ServerMessageUpdateTacZCache message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        if (context.getDirection().getReceptionSide().isServer()) {
            context.enqueueWork(() -> {
                ServerPlayer player = context.getSender();
                if (player == null) {
                    return;
                }
                
                // 获取玩家主手物品
                var mainHandItem = player.getMainHandItem();
                var offHandItem = player.getOffhandItem();
                
                // 检查主手是否是枪械
                if (mainHandItem.getItem() instanceof com.tacz.guns.api.item.IGun) {
                    AttachmentPropertyManager.postChangeEvent(player, mainHandItem);
                    return;
                }
                
                // 检查副手是否是枪械
                if (offHandItem.getItem() instanceof com.tacz.guns.api.item.IGun) {
                    AttachmentPropertyManager.postChangeEvent(player, offHandItem);
                    return;
                }
                
                // 如果玩家没有持枪，也触发一次更新以确保属性正确应用
                AttachmentPropertyManager.postChangeEvent(player, net.minecraft.world.item.ItemStack.EMPTY);
            });
        }
        context.setPacketHandled(true);
    }
}