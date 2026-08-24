package archives.tater.dealwithit.client;

import archives.tater.dealwithit.DealWithIt;
import archives.tater.dealwithit.client.datagen.DeckProvider;
import archives.tater.dealwithit.client.datagen.ModAtlasProvider;
import archives.tater.dealwithit.client.datagen.ModItemTagProvider;
import archives.tater.dealwithit.client.datagen.ModModelProvider;
import archives.tater.dealwithit.component.DeckContents;
import archives.tater.dealwithit.registry.DealWithItItems;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;

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

			output.deck(DealWithIt.id("playing_cards_red"), "Playing Cards (Red)", playingCards);
			output.deck(DealWithIt.id("playing_cards_green"), "Playing Cards (Green)", playingCards);
			output.deck(DealWithIt.id("playing_cards_blue"), "Playing Cards (Blue)", playingCards);
			output.deck(DealWithIt.id("playing_cards_yellow"), "Playing Cards (Yellow)", playingCards);
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
					.cards(2, Stream.of("zero", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "skip", "draw_two", "reverse").flatMap(number ->
							Stream.of("red", "yellow", "green", "blue").map(color -> card(color + "_" + number, capitalize(color) + " " + snakeToTitleCase(number)))
					))
					.cards(4, card("wild", "Wild"), card("wild_draw_4", "Wild Draw 4"))
					.build();

			output.deck(DealWithIt.id("uno"), "Uno", uno);
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

		pack.addProvider(ModAtlasProvider::new);
		pack.addProvider(ModModelProvider::new);
		pack.addProvider((output, registriesFuture) -> new FabricLanguageProvider(output, registriesFuture) {
			@Override
			public void generateTranslations(HolderLookup.Provider registryLookup, TranslationBuilder translationBuilder) {
				PLAYING_CARDS.generateTranslations(translationBuilder);
				UNO.generateTranslations(translationBuilder);

				translationBuilder.add(DealWithItItems.CARD, "Card");
				translationBuilder.add(DealWithItItems.CARD_BOX, "Card Box");
				translationBuilder.add(DeckContents.FILL, "%s/%s");
			}
		});
	}
}
