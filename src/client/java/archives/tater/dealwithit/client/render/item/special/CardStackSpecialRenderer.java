package archives.tater.dealwithit.client.render.item.special;

import archives.tater.dealwithit.client.render.block.entity.CardStackRenderer;
import archives.tater.dealwithit.component.CardInstance;
import archives.tater.dealwithit.component.CardStack;
import archives.tater.dealwithit.registry.DealWithItComponents;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.resources.model.sprite.SpriteGetter;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

import static java.lang.Math.min;

public class CardStackSpecialRenderer implements SpecialModelRenderer<List<CardInstance>> {
    private final Vec3 offset;
    private final int shadeInterval;
    private final int limit;
    private final boolean reversed;
    private final SpriteGetter sprites;

    public CardStackSpecialRenderer(Vec3 offset, int shadeInterval, int limit, boolean reversed, SpriteGetter sprites) {
        this.shadeInterval = shadeInterval;
        this.limit = limit;
        this.reversed = reversed;
        this.sprites = sprites;
        this.offset = offset;
    }

    @Override
    public void submit(@Nullable List<CardInstance> argument, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, int overlayCoords, boolean hasFoil, int outlineColor) {
        if (argument == null) return;
        poseStack.pushPose();
        var cardCount = argument.size();
        poseStack.translate(offset.scale(-(min(cardCount, limit) - 1) / 2.0));

        for (var i = 0; i < limit && i < cardCount; i++) {
            var index = reversed ? cardCount - 1 - i : i;
            var entry = argument.get(index);
            CardSpecialRenderer.renderCard(
                    entry,
                    false,
                    sprites,
                    poseStack,
                    submitNodeCollector,
                    shadeInterval == 0 ? 0xffffffff : CardStackRenderer.getColor(index, cardCount, shadeInterval),
                    lightCoords,
                    overlayCoords
            );
            poseStack.translate(offset);
        }
        poseStack.popPose();
    }

    @Override
    public void getExtents(Consumer<Vector3fc> output) {
        output.accept(new Vector3f(0, 0, 0.5f));
        output.accept(new Vector3f(0, 1, 0.5f));
        output.accept(new Vector3f(1, 0, 0.5f));
        output.accept(new Vector3f(1, 1, 0.5f));
    }

    @Override
    public @Nullable List<CardInstance> extractArgument(ItemStack stack) {
        return stack.getOrDefault(DealWithItComponents.CARD_STACK, CardStack.EMPTY).cards();
    }

    public record Unbaked(Vec3 offset, int shadeInterval, int limit, boolean reversed) implements SpecialModelRenderer.Unbaked<List<CardInstance>> {
        public static final MapCodec<Unbaked> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Vec3.CODEC.fieldOf("offset").forGetter(Unbaked::offset),
                ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("shadeInterval", 0).forGetter(Unbaked::shadeInterval),
                ExtraCodecs.POSITIVE_INT.optionalFieldOf("limit", Integer.MAX_VALUE).forGetter(Unbaked::limit),
                Codec.BOOL.optionalFieldOf("reversed", false).forGetter(Unbaked::reversed)
        ).apply(instance, Unbaked::new));

        public Unbaked(Vec3 offset, int shadeInterval) {
            this(offset, shadeInterval, Integer.MAX_VALUE, false);
        }

        @Override
        public SpecialModelRenderer<List<CardInstance>> bake(BakingContext context) {
            return new CardStackSpecialRenderer(
                    offset,
                    shadeInterval,
                    limit,
                    reversed,
                    context.sprites()
            );
        }

        @Override
        public MapCodec<? extends SpecialModelRenderer.Unbaked<List<CardInstance>>> type() {
            return CODEC;
        }
    }
}
