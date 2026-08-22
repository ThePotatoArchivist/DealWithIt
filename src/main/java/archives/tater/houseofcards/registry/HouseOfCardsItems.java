package archives.tater.houseofcards.registry;

import archives.tater.houseofcards.block.CardStackBlock;
import archives.tater.houseofcards.data.Card;

import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.item.v1.ItemClickBehaviorCallback;
import net.fabricmc.fabric.api.util.EventResult;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.item.Item;

import java.util.function.Function;

public interface HouseOfCardsItems {

    Item CARD = register(HouseOfCardsItemIds.CARD, new Item.Properties().stacksTo(1));
    Item CARD_BOX = register(HouseOfCardsItemIds.CARD_BOX, new Item.Properties().stacksTo(1));

    private static Item register(ResourceKey<Item> id, Function<Item.Properties, Item> item, Item.Properties properties) {
        return Registry.register(BuiltInRegistries.ITEM, id, item.apply(properties.setId(id)));
    }

    private static Item register(ResourceKey<Item> id, Item.Properties properties) {
        return register(id, Item::new, properties);
    }

    static void init() {
        ItemClickBehaviorCallback.EVENT.register((hoveredItem, hoveredSlot, itemHeldByCursor, slotHeldByCursor, clickAction, player) -> {
            if (clickAction != ClickAction.SECONDARY || !hoveredItem.is(HouseOfCardsItemTags.FLIPPABLE) || !itemHeldByCursor.isEmpty()) return EventResult.PASS;

            Card.flip(hoveredItem, player);

            return EventResult.DENY;
        });

        UseItemCallback.EVENT.register((player, level, hand) -> {
            if (player.isSpectator()) return InteractionResult.PASS;

            var stack = player.getItemInHand(hand);
            if (!stack.is(HouseOfCardsItemTags.FLIPPABLE)) return InteractionResult.PASS;

            Card.flip(stack, player);

            return InteractionResult.SUCCESS;
        });

        UseBlockCallback.EVENT.register((player, level, hand, hitResult) -> {
            if (player.isSpectator()) return InteractionResult.PASS;
            if (level.getBlockState(hitResult.getBlockPos()).is(HouseOfCardsBlocks.CARD_STACK)) return InteractionResult.PASS;

            var stack = player.getItemInHand(hand);
            if (!stack.has(HouseOfCardsComponents.CARD)) return InteractionResult.PASS;

            return CardStackBlock.place(player, hand, stack, hitResult) ? InteractionResult.SUCCESS : InteractionResult.PASS;
        });
    }
}
