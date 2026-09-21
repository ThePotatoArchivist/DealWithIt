package archives.tater.dealwithit.client.event;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ItemSlotMouseAction;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;

import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class ItemSlotMouseActionRegistry {

    private static final List<Factory<AbstractContainerScreen<?>>> REGISTERED_FACTORIES = new ArrayList<>();

    public static void register(Factory<AbstractContainerScreen<?>> factory) {
        REGISTERED_FACTORIES.add(factory);
    }

    public static void register(Function<Minecraft, @Nullable ItemSlotMouseAction> factory) {
        register((_, minecraft) -> factory.apply(minecraft));
    }

    @SuppressWarnings("unchecked")
    public static <T extends AbstractContainerScreen<?>> void registerForScreen(Class<T> screenClass, Factory<T> factory) {
        register((screen, minecraft) -> screenClass.isInstance(screen) ? factory.create((T) screen, minecraft) : null);
    }

    @ApiStatus.Internal
    public static void registerActions(AbstractContainerScreen<?> screen, Minecraft minecraft, Consumer<ItemSlotMouseAction> register) {
        for (var factory : REGISTERED_FACTORIES) {
            var action = factory.create(screen, minecraft);
            if (action != null) register.accept(action);
        }
    }

    @FunctionalInterface
    public interface Factory<T extends AbstractContainerScreen<?>> {
        @Nullable ItemSlotMouseAction create(T screen, Minecraft minecraft);
    }
}
