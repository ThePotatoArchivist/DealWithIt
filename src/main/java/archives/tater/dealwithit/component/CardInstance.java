package archives.tater.dealwithit.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record CardInstance(CardComponent card, boolean faceDown) {
    public static final Codec<CardInstance> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            CardComponent.MAP_CODEC.forGetter(CardInstance::card),
            Codec.BOOL.fieldOf("face_down").forGetter(CardInstance::faceDown)
    ).apply(instance, CardInstance::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, CardInstance> STREAM_CODEC = StreamCodec.composite(
            CardComponent.STREAM_CODEC, CardInstance::card,
            ByteBufCodecs.BOOL, CardInstance::faceDown,
            CardInstance::new
    );

    public CardInstance flipped() {
        return new CardInstance(card, !faceDown);
    }
}
