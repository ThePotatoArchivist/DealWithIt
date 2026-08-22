package archives.tater.dealwithit.client.datagen;

import archives.tater.dealwithit.data.Card;
import archives.tater.dealwithit.data.Deck;
import archives.tater.dealwithit.registry.DealWithItRegistries;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider.TranslationBuilder;

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
        generate((id, translation) -> {
            var cards = new Object2IntLinkedOpenHashMap<Holder.Reference<Card>>(); // Linked only for datagen to stay in order
            var cardTranslations = new HashMap<Holder.Reference<Card>, String>();
            decks.put(ResourceKey.create(DealWithItRegistries.DECK, id), new UnbakedDeck(
                    Component.translatable(makeDescriptionId("deck", id)),
                    translation,
                    cards,
                    cardTranslations
            ));

            return (count, info) -> {
                var cardId = id.withSuffix("/" + info.path);
                var card = registry.register(ResourceKey.create(DealWithItRegistries.CARD, cardId), new Card(Component.translatable(makeDescriptionId("card", cardId))));
                cards.put(card, count);
                cardTranslations.put(card, info.translation);
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

    public void generateTranslations(TranslationBuilder translationBuilder) {
        decks.forEach((key, unbaked) -> {
            translationBuilder.add(makeDescriptionId("deck", key.identifier()), unbaked.translation);
            unbaked.cardTranslations.forEach((card, translation) -> {
                translationBuilder.add(makeDescriptionId("card", card.key().identifier()), translation);
            });
        });
    }

    public static CardInfo card(String path, String translation) {
        return new CardInfo(path, translation);
    }

    @FunctionalInterface
    public interface DeckOutput {
        DeckBuilder deck(Identifier id, String translation);
    }

    @FunctionalInterface
    public interface DeckBuilder {
        void addCard(int count, CardInfo info);

        default DeckBuilder card(int count, CardInfo info) {
            addCard(count, info);
            return this;
        }

        default DeckBuilder card(CardInfo info) {
            return card(1, info);
        }

        default DeckBuilder cards(int count, CardInfo... infos) {
            for (var path : infos)
                card(count, path);
            return this;
        }

        default DeckBuilder cards(CardInfo... infos) {
            return cards(1, infos);
        }

        default DeckBuilder cards(int count, Stream<CardInfo> infos) {
            infos.forEach(path -> card(count, path));
            return this;
        }

        default DeckBuilder cards(Stream<CardInfo> infos) {
            return cards(1, infos);
        }
    }

    public record CardInfo(String path, String translation) {}
    record UnbakedDeck(Component description, String translation, Object2IntMap<Holder.Reference<Card>> cards, Map<Holder.Reference<Card>, String> cardTranslations) {}
}
