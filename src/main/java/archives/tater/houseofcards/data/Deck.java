package archives.tater.houseofcards.data;

import archives.tater.houseofcards.registry.HouseOfCardsRegistries;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.util.ExtraCodecs;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

import java.util.function.Function;

import static java.util.stream.Collectors.toMap;

public record Deck(
        Component description,
        Object2IntMap<Holder<Card>> cards
) {
    public static final Codec<Object2IntMap<Holder<Card>>> LONG_CARDS_CODEC = Codec.unboundedMap(Card.CODEC, ExtraCodecs.POSITIVE_INT).xmap(Object2IntOpenHashMap::new, Function.identity());
    public static final Codec<Object2IntMap<Holder<Card>>> SHORT_CARDS_CODEC = Card.CODEC.listOf().xmap(cards -> new Object2IntOpenHashMap<>(cards.stream().collect(toMap(Function.identity(), _ -> 1))), map -> map.keySet().stream().toList());
    public static final Codec<Object2IntMap<Holder<Card>>> CARDS_CODEC = Codec.either(LONG_CARDS_CODEC, SHORT_CARDS_CODEC).xmap(
            either -> either.map(Function.identity(), Function.identity()),
            map -> map.values().intStream().allMatch(count -> count == 1) ? Either.right(map) : Either.left(map)
    );

    public static final Codec<Deck> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ComponentSerialization.CODEC.fieldOf("description").forGetter(Deck::description),
            CARDS_CODEC.fieldOf("cards").forGetter(Deck::cards)
    ).apply(instance, Deck::new));

    public static final Codec<Holder<Deck>> CODEC = RegistryFixedCodec.create(HouseOfCardsRegistries.DECK);
    public static final StreamCodec<RegistryFriendlyByteBuf, Holder<Deck>> STREAM_CODEC = ByteBufCodecs.holderRegistry(HouseOfCardsRegistries.DECK);

    public int size() {
        return cards.values().intStream().sum();
    }
}
