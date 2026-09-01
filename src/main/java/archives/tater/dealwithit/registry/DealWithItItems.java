package archives.tater.dealwithit.registry;

import archives.tater.dealwithit.block.CardStackBlock;
import archives.tater.dealwithit.block.entity.CardStackBlockEntity;
import archives.tater.dealwithit.component.CardInstance;
import archives.tater.dealwithit.component.CardStack;
import archives.tater.dealwithit.event.ItemStackBarCallback;
import archives.tater.dealwithit.event.ItemStackBarCallback.BarDisplay;
import archives.tater.dealwithit.event.ItemStackUseCallback;
import archives.tater.dealwithit.event.ItemStackUseOnCallback;

import net.fabricmc.fabric.api.item.v1.ItemClickBehaviorCallback;
import net.fabricmc.fabric.api.util.EventResult;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.function.Function;

public interface DealWithItItems {

    Item CARD = register(DealWithItItemIds.CARD, new Item.Properties().stacksTo(1));
    Item CARD_STACK = register(DealWithItItemIds.CARD_STACK, new Item.Properties().stacksTo(1));
    Item CARD_BOX = register(DealWithItItemIds.CARD_BOX, new Item.Properties().stacksTo(1));
    Item BLANK_CARD_BOX = register(DealWithItItemIds.BLANK_CARD_BOX, new Item.Properties().stacksTo(1));

    private static Item register(ResourceKey<Item> id, Function<Item.Properties, Item> item, Item.Properties properties) {
        return Registry.register(BuiltInRegistries.ITEM, id, item.apply(properties.setId(id)));
    }

    private static Item register(ResourceKey<Item> id, Item.Properties properties) {
        return register(id, Item::new, properties);
    }

    private static boolean tryInsert(ItemStack box, ItemStack cardItem) {
        var contents = box.get(DealWithItComponents.DECK_CONTENTS);
        var card = cardItem.get(DealWithItComponents.CARD);
        if (contents == null || card == null) return false;
        if (!contents.canInsert(card)) return false;
        box.set(DealWithItComponents.DECK_CONTENTS, contents.withAdded(card));
        cardItem.shrink(1);
        return true;
    }

    static void init() {
        ItemClickBehaviorCallback.EVENT.register((hoveredItem, hoveredSlot, itemHeldByCursor, slotHeldByCursor, clickAction, player) -> {
            if (hoveredItem.has(DealWithItComponents.CARD) && itemHeldByCursor.isEmpty() && clickAction == ClickAction.SECONDARY) {
                CardInstance.flip(hoveredItem, player);
                return EventResult.DENY;
            }

            if (clickAction == ClickAction.PRIMARY && (tryInsert(hoveredItem, itemHeldByCursor) || tryInsert(itemHeldByCursor, hoveredItem))) {
                player.level().playLocalSound(player, DealWithItSounds.CARD_BOX_INSERT, player.getSoundSource(), 0.3f, 1);
                return EventResult.DENY;
            }

            return EventResult.PASS;
        });

        ItemStackUseCallback.EVENT.register((_, player, hand) -> {
            var stack = player.getItemInHand(hand);

            if (stack.has(DealWithItComponents.CARD)) {
                CardInstance.flip(stack, player);
                return InteractionResult.SUCCESS;
            }

            var cardStack = stack.get(DealWithItComponents.CARD_STACK);
            if (cardStack != null) {
                stack.set(DealWithItComponents.CARD_STACK, new CardStack(cardStack.cards().reversed().stream().map(CardInstance::flipped).toList()));
                player.playSound(DealWithItSounds.CARD_FLIP);
                return InteractionResult.SUCCESS;
            }

            return InteractionResult.PASS;
        });

        ItemStackUseOnCallback.EVENT.register(context -> {
            var level = context.getLevel();
            var player = context.getPlayer();
            if (player == null) return InteractionResult.PASS;
            var stack = context.getItemInHand();

            if (!stack.has(DealWithItComponents.CARD) && !stack.has(DealWithItComponents.DECK_CONTENTS) && !stack.has(DealWithItComponents.CARD_STACK)) return InteractionResult.PASS;

            if (level.getBlockEntity(context.getClickedPos()) instanceof CardStackBlockEntity blockEntity)
                return CardStackBlock.addToStack(player, context, blockEntity) ? InteractionResult.SUCCESS : InteractionResult.FAIL;

            return CardStackBlock.placeStack(player, context) ? InteractionResult.SUCCESS : InteractionResult.FAIL;
        });

        ItemStackBarCallback.EVENT.register(stack -> {
            var contents = stack.get(DealWithItComponents.DECK_CONTENTS);
            if (contents == null || contents.isComplete()) return null;

            return new BarDisplay(
                    0xFF7087FF,
                    contents.cardCount() == 0 ? 0 : 12 * contents.cardCount() / contents.deck().value().size() + 1
            );
        });
    }
}
