package archives.tater.houseofcards.client.datagen;

import archives.tater.houseofcards.registry.HouseOfCardsItemIds;
import archives.tater.houseofcards.registry.HouseOfCardsItemTags;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;

import net.minecraft.core.HolderLookup;

import java.util.concurrent.CompletableFuture;

public class ModItemTagProvider extends FabricTagsProvider.ItemTagsProvider {
    public ModItemTagProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registryLookupFuture) {
        super(output, registryLookupFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider registries) {
        builder(HouseOfCardsItemTags.FLIPPABLE)
                .add(HouseOfCardsItemIds.CARD);
    }
}
