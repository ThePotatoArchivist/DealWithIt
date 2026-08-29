package archives.tater.dealwithit.client.datagen;

import archives.tater.dealwithit.data.Deck;
import archives.tater.dealwithit.registry.DealWithItDeckTags;
import archives.tater.dealwithit.registry.DealWithItRegistries;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;

import net.minecraft.core.HolderLookup;

import java.util.concurrent.CompletableFuture;

public class DeckTagsProvider extends FabricTagsProvider<Deck> {
    public DeckTagsProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registryLookupFuture) {
        super(output, DealWithItRegistries.DECK, registryLookupFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider registries) {
        builder(DealWithItDeckTags.CREATIVE_TAB_ORDER)
                .addOptional(Decks.PLAYING_CARDS)
                .addOptional(Decks.GREEN_PLAYING_CARDS)
                .addOptional(Decks.BLUE_PLAYING_CARDS)
                .addOptional(Decks.YELLOW_PLAYING_CARDS)
                .addOptional(Decks.JOKERS_PLAYING_CARDS)
                .addOptional(Decks.GREEN_JOKERS_PLAYING_CARDS)
                .addOptional(Decks.BLUE_JOKERS_PLAYING_CARDS)
                .addOptional(Decks.YELLOW_JOKERS_PLAYING_CARDS)
                .addOptional(Decks.UNO);
    }
}
