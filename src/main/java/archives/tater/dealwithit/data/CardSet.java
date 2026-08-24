package archives.tater.dealwithit.data;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.Comparator.comparing;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;
import static net.minecraft.network.codec.ByteBufCodecs.list;

public sealed interface CardSet {
    
    Codec<CardSet> CODEC = Codec.either(Single.CODEC, CardSet.Multi.CODEC).xmap(Either::unwrap, CardSet::wrapEither);
    
    StreamCodec<RegistryFriendlyByteBuf, CardSet> STREAM_CODEC = ByteBufCodecs.either(Single.STREAM_CODEC, CardSet.Multi.STREAM_CODEC).map(Either::unwrap, CardSet::wrapEither);

    CardSet EMPTY = new Single(Set.of());

    int count();

    int count(Holder<Card> card);

    Stream<Holder<Card>> stream();

    static Mutable mutable(CardSet set, CardSet limits) {
        return switch (limits) {
            case Single single -> new Mutable.Single(set, single);
            case Multi multi -> new Mutable.Multi(set, multi);
        };
    }

    static Mutable mutable() {
        return new Mutable.Multi();
    }

    private static Either<Single, Multi> wrapEither(CardSet set) {
        return switch (set) {
            case Single single -> Either.left(single);
            case Multi multi -> Either.right(multi);
        };
    }

    record Single(@Unmodifiable Set<Holder<Card>> cards) implements CardSet {

        private Single(List<Holder<Card>> cards) {
            this(Set.copyOf(cards));
        }

        private List<Holder<Card>> toList() {
            return List.copyOf(cards);
        }

        private List<Holder<Card>> toSortedList() {
            return cards.stream().sorted(comparing(card -> card.unwrapKey().orElseThrow().identifier())).toList();
        }

        public static final Codec<Single> CODEC = Card.CODEC.listOf().xmap(Single::new, Single::toSortedList); // So it's consistent in datagen
        public static final StreamCodec<RegistryFriendlyByteBuf, Single> STREAM_CODEC = Card.STREAM_CODEC.apply(list()).map(Single::new, Single::toList);

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

    record Multi(@Unmodifiable Object2IntMap<Holder<Card>> cards) implements CardSet {

        public static final Codec<Multi> CODEC = Codec.unboundedMap(Card.CODEC, ExtraCodecs.NON_NEGATIVE_INT).xmap(map -> new Multi(new Object2IntOpenHashMap<>(map)), multi -> multi.cards);

        private static final StreamCodec<RegistryFriendlyByteBuf, Object2IntMap<Holder<Card>>> CARD_MAP_CODEC = ByteBufCodecs.map(Object2IntOpenHashMap::new, Card.STREAM_CODEC, ByteBufCodecs.INT);
        public static final StreamCodec<RegistryFriendlyByteBuf, Multi> STREAM_CODEC = CARD_MAP_CODEC.map(CardSet.Multi::new, CardSet.Multi::cards);

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

    interface Mutable {

        boolean canAdd(Holder<Card> card);

        void add(Holder<Card> card);

        boolean canAdd(Holder<Card> card, int amount);

        void add(Holder<Card> card, int amount);

        CardSet build();

        class Single implements Mutable {
            private final Set<Holder<Card>> cards;
            private final CardSet.Single limits;

            public Single(CardSet cards, CardSet.Single limits) {
                this.cards = cards.stream().collect(toSet());
                this.limits = limits;
            }

            @Override
            public boolean canAdd(Holder<Card> card) {
                return limits.cards.contains(card) && !cards.contains(card);
            }

            @Override
            public void add(Holder<Card> card) {
                assert canAdd(card);
                cards.add(card);
            }

            @Override
            public boolean canAdd(Holder<Card> card, int amount) {
                return canAdd(card) && amount == 1;
            }

            @Override
            public void add(Holder<Card> card, int amount) {
                assert canAdd(card, amount);
                add(card);
            }

            @Override
            public CardSet build() {
                return cards.size() == limits.count() ? limits : new CardSet.Single(Set.copyOf(cards));
            }
        }

        class Multi implements Mutable {
            private final Object2IntOpenHashMap<Holder<Card>> cards;
            private final CardSet.@Nullable Multi limits;

            private Multi(Object2IntMap<Holder<Card>> cards, CardSet.@Nullable Multi limits) {
                this.cards = new Object2IntOpenHashMap<>(cards);
                this.limits = limits;
            }

            public Multi() {
                this(new Object2IntOpenHashMap<>(), null);
            }

            private Multi(CardSet cards, CardSet.Multi limits) {
                this(switch (cards) {
                    case CardSet.Single single -> new Object2IntOpenHashMap<>(single.cards.stream().collect(toMap(identity(), _ -> 1)));
                    case CardSet.Multi multi -> multi.cards;
                }, limits);
            }

            @Override
            public boolean canAdd(Holder<Card> card) {
                return limits == null || cards.getInt(card) < limits.count(card);
            }

            @Override
            public void add(Holder<Card> card) {
                assert canAdd(card);
                cards.addTo(card, 1);
            }

            @Override
            public boolean canAdd(Holder<Card> card, int amount) {
                return limits == null || cards.getInt(card) + amount <= limits.count(card);
            }

            @Override
            public void add(Holder<Card> card, int amount) {
                assert canAdd(card, amount);
                cards.addTo(card, amount);
            }

            @Override
            public CardSet build() {
                if (limits != null && cards.values().intStream().sum() == limits.count()) return limits;

                if (cards.values().intStream().allMatch(amount -> amount == 1))
                    return new CardSet.Single(Set.copyOf(cards.keySet()));

                return new CardSet.Multi(Object2IntMaps.unmodifiable(cards));
            }
        }
    }
}
