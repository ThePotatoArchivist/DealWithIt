package archives.tater.houseofcards.client.render;

import archives.tater.houseofcards.client.HouseOfCardsAtlases;
import archives.tater.houseofcards.component.CardComponent;
import archives.tater.houseofcards.registry.HouseOfCardsComponents;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.resources.model.sprite.SpriteGetter;
import net.minecraft.world.item.ItemStack;

import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;

public class CardSpecialRenderer implements SpecialModelRenderer<CardComponent> {
    private final SpriteGetter sprites;

    public CardSpecialRenderer(SpriteGetter sprites) {
        this.sprites = sprites;
    }

    @Override
    public void submit(@Nullable CardComponent argument, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, int overlayCoords, boolean hasFoil, int outlineColor) {
        if (argument == null) return;
        final var key = argument.card().unwrapKey().orElseThrow();

        final var spriteId = HouseOfCardsAtlases.CARD_MAPPER.apply(key.identifier());
        final var sprite = sprites.get(spriteId);

        submitNodeCollector.submitCustomGeometry(poseStack, spriteId.renderType(RenderTypes::itemCutout), (pose, buffer) -> {
            vertex(buffer, pose, lightCoords, overlayCoords, 0, 0, sprite.getU0(), sprite.getV1());
            vertex(buffer, pose, lightCoords, overlayCoords, 1, 0, sprite.getU1(), sprite.getV1());
            vertex(buffer, pose, lightCoords, overlayCoords, 1, 1, sprite.getU1(), sprite.getV0());
            vertex(buffer, pose, lightCoords, overlayCoords, 0, 1, sprite.getU0(), sprite.getV0());
        });
    }

    private static void vertex(final VertexConsumer builder, final PoseStack.Pose pose, final int lightCoords, final int overlayCoords, final float x, final float y, final float u, final float v) {
        builder.addVertex(pose, x, y, 0.5f)
            .setColor(-1)
            .setUv(u, v)
            .setOverlay(overlayCoords)
            .setLight(lightCoords)
            .setNormal(pose, 0.0F, 1.0F, 0.0F);
    }

    @Override
    public void getExtents(Consumer<Vector3fc> output) {
        output.accept(new Vector3f(0, 0, 0.5f));
        output.accept(new Vector3f(0, 1, 0.5f));
        output.accept(new Vector3f(1, 0, 0.5f));
        output.accept(new Vector3f(1, 1, 0.5f));
    }

    @Override
    public @Nullable CardComponent extractArgument(ItemStack stack) {
        return stack.get(HouseOfCardsComponents.CARD);
    }

    public record Unbaked() implements SpecialModelRenderer.Unbaked<CardComponent> {
        public static final MapCodec<Unbaked> CODEC = MapCodec.unit(new Unbaked());

        @Override
        public SpecialModelRenderer<CardComponent> bake(BakingContext context) {
            return new CardSpecialRenderer(context.sprites());
        }

        @Override
        public MapCodec<? extends SpecialModelRenderer.Unbaked<CardComponent>> type() {
            return CODEC;
        }
    }
}
