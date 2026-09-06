package archives.tater.dealwithit.mixin;

import archives.tater.dealwithit.event.ItemStackBarCallback;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.world.item.ItemStack;

@Mixin(ItemStack.class)
public class ItemStackMixin {
	@SuppressWarnings("ConstantValue")
    @ModifyReturnValue(
			method = "isBarVisible",
			at = @At("RETURN")
	)
	private boolean barVisibleEvent(boolean original) {
		return original || ItemStackBarCallback.EVENT.invoker().getBar((ItemStack) (Object) this) != null;
	}

	@ModifyReturnValue(
			method = "getBarColor",
			at = @At("RETURN")
	)
	private int barColorEvent(int original) {
		var barDisplay = ItemStackBarCallback.EVENT.invoker().getBar((ItemStack) (Object) this);
		if (barDisplay == null) return original;
		return barDisplay.barColor();
	}

	@ModifyReturnValue(
			method = "getBarWidth",
			at = @At("RETURN")
	)
	private int barWidthEvent(int original) {
		var barDisplay = ItemStackBarCallback.EVENT.invoker().getBar((ItemStack) (Object) this);
		if (barDisplay == null) return original;
		return barDisplay.barWidth();
	}
}