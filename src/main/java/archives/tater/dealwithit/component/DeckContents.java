package archives.tater.dealwithit.component;

import archives.tater.dealwithit.DealWithIt;
import archives.tater.dealwithit.ItemModelProviderComponent;
import archives.tater.dealwithit.data.CardSet;
import archives.tater.dealwithit.data.Deck;
import archives.tater.dealwithit.registry.DealWithItComponents;
import archives.tater.dealwithit.registry.DealWithItItems;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;

import java.util.function.Consumer;

import static java.util.function.Function.identity;

public record DeckContents(
        Holder<Deck> deck,
        CardSet cards
) implements TooltipProvider, ItemModelProviderComponent {
    public static final Codec<DeckContents> FULL_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Deck.CODEC.fieldOf("deck").forGetter(DeckContents::deck),
            CardSet.CODEC.fieldOf("cards").forGetter(DeckContents::cards)
    ).apply(instance, DeckContents::new));

    public static final Codec<DeckContents> CODEC = Codec.either(FULL_CODEC, Deck.CODEC).xmap(
            either -> either.map(identity(), DeckContents::new),
            contents -> contents.isComplete() ? Either.right(contents.deck) : Either.left(contents)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, DeckContents> STREAM_CODEC = StreamCodec.composite(
            Deck.STREAM_CODEC, DeckContents::deck,
            CardSet.STREAM_CODEC, DeckContents::cards,
            DeckContents::new
    );

    public static final String FILL = "item." + DealWithIt.MOD_ID + ".card_box.fill";
    public DeckContents(Holder<Deck> deck) {
        this(deck, deck.value().cards());
    }

    public int cardCount() {
        return cards.count();
    }

    public boolean isComplete() {
        return cards.equals(deck.value().cards());
    }

    public DeckContents withCards(CardSet cards) {
        return new DeckContents(deck, cards);
    }

    public DeckContents withCards(CardSet.Mutable cards) {
        return withCards(cards.build());
    }

    public DeckContents withAdded(CardComponent card) {
        if (!canInsert(card)) return this;
        var cards = mutableCards();
        cards.add(card.card());
        return withCards(cards);
    }

    public CardSet.Mutable mutableCards() {
        return new CardSet.Mutable(this.cards, deck.value().cards());
    }

    public boolean canInsert(CardComponent card) {
        return canInsert(card, deck, cards);
    }

    @Override
    public Identifier modelId() {
        return deck.unwrapKey().orElseThrow().identifier();
    }

    @Override
    public void addToTooltip(Item.TooltipContext context, Consumer<Component> consumer, TooltipFlag flag, DataComponentGetter components) {
        consumer.accept(deck.value().description().copy().withColor(TextColor.GRAY));
        consumer.accept(Component.translatable(FILL, cardCount(), deck.value().size()).withColor(TextColor.GRAY));
    }

    public static ItemStack createStack(Holder<Deck> deck) {
        var stack = DealWithItItems.CARD_BOX.getDefaultInstance();
        stack.set(DealWithItComponents.DECK_CONTENTS, new DeckContents(deck));
        return stack;
    }

    public static boolean canInsert(CardComponent card, Holder<Deck> deck, CardSet.Mutable cards) {
        return card.deck() == deck && cards.canAdd(card.card());
    }

    public static boolean canInsert(CardComponent card, Holder<Deck> deck, CardSet cards) {
        return card.deck() == deck && cards.count(card.card()) < deck.value().cards().count(card.card());
    }

    public static boolean tryInsert(CardComponent card, Holder<Deck> deck, CardSet.Mutable cards) {
        if (!canInsert(card, deck, cards)) return false;
        cards.add(card.card());
        return true;
    }
}
