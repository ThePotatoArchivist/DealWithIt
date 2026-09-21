package archives.tater.dealwithit.client.mixin;

import archives.tater.dealwithit.client.event.ItemSlotMouseActionRegistry;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.ItemSlotMouseAction;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;

@Mixin(AbstractContainerScreen.class)
public abstract class AbstractContainerScreenMixin extends Screen {
    @Shadow
    protected abstract void addItemSlotMouseAction(ItemSlotMouseAction itemSlotMouseAction);

    protected AbstractContainerScreenMixin(Component title) {
        super(title);
    }

    @Inject(
            method = "init",
            at = @At("TAIL")
    )
    private void registerItemSlotMouseActions(CallbackInfo ci) {
        ItemSlotMouseActionRegistry.registerActions((AbstractContainerScreen<?>) (Object) this, minecraft, this::addItemSlotMouseAction);
    }
}
