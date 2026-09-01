package archives.tater.dealwithit.client;

import archives.tater.dealwithit.DealWithIt;
import archives.tater.dealwithit.client.atlas.CartesianComposite;
import archives.tater.dealwithit.client.mixin.SpriteSourcesAccessor;
import archives.tater.dealwithit.client.render.block.entity.CardStackRenderer;
import archives.tater.dealwithit.client.render.item.special.CardSpecialRenderer;
import archives.tater.dealwithit.client.render.item.special.CardStackCountSpecialRenderer;
import archives.tater.dealwithit.client.render.item.special.CardStackSpecialRenderer;
import archives.tater.dealwithit.client.render.item.model.ComponentModel;
import archives.tater.dealwithit.client.render.item.property.CardStackCount;
import archives.tater.dealwithit.registry.DealWithItBlockEntities;
import archives.tater.dealwithit.registry.DealWithItComponents;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.item.v1.ItemComponentTooltipProviderRegistry;

import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.item.ItemModels;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperties;
import net.minecraft.client.renderer.special.SpecialModelRenderers;

public class DealWithItClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		DealWithItAtlases.init();

		BlockEntityRenderers.register(DealWithItBlockEntities.CARD_STACK, CardStackRenderer::new);

		RangeSelectItemModelProperties.ID_MAPPER.put(DealWithIt.id("card_stack_count"), CardStackCount.CODEC);
		ItemModels.ID_MAPPER.put(DealWithIt.id("component"), ComponentModel.Unbaked.CODEC);
		SpecialModelRenderers.ID_MAPPER.put(DealWithIt.id("card"), CardSpecialRenderer.Unbaked.CODEC);
		SpecialModelRenderers.ID_MAPPER.put(DealWithIt.id("card_stack"), CardStackSpecialRenderer.Unbaked.CODEC);
		SpecialModelRenderers.ID_MAPPER.put(DealWithIt.id("card_stack_count"), CardStackCountSpecialRenderer.Unbaked.CODEC);
		SpriteSourcesAccessor.getID_MAPPER().put(DealWithIt.id("cartesian_composite"), CartesianComposite.CODEC);

		ItemComponentTooltipProviderRegistry.addFirst(DealWithItComponents.DECK_CONTENTS);
		ItemComponentTooltipProviderRegistry.addFirst(DealWithItComponents.CARD);

	}
}