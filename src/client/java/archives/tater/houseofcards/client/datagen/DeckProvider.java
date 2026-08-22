package archives.tater.houseofcards.client.datagen;

import archives.tater.houseofcards.Card;
import archives.tater.houseofcards.Deck;
import archives.tater.houseofcards.registry.HouseOfCardsRegistries;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;

import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import static net.minecraft.util.Util.makeDescriptionId;

public abstract class DeckProvider implements RegistrySetBuilder.RegistryBootstrap<Card>, FabricDataGenerator.Pack.RegistryDependentFactory<FabricDynamicRegistryProvider> {
    private final Map<ResourceKey<Deck>, UnbakedDeck> decks = new HashMap<>();

    protected abstract void generate(DeckOutput output);

    public abstract String getName();

    @Override
    public void run(BootstrapContext<Card> registry) {
        generate((id) -> {
            var cards = new Object2IntLinkedOpenHashMap<Holder.Reference<Card>>(); // Linked only for datagen to stay in order
            decks.put(ResourceKey.create(HouseOfCardsRegistries.DECK, id), new UnbakedDeck(
                    Component.translatable(makeDescriptionId("deck", id)),
                    cards
            ));

            return (count, path) -> {
                var card = id.withSuffix("/" + path);
                cards.put(registry.register(ResourceKey.create(HouseOfCardsRegistries.CARD, card), new Card(Component.translatable(makeDescriptionId("card", card)))), count);
            };
        });
    }

    @Override
    public FabricDynamicRegistryProvider create(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        return new FabricDynamicRegistryProvider(output, registriesFuture) {
            @Override
            protected void configure(HolderLookup.Provider registries, Entries entries) {
                decks.forEach((id, unbaked) -> {
                    for (var card : unbaked.cards.keySet())
                        entries.add(card);

                    entries.add(id, new Deck(unbaked.description, Object2IntMaps.unmodifiable(unbaked.cards)));
                });
            }

            @Override
            public String getName() {
                return DeckProvider.this.getName() + " Decks";
            }
        };
    }

    @FunctionalInterface
    public interface DeckOutput {
        DeckBuilder deck(Identifier id);
    }

    @FunctionalInterface
    public interface DeckBuilder {
        void addCard(int count, String path);

        default DeckBuilder card(int count, String path) {
            addCard(count, path);
            return this;
        }

        default DeckBuilder card(String path) {
            return card(1, path);
        }

        default DeckBuilder cards(int count, String... paths) {
            for (var path : paths)
                card(count, path);
            return this;
        }

        default DeckBuilder cards(String... paths) {
            return cards(1, paths);
        }

        default DeckBuilder cards(int count, Stream<String> paths) {
            paths.forEach(path -> card(count, path));
            return this;
        }

        default DeckBuilder cards(Stream<String> paths) {
            return cards(1, paths);
        }
    }

    record UnbakedDeck(Component description, Object2IntMap<Holder.Reference<Card>> cards) {}
}
