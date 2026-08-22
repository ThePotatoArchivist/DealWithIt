package archives.tater.houseofcards.registry;

import archives.tater.houseofcards.HouseOfCards;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;

public interface HouseOfCardsBlockIds {

    ResourceKey<Block> CARD_STACK = create("card_stack");

    private static ResourceKey<Block> create(String path) {
        return ResourceKey.create(Registries.BLOCK, HouseOfCards.id(path));
    }
}
