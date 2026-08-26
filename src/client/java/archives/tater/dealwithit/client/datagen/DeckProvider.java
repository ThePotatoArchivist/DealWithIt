package archives.tater.dealwithit.client.datagen;

import archives.tater.dealwithit.DealWithIt;
import archives.tater.dealwithit.component.DeckContents;
import archives.tater.dealwithit.data.Card;
import archives.tater.dealwithit.data.CardSet;
import archives.tater.dealwithit.data.Deck;
import archives.tater.dealwithit.data.DeckType;
import archives.tater.dealwithit.registry.DealWithItComponents;
import archives.tater.dealwithit.registry.DealWithItItems;
import archives.tater.dealwithit.registry.DealWithItRegistries;

import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricCodecDataProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider.TranslationBuilder;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;

import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.renderer.item.ClientItem;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStackTemplate;

import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

import static net.minecraft.client.data.models.model.ItemModelUtils.plainModel;
import static net.minecraft.util.Util.makeDescriptionId;

public abstract class DeckProvider implements FabricDataGenerator.Pack.RegistryDependentFactory<DataProvider> {
    private final List<UnbakedCard> cards = new ArrayList<>();
    private final List<UnbakedDeckType> deckTypes = new ArrayList<>();
    private final List<UnbakedDeck> decks = new ArrayList<>();

    public DeckProvider() {
        generate(new DeckOutput() {
            @Override
            public DeckTypeBuilder deckType(Identifier id) {
                var cards = new Object2IntLinkedOpenHashMap<UnbakedCard>();
                return new DeckTypeBuilder() {
                    @Override
                    public void addCard(int count, CardInfo info) {
                        var cardId = id.withSuffix("/" + info.path);
                        var card = new UnbakedCard(ResourceKey.create(DealWithItRegistries.CARD, cardId), Component.translatable(makeDescriptionId("card", cardId)), info.translation);
                        cards.put(card, count);
                        DeckProvider.this.cards.add(card);
                    }

                    @Override
                    public UnbakedDeckType build() {
                        var type = new UnbakedDeckType(ResourceKey.create(DealWithItRegistries.DECK_TYPE, id), cards);
                        deckTypes.add(type);
                        return type;
                    }
                };
            }

            @Override
            public void deck(Identifier id, String translation, UnbakedDeckType type, @Nullable RecipeFactory recipe) {
                decks.add(new UnbakedDeck(ResourceKey.create(DealWithItRegistries.DECK, id), Component.translatable(makeDescriptionId("deck", id)), translation, type, recipe));
            }
        });
    }

    protected abstract void generate(DeckOutput output);

    public abstract String getName();

    public void buildRegistry(RegistrySetBuilder registryBuilder) {
        registryBuilder.add(DealWithItRegistries.CARD, this::bootstrapCards);
        registryBuilder.add(DealWithItRegistries.DECK_TYPE, this::bootstrapDeckTypes);
        registryBuilder.add(DealWithItRegistries.DECK, this::bootstrapDecks);
    }

    public void bootstrapCards(BootstrapContext<Card> registry) {
        for (var card : cards)
            registry.register(card.key, new Card(card.description));
    }

    public void bootstrapDeckTypes(BootstrapContext<DeckType> registry) {
        for (var type : deckTypes) {
            var cards = CardSet.mutable();
            type.cards.forEach((card, amount) ->
                    cards.add(registry.lookup(DealWithItRegistries.CARD).getOrThrow(card.key), amount)
            );
            registry.register(type.key, new DeckType(cards.build()));
        }
    }

    public void bootstrapDecks(BootstrapContext<Deck> registry) {
        for (var deck : decks)
            registry.register(deck.key, new Deck(deck.description, registry.lookup(DealWithItRegistries.DECK_TYPE).getOrThrow(deck.type.key)));
    }

