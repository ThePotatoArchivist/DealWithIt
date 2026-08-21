package archives.tater.houseofcards.client.datagen;

import archives.tater.houseofcards.Card;
import archives.tater.houseofcards.Deck;
import archives.tater.houseofcards.registry.HouseOfCardsRegistries;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

public abstract class DeckProvider implements RegistrySetBuilder.RegistryBootstrap<Card>, FabricDataGenerator.Pack.RegistryDependentFactory<FabricDynamicRegistryProvider> {
    private final Map<ResourceKey<Deck>, UnbakedDeck> decks = new HashMap<>();

    protected abstract void generate(DeckOutput output);

    public abstract String getName();

    @Override
    public void run(BootstrapContext<Card> registry) {
        generate((id, cards) -> {
            var namespace = id.getNamespace();
            decks.put(ResourceKey.create(HouseOfCardsRegistries.DECK, id), new UnbakedDeck(
                    Component.translatable(id.toLanguageKey("deck")),
                    TagKey.create(HouseOfCardsRegistries.CARD, id),
                    cards
                            .map(path -> Identifier.fromNamespaceAndPath(namespace, path))
                            .map(card -> registry.register(ResourceKey.create(HouseOfCardsRegistries.CARD, card), new Card(Component.translatable(card.toLanguageKey("card")))))
                            .toList()
            ));
        });
    }

    @Override
    public FabricDynamicRegistryProvider create(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        return new FabricDynamicRegistryProvider(output, registriesFuture) {
            @Override
            protected void configure(HolderLookup.Provider registries, Entries entries) {
                decks.forEach((id, unbaked) -> {
                    for (var card : unbaked.cards)
                        entries.add(card);

                    entries.add(id, new Deck(unbaked.description, HolderSet.direct(unbaked.cards)));
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
        default void deck(Identifier id, String... cards) {
            deck(id, Arrays.stream(cards));
        }

        void deck(Identifier id, Stream<String> cards);
    }

    record UnbakedDeck(Component description, TagKey<Card> tag, List<Holder.Reference<Card>> cards) {}
}
