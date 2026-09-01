package archives.tater.dealwithit.component;

import archives.tater.dealwithit.registry.DealWithItComponents;
import archives.tater.dealwithit.registry.DealWithItItems;

import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;

import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

public record CardStack(List<CardInstance> cards) {
    public static final Codec<CardStack> CODEC = CardInstance.CODEC.listOf().xmap(CardStack::new, CardStack::cards);
    public static final StreamCodec<RegistryFriendlyByteBuf, CardStack> STREAM_CODEC = CardInstance.STREAM_CODEC.apply(ByteBufCodecs.list()).map(CardStack::new, CardStack::cards);
    public static final CardStack EMPTY = new CardStack(List.of());

    public static @Nullable CardInstance pop(ItemStack stack, UseOnContext context) {
        return pop(stack, requireNonNull(context.getPlayer()), context.getHand());
    }

    public static @Nullable CardInstance pop(ItemStack stack, Player player, InteractionHand hand) {
        return pop(stack, replacement -> player.setItemInHand(hand, replacement));
    }

    public static @Nullable CardInstance pop(ItemStack stack, Consumer<ItemStack> replaceStack) {
        var cardStack = stack.get(DealWithItComponents.CARD_STACK);
        if (cardStack == null || cardStack.cards.isEmpty()) return null;
        setCardStack(cardStack.cards.subList(0, cardStack.cards.size() - 1), stack, replaceStack);
        return cardStack.cards.getLast();
    }

    public static boolean tryClickPop(ItemStack target, Consumer<ItemStack> setTarget, ItemStack empty, Consumer<ItemStack> setEmpty) {
        if (!empty.isEmpty()) return false;
        var card = pop(target, setTarget);
        if (card == null) return false;
        setEmpty.accept(CardInstance.createStack(card));
        return true;
    }

    public static boolean tryClickCombine(ItemStack target, Consumer<ItemStack> setTarget, ItemStack additional, Consumer<ItemStack> setAdditional) {
        var targetCards = getCards(target);
        var additionalCards = getCards(additional);
        if (targetCards == null || additionalCards == null) return false;
        var cards = Stream.concat(targetCards, additionalCards).toList();
        setCardStack(cards, target, setTarget);
        setAdditional.accept(ItemStack.EMPTY);
        return true;
    }

    public static @Nullable Stream<CardInstance> getCards(ItemStack stack) {
        var card = stack.get(DealWithItComponents.CARD);
        if (card != null) return Stream.of(card);

        var cardStack = stack.get(DealWithItComponents.CARD_STACK);
        if (cardStack != null) return cardStack.cards().stream();

        return null;
    }

    public static void setCardStack(List<CardInstance> cards, ItemStack stack, Consumer<ItemStack> replaceStack) {
        if (cards.isEmpty()) {
            replaceStack.accept(ItemStack.EMPTY);
            return;
        }

        if (stack.has(DealWithItComponents.CARD_STACK)) {
            if (cards.size() == 1)
                replaceStack.accept(CardInstance.createStack(cards.getFirst()));
            else
                stack.set(DealWithItComponents.CARD_STACK, new CardStack(cards));
        } else {
            if (cards.size() == 1 && stack.has(DealWithItComponents.CARD))
                stack.set(DealWithItComponents.CARD, cards.getFirst());
            else
                replaceStack.accept(toStack(cards));
        }
    }

    public static ItemStack toStack(List<CardInstance> cards) {
        return switch (cards.size()) {
            case 0 -> ItemStack.EMPTY;
            case 1 -> CardInstance.createStack(cards.getFirst());
            default -> {
                var stack = DealWithItItems.CARD_STACK.getDefaultInstance();
                stack.set(DealWithItComponents.CARD_STACK, new CardStack(cards));
                yield stack;
            }
        };
    }
}
