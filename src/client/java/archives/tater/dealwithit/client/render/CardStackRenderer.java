package archives.tater.dealwithit.client.render;

import archives.tater.dealwithit.block.entity.CardStackBlockEntity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.sprite.SpriteGetter;
import net.minecraft.world.phys.Vec3;

import org.jspecify.annotations.Nullable;

import java.util.List;

import static net.minecraft.util.Mth.HALF_PI;
import static net.minecraft.util.Mth.PI;

public class CardStackRenderer implements BlockEntityRenderer<CardStackBlockEntity, CardStackRenderer.CardStackRenderState> {

    private final SpriteGetter sprites;

    public CardStackRenderer(BlockEntityRendererProvider.Context context) {
        this.sprites = context.sprites();
    }

    @Override
    public CardStackRenderState createRenderState() {
        return new CardStackRenderState();
    }

    @Override
    public void extractRenderState(CardStackBlockEntity blockEntity, CardStackRenderState state, float partialTicks, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
        state.cards = List.copyOf(blockEntity.getCards());
    }

    @Override
    public void submit(CardStackRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        poseStack.pushPose();

        poseStack.translate(0.5f, 0, 0.5f);
        poseStack.mulPose(Axis.YP.rotation(PI));

        for (int i = 0; i < state.cards.size(); i++) {
            var instance = state.cards.get(i);

            poseStack.translate(0, 1f / CardStackBlockEntity.FULL_HEIGHT, 0);

            poseStack.pushPose();
            poseStack.mulPose(Axis.YN.rotationDegrees(instance.angle()));
            poseStack.mulPose(Axis.XN.rotation(HALF_PI));
            poseStack.translate(-0.5f, -0.5f, -0.5f);

            CardSpecialRenderer.renderCard(
                    instance.card(),
                    instance.faceDown(),
                    sprites,
                    poseStack,
                    submitNodeCollector,
                    i + 1 >= state.cards.size() ? -1 : i % 2 == 0 ? 0xdddddd : 0xbbbbbb,
                    state.lightCoords,
                    OverlayTexture.NO_OVERLAY
            );

            poseStack.popPose();
        }

        poseStack.popPose();
    }

    public static class CardStackRenderState extends BlockEntityRenderState {
        public List<CardStackBlockEntity.CardInstance> cards = List.of();
    }
}
