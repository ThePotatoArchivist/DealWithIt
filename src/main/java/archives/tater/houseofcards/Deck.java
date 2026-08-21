package archives.tater.houseofcards;

import archives.tater.houseofcards.registry.HouseOfCardsRegistries;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFixedCodec;

public record Deck(
        Component description,
        HolderSet<Card> cards
) {
    public static final Codec<Deck> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ComponentSerialization.CODEC.fieldOf("description").forGetter(Deck::description),
            Card.SET_CODEC.fieldOf("cards").forGetter(Deck::cards)
    ).apply(instance, Deck::new));

    public static final Codec<Holder<Deck>> CODEC = RegistryFixedCodec.create(HouseOfCardsRegistries.DECK);
    public static final StreamCodec<RegistryFriendlyByteBuf, Holder<Deck>> STREAM_CODEC = ByteBufCodecs.holderRegistry(HouseOfCardsRegistries.DECK);
}
