package archives.tater.dealwithit.client.render.item.special;

import archives.tater.dealwithit.component.CardStack;
import archives.tater.dealwithit.registry.DealWithItComponents;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Font.DisplayMode;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.network.chat.Style;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemStack;

import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;

public class CardStackCountSpecialRenderer implements SpecialModelRenderer<Integer> {
    private final Font font;
    private final boolean dropShadow;
    private final DisplayMode displayMode;
    private final int color;
    private final int backgroundColor;

    public CardStackCountSpecialRenderer(Font font, boolean dropShadow, DisplayMode displayMode, int color, int backgroundColor) {
        this.font = font;
        this.dropShadow = dropShadow;
        this.displayMode = displayMode;
        this.color = color;
        this.backgroundColor = backgroundColor;
    }

    @Override
    public void submit(@Nullable Integer argument, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, int overlayCoords, boolean hasFoil, int outlineColor) {
        if (argument == null || argument < 2) return;

        poseStack.pushPose();
        poseStack.translate(0, 0, 10);
        poseStack.scale(1f / 16, -1f / 16, 1f / 16);
        var sequence = FormattedCharSequence.forward(argument.toString(), Style.EMPTY);
        poseStack.translate(17 - font.width(sequence), 2 - font.lineHeight, 0);
        submitNodeCollector.submitText(poseStack, 0, 0, sequence, dropShadow, displayMode, lightCoords, color, backgroundColor, outlineColor);
        poseStack.popPose();
    }

    @Override
    public void getExtents(Consumer<Vector3fc> output) {
        output.accept(new Vector3f(0,-1 / 16f, 0));
        output.accept(new Vector3f(17 / 16f,-1 / 16f, 0));
        output.accept(new Vector3f(17 / 16f,1, 0));
        output.accept(new Vector3f(0,1, 0));
    }

    @Override
    public @Nullable Integer extractArgument(ItemStack stack) {
        return stack.getOrDefault(DealWithItComponents.CARD_STACK, CardStack.EMPTY).cards().size();
    }

    public record Unbaked(
            boolean dropShadow,
            DisplayMode displayMode,
            int color,
            int backgroundColor
    ) implements SpecialModelRenderer.Unbaked<Integer> {
        @SuppressWarnings({"DataFlowIssue", "RedundantTypeArguments"})
        private static final Codec<DisplayMode> DISPLAY_MODE_CODEC = Codec.<DisplayMode>stringResolver(mode -> switch (mode) {
            case NORMAL -> "normal";
            case SEE_THROUGH -> "see_through";
            case POLYGON_OFFSET -> "polygon_offset";
        }, s -> switch (s) {
            case "normal" -> DisplayMode.NORMAL;
            case "see_through" -> DisplayMode.SEE_THROUGH;
            case "polygon_offset" -> DisplayMode.POLYGON_OFFSET;
            default -> null;
        });

        public static final boolean DEFAULT_SHADOW = true;
        public static final int DEFAULT_COLOR = 0xffffffff;
        public static final int DEFAULT_BACKGROUND_COLOR = 0;

        public static final MapCodec<Unbaked> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.BOOL.optionalFieldOf("drop_shadow", DEFAULT_SHADOW).forGetter(Unbaked::dropShadow),
                DISPLAY_MODE_CODEC.optionalFieldOf("display_mode", DisplayMode.NORMAL).forGetter(Unbaked::displayMode),
                ExtraCodecs.STRING_ARGB_COLOR.optionalFieldOf("color", DEFAULT_COLOR).forGetter(Unbaked::color),
                ExtraCodecs.STRING_ARGB_COLOR.optionalFieldOf("background_color", DEFAULT_BACKGROUND_COLOR).forGetter(Unbaked::backgroundColor)
        ).apply(instance, Unbaked::new));

        public Unbaked() {
            this(DEFAULT_SHADOW, DisplayMode.NORMAL, DEFAULT_COLOR, DEFAULT_BACKGROUND_COLOR);
        }

        @Override
        public SpecialModelRenderer<Integer> bake(BakingContext context) {
            return new CardStackCountSpecialRenderer(Minecraft.getInstance().font, dropShadow, displayMode, color, backgroundColor);
        }

        @Override
        public MapCodec<? extends SpecialModelRenderer.Unbaked<Integer>> type() {
            return CODEC;
        }
    }
}
