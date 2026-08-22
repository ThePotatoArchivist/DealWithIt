package archives.tater.houseofcards.registry;

import archives.tater.houseofcards.HouseOfCards;
import archives.tater.houseofcards.block.entity.CardStackBlockEntity;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public interface HouseOfCardsBlockEntities {

    BlockEntityType<CardStackBlockEntity> CARD_STACK = register("card_stack", CardStackBlockEntity::new, HouseOfCardsBlocks.CARD_STACK);

    private static <T extends BlockEntity> BlockEntityType<T> register(String path, FabricBlockEntityTypeBuilder.Factory<T> factory, Block... supportedBlocks) {
        return Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, HouseOfCards.id(path), FabricBlockEntityTypeBuilder.create(factory, supportedBlocks).build());
    }

    static void init() {

    }
}
