package archives.tater.dealwithit.client;

import archives.tater.dealwithit.DealWithIt;
import archives.tater.dealwithit.client.datagen.*;
import archives.tater.dealwithit.client.mixin.ShapedRecipeBuilderAccessor;
import archives.tater.dealwithit.registry.DealWithItItems;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.stream.Stream;

import static archives.tater.dealwithit.Util.snakeToTitleCase;
import static org.apache.commons.lang3.StringUtils.capitalize;

public class DealWithItDataGenerator implements DataGeneratorEntrypoint {
	public static final DeckProvider PLAYING_CARDS = new DeckProvider() {
		@Override
		protected void generate(DeckOutput output) {
			var playingCards = output.deckType(DealWithIt.id("playing_cards"))
					.cards(Stream.of("ace", "two", "three", "four", "five", "six", "seven", "eight", "nine", "ten", "jack", "queen", "king").flatMap(number ->
							Stream.of("spades", "hearts", "clubs", "diamonds").map(suit -> card(number + "_" + suit, capitalize(number) + " of " + capitalize(suit)))
					))
					.build();

			output.deck(DealWithIt.id("playing_cards_red"), "Playing Cards (Red)", playingCards, dyeBox(Items.DYE.red()));
			output.deck(DealWithIt.id("playing_cards_green"), "Playing Cards (Green)", playingCards, dyeBox(Items.DYE.green()));
			output.deck(DealWithIt.id("playing_cards_blue"), "Playing Cards (Blue)", playingCards, dyeBox(Items.DYE.blue()));
			output.deck(DealWithIt.id("playing_cards_yellow"), "Playing Cards (Yellow)", playingCards, dyeBox(Items.DYE.yellow()));
		}

		private static RecipeFactory dyeBox(Item dye) {
			return (provider, _, result) -> provider.shapeless(RecipeCategory.MISC, result)
					.requires(DealWithItItems.BLANK_CARD_BOX)
					.requires(dye);
		}

		@Override
		public String getName() {
			return "Playing Cards";
		}
	};

	public static final DeckProvider UNO = new DeckProvider() {
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

	@Override
	public void buildRegistry(RegistrySetBuilder registryBuilder) {
		PLAYING_CARDS.buildRegistry(registryBuilder);
		UNO.buildRegistry(registryBuilder);
	}

	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		var pack = fabricDataGenerator.createPack();

		pack.addProvider(PLAYING_CARDS);
		pack.addProvider(UNO);
		pack.addProvider(ModItemTagProvider::new);
		pack.addProvider(ModRecipeProvider::new);

		pack.addProvider(ModAtlasProvider::new);
		pack.addProvider(ModModelProvider::new);
		pack.addProvider(ModSoundsProvider::new);
		pack.addProvider(ModLanguageProvider::new);
	}
}
