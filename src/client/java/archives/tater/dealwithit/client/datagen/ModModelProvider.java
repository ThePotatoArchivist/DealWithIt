package archives.tater.dealwithit.client.datagen;

import archives.tater.dealwithit.client.render.CardSpecialRenderer;
import archives.tater.dealwithit.client.render.ComponentModel;
import archives.tater.dealwithit.registry.DealWithItBlocks;
import archives.tater.dealwithit.registry.DealWithItComponents;
import archives.tater.dealwithit.registry.DealWithItItems;

import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;

import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.renderer.item.properties.conditional.HasComponent;
import net.minecraft.client.renderer.item.properties.select.DisplayContext;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemDisplayContext;

import static net.minecraft.client.data.models.model.ItemModelUtils.*;

public class ModModelProvider extends FabricModelProvider {
    public ModModelProvider(FabricPackOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators blockModelGenerators) {
        blockModelGenerators.createParticleOnlyBlock(DealWithItBlocks.CARD_STACK);
    }

    @Override
    public void generateItemModels(ItemModelGenerators itemModelGenerators) {
        var faceDown = specialModel(Identifier.withDefaultNamespace("item/generated"), new CardSpecialRenderer.Unbaked(true));
        var faceUp = specialModel(Identifier.withDefaultNamespace("item/generated"), new CardSpecialRenderer.Unbaked(false));
        itemModelGenerators.itemModelOutput.accept(DealWithItItems.CARD, ItemModelUtils.select(new DisplayContext(),
                conditional(new HasComponent(DealWithItComponents.FACE_DOWN, false),
                        faceDown,
                        faceUp
                ),
                when(ItemDisplayContext.THIRD_PERSON_LEFT_HAND, faceDown),
                when(ItemDisplayContext.THIRD_PERSON_RIGHT_HAND, faceDown),
                when(ItemDisplayContext.ON_SHELF, faceDown)
        ));

        var blank = plainModel(itemModelGenerators.createFlatItemModel(DealWithItItems.BLANK_CARD_BOX, ModelTemplates.FLAT_ITEM));
        itemModelGenerators.itemModelOutput.accept(DealWithItItems.BLANK_CARD_BOX, blank);
        itemModelGenerators.itemModelOutput.accept(DealWithItItems.CARD_BOX, new ComponentModel.Unbaked<>(DealWithItComponents.DECK_CONTENTS, "dealwithit/card_box/", blank));
    }
}
