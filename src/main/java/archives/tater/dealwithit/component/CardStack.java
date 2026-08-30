package archives.tater.dealwithit.component;

import archives.tater.dealwithit.registry.DealWithItComponents;
import archives.tater.dealwithit.registry.DealWithItItems;

import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Unit;
import net.minecraft.world.item.ItemStack;

import org.jspecify.annotations.Nullable;

import java.util.List;

public record CardStack(List<CardInstance> cards) {
    public static final Codec<CardStack> CODEC = CardInstance.CODEC.listOf().xmap(CardStack::new, CardStack::cards);
    public static final StreamCodec<RegistryFriendlyByteBuf, CardStack> STREAM_CODEC = CardInstance.STREAM_CODEC.apply(ByteBufCodecs.list()).map(CardStack::new, CardStack::cards);
    public static final CardStack EMPTY = new CardStack(List.of());

    public static @Nullable CardInstance pop(ItemStack stack) {
        var cardStack = stack.get(DealWithItComponents.CARDS);
        if (cardStack == null || cardStack.cards.isEmpty()) return null;
        var newCards = cardStack.cards.subList(0, cardStack.cards.size() - 1);
        if (newCards.isEmpty())
            stack.setCount(0);
        else
            stack.set(DealWithItComponents.CARDS, new CardStack(newCards));
        return cardStack.cards.getLast();
    }

    public static ItemStack toStack(List<CardInstance> cards) {
        return switch (cards.size()) {
            case 0 -> ItemStack.EMPTY;
            case 1 -> {
                var stack = CardComponent.createStack(cards.getFirst().card());
                if (cards.getFirst().faceDown())
                    stack.set(DealWithItComponents.FACE_DOWN, Unit.INSTANCE);
                yield stack;
            }
            default -> {
                var stack = DealWithItItems.CARD_STACK.getDefaultInstance();
                stack.set(DealWithItComponents.CARDS, new CardStack(cards));
                yield stack;
            }
        };
    }
}
