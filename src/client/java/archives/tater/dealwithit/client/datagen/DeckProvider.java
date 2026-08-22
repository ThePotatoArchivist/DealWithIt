package archives.tater.dealwithit.client.datagen;

import archives.tater.dealwithit.data.Card;
import archives.tater.dealwithit.data.Deck;
import archives.tater.dealwithit.data.DeckType;
import archives.tater.dealwithit.registry.DealWithItRegistries;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider.TranslationBuilder;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;

import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;
import static net.minecraft.util.Util.makeDescriptionId;

public abstract class DeckProvider implements FabricDataGenerator.Pack.RegistryDependentFactory<FabricDynamicRegistryProvider> {
    private final List<UnbakedDeckType> deckTypes = new ArrayList<>();
    private final List<UnbakedDeck> decks = new ArrayList<>();

    protected abstract void generate(DeckOutput output);

    public abstract String getName();

    public void bootstrapCards(BootstrapContext<Card> registry) {
        generate(new DeckOutput() {
            @Override
            public DeckTypeBuilder deckType(Identifier id) {
                var translations = new HashMap<Holder.Reference<Card>, String>();
                var cards = new Object2IntLinkedOpenHashMap<Holder<Card>>();
                return new DeckTypeBuilder() {
                    @Override
                    public void addCard(int count, CardInfo info) {
                        var cardId = id.withSuffix("/" + info.path);
                        var card = new Card(Component.translatable(makeDescriptionId("card", cardId)));
                        var holder = registry.register(ResourceKey.create(DealWithItRegistries.CARD, cardId), card);
                        cards.put(holder, count);
                        translations.put(holder, info.translation);
                    }

                    @Override
                    public UnbakedDeckType build() {
                        var type = new UnbakedDeckType(ResourceKey.create(DealWithItRegistries.DECK_TYPE, id), cards, translations, new Ref<>());
                        deckTypes.add(type);
                        return type;
                    }
                };
            }

            @Override
            public void deck(Identifier id, String translation, UnbakedDeckType type) {
                decks.add(new UnbakedDeck(ResourceKey.create(DealWithItRegistries.DECK, id), Component.translatable(makeDescriptionId("deck", id)), translation, type));
            }
        });
    }

    public void bootstrapDeckTypes(BootstrapContext<DeckType> registry) {
        for (var type : deckTypes)
            type.holder.value = registry.register(type.key, new DeckType(type.cards));
    }

    @Override
    public FabricDynamicRegistryProvider create(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        return new FabricDynamicRegistryProvider(output, registriesFuture) {
            @Override
            protected void configure(HolderLookup.Provider registries, Entries entries) {
                for (var type : deckTypes) {
                    for (var card : type.cards.keySet())
                        entries.add((Holder.Reference<Card>) card);
                    entries.add(requireNonNull(type.holder.value));
                }
                for (var deck : decks)
                    entries.add(deck.key, new Deck(deck.description, requireNonNull(deck.type.holder.value)));
            }

            @Override
            public String getName() {
                return DeckProvider.this.getName();
            }
        };
    }

    public void generateTranslations(TranslationBuilder translationBuilder) {
        for (var type : deckTypes)
            type.cardTranslations.forEach((card, translation) -> {
                translationBuilder.add(makeDescriptionId("card", card.key().identifier()), translation);
            });
        for (var deck : decks)
            translationBuilder.add(makeDescriptionId("deck", deck.key.identifier()), deck.translation);
    }

    public static CardInfo card(String path, String translation) {
        return new CardInfo(path, translation);
    }

    public interface DeckOutput {
        DeckTypeBuilder deckType(Identifier id);

        void deck(Identifier id, String translation, UnbakedDeckType type);
    }

    public interface DeckTypeBuilder {
        void addCard(int count, CardInfo info);

        UnbakedDeckType build();

        default DeckTypeBuilder card(int count, CardInfo info) {
            addCard(count, info);
            return this;
        }

        default DeckTypeBuilder card(CardInfo info) {
            return card(1, info);
        }

        default DeckTypeBuilder cards(int count, CardInfo... infos) {
            for (var path : infos)
                card(count, path);
            return this;
        }

        default DeckTypeBuilder cards(CardInfo... infos) {
            return cards(1, infos);
        }

        default DeckTypeBuilder cards(int count, Stream<CardInfo> infos) {
            infos.forEach(path -> card(count, path));
            return this;
        }

        default DeckTypeBuilder cards(Stream<CardInfo> infos) {
            return cards(1, infos);
        }
    }

    public static class Ref<T> { @Nullable T value; }
    public record CardInfo(String path, String translation) {}
    public record UnbakedDeckType(ResourceKey<DeckType> key, Object2IntMap<Holder<Card>> cards, Map<Holder.Reference<Card>, String> cardTranslations, Ref<Holder.Reference<DeckType>> holder) {}
    public record UnbakedDeck(ResourceKey<Deck> key, Component description, String translation, UnbakedDeckType type) {}
}
