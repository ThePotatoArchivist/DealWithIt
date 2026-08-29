package archives.tater.dealwithit.client;

import archives.tater.dealwithit.client.datagen.*;
import archives.tater.dealwithit.registry.DealWithItDataPacks;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.resources.Identifier;

public class DealWithItDataGenerator implements DataGeneratorEntrypoint {

    @Override
	public void buildRegistry(RegistrySetBuilder registryBuilder) {
		DeckProviders.PlayingCards.PLAYING_CARDS.buildRegistry(registryBuilder);
		DeckProviders.PlayingCards.JOKERS.buildRegistry(registryBuilder);
		DeckProviders.PlayingCards.COLORED.buildRegistry(registryBuilder);
		DeckProviders.PlayingCards.COLORED_JOKERS.buildRegistry(registryBuilder);
		DeckProviders.UNO.buildRegistry(registryBuilder);
	}

	private static FabricDataGenerator.Pack createPack(FabricDataGenerator fabricDataGenerator, Identifier id) {
		var pack = fabricDataGenerator.createBuiltinResourcePack(id);
		pack.addProvider(PackMetaGen.pack(id));
		return pack;
	}

	private static FabricDataGenerator.Pack createDeckPack(FabricDataGenerator fabricDataGenerator, Identifier id, DeckProvider deckProvider) {
		var pack = createPack(fabricDataGenerator, id);
		pack.addProvider(deckProvider::serverData);
		return pack;
	}

	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		var pack = fabricDataGenerator.createPack();

		pack.addProvider(ModRecipeProvider::new);
		pack.addProvider(DeckTagsProvider::new);

		pack.addProvider(DeckProviders.PlayingCards.PLAYING_CARDS::clientData);
		pack.addProvider(DeckProviders.PlayingCards.JOKERS::clientData);
		pack.addProvider(DeckProviders.PlayingCards.COLORED::clientData);
		pack.addProvider(DeckProviders.PlayingCards.COLORED_JOKERS::clientData);
		pack.addProvider(DeckProviders.UNO::clientData);
		pack.addProvider(ModAtlasProvider::new);
		pack.addProvider(ModModelProvider::new);
		pack.addProvider(ModSoundsProvider::new);
		pack.addProvider(ModLanguageProvider::new);

		createDeckPack(fabricDataGenerator, DealWithItDataPacks.PLAYING_CARDS, DeckProviders.PlayingCards.PLAYING_CARDS);
		createDeckPack(fabricDataGenerator, DealWithItDataPacks.JOKERS, DeckProviders.PlayingCards.JOKERS);
		createDeckPack(fabricDataGenerator, DealWithItDataPacks.COLORED, DeckProviders.PlayingCards.COLORED);
		createDeckPack(fabricDataGenerator, DealWithItDataPacks.COLORED_JOKERS, DeckProviders.PlayingCards.COLORED_JOKERS);
		createDeckPack(fabricDataGenerator, DealWithItDataPacks.UNO, DeckProviders.UNO);
	}
}
