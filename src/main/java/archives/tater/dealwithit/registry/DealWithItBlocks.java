package archives.tater.dealwithit.registry;

import archives.tater.dealwithit.block.CardStackBlock;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.function.Function;

public interface DealWithItBlocks {

    Block CARD_STACK = register(DealWithItBlockIds.CARD_STACK, CardStackBlock::new, BlockBehaviour.Properties.of()
            .noCollision()
            .strength(0.2f, 0)
            .noLootTable()
            .noTerrainParticles()
    );

    private static Block register(ResourceKey<Block> id, Function<BlockBehaviour.Properties, Block> item, BlockBehaviour.Properties properties) {
        return Registry.register(BuiltInRegistries.BLOCK, id, item.apply(properties.setId(id)));
    }

    private static Block register(ResourceKey<Block> id, BlockBehaviour.Properties properties) {
        return register(id, Block::new, properties);
    }

    static void init() {

    }
}
