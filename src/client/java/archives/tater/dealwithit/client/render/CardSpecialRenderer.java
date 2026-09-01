package archives.tater.dealwithit.client.render;

import archives.tater.dealwithit.client.DealWithItAtlases;
import archives.tater.dealwithit.component.CardInstance;
import archives.tater.dealwithit.registry.DealWithItComponents;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.resources.model.sprite.SpriteGetter;
import net.minecraft.world.item.ItemStack;

import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;

public class CardSpecialRenderer implements SpecialModelRenderer<CardInstance> {
    private final SpriteGetter sprites;
    private final boolean forceHidden;

    public CardSpecialRenderer(SpriteGetter sprites, boolean forceHidden) {
        this.sprites = sprites;
        this.forceHidden = forceHidden;
    }

    @Override
    public void submit(@Nullable CardInstance argument, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, int overlayCoords, boolean hasFoil, int outlineColor) {
        if (argument == null) return;

        renderCard(argument, forceHidden, sprites, poseStack, submitNodeCollector, -1, lightCoords, overlayCoords);
    }

    public static void renderCard(CardInstance card, boolean forceHidden, SpriteGetter sprites, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int color, int lightCoords, int overlayCoords) {
        final var backSprite = sprites.get(DealWithItAtlases.DECK_BACK_MAPPER.apply(card.deck().unwrapKey().orElseThrow().identifier()));
        final var cardSprite = sprites.get(DealWithItAtlases.CARD_MAPPER.apply(card.card().unwrapKey().orElseThrow().identifier()));
        final var frontSprite = forceHidden || card.faceDown() ? backSprite : cardSprite;

        submitNodeCollector.submitCustomGeometry(poseStack, DealWithItAtlases.CARDS_RENDER_TYPE, (pose, buffer) -> {
            vertex(buffer, pose, color, lightCoords, overlayCoords, 0, 0, frontSprite.getU0(), frontSprite.getV1(), 1);
            vertex(buffer, pose, color, lightCoords, overlayCoords, 1, 0, frontSprite.getU1(), frontSprite.getV1(), 1);
            vertex(buffer, pose, color, lightCoords, overlayCoords, 1, 1, frontSprite.getU1(), frontSprite.getV0(), 1);
            vertex(buffer, pose, color, lightCoords, overlayCoords, 0, 1, frontSprite.getU0(), frontSprite.getV0(), 1);

            vertex(buffer, pose, color, lightCoords, overlayCoords, 1, 0, backSprite.getU0(), backSprite.getV1(), -1);
            vertex(buffer, pose, color, lightCoords, overlayCoords, 0, 0, backSprite.getU1(), backSprite.getV1(), -1);
            vertex(buffer, pose, color, lightCoords, overlayCoords, 0, 1, backSprite.getU1(), backSprite.getV0(), -1);
            vertex(buffer, pose, color, lightCoords, overlayCoords, 1, 1, backSprite.getU0(), backSprite.getV0(), -1);
        });
    }

    private static void vertex(final VertexConsumer builder, final PoseStack.Pose pose, final int color, final int lightCoords, final int overlayCoords, final float x, final float y, final float u, final float v, final float normalZ) {
        builder.addVertex(pose, x, y, 0.5f)
            .setColor(color)
            .setUv(u, v)
            .setOverlay(overlayCoords)
            .setLight(lightCoords)
            .setNormal(pose, 0.0F, 0.0F, normalZ);
    }

    @Override
    public void getExtents(Consumer<Vector3fc> output) {
        output.accept(new Vector3f(0, 0, 0.5f));
        output.accept(new Vector3f(0, 1, 0.5f));
        output.accept(new Vector3f(1, 0, 0.5f));
        output.accept(new Vector3f(1, 1, 0.5f));
    }

    @Override
    public @Nullable CardInstance extractArgument(ItemStack stack) {
        return stack.get(DealWithItComponents.CARD);
    }

    public record Unbaked(boolean forceHidden) implements SpecialModelRenderer.Unbaked<CardInstance> {
        public static final MapCodec<Unbaked> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.BOOL.fieldOf("force_hidden").forGetter(Unbaked::forceHidden)
        ).apply(instance, Unbaked::new));

        @Override
        public CardSpecialRenderer bake(BakingContext context) {
            return new CardSpecialRenderer(context.sprites(), forceHidden);
        }

        @Override
        public MapCodec<? extends SpecialModelRenderer.Unbaked<CardInstance>> type() {
            return CODEC;
        }
    }
}
