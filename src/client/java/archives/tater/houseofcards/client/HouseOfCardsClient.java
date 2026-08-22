package archives.tater.houseofcards.client;

import archives.tater.houseofcards.HouseOfCards;
import archives.tater.houseofcards.client.render.CardSpecialRenderer;
import archives.tater.houseofcards.registry.HouseOfCardsComponents;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.item.v1.ItemComponentTooltipProviderRegistry;

import net.minecraft.client.renderer.special.SpecialModelRenderers;

public class HouseOfCardsClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		HouseOfCardsAtlases.init();

		SpecialModelRenderers.ID_MAPPER.put(HouseOfCards.id("card"), CardSpecialRenderer.Unbaked.CODEC);

		ItemComponentTooltipProviderRegistry.addFirst(HouseOfCardsComponents.DECK_CONTENTS);
		ItemComponentTooltipProviderRegistry.addFirst(HouseOfCardsComponents.CARD);

	}
}