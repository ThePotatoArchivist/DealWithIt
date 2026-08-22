package archives.tater.dealwithit.component;

import archives.tater.dealwithit.data.Card;
import archives.tater.dealwithit.data.Deck;
import archives.tater.dealwithit.registry.DealWithItComponents;
import archives.tater.dealwithit.registry.DealWithItItems;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;

import java.util.function.Consumer;

public record CardComponent(Holder<Deck> deck, Holder<Card> card) implements TooltipProvider {

    public static final MapCodec<CardComponent> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Deck.CODEC.fieldOf("deck").forGetter(CardComponent::deck),
            Card.CODEC.fieldOf("card").forGetter(CardComponent::card)
    ).apply(instance, CardComponent::new));

    public static final Codec<CardComponent> CODEC = MAP_CODEC.codec();

    public static final StreamCodec<RegistryFriendlyByteBuf, CardComponent> STREAM_CODEC = StreamCodec.composite(
            Deck.STREAM_CODEC, CardComponent::deck,
            Card.STREAM_CODEC, CardComponent::card,
            CardComponent::new
    );

    @Override
    public void addToTooltip(Item.TooltipContext context, Consumer<Component> consumer, TooltipFlag flag, DataComponentGetter components) {
        if (components.get(DealWithItComponents.FACE_DOWN) == null)
            consumer.accept(card.value().description().copy().withColor(TextColor.GRAY));
    }

    public static ItemStack createStack(CardComponent value) {
        var stack = DealWithItItems.CARD.getDefaultInstance();
        stack.set(DealWithItComponents.CARD, value);
        return stack;
    }
}
