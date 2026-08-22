package archives.tater.houseofcards.client;

import archives.tater.houseofcards.registry.HouseOfCardsComponents;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.item.v1.ItemComponentTooltipProviderRegistry;

public class HouseOfCardsClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.

		ItemComponentTooltipProviderRegistry.addFirst(HouseOfCardsComponents.DECK_CONTENTS);
		ItemComponentTooltipProviderRegistry.addFirst(HouseOfCardsComponents.CARD);
	}
}