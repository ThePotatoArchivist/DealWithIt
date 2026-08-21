package archives.tater.houseofcards.client;

import archives.tater.houseofcards.HouseOfCards;
import archives.tater.houseofcards.client.datagen.DeckProvider;
import archives.tater.houseofcards.registry.HouseOfCardsRegistries;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

import net.minecraft.core.RegistrySetBuilder;

import java.util.stream.Stream;

public class HouseOfCardsDataGenerator implements DataGeneratorEntrypoint {
	public static final DeckProvider PLAYING_CARDS = new DeckProvider() {
		@Override
		protected void generate(DeckOutput output) {
			output.deck(HouseOfCards.id("playing_cards"), Stream.of("ace", "two", "three", "four", "five", "six", "seven", "eight", "nine", "ten", "jack", "queen", "king").flatMap(number ->
					Stream.of("spades", "hearts", "clubs", "diamonds").map(suit -> number + "_" + suit)
			));
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

		pack.addProvider(PLAYING_CARDS);
	}
}
