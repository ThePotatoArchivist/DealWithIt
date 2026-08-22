package archives.tater.houseofcards.registry;

import archives.tater.houseofcards.data.Card;
import archives.tater.houseofcards.data.Deck;
import archives.tater.houseofcards.HouseOfCards;

import net.fabricmc.fabric.api.event.registry.DynamicRegistries;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

public interface HouseOfCardsRegistries {
    private static <T> ResourceKey<Registry<T>> create(String path) {
        return ResourceKey.createRegistryKey(HouseOfCards.id(path));
    }

    ResourceKey<Registry<Card>> CARD = create("card");
    ResourceKey<Registry<Deck>> DECK = create("deck");

    static void init() {
        DynamicRegistries.registerSynced(CARD, Card.DIRECT_CODEC);
        DynamicRegistries.registerSynced(DECK, Deck.DIRECT_CODEC);
    }
}
