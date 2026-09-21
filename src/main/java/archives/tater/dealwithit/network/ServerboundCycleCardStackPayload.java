package archives.tater.dealwithit.network;

import archives.tater.dealwithit.DealWithIt;

import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import io.netty.buffer.ByteBuf;

public record ServerboundCycleCardStackPayload(int slotIndex, int amount) implements CustomPacketPayload {
    public static final Type<ServerboundCycleCardStackPayload> TYPE = new Type<>(DealWithIt.id("cycle_card_stack"));
    public static final StreamCodec<ByteBuf, ServerboundCycleCardStackPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, ServerboundCycleCardStackPayload::slotIndex,
            ByteBufCodecs.VAR_INT, ServerboundCycleCardStackPayload::amount,
            ServerboundCycleCardStackPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
