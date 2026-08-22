package archives.tater.houseofcards.client.datagen;

import archives.tater.houseofcards.client.render.CardSpecialRenderer;
import archives.tater.houseofcards.registry.HouseOfCardsBlocks;
import archives.tater.houseofcards.registry.HouseOfCardsComponents;
import archives.tater.houseofcards.registry.HouseOfCardsItems;

import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;

import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.ItemModelUtils;
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
        blockModelGenerators.createParticleOnlyBlock(HouseOfCardsBlocks.CARD_STACK);
    }

    @Override
    public void generateItemModels(ItemModelGenerators itemModelGenerators) {
        var faceDown = specialModel(Identifier.withDefaultNamespace("item/generated"), new CardSpecialRenderer.Unbaked(true));
        var faceUp = specialModel(Identifier.withDefaultNamespace("item/generated"), new CardSpecialRenderer.Unbaked(false));
        itemModelGenerators.itemModelOutput.accept(HouseOfCardsItems.CARD, ItemModelUtils.select(new DisplayContext(),
                conditional(new HasComponent(HouseOfCardsComponents.FACE_DOWN, false),
                        faceDown,
                        faceUp
                ),
                when(ItemDisplayContext.THIRD_PERSON_LEFT_HAND, faceDown),
                when(ItemDisplayContext.THIRD_PERSON_RIGHT_HAND, faceDown),
                when(ItemDisplayContext.ON_SHELF, faceDown)
        ));
    }
}
