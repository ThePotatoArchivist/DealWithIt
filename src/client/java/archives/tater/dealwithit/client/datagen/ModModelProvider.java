package archives.tater.dealwithit.client.datagen;

import archives.tater.dealwithit.DealWithIt;
import archives.tater.dealwithit.client.render.block.entity.CardStackRenderer;
import archives.tater.dealwithit.client.render.item.model.ComponentModel;
import archives.tater.dealwithit.client.render.item.property.CardStackCount;
import archives.tater.dealwithit.client.render.item.special.CardSpecialRenderer;
import archives.tater.dealwithit.client.render.item.special.CardStackCountSpecialRenderer;
import archives.tater.dealwithit.client.render.item.special.CardStackSpecialRenderer;
import archives.tater.dealwithit.registry.DealWithItBlocks;
import archives.tater.dealwithit.registry.DealWithItComponents;
import archives.tater.dealwithit.registry.DealWithItItems;

import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;

import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.renderer.item.ClientItem;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.properties.select.DisplayContext;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.phys.Vec3;

import java.util.List;

import static net.minecraft.client.data.models.model.ItemModelUtils.*;

public class ModModelProvider extends FabricModelProvider {

    public static final Identifier TEMPLATE_CARD = DealWithIt.id("item/template_card");

    public ModModelProvider(FabricPackOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators blockModelGenerators) {
        blockModelGenerators.createParticleOnlyBlock(DealWithItBlocks.CARD_STACK);
    }

    @Override
    public void generateItemModels(ItemModelGenerators itemModelGenerators) {
        itemModelGenerators.itemModelOutput.accept(DealWithItItems.CARD, conditional(
                hasComponent(DealWithItComponents.CARD),
                select(new DisplayContext(),
                        specialModel(TEMPLATE_CARD, new CardSpecialRenderer.Unbaked(false)),
                        when(
                                List.of(ItemDisplayContext.THIRD_PERSON_LEFT_HAND, ItemDisplayContext.THIRD_PERSON_RIGHT_HAND),
                                specialModel(TEMPLATE_CARD, new CardSpecialRenderer.Unbaked(true))
                        )
                ),
                plainModel(itemModelGenerators.createFlatItemModel(DealWithItItems.CARD, ModelTemplates.FLAT_ITEM))
        ));

        itemModelGenerators.itemModelOutput.accept(DealWithItItems.CARD_STACK, select(
                new DisplayContext(),
                createFanVariants(false),
                when(List.of(ItemDisplayContext.THIRD_PERSON_LEFT_HAND, ItemDisplayContext.THIRD_PERSON_RIGHT_HAND), createFanVariants(true)),
                when(ItemDisplayContext.GUI, composite(
                        specialModel(TEMPLATE_CARD, new CardStackSpecialRenderer.Unbaked(new Vec3(-2 / 16f, 0, -1), false, 1, 3, true)),
                        specialModel(TEMPLATE_CARD,  new CardStackCountSpecialRenderer.Unbaked())
                ))
        ), new ClientItem.Properties(true, true, 1f));

        var blank = plainModel(itemModelGenerators.createFlatItemModel(DealWithItItems.BLANK_CARD_BOX, ModelTemplates.FLAT_ITEM));
        itemModelGenerators.itemModelOutput.accept(DealWithItItems.BLANK_CARD_BOX, blank);
        itemModelGenerators.itemModelOutput.accept(DealWithItItems.CARD_BOX, new ComponentModel.Unbaked<>(DealWithItComponents.DECK_CONTENTS, "dealwithit/card_box/", blank));
    }

    private static ItemModel.Unbaked createFanVariants(boolean forceHidden) {
        return rangeSelect(
                new CardStackCount(),
                specialModel(TEMPLATE_CARD, new CardStackSpecialRenderer.Unbaked(new Vec3(4 / 16f, 0, CardStackRenderer.INTERVAL), forceHidden, 1)),
                override(specialModel(TEMPLATE_CARD, new CardStackSpecialRenderer.Unbaked(new Vec3(2 / 16f, 0, CardStackRenderer.INTERVAL), forceHidden, 1)), 5),
                override(specialModel(TEMPLATE_CARD, new CardStackSpecialRenderer.Unbaked(new Vec3(1 / 16f, 0, CardStackRenderer.INTERVAL), forceHidden, 1)), 9),
                override(specialModel(TEMPLATE_CARD, new CardStackSpecialRenderer.Unbaked(new Vec3(0, 0, CardStackRenderer.INTERVAL), forceHidden, 4)), 17)
        );
    }
}
