package archives.tater.dealwithit.client.datagen;

import archives.tater.dealwithit.DealWithIt;
import archives.tater.dealwithit.client.mixin.ShapedRecipeBuilderAccessor;
import archives.tater.dealwithit.data.Card;
import archives.tater.dealwithit.data.DeckType;
import archives.tater.dealwithit.registry.DealWithItItems;
import archives.tater.dealwithit.registry.DealWithItRegistries;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static archives.tater.dealwithit.Util.mapEntries;
import static archives.tater.dealwithit.Util.snakeToTitleCase;
import static java.util.function.Function.identity;
import static net.minecraft.util.Util.makeEnumMap;
import static org.apache.commons.lang3.StringUtils.capitalize;

public interface DeckProviders {
    interface PlayingCards {
        ResourceKey<DeckType> TYPE = ResourceKey.create(DealWithItRegistries.DECK_TYPE, DealWithIt.id("playing_cards"));

        enum Number {
            ACE("ace"),
            TWO("two"),
            THREE("three"),
            FOUR("four"),
            FIVE("five"),
            SIX("six"),
            SEVEN("seven"),
            EIGHT("eight"),
            NINE("nine"),
            TEN("ten"),
            JACK("jack"),
            QUEEN("queen"),
            KING("king");

            public final String name;

            Number(String name) {
                this.name = name;
            }
        }

        enum Suit {
            SPADES("spades"),
            HEARTS("hearts"),
            CLUBS("clubs"),
            DIAMONDS("diamonds");

            public final String name;

            Suit(String name) {
                this.name = name;
            }
        }

        Map<Number, Map<Suit, ResourceKey<Card>>> KEYS = makeEnumMap(Number.class, number ->
                makeEnumMap(Suit.class, suit ->
                        ResourceKey.create(DealWithItRegistries.CARD, DealWithIt.id("playing_cards/" + number.name + "_" + suit.name))
                )
        );

        DeckProvider PLAYING_CARDS = new DeckProvider() {
            @Override
            protected void generate(DeckOutput output) {
                output.deckType(TYPE)
                        .cards(mapEntries(KEYS, (number, map) ->
                                mapEntries(map, (suit, key) ->
                                        output.card(key, capitalize(number.name) + " of " + capitalize(suit.name))
                                )
                        ).flatMap(identity()))
                        .build();

                output.deck(DealWithIt.id("playing_cards"), "Playing Cards", TYPE, dyeBox(Items.DYE.red()));
            }

            @Override
            public String getName() {
                return "Playing Cards";
            }
        };

        DeckProvider JOKERS = new DeckProvider() {
            @Override
            protected void generate(DeckOutput output) {
                output.deckType(TYPE)
                        .cards(KEYS.values().stream().flatMap(map -> map.values().stream()))
                        .cards(List.of(
                                output.card(DealWithIt.id("playing_cards/red_joker"), "Red Joker"),
                                output.card(DealWithIt.id("playing_cards/black_joker"), "Black Joker")
                        ))
                        .build();
            }

            @Override
            public String getName() {
                return "Jokers";
            }
        };

        DeckProvider NERTZ = new DeckProvider() {
            @Override
            protected void generate(DeckOutput output) {
                output.deck(DealWithIt.id("playing_cards_green"), "Playing Cards (Green)", TYPE, dyeBox(Items.DYE.green()));
                output.deck(DealWithIt.id("playing_cards_blue"), "Playing Cards (Blue)", TYPE, dyeBox(Items.DYE.blue()));
                output.deck(DealWithIt.id("playing_cards_yellow"), "Playing Cards (Yellow)", TYPE, dyeBox(Items.DYE.yellow()));
            }

            @Override
            public String getName() {
                return "Nertz";
            }
        };
    }

    DeckProvider UNO = new DeckProvider() {
        @Override
        protected void generate(DeckOutput output) {
            var uno = output.deckType(DealWithIt.id("uno"))
                    .cards(2, Stream.of( "one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "skip", "draw_two", "reverse").flatMap(number ->
                            Stream.of("red", "yellow", "green", "blue").map(color -> output.card(DealWithIt.id("uno/" + color + "_" + number), capitalize(color) + " " + snakeToTitleCase(number)))
                    ))
                    .cards(Stream.of("red", "yellow", "green", "blue").map(color -> output.card(DealWithIt.id("uno/" + color + "_zero"), capitalize(color) + " Zero")))
                    .cards(4, List.of(
                            output.card(DealWithIt.id("uno/wild"), "Wild"),
                            output.card(DealWithIt.id("uno/wild_draw_4"), "Wild Draw 4")
                    ))
                    .build();

            output.deck(DealWithIt.id("uno"), "Uno", uno, (_, registries, result) ->
                    ShapedRecipeBuilderAccessor.createShapedRecipeBuilder(registries.lookupOrThrow(Registries.ITEM), RecipeCategory.MISC, result)
                            .pattern(" R ")
                            .pattern("Y#G")
                            .pattern(" B ")
                            .define('R', Items.DYE.red())
                            .define('G', Items.DYE.green())
                            .define('B', Items.DYE.blue())
                            .define('Y', Items.DYE.yellow())
                            .define('#', DealWithItItems.BLANK_CARD_BOX)
            );
        }

        @Override
        public String getName() {
            return "Uno";
        }
    };

    private static DeckProvider.ItemRecipeFactory dyeBox(Item dye) {
        return (provider, _, result) -> provider.shapeless(RecipeCategory.MISC, result)
                .requires(DealWithItItems.BLANK_CARD_BOX)
                .requires(dye);
    }
}
