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
                
                var mainHandItem = player.getMainHandItem();
                var offHandItem = player.getOffhandItem();
                
                if (mainHandItem.getItem() instanceof com.tacz.guns.api.item.IGun) {
                    AttachmentPropertyManager.postChangeEvent(player, mainHandItem);
                    return;
                }
                
                if (offHandItem.getItem() instanceof com.tacz.guns.api.item.IGun) {
                    AttachmentPropertyManager.postChangeEvent(player, offHandItem);
                    return;
                }
                
                // 玩家未持枪时也更新一次，确保属性正确应用
                AttachmentPropertyManager.postChangeEvent(player, net.minecraft.world.item.ItemStack.EMPTY);
            });
        }
        context.setPacketHandled(true);
    }
}