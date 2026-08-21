package archives.tater.houseofcards;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;

import java.util.function.Consumer;

public record DeckContents(
        Holder<Deck> deck,
        HolderSet<Card> cards
) implements TooltipProvider {
    public static final Codec<DeckContents> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Deck.CODEC.fieldOf("deck").forGetter(DeckContents::deck),
            Card.SET_CODEC.fieldOf("cards").forGetter(DeckContents::cards)
    ).apply(instance, DeckContents::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, DeckContents> STREAM_CODEC = StreamCodec.composite(
            Deck.STREAM_CODEC, DeckContents::deck,
            Card.SET_STREAM_CODEC, DeckContents::cards,
            DeckContents::new
    );

    public DeckContents(Holder<Deck> deck) {
        this(deck, deck.value().cards());
    }

    @Override
    public void addToTooltip(Item.TooltipContext context, Consumer<Component> consumer, TooltipFlag flag, DataComponentGetter components) {
        consumer.accept(deck.value().description().copy().withColor(TextColor.GRAY));
        consumer.accept(Component.translatable("item." + HouseOfCards.MOD_ID + ".card_box.contents", cards.size(), deck.value().cards().size()).withColor(TextColor.GRAY));
    }
}
