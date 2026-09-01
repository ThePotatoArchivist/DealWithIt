package archives.tater.dealwithit.mixin;

import archives.tater.dealwithit.block.CardStackBlock;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Shadow
    public abstract Level level();

    @SuppressWarnings("ConstantValue")
    @Inject(
            method = "checkFallDamage",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Block;fallOn(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/Entity;D)V")
    )
    private void tryPlaceCardStack(double ya, boolean onGround, BlockState onState, BlockPos pos, CallbackInfo ci) {
        if (!level().isClientSide() && (Object) this instanceof ItemEntity itemEntity)
            CardStackBlock.placeDroppedStack(itemEntity);
    }
}
