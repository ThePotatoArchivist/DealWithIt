package archives.tater.dealwithit.client.datagen;

import archives.tater.dealwithit.DealWithIt;
import archives.tater.dealwithit.data.Deck;
import archives.tater.dealwithit.registry.DealWithItRegistries;

import net.minecraft.resources.ResourceKey;

public interface Decks {
    ResourceKey<Deck> UNO = create("uno");
    ResourceKey<Deck> PLAYING_CARDS = create("playing_cards");
    ResourceKey<Deck> GREEN_PLAYING_CARDS = create("playing_cards_green");
    ResourceKey<Deck> BLUE_PLAYING_CARDS = create("playing_cards_blue");
    ResourceKey<Deck> YELLOW_PLAYING_CARDS = create("playing_cards_yellow");
    ResourceKey<Deck> JOKERS_PLAYING_CARDS = create("playing_cards_jokers");
    ResourceKey<Deck> GREEN_JOKERS_PLAYING_CARDS = create("playing_cards_jokers_green");
    ResourceKey<Deck> BLUE_JOKERS_PLAYING_CARDS = create("playing_cards_jokers_blue");
    ResourceKey<Deck> YELLOW_JOKERS_PLAYING_CARDS = create("playing_cards_jokers_yellow");

    private static ResourceKey<Deck> create(String path) {
        return ResourceKey.create(DealWithItRegistries.DECK, DealWithIt.id(path));
    }
}
