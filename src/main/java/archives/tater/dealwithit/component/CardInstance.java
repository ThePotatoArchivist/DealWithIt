package archives.tater.dealwithit.component;

import archives.tater.dealwithit.ItemModelProviderComponent;
import archives.tater.dealwithit.data.Card;
import archives.tater.dealwithit.data.Deck;
import archives.tater.dealwithit.registry.DealWithItComponents;
import archives.tater.dealwithit.registry.DealWithItItems;
import archives.tater.dealwithit.registry.DealWithItSounds;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;

import java.util.function.Consumer;

public record CardInstance(Holder<Deck> deck, Holder<Card> card, boolean faceDown) implements TooltipProvider, ItemModelProviderComponent {

    public static final MapCodec<CardInstance> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Deck.CODEC.fieldOf("deck").forGetter(CardInstance::deck),
            Card.CODEC.fieldOf("card").forGetter(CardInstance::card),
            Codec.BOOL.fieldOf("face_down").forGetter(CardInstance::faceDown)
    ).apply(instance, CardInstance::new));

    public static final Codec<CardInstance> CODEC = MAP_CODEC.codec();

    public static final StreamCodec<RegistryFriendlyByteBuf, CardInstance> STREAM_CODEC = StreamCodec.composite(
            Deck.STREAM_CODEC, CardInstance::deck,
            Card.STREAM_CODEC, CardInstance::card,
            ByteBufCodecs.BOOL, CardInstance::faceDown,
            CardInstance::new
    );

    public CardInstance flipped() {
        return new CardInstance(deck, card, !faceDown);
    }

    @Override
    public void addToTooltip(Item.TooltipContext context, Consumer<Component> consumer, TooltipFlag flag, DataComponentGetter components) {
        consumer.accept((faceDown ? deck.value().description() : card.value().description()).copy().withColor(TextColor.GRAY));
    }

    public static void flip(ItemStack stack, Player player) {
        flip(stack);
        player.playSound(DealWithItSounds.CARD_FLIP);
    }

    public static void flip(ItemStack stack) {
        var card = stack.get(DealWithItComponents.CARD);
        if (card == null) return;
        stack.set(DealWithItComponents.CARD, card.flipped());
    }

    public static ItemStack createStack(CardInstance value) {
        var stack = DealWithItItems.CARD.getDefaultInstance();
        stack.set(DealWithItComponents.CARD, value);
        return stack;
    }

    public static ItemStack createStack(CardInstance value, boolean flipped) {
        return createStack(flipped ? value.flipped() : value);
    }

    @Override
    public Identifier modelId() {
        return deck.unwrapKey().orElseThrow().identifier();
    }
}
