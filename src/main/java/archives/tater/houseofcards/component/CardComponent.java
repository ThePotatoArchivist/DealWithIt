package archives.tater.houseofcards.component;

import archives.tater.houseofcards.data.Card;

import com.mojang.serialization.Codec;
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

public record CardComponent(Holder<Card> card) implements TooltipProvider {
    public static final Codec<CardComponent> CODEC = Card.CODEC.xmap(CardComponent::new, CardComponent::card);
    public static final StreamCodec<RegistryFriendlyByteBuf, CardComponent> STREAM_CODEC = Card.STREAM_CODEC.map(CardComponent::new, CardComponent::card);

    @Override
    public void addToTooltip(Item.TooltipContext context, Consumer<Component> consumer, TooltipFlag flag, DataComponentGetter components) {
        consumer.accept(card.value().description().copy().withColor(TextColor.GRAY));
    }
}
