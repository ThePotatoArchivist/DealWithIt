package archives.tater.dealwithit.client.atlas;

import archives.tater.dealwithit.DealWithIt;
import archives.tater.dealwithit.client.mixin.NativeImageAccessor;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.atlas.SpriteResourceLoader;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.client.renderer.texture.atlas.sources.LazyLoadedImage;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.ARGB;

import org.jspecify.annotations.Nullable;
import org.lwjgl.system.MemoryUtil;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static archives.tater.dealwithit.Util.mapNonNullValues;
import static archives.tater.dealwithit.Util.withAppended;
import static java.util.function.Function.identity;

public record CartesianComposite(
        Identifier basePath,
        List<Layer> layers
) implements SpriteSource {
    public static final MapCodec<CartesianComposite> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Identifier.CODEC.fieldOf("base_path").forGetter(CartesianComposite::basePath),
            Layer.CODEC.listOf(1, Integer.MAX_VALUE).fieldOf("layers").forGetter(CartesianComposite::layers)
    ).apply(instance, CartesianComposite::new));

    public CartesianComposite(Identifier basePath, Layer... layers) {
        this(basePath, Arrays.asList(layers));
    }

    @Override
    public void run(ResourceManager resourceManager, Output output) {
        var fullSize = layers.stream().mapToInt(Layer::size).reduce(1, (a, b) -> a * b);
        layers.stream()
                .map(layer -> {
                    var size = fullSize / layer.size();
                    return mapNonNullValues(layer.textures, id -> loadImage(id, resourceManager, size));
                })
                .reduce(
                        Stream.of(new CompositedSpriteSupplier(List.of(), basePath)),
                        (stream, layer) ->
                                stream.flatMap(composite ->
                                        layer.entrySet().stream().map(entry ->
                                                composite.join(entry.getValue(), entry.getKey()))),
                        (_, _) -> { throw new IllegalStateException("This shouldn't happen"); }
                )
                .forEach(composite -> {
                    output.add(composite.id, composite);
                });
    }

    @Override
    public MapCodec<? extends SpriteSource> codec() {
        return CODEC;
    }

    private static @Nullable LazyLoadedImage loadImage(Identifier texture, ResourceManager manager, int count) {
        var textureId = TEXTURE_ID_CONVERTER.idToFile(texture);
        try {
            return new LazyLoadedImage(textureId, manager.getResourceOrThrow(textureId), count);
        } catch (FileNotFoundException e) {
            DealWithIt.LOGGER.error("Unable to find texture {}", textureId, e);
            return null;
        }
    }

    private record CompositedSpriteSupplier(List<LazyLoadedImage> layers, Identifier id) implements SpriteSource.DiscardableLoader {
        @Override
        public @Nullable SpriteContents get(SpriteResourceLoader loader) {
            try {
                var result = composite(layers);
                return new SpriteContents(id, new FrameSize(result.getWidth(), result.getHeight()), result);
            } catch (IOException | IllegalStateException | IllegalArgumentException e) {
                DealWithIt.LOGGER.error("Unable to composite {}", id, e);
                return null;
            } finally {
                for (var layer : layers)
                    layer.release();
            }
        }

        private static NativeImage composite(List<LazyLoadedImage> layers) throws IOException, IllegalStateException, IllegalArgumentException {
            var images = new ArrayList<NativeImage>();
            for (var layer : layers) {
                var image = layer.get();
                access(image).callCheckAllocated();
                images.add(image);
            }
            var width = images.getFirst().getWidth();
            var height = images.getFirst().getHeight();
            for (var image : images) {
                if (image.getWidth() != width || image.getHeight() != height)
                    throw new IllegalArgumentException("Sprites were not all the same size");
            }
            var result = new NativeImage(width, height, false);

            var pixelCount = width * height;
            var sourceBuffers = images.stream().map(image -> MemoryUtil.memIntBuffer(access(image).dealwithit$getPixels(), pixelCount)).toList();
            var targetBuffer = MemoryUtil.memIntBuffer(access(result).dealwithit$getPixels(), pixelCount);

            for (var i = 0; i < pixelCount; i++) {
                var pixel = 0x00000000;
                for (var buffer : sourceBuffers)
                    pixel = ARGB.alphaBlend(pixel, ARGB.fromABGR(buffer.get(i)));
                targetBuffer.put(i, ARGB.toABGR(pixel));
            }

            return result;
        }

        private static NativeImageAccessor access(NativeImage image) {
            return (NativeImageAccessor) (Object) image;
        }

        public CompositedSpriteSupplier join(LazyLoadedImage layer, String suffix) {
            return new CompositedSpriteSupplier(withAppended(layers, layer), id.withSuffix(suffix));
        }
    }

    public record Layer(Map<String, Identifier> textures) {
        public static final Codec<Layer> FULL_CODEC = Codec.unboundedMap(Codec.STRING, Identifier.CODEC).xmap(Layer::new, Layer::textures);
        public static final Codec<Layer> CODEC = Codec.either(FULL_CODEC, Identifier.CODEC).xmap(
                either -> either.map(identity(), Layer::new),
                layer -> layer.size() == 1 && layer.textures.containsKey("") ? Either.right(layer.textures.values().stream().findAny().orElseThrow()) : Either.left(layer)
        );

        public Layer(Identifier texture) {
            this(Map.of("", texture));
        }

        public int size() {
            return textures.size();
        }
    }
}
