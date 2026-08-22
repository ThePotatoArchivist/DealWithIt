package archives.tater.houseofcards.registry;

import archives.tater.houseofcards.HouseOfCards;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

public interface HouseOfCardsItemIds {

    ResourceKey<Item> CARD = create("card");
    ResourceKey<Item> CARD_BOX = create("card_box");

    private static ResourceKey<Item> create(String path) {
        return ResourceKey.create(Registries.ITEM, HouseOfCards.id(path));
    }
}