    @Override
    public DataProvider create(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        return new MultiDataProvider(
                getName(),
                new FabricDynamicRegistryProvider(output, registriesFuture) {
                    @Override
                    protected void configure(HolderLookup.Provider registries, Entries entries) {
                        entries.addAll(registries.lookupOrThrow(DealWithItRegistries.CARD));
                        entries.addAll(registries.lookupOrThrow(DealWithItRegistries.DECK_TYPE));
                        entries.addAll(registries.lookupOrThrow(DealWithItRegistries.DECK));
                    }

                    @Override
                    public String getName() {
                        return DeckProvider.this.getName() + " Registries";
                    }
                },
                new FabricModelProvider(output) {
                    @Override
                    public void generateBlockStateModels(BlockModelGenerators blockModelGenerators) {

                    }

                    @Override
                    public void generateItemModels(ItemModelGenerators itemModelGenerators) {
                        for (var deck : decks) {
                            var id = deck.key.identifier().withPrefix("item/" + DealWithIt.MOD_ID + "/card_box/");
                            ModelTemplates.FLAT_ITEM.create(id, TextureMapping.layer0(new Material(id)), itemModelGenerators.modelOutput);
                        }
                    }
                },
                new FabricCodecDataProvider<>(output, registriesFuture, PackOutput.Target.RESOURCE_PACK, "items", ClientItem.CODEC) {
                    @Override
                    public String getName() {
                        return DeckProvider.this.getName() + " Item Models";
                    }

                    @Override
                    protected void configure(BiConsumer<Identifier, ClientItem> provider, HolderLookup.Provider registryLookup) {
                        for (var deck : decks) {
                            var id = deck.key.identifier().withPrefix(DealWithIt.MOD_ID + "/card_box/");
                            provider.accept(id, new ClientItem(plainModel(id.withPrefix("item/")), ClientItem.Properties.DEFAULT));
                        }
                    }
                },
                new FabricRecipeProvider(output, registriesFuture) {
                    @Override
                    protected RecipeProvider createRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
                        return new RecipeProvider(registries, output) {
                            @Override
                            public void buildRecipes() {
                                for (var deck : decks) {
                                    if (deck.recipe == null) continue;
                                    deck.recipe
                                            .createRecipe(this, registries, new ItemStackTemplate(DealWithItItems.CARD_BOX, DataComponentPatch.builder()
                                                .set(DealWithItComponents.DECK_CONTENTS, new DeckContents(registries.getOrThrow(deck.key)))
                                                .build()))
                                            .unlockedBy(getHasName(DealWithItItems.BLANK_CARD_BOX), has(DealWithItItems.BLANK_CARD_BOX))
                                            .save(output, ResourceKey.create(Registries.RECIPE, deck.key.identifier()));
                                }
                            }
                        };
                    }

                    @Override
                    public String getName() {
                        return "";
                    }
                }
        );
    }

    public void generateTranslations(TranslationBuilder translationBuilder) {
        for (var card : cards)
            translationBuilder.add(makeDescriptionId("card", card.key.identifier()), card.translation);
        for (var deck : decks)
            translationBuilder.add(makeDescriptionId("deck", deck.key.identifier()), deck.translation);
    }

    public static CardInfo card(String path, String translation) {
        return new CardInfo(path, translation);
    }

    @FunctionalInterface
    public interface RecipeFactory {
        RecipeBuilder createRecipe(RecipeProvider provider, HolderLookup.Provider registries, ItemStackTemplate result);
    }

    public interface DeckOutput {
        DeckTypeBuilder deckType(Identifier id);

        void deck(Identifier id, String translation, UnbakedDeckType type, @Nullable RecipeFactory recipe);

        default void deck(Identifier id, String translation, UnbakedDeckType type) {
            deck(id, translation, type, null);
        }
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

    public record CardInfo(String path, String translation) {}
    public record UnbakedCard(ResourceKey<Card> key, Component description, String translation) {}
    public record UnbakedDeckType(ResourceKey<DeckType> key, Object2IntMap<UnbakedCard> cards) {}
    public record UnbakedDeck(ResourceKey<Deck> key, Component description, String translation, UnbakedDeckType type, @Nullable RecipeFactory recipe) {}
}
