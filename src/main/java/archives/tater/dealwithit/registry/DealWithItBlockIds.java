package archives.tater.dealwithit.registry;

import archives.tater.dealwithit.DealWithIt;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;

public interface DealWithItBlockIds {

    ResourceKey<Block> CARD_STACK = create("card_stack");

    private static ResourceKey<Block> create(String path) {
        return ResourceKey.create(Registries.BLOCK, DealWithIt.id(path));
    }
}
