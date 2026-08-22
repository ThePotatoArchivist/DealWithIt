package archives.tater.dealwithit.registry;

import archives.tater.dealwithit.DealWithIt;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public interface DealWithItItemTags {

    TagKey<Item> FLIPPABLE = create("flippable");

    private static TagKey<Item> create(String path) {
        return TagKey.create(Registries.ITEM, DealWithIt.id(path));
    }
}
