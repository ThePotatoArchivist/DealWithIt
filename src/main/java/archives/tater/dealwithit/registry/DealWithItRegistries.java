package archives.tater.dealwithit.registry;

import archives.tater.dealwithit.data.Card;
import archives.tater.dealwithit.data.Deck;
import archives.tater.dealwithit.DealWithIt;

import net.fabricmc.fabric.api.event.registry.DynamicRegistries;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

public interface DealWithItRegistries {

    ResourceKey<Registry<Deck>> DECK = create("deck");
    ResourceKey<Registry<Card>> CARD = create("card");

    private static <T> ResourceKey<Registry<T>> create(String path) {
        return ResourceKey.createRegistryKey(DealWithIt.id(path));
    }

    static void init() {
        DynamicRegistries.registerSynced(CARD, Card.DIRECT_CODEC);
        DynamicRegistries.registerSynced(DECK, Deck.DIRECT_CODEC);
    }
}
