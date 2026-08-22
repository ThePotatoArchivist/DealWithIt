package archives.tater.houseofcards.client.datagen;

import archives.tater.houseofcards.client.render.CardSpecialRenderer;
import archives.tater.houseofcards.registry.HouseOfCardsItems;

import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;

import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.resources.Identifier;

import static net.minecraft.client.data.models.model.ItemModelUtils.specialModel;

public class ModModelProvider extends FabricModelProvider {
    public ModModelProvider(FabricPackOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators blockModelGenerators) {

    }

    @Override
    public void generateItemModels(ItemModelGenerators itemModelGenerators) {
        itemModelGenerators.itemModelOutput.accept(HouseOfCardsItems.CARD, specialModel(Identifier.withDefaultNamespace("item/generated"), new CardSpecialRenderer.Unbaked()));
    }
}
