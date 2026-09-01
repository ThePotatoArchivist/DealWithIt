package archives.tater.dealwithit.registry;

import archives.tater.dealwithit.DealWithIt;
import archives.tater.dealwithit.component.CardInstance;
import archives.tater.dealwithit.component.CardStack;
import archives.tater.dealwithit.component.DeckContents;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.function.Consumer;

public interface DealWithItComponents {

    DataComponentType<CardInstance> CARD = createCached("card", CardInstance.CODEC, CardInstance.STREAM_CODEC);
    DataComponentType<CardStack> CARD_STACK = createCached("card_stack", CardStack.CODEC, CardStack.STREAM_CODEC);
    DataComponentType<DeckContents> DECK_CONTENTS = createCached("deck_contents", DeckContents.CODEC, DeckContents.STREAM_CODEC);

    private static <T> DataComponentType<T> create(String path, DataComponentType.Builder<T> builder) {
        return Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, DealWithIt.id(path), builder.build());
    }

    private static <T> DataComponentType<T> create(String path, Consumer<DataComponentType.Builder<T>> init) {
        var builder = DataComponentType.<T>builder();
        init.accept(builder);
        return create(path, builder);
    }

    private static <T> DataComponentType<T> createUncached(String path, Codec<T> codec, StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec) {
        return create(path, DataComponentType.<T>builder()
                .persistent(codec)
                .networkSynchronized(streamCodec)
        );
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
