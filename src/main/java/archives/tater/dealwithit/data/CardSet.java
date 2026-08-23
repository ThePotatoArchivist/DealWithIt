package archives.tater.dealwithit.data;

import archives.tater.dealwithit.registry.DealWithItRegistries;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import org.jspecify.annotations.Nullable;

import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public sealed interface CardSet {
    
    Codec<CardSet> CODEC = Codec.either(Single.CODEC, Multi.CODEC).xmap(Either::unwrap, CardSet::wrapEither);
    
    StreamCodec<RegistryFriendlyByteBuf, CardSet> STREAM_CODEC = ByteBufCodecs.either(Single.STREAM_CODEC, Multi.STREAM_CODEC).map(Either::unwrap, CardSet::wrapEither);

    CardSet EMPTY = new Single(HolderSet.empty());

    int count();

    int count(Holder<Card> card);

    Stream<Holder<Card>> stream();

    private static Either<Single, Multi> wrapEither(CardSet set) {
        return switch (set) {
            case Single single -> Either.left(single);
            case Multi multi -> Either.right(multi);
        };
    }

    record Single(HolderSet<Card> cards) implements CardSet {

        public static final Codec<Single> CODEC = RegistryCodecs.homogeneousList(DealWithItRegistries.CARD, true).xmap(Single::new, Single::cards);
        public static final StreamCodec<RegistryFriendlyByteBuf, Single> STREAM_CODEC = ByteBufCodecs.holderSet(DealWithItRegistries.CARD).map(Single::new, Single::cards);

        @Override
        public int count() {
            return cards.size();
        }

        @Override
        public int count(Holder<Card> card) {
            return cards.contains(card) ? 1 : 0;
        }

        @Override
        public Stream<Holder<Card>> stream() {
            return cards.stream();
        }
    }

    record Multi(Object2IntMap<Holder<Card>> cards) implements CardSet {

        public static final Codec<Multi> CODEC = Codec.unboundedMap(Card.CODEC, ExtraCodecs.NON_NEGATIVE_INT).xmap(map -> new Multi(new Object2IntOpenHashMap<>(map)), multi -> multi.cards);

        private static final StreamCodec<RegistryFriendlyByteBuf, Object2IntMap<Holder<Card>>> CARD_MAP_CODEC = ByteBufCodecs.map(Object2IntOpenHashMap::new, Card.STREAM_CODEC, ByteBufCodecs.INT);
        public static final StreamCodec<RegistryFriendlyByteBuf, Multi> STREAM_CODEC = CARD_MAP_CODEC.map(Multi::new, Multi::cards);

        @Override
        public int count() {
            return cards.values().intStream().sum();
        }

        @Override
        public int count(Holder<Card> card) {
            return cards.getInt(card);
        }

        @Override
        public Stream<Holder<Card>> stream() {
            return cards.object2IntEntrySet().stream().mapMulti((entry, yield) -> {
                for (int i = 0; i < entry.getIntValue(); i++)
                    yield.accept(entry.getKey());
            });
        }
    }

    class Mutable {
        private final Object2IntOpenHashMap<Holder<Card>> cards;
        private final @Nullable CardSet limits;

        private Mutable(Object2IntMap<Holder<Card>> cards, @Nullable CardSet limits) {
            this.cards = new Object2IntOpenHashMap<>(cards);
            this.limits = limits;
        }

        public Mutable() {
            this(new Object2IntOpenHashMap<>(), null);
        }

        public Mutable(CardSet set, CardSet limits) {
            this(switch (set) {
                case Single single -> new Object2IntOpenHashMap<>(single.cards.stream().collect(Collectors.toMap(Function.identity(), _ -> 1)));
                case Multi multi -> multi.cards;
            }, limits);
        }

        public boolean canAdd(Holder<Card> card) {
            return limits == null || cards.getInt(card) < limits.count(card);
        }

        public void add(Holder<Card> card) {
            cards.addTo(card, 1);
        }

        public boolean canAdd(Holder<Card> card, int amount) {
            return limits == null || cards.getInt(card) + amount <= limits.count(card);
        }

        public void add(Holder<Card> card, int amount) {
            cards.addTo(card, amount);
        }

        public CardSet build() {
            if (limits != null && cards.values().intStream().sum() == limits.count()) return limits;

            if (limits instanceof Single || cards.values().intStream().allMatch(amount -> amount == 1))
                return new Single(HolderSet.direct(cards.keySet().stream().toList()));
            return new Multi(Object2IntMaps.unmodifiable(cards));
        };
    }
}
