package archives.tater.dealwithit.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

@FunctionalInterface
public interface ItemStackUseCallback {
    Event<ItemStackUseCallback> EVENT = EventFactory.createArrayBacked(ItemStackUseCallback.class, listeners -> (level, player, hand) -> {
        for (var listener : listeners) {
            var result = listener.interact(level, player, hand);
            if (result != InteractionResult.PASS) return result;
        }
        return InteractionResult.PASS;
    });

    InteractionResult interact(final Level level, final Player player, final InteractionHand hand);
}
