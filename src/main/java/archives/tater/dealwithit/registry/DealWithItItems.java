package archives.tater.dealwithit.registry;

import archives.tater.dealwithit.block.CardStackBlock;
import archives.tater.dealwithit.block.entity.CardStackBlockEntity;
import archives.tater.dealwithit.data.Card;
import archives.tater.dealwithit.item.CardBoxItem;

import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.item.v1.ItemClickBehaviorCallback;
import net.fabricmc.fabric.api.util.EventResult;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.function.Function;

public interface DealWithItItems {

    Item CARD = register(DealWithItItemIds.CARD, new Item.Properties().stacksTo(1));
    Item CARD_BOX = register(DealWithItItemIds.CARD_BOX, CardBoxItem::new, new Item.Properties().stacksTo(1));
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
            if (hoveredItem.is(DealWithItItemTags.FLIPPABLE) && itemHeldByCursor.isEmpty() && clickAction == ClickAction.SECONDARY) {
                Card.flip(hoveredItem, player);
                return EventResult.DENY;
            }

            if (clickAction == ClickAction.PRIMARY && (tryInsert(hoveredItem, itemHeldByCursor) || tryInsert(itemHeldByCursor, hoveredItem))) {
                player.level().playLocalSound(player, DealWithItSounds.CARD_BOX_INSERT, player.getSoundSource(), 0.3f, 1);
                return EventResult.DENY;
            }

            return EventResult.PASS;
        });

        UseItemCallback.EVENT.register((player, level, hand) -> {
            if (player.isSpectator()) return InteractionResult.PASS;

            var stack = player.getItemInHand(hand);
            if (!stack.is(DealWithItItemTags.FLIPPABLE)) return InteractionResult.PASS;

            Card.flip(stack, player);

            return InteractionResult.SUCCESS;
        });

        UseBlockCallback.EVENT.register((player, level, hand, hitResult) -> {
            if (player.isSpectator()) return InteractionResult.PASS;
            var stack = player.getItemInHand(hand);

            if (level.getBlockEntity(hitResult.getBlockPos()) instanceof CardStackBlockEntity blockEntity) {
                if (!stack.has(DealWithItComponents.CARD)) return InteractionResult.PASS;

                if (!blockEntity.pushCard(stack, player.getYHeadRot(), player.isSecondaryUseActive())) return InteractionResult.FAIL;

                level.playSound(player, hitResult.getBlockPos(), DealWithItSounds.CARD_STACK_PLACE, SoundSource.BLOCKS);
                stack.consume(1, player);

                return InteractionResult.SUCCESS;
            }

            if (!stack.has(DealWithItComponents.CARD) && !stack.has(DealWithItComponents.DECK_CONTENTS)) return InteractionResult.PASS;

            return CardStackBlock.place(player, hand, stack, hitResult) ? InteractionResult.SUCCESS : InteractionResult.PASS;
        });
    }
}
