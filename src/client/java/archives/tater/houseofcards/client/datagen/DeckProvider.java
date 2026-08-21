package archives.tater.houseofcards.client.datagen;

import archives.tater.houseofcards.Card;
import archives.tater.houseofcards.Deck;
import archives.tater.houseofcards.registry.HouseOfCardsRegistries;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;

import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

public abstract class DeckProvider {
    private final Map<ResourceKey<Deck>, UnbakedDeck> decks = new HashMap<>();
    private @Nullable BootstrapContext<Card> cardContext;

    protected final void deck(Identifier id, String... cards) {
        deck(id, Arrays.stream(cards));
    }

    protected final void deck(Identifier id, Stream<String> cards) {
        var namespace = id.getNamespace();
        var context = requireNonNull(cardContext);
        decks.put(ResourceKey.create(HouseOfCardsRegistries.DECK, id), new UnbakedDeck(
                Component.translatable(id.toLanguageKey("deck")),
                TagKey.create(HouseOfCardsRegistries.CARD, id),
                cards
                        .map(path -> Identifier.fromNamespaceAndPath(namespace, path))
                        .map(card -> context.register(ResourceKey.create(HouseOfCardsRegistries.CARD, card), new Card(Component.translatable(card.toLanguageKey("card")))))
                        .toList()
        ));
    }

    protected abstract void generate();

    public abstract String getName();

    public final void bootstrapCards(BootstrapContext<Card> context) {
        cardContext = context;
        generate();
    }

    public final FabricDynamicRegistryProvider registryProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        return new FabricDynamicRegistryProvider(output, registriesFuture) {
            @Override
            protected void configure(HolderLookup.Provider registries, Entries entries) {
                decks.forEach((id, unbaked) -> {
                    for (var card : unbaked.cards)
                        entries.add(card);

                    entries.add(id, new Deck(unbaked.description, registries.getOrThrow(unbaked.tag)));
                });
            }

            @Override
            public String getName() {
                return DeckProvider.this.getName() + " Decks";
            }
        };
    }

    public final FabricTagsProvider<Card> tagsProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        return new FabricTagsProvider<>(output, HouseOfCardsRegistries.CARD, registriesFuture) {
            @Override
            protected void addTags(HolderLookup.Provider registries) {
                for (var unbaked : decks.values())
                    builder(unbaked.tag)
                            .addAll(unbaked.cards.stream().map(Holder.Reference::key));
            }
        };
    }

    record UnbakedDeck(Component description, TagKey<Card> tag, List<Holder.Reference<Card>> cards) {}
}
