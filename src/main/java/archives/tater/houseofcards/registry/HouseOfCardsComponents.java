package archives.tater.houseofcards.registry;

import archives.tater.houseofcards.HouseOfCards;
import archives.tater.houseofcards.component.CardComponent;
import archives.tater.houseofcards.component.DeckContents;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.function.Consumer;

public interface HouseOfCardsComponents {

    DataComponentType<CardComponent> CARD = createCached("card", CardComponent.CODEC, CardComponent.STREAM_CODEC);
    DataComponentType<DeckContents> DECK_CONTENTS = createCached("deck_contents", DeckContents.CODEC, DeckContents.STREAM_CODEC);

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

    static void init() {

    }
}
