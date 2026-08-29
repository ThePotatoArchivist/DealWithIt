package archives.tater.dealwithit.client.datagen;

import archives.tater.dealwithit.registry.DealWithItItemIds;
import archives.tater.dealwithit.registry.DealWithItItemTags;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;

import net.minecraft.core.HolderLookup;

import java.util.concurrent.CompletableFuture;

public class ModItemTagsProvider extends FabricTagsProvider.ItemTagsProvider {
    public ModItemTagsProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registryLookupFuture) {
        super(output, registryLookupFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider registries) {
        builder(DealWithItItemTags.FLIPPABLE)
                .add(DealWithItItemIds.CARD);
    }
}
