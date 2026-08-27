package archives.tater.dealwithit.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;

@FunctionalInterface
public interface ItemStackUseOnCallback {
    Event<ItemStackUseOnCallback> EVENT = EventFactory.createArrayBacked(ItemStackUseOnCallback.class, listeners -> context -> {
        for (var listener : listeners) {
            var result = listener.interact(context);
            if (result != InteractionResult.PASS) return result;
        }
        return InteractionResult.PASS;
    });

    InteractionResult interact(UseOnContext context);
}
