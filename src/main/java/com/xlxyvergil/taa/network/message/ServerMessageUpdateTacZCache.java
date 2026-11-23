package com.xlxyvergil.taa.network.message;

import com.tacz.guns.resource.modifier.AttachmentPropertyManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * 空载荷的服务端缓存刷新包 - 客户端 -> 服务端
 * <p>
 * 客户端在玩家手上枪械的属性值变化后，先本地重算 TACZ 缓存，再发这个包让服务端
 * 对自己的那把枪也跑一次 {@code AttachmentPropertyManager#postChangeEvent}，
 * 保证两端枪械缓存一致（否则服务端按旧缓存结算伤害，客户端面板却已是新值）。
 * <p>
 * 无字段：encode 不写任何字节、decode 直接 new 一个实例，收到包本身就是要重算的信号。
 */
public class ServerMessageUpdateTacZCache {
    public ServerMessageUpdateTacZCache() {
    }

    /**
     * 无字段可写，只保留方法签名以符合 SimpleChannel 的注册方式
     */
    public static void encode(ServerMessageUpdateTacZCache message, FriendlyByteBuf buf) {
    }

    /**
     * 无字段可读，直接造一个空实例；必须与 encode 对称，不能返回 null
     */
    public static ServerMessageUpdateTacZCache decode(FriendlyByteBuf buf) {
        return new ServerMessageUpdateTacZCache();
    }

    /**
     * 收到请求后按主手、副手依次找枪重算 TACZ 缓存
     * 只在服务端处理；属性相关的计算必须回主线程，所以实际逻辑都在 enqueueWork 里。
     */
    public static void handle(ServerMessageUpdateTacZCache message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        // 方向判断放在 enqueueWork 外面：非服务端方向的包没必要排进主线程队列
        if (context.getDirection().getReceptionSide().isServer()) {
            context.enqueueWork(() -> {
                ServerPlayer player = context.getSender();
                if (player == null) {
                    return;
                }
                
                // 先取双持物品再逐个判类型：主手是枪就用主手，否则再看副手
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
        // 无论包是否真的处理过都要标记已处理，否则 SimpleChannel 会提示该包未被处理
        context.setPacketHandled(true);
    }
}