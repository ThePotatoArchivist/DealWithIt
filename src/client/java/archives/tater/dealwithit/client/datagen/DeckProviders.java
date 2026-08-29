package archives.tater.dealwithit.client.datagen;

import archives.tater.dealwithit.DealWithIt;
import archives.tater.dealwithit.client.mixin.ShapedRecipeBuilderAccessor;
import archives.tater.dealwithit.data.DeckType;
import archives.tater.dealwithit.registry.DealWithItItems;
import archives.tater.dealwithit.registry.DealWithItRegistries;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.stream.Stream;

import static archives.tater.dealwithit.Util.snakeToTitleCase;
import static org.apache.commons.lang3.StringUtils.capitalize;

public interface DeckProviders {
    ResourceKey<DeckType> PLAYING_CARDS_TYPE = ResourceKey.create(DealWithItRegistries.DECK_TYPE, DealWithIt.id("playing_cards"));

    DeckProvider PLAYING_CARDS = new DeckProvider() {
        @Override
        protected void generate(DeckOutput output) {
            output.deckType(PLAYING_CARDS_TYPE)
                    .cards(Stream.of("ace", "two", "three", "four", "five", "six", "seven", "eight", "nine", "ten", "jack", "queen", "king").flatMap(number ->
                            Stream.of("spades", "hearts", "clubs", "diamonds").map(suit -> card(number + "_" + suit, capitalize(number) + " of " + capitalize(suit)))
                    ))
                    .build();

            output.deck(DealWithIt.id("playing_cards"), "Playing Cards", PLAYING_CARDS_TYPE, dyeBox(Items.DYE.red()));
        }

        @Override
        public String getName() {
            return "Playing Cards";
        }
    };

    DeckProvider NERTZ = new DeckProvider() {
        @Override
        protected void generate(DeckOutput output) {
            output.deck(DealWithIt.id("playing_cards_green"), "Playing Cards (Green)", PLAYING_CARDS_TYPE, dyeBox(Items.DYE.green()));
            output.deck(DealWithIt.id("playing_cards_blue"), "Playing Cards (Blue)", PLAYING_CARDS_TYPE, dyeBox(Items.DYE.blue()));
            output.deck(DealWithIt.id("playing_cards_yellow"), "Playing Cards (Yellow)", PLAYING_CARDS_TYPE, dyeBox(Items.DYE.yellow()));
        }

        @Override
        public String getName() {
            return "Nertz";
        }
    };

    DeckProvider UNO = new DeckProvider() {
        @Override
        protected void generate(DeckOutput output) {
            var uno = output.deckType(DealWithIt.id("uno"))
                    .cards(2, Stream.of( "one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "skip", "draw_two", "reverse").flatMap(number ->
                            Stream.of("red", "yellow", "green", "blue").map(color -> card(color + "_" + number, capitalize(color) + " " + snakeToTitleCase(number)))
                    ))
                    .cards(Stream.of("red", "yellow", "green", "blue").map(color -> card(color + "_zero", capitalize(color) + " Zero")))
                    .cards(4, card("wild", "Wild"), card("wild_draw_4", "Wild Draw 4"))
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
