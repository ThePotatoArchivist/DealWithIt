package archives.tater.dealwithit.client.render.item.property;

import archives.tater.dealwithit.component.CardStack;
import archives.tater.dealwithit.registry.DealWithItComponents;

import com.mojang.serialization.MapCodec;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperty;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemStack;

import org.jspecify.annotations.Nullable;

public record CardStackCount() implements RangeSelectItemModelProperty {
    public static final MapCodec<CardStackCount> CODEC = MapCodec.unit(new CardStackCount());

    @Override
    public float get(ItemStack itemStack, @Nullable ClientLevel level, @Nullable ItemOwner owner, int seed) {
        return itemStack.getOrDefault(DealWithItComponents.CARD_STACK, CardStack.EMPTY).cards().size();
    }

    @Override
    public MapCodec<? extends RangeSelectItemModelProperty> type() {
        return CODEC;
    }
}
