package archives.tater.houseofcards.client;

import archives.tater.houseofcards.HouseOfCards;
import archives.tater.houseofcards.client.datagen.DeckProvider;
import archives.tater.houseofcards.client.datagen.ModAtlasProvider;
import archives.tater.houseofcards.client.datagen.ModModelProvider;
import archives.tater.houseofcards.component.DeckContents;
import archives.tater.houseofcards.registry.HouseOfCardsItems;
import archives.tater.houseofcards.registry.HouseOfCardsRegistries;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;

import java.util.stream.Stream;

import static archives.tater.houseofcards.Util.snakeToTitleCase;
import static org.apache.commons.lang3.StringUtils.capitalize;

public class HouseOfCardsDataGenerator implements DataGeneratorEntrypoint {
	public static final DeckProvider PLAYING_CARDS = new DeckProvider() {
		@Override
		protected void generate(DeckOutput output) {
			output.deck(HouseOfCards.id("playing_cards"), "Playing Cards")
					.cards(Stream.of("ace", "two", "three", "four", "five", "six", "seven", "eight", "nine", "ten", "jack", "queen", "king").flatMap(number ->
							Stream.of("spades", "hearts", "clubs", "diamonds").map(suit -> card(number + "_" + suit, capitalize(number) + " of " + capitalize(suit)))
					));

			output.deck(HouseOfCards.id("uno"), "Uno")
					.cards(2, Stream.of("zero", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "skip", "draw_two", "reverse").flatMap(number ->
							Stream.of("red", "yellow", "green", "blue").map(color -> card(color + "_" + number, capitalize(color) + " " + snakeToTitleCase(number)))
					))
					.cards(4, card("wild", "Wild"), card("wild_draw_4", "Wild Draw 4"));
		}

		@Override
		public String getName() {
			return "Test";
		}
	};

	@Override
	public void buildRegistry(RegistrySetBuilder registryBuilder) {
		registryBuilder.add(HouseOfCardsRegistries.CARD, PLAYING_CARDS);
	}

	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		var pack = fabricDataGenerator.createPack();

		pack.addProvider(ModAtlasProvider::new);
		pack.addProvider(PLAYING_CARDS);
		pack.addProvider(ModModelProvider::new);
		pack.addProvider((output, registriesFuture) -> new FabricLanguageProvider(output, registriesFuture) {
			@Override
			public void generateTranslations(HolderLookup.Provider registryLookup, TranslationBuilder translationBuilder) {
				PLAYING_CARDS.generateTranslations(translationBuilder);

				translationBuilder.add(HouseOfCardsItems.CARD, "Card");
				translationBuilder.add(HouseOfCardsItems.CARD_BOX, "Card Box");
				translationBuilder.add(DeckContents.FILL, "%s/%s");
			}
		});
	}
}
