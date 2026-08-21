package archives.tater.houseofcards.client;

import archives.tater.houseofcards.HouseOfCards;
import archives.tater.houseofcards.client.datagen.DeckProvider;
import archives.tater.houseofcards.registry.HouseOfCardsRegistries;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

import net.minecraft.core.RegistrySetBuilder;

public class HouseOfCardsDataGenerator implements DataGeneratorEntrypoint {
	public static final DeckProvider TEST = new DeckProvider() {
		@Override
		protected void generate() {
			deck(HouseOfCards.id("test"), "card1", "card2");
		}

		@Override
		public String getName() {
			return "Test";
		}
	};

	@Override
	public void buildRegistry(RegistrySetBuilder registryBuilder) {
		registryBuilder.add(HouseOfCardsRegistries.CARD, TEST::bootstrapCards);
	}

	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		var pack = fabricDataGenerator.createPack();

		pack.addProvider(TEST::registryProvider);
		pack.addProvider(TEST::tagsProvider);
	}
}
