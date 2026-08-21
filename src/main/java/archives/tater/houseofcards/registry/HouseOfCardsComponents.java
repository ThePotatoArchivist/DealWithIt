package archives.tater.houseofcards.registry;

import archives.tater.houseofcards.Card;
import archives.tater.houseofcards.DeckContents;
import archives.tater.houseofcards.HouseOfCards;

import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.function.Consumer;

public interface HouseOfCardsComponents {
    private static <T> DataComponentType<T> create(String path, DataComponentType.Builder<T> builder) {
        return Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, HouseOfCards.id(path), builder.build());
    }

    private static <T> DataComponentType<T> create(String path, Consumer<DataComponentType.Builder<T>> init) {
        var builder = DataComponentType.<T>builder();
        init.accept(builder);
        return create(path, builder);
    }

    private static <T> DataComponentType<T> createCached(String path, Codec<T> codec, StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec) {
        return create(path, DataComponentType.<T>builder()
                .persistent(codec)
                .networkSynchronized(streamCodec)
                .cacheEncoding()
        );
    }

    DataComponentType<Holder<Card>> CARD = createCached("card", Card.CODEC, Card.STREAM_CODEC);
    DataComponentType<DeckContents> DECK_CONTENTS = createCached("deck_contents", DeckContents.CODEC, DeckContents.STREAM_CODEC);

    static void init() {

    }
}
