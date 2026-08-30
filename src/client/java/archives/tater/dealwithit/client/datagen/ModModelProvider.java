package archives.tater.dealwithit.client.datagen;

import archives.tater.dealwithit.client.render.*;
import archives.tater.dealwithit.registry.DealWithItBlocks;
import archives.tater.dealwithit.registry.DealWithItComponents;
import archives.tater.dealwithit.registry.DealWithItItems;

import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;

import com.mojang.math.Transformation;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.renderer.item.ClientItem;
import net.minecraft.client.renderer.item.properties.conditional.HasComponent;
import net.minecraft.client.renderer.item.properties.select.DisplayContext;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.phys.Vec3;

import org.joml.Quaternionf;
import org.joml.Vector3f;

import static net.minecraft.client.data.models.model.ItemModelUtils.*;

public class ModModelProvider extends FabricModelProvider {

    public static final Identifier TEMPLATE_GENERATED = Identifier.withDefaultNamespace("item/generated");

    public ModModelProvider(FabricPackOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators blockModelGenerators) {
        blockModelGenerators.createParticleOnlyBlock(DealWithItBlocks.CARD_STACK);
    }

    @Override
    public void generateItemModels(ItemModelGenerators itemModelGenerators) {
        var faceDown = specialModel(TEMPLATE_GENERATED, new CardSpecialRenderer.Unbaked(true));
        var faceUp = specialModel(TEMPLATE_GENERATED, new CardSpecialRenderer.Unbaked(false));
        itemModelGenerators.itemModelOutput.accept(DealWithItItems.CARD, conditional(
                hasComponent(DealWithItComponents.CARD),
                select(new DisplayContext(),
                        conditional(new HasComponent(DealWithItComponents.FACE_DOWN, false),
                                faceDown,
                                faceUp
                        ),
                        when(ItemDisplayContext.THIRD_PERSON_LEFT_HAND, faceDown),
                        when(ItemDisplayContext.THIRD_PERSON_RIGHT_HAND, faceDown)
                ),
                plainModel(itemModelGenerators.createFlatItemModel(DealWithItItems.CARD, ModelTemplates.FLAT_ITEM))
        ));

        itemModelGenerators.itemModelOutput.accept(DealWithItItems.CARD_STACK, select(
                new DisplayContext(),
                specialModel(TEMPLATE_GENERATED, new CardStackSpecialRenderer.Unbaked(new Vec3(0, 0, CardStackRenderer.INTERVAL), true, Integer.MAX_VALUE, false)),
                when(ItemDisplayContext.GUI, composite(
                        specialModel(TEMPLATE_GENERATED, new Transformation(
                                new Vector3f(2 / 16f, 0, 0),
                                new Quaternionf(),
                                new Vector3f(1),
                                new Quaternionf()
                        ), new CardStackSpecialRenderer.Unbaked(new Vec3(-2 / 16f, 0, -1), false, 3, true)),
                        specialModel(TEMPLATE_GENERATED, new CardStackCountSpecialRenderer.Unbaked())
                ))
        ), new ClientItem.Properties(true, true, 1f));

        var blank = plainModel(itemModelGenerators.createFlatItemModel(DealWithItItems.BLANK_CARD_BOX, ModelTemplates.FLAT_ITEM));
        itemModelGenerators.itemModelOutput.accept(DealWithItItems.BLANK_CARD_BOX, blank);
        itemModelGenerators.itemModelOutput.accept(DealWithItItems.CARD_BOX, new ComponentModel.Unbaked<>(DealWithItComponents.DECK_CONTENTS, "dealwithit/card_box/", blank));
    }
}
