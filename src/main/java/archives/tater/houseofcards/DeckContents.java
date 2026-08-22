package archives.tater.houseofcards;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

import java.util.function.Consumer;

public record DeckContents(
        Holder<Deck> deck,
        Object2IntMap<Holder<Card>> cards
) implements TooltipProvider {
    public static final Codec<DeckContents> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Deck.CODEC.fieldOf("deck").forGetter(DeckContents::deck),
            Deck.CARDS_CODEC.fieldOf("cards").forGetter(DeckContents::cards)
    ).apply(instance, DeckContents::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, DeckContents> STREAM_CODEC = StreamCodec.composite(
            Deck.STREAM_CODEC, DeckContents::deck,
            ByteBufCodecs.map(Object2IntOpenHashMap::new, Card.STREAM_CODEC, ByteBufCodecs.INT), DeckContents::cards,
            DeckContents::new
    );

    public DeckContents(Holder<Deck> deck) {
        this(deck, deck.value().cards());
    }

    public int cardCount() {
        return cards.values().intStream().sum();
    }

    @Override
    public void addToTooltip(Item.TooltipContext context, Consumer<Component> consumer, TooltipFlag flag, DataComponentGetter components) {
        consumer.accept(deck.value().description().copy().withColor(TextColor.GRAY));
        consumer.accept(Component.translatable("item." + HouseOfCards.MOD_ID + ".card_box.contents", cardCount(), deck.value().size()).withColor(TextColor.GRAY));
    }
}
