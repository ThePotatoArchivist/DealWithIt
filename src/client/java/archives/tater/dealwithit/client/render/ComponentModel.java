package archives.tater.dealwithit.client.render;

import archives.tater.dealwithit.ItemModelProviderComponent;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemModels;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

import org.joml.Matrix4fc;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

public class ComponentModel implements ItemModel {
    private final DataComponentType<?> componentType;
    private final String prefix;
    private final ItemModel fallback;
    private final ModelManager modelManager;

    public <T> ComponentModel(DataComponentType<T> componentType, String prefix, ItemModel fallback, ModelManager modelManager) {
        this.componentType = componentType;
        this.prefix = prefix;
        this.fallback = fallback;
        this.modelManager = modelManager;
    }

    @Override
    public void update(ItemStackRenderState output, ItemStack item, ItemModelResolver resolver, ItemDisplayContext displayContext, @Nullable ClientLevel level, @Nullable ItemOwner owner, int seed) {
        var componentValue = item.get(componentType);
        var componentId = switch (componentValue) {
            case Identifier id -> id;
            case ResourceKey<?> key -> key.identifier();
            case Holder.Reference<?> holder -> holder.key().identifier();
            case ItemModelProviderComponent provider -> provider.modelId();
            case null, default -> null;
        };
        var model = componentId == null ? fallback : modelManager.getItemModel(componentId.withPrefix(prefix));
        model.update(output, item, resolver, displayContext, level, owner, seed);
    }

    public record Unbaked<T>(DataComponentType<T> component, String prefix, Optional<ItemModel.Unbaked> fallback) implements ItemModel.Unbaked {
        public static final MapCodec<Unbaked<?>> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                BuiltInRegistries.DATA_COMPONENT_TYPE.byNameCodec().fieldOf("component").forGetter(Unbaked::component),
                Codec.STRING.fieldOf("prefix").forGetter(Unbaked::prefix),
                ItemModels.CODEC.optionalFieldOf("fallback").forGetter(Unbaked::fallback)
        ).apply(instance, Unbaked::new));

        public Unbaked(DataComponentType<T> component, String prefix) {
            this(component, prefix, Optional.empty());
        }

        @Override
        public MapCodec<? extends ItemModel.Unbaked> type() {
            return CODEC;
        }

        @Override
        public ItemModel bake(BakingContext context, Matrix4fc transformation) {
            return new ComponentModel(component, prefix, fallback.map(unbaked -> unbaked.bake(context, transformation)).orElseGet(context::missingItemModel), Minecraft.getInstance().getModelManager());
        }

        @Override
        public void resolveDependencies(Resolver resolver) {
            fallback.ifPresent(model -> model.resolveDependencies(resolver));
        }
    }
}
