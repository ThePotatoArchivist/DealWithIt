package archives.tater.dealwithit.client;

import archives.tater.dealwithit.DealWithIt;
import archives.tater.dealwithit.client.render.CardSpecialRenderer;
import archives.tater.dealwithit.client.render.CardStackRenderer;
import archives.tater.dealwithit.registry.DealWithItBlockEntities;
import archives.tater.dealwithit.registry.DealWithItComponents;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.item.v1.ItemComponentTooltipProviderRegistry;

import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.special.SpecialModelRenderers;

public class DealWithItClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		DealWithItAtlases.init();

		BlockEntityRenderers.register(DealWithItBlockEntities.CARD_STACK, CardStackRenderer::new);

		SpecialModelRenderers.ID_MAPPER.put(DealWithIt.id("card"), CardSpecialRenderer.Unbaked.CODEC);

		ItemComponentTooltipProviderRegistry.addFirst(DealWithItComponents.DECK_CONTENTS);
		ItemComponentTooltipProviderRegistry.addFirst(DealWithItComponents.CARD);

	}
}