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
import net.minecraft.core.HolderGetter;
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
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

import static net.minecraft.client.data.models.model.ItemModelUtils.plainModel;
import static net.minecraft.util.Util.makeDescriptionId;

public abstract class DeckProvider {
    private final List<UnbakedCard> cards = new ArrayList<>();
    private final List<UnbakedDeckType> deckTypes = new ArrayList<>();
    private final List<UnbakedDeck> decks = new ArrayList<>();

    public DeckProvider() {
        generate(new DeckOutput() {
            @Override
            public DeckTypeBuilder deckType(ResourceKey<DeckType> key) {
                var cards = new Object2IntLinkedOpenHashMap<ResourceKey<Card>>();
                return new DeckTypeBuilder() {
                    @Override
                    public DeckTypeBuilder card(int count, ResourceKey<Card> card) {
                        cards.put(card, count);
                        return this;
                    }

                    @Override
                    public ResourceKey<DeckType> build() {
                        var type = new UnbakedDeckType(key, cards);
                        deckTypes.add(type);
                        return key;
                    }
                };
            }

            @Override
            public void deck(Identifier id, String translation, ResourceKey<DeckType> type, DeckProvider.@Nullable ItemRecipeFactory recipe) {
                decks.add(new UnbakedDeck(ResourceKey.create(DealWithItRegistries.DECK, id), Component.translatable(makeDescriptionId("deck", id)), translation, type, recipe));
            }

            @Override
            public ResourceKey<Card> card(ResourceKey<Card> key, String translation) {
                cards.add(new UnbakedCard(key, Component.translatable(makeDescriptionId("card", key.identifier())), translation));
                return key;
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
            registry.register(card.key, card.bake());
    }

    public void bootstrapDeckTypes(BootstrapContext<DeckType> registry) {
        var cardRegistry = registry.lookup(DealWithItRegistries.CARD);
        for (var type : deckTypes)
            registry.register(type.key, type.bake(cardRegistry));
    }

    public void bootstrapDecks(BootstrapContext<Deck> registry) {
        var deckTypeRegistry = registry.lookup(DealWithItRegistries.DECK_TYPE);
        for (var deck : decks)
            registry.register(deck.key, deck.bake(deckTypeRegistry));
    }

    public DataProvider serverData(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        return new MultiDataProvider(
                getName(),
                new FabricDynamicRegistryProvider(output, registriesFuture) {
                    @Override
                    protected void configure(HolderLookup.Provider registries, Entries entries) {
                        for (var card : cards)
                            entries.add(card.key, card.bake());
                        for (var deckType : deckTypes)
                            entries.add(deckType.key, deckType.bake(registries.lookupOrThrow(DealWithItRegistries.CARD)));
                        for (var deck : decks)
                            entries.add(deck.key, deck.bake(registries.lookupOrThrow(DealWithItRegistries.DECK_TYPE)));
                    }

                    @Override
                    public String getName() {
                        return DeckProvider.this.getName() + " Registries";
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

    public DataProvider clientData(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        return new MultiDataProvider(getName(),
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

                    @Override
                    public String getName() {
                        return DeckProvider.this.getName() + " " + super.getName();
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
                }
        );
    }

    public void generateTranslations(TranslationBuilder translationBuilder) {
        for (var card : cards)
            translationBuilder.add(makeDescriptionId("card", card.key.identifier()), card.translation);
        for (var deck : decks)
            translationBuilder.add(makeDescriptionId("deck", deck.key.identifier()), deck.translation);
    }

    @FunctionalInterface
    public interface ItemRecipeFactory {
        RecipeBuilder createRecipe(RecipeProvider provider, HolderLookup.Provider registries, ItemStackTemplate result);
    }

    public interface DeckOutput {
        DeckTypeBuilder deckType(ResourceKey<DeckType> key);

        default DeckTypeBuilder deckType(Identifier id) {
            return deckType(ResourceKey.create(DealWithItRegistries.DECK_TYPE, id));
        }

        void deck(Identifier id, String translation, ResourceKey<DeckType> type, DeckProvider.@Nullable ItemRecipeFactory recipe);

        default void deck(Identifier id, String translation, ResourceKey<DeckType> type) {
            deck(id, translation, type, null);
        }

        ResourceKey<Card> card(ResourceKey<Card> key, String translation);

        default ResourceKey<Card> card(Identifier id, String translation) {
            return card(ResourceKey.create(DealWithItRegistries.CARD, id), translation);
        }
    }

    public interface DeckTypeBuilder {
        ResourceKey<DeckType> build();

        DeckTypeBuilder card(int count, ResourceKey<Card> card);

        default DeckTypeBuilder card(ResourceKey<Card> info) {
            return card(1, info);
        }

        default DeckTypeBuilder cards(int count, Collection<ResourceKey<Card>> cards) {
            for (var card : cards)
                card(count, card);
            return this;
        }

        default DeckTypeBuilder cards(Collection<ResourceKey<Card>> cards) {
            return cards(1, cards);
        }

        default DeckTypeBuilder cards(int count, Stream<ResourceKey<Card>> cards) {
            cards.forEach(path -> card(count, path));
            return this;
        }

        default DeckTypeBuilder cards(Stream<ResourceKey<Card>> cards) {
            return cards(1, cards);
        }
    }

    private record UnbakedCard(ResourceKey<Card> key, Component description, String translation) {
        Card bake() {
            return new Card(description);
        }
    }

    private record UnbakedDeckType(ResourceKey<DeckType> key, Object2IntMap<ResourceKey<Card>> cards) {
        DeckType bake(HolderGetter<Card> cardRegistry) {
            var cards = CardSet.mutable();
            this.cards.forEach((card, amount) ->
                    cards.add(cardRegistry.getOrThrow(card), amount)
            );
            return new DeckType(cards.build());
        }
    }

    private record UnbakedDeck(ResourceKey<Deck> key, Component description, String translation, ResourceKey<DeckType> type, DeckProvider.@Nullable ItemRecipeFactory recipe) {
        Deck bake(HolderGetter<DeckType> deckTypeRegistry) {
            return new Deck(description, deckTypeRegistry.getOrThrow(type));
        }
    }
}
