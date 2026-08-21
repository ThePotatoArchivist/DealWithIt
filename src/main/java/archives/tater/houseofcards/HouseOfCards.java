package archives.tater.houseofcards;

import archives.tater.houseofcards.registry.HouseOfCardsComponents;
import archives.tater.houseofcards.registry.HouseOfCardsItems;
import archives.tater.houseofcards.registry.HouseOfCardsRegistries;

import net.fabricmc.api.ModInitializer;

import net.minecraft.resources.Identifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HouseOfCards implements ModInitializer {
	public static final String MOD_ID = "houseofcards";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		HouseOfCardsRegistries.init();
		HouseOfCardsComponents.init();
		HouseOfCardsItems.init();
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
