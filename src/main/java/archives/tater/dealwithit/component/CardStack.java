package archives.tater.dealwithit.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;

public record CardStack(List<Entry> cards) {
    public static final Codec<CardStack> CODEC = Entry.CODEC.listOf().xmap(CardStack::new, CardStack::cards);
    public static final StreamCodec<RegistryFriendlyByteBuf, CardStack> STREAM_CODEC = Entry.STREAM_CODEC.apply(ByteBufCodecs.list()).map(CardStack::new, CardStack::cards);
    public static final CardStack EMPTY = new CardStack(List.of());

    public record Entry(CardComponent card, boolean faceDown) {
        public static final Codec<Entry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                CardComponent.MAP_CODEC.forGetter(Entry::card),
                Codec.BOOL.fieldOf("face_down").forGetter(Entry::faceDown)
        ).apply(instance, Entry::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, Entry> STREAM_CODEC = StreamCodec.composite(
                CardComponent.STREAM_CODEC, Entry::card,
                ByteBufCodecs.BOOL, Entry::faceDown,
                Entry::new
        );
    }
}
