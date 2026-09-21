package archives.tater.dealwithit.data;

import archives.tater.dealwithit.registry.DealWithItRegistries;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFixedCodec;

public record Deck(
        Component description,
        Holder<DeckType> type
) {
    public static final Codec<Deck> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ComponentSerialization.CODEC.fieldOf("description").forGetter(Deck::description),
            DeckType.CODEC.fieldOf("type").forGetter(Deck::type)
    ).apply(instance, Deck::new));

    public static final Codec<Holder<Deck>> CODEC = RegistryFixedCodec.create(DealWithItRegistries.DECK);
    public static final StreamCodec<RegistryFriendlyByteBuf, Holder<Deck>> STREAM_CODEC = ByteBufCodecs.holderRegistry(DealWithItRegistries.DECK);

    public CardSet cards() {
        return type.value().cards();
    }

    public int size() {
        return type.value().size();
    }
}
