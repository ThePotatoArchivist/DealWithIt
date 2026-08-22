package archives.tater.houseofcards.component;

import archives.tater.houseofcards.data.Card;
import archives.tater.houseofcards.data.Deck;
import archives.tater.houseofcards.registry.HouseOfCardsComponents;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;

import java.util.function.Consumer;

public record CardComponent(Holder<Deck> deck, Holder<Card> card) implements TooltipProvider {

    public static final Codec<CardComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Deck.CODEC.fieldOf("deck").forGetter(CardComponent::deck),
            Card.CODEC.fieldOf("card").forGetter(CardComponent::card)
    ).apply(instance, CardComponent::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, CardComponent> STREAM_CODEC = StreamCodec.composite(
            Deck.STREAM_CODEC, CardComponent::deck,
            Card.STREAM_CODEC, CardComponent::card,
            CardComponent::new
    );

    @Override
    public void addToTooltip(Item.TooltipContext context, Consumer<Component> consumer, TooltipFlag flag, DataComponentGetter components) {
        if (components.get(HouseOfCardsComponents.FACE_DOWN) == null)
            consumer.accept(card.value().description().copy().withColor(TextColor.GRAY));
    }
}
