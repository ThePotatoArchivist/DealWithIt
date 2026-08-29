package archives.tater.dealwithit.mixin;

import archives.tater.dealwithit.event.ItemStackBarCallback;
import archives.tater.dealwithit.event.ItemStackUseCallback;
import archives.tater.dealwithit.event.ItemStackUseOnCallback;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

@Mixin(ItemStack.class)
public class ItemStackMixin {
	@WrapOperation(
			method = "use",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item;use(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/InteractionResult;")
	)
	private InteractionResult useEvent(Item instance, Level level, Player player, InteractionHand hand, Operation<InteractionResult> original) {
		var result = ItemStackUseCallback.EVENT.invoker().interact(level, player, hand);
		if (result != InteractionResult.PASS) return result;
		return original.call(instance, level, player, hand);
	}

	@WrapOperation(
			method = "useOn",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item;useOn(Lnet/minecraft/world/item/context/UseOnContext;)Lnet/minecraft/world/InteractionResult;")
	)
	private InteractionResult useOnEvent(Item instance, UseOnContext context, Operation<InteractionResult> original) {
		var result = ItemStackUseOnCallback.EVENT.invoker().interact(context);
		if (result != InteractionResult.PASS) return result;
		return original.call(instance, context);
	}

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