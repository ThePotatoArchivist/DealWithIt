package archives.tater.dealwithit.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

import net.minecraft.world.item.ItemStack;

import org.jspecify.annotations.Nullable;

public interface ItemStackBarCallback {
    Event<ItemStackBarCallback> EVENT = EventFactory.createArrayBacked(ItemStackBarCallback.class, listeners -> stack -> {
        for (var listener : listeners) {
            var bar = listener.getBar(stack);
            if (bar != null) return bar;
        }
        return null;
    });

    @Nullable BarDisplay getBar(ItemStack stack);

    record BarDisplay(int barColor, int barWidth) {}
}
