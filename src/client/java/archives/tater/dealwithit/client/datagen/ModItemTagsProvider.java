package archives.tater.dealwithit.client.datagen;

import archives.tater.dealwithit.registry.DealWithItItemIds;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;

import java.util.concurrent.CompletableFuture;

public class ModItemTagsProvider extends FabricTagsProvider.ItemTagsProvider {
    public ModItemTagsProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registryLookupFuture) {
        super(output, registryLookupFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider registries) {
        builder(TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("kitchenprojectiles", "throwable_knives")))
                .add(DealWithItItemIds.CARD);
        builder(TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("kitchenprojectiles", "light_knives")))
                .add(DealWithItItemIds.CARD);
    }
}
