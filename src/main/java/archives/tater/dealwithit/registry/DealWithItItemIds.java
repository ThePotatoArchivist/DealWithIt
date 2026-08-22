package archives.tater.dealwithit.registry;

import archives.tater.dealwithit.DealWithIt;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

public interface DealWithItItemIds {

    ResourceKey<Item> CARD = create("card");
    ResourceKey<Item> CARD_BOX = create("card_box");

    private static ResourceKey<Item> create(String path) {
        return ResourceKey.create(Registries.ITEM, DealWithIt.id(path));
    }
}
